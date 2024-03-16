package server.service;

import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private ExpenseRepository expenseRepository;
    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;

    private EventService eventService;
    private ParticipantService participantService;

    /**
     * Constructor for service
     * 
     * @param expenseRepository     repo for expenses
     * @param eventRepository       repo for events
     * @param participantRepository repo for participants
     */
    public ExpenseService(ExpenseRepository expenseRepository,
            EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        eventService = new EventService(eventRepository);
        participantService = new ParticipantService(participantRepository, eventRepository);

    }

    /**
     * creates an expense
     * 
     * @param eventId id of event where the expense is created
     * @param expense created expense
     * @return expense
     */
    @Transactional
    public ResponseEntity<Expense> createExpense(Long eventId, Expense expense) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new IllegalArgumentException("Event with given ID not found");
        else {
            Expense newExpese = new Expense();
            expense.toString();
            newExpese.setAmount(expense.getAmount());
            newExpese.setOwers(expense.getOwers());
            newExpese.setTag(expense.getTag());
            newExpese.setTitle(expense.getTitle());
            newExpese.setEvent(event.get());
            newExpese.toString();
            Expense saved = balancing(expense, event, newExpese, expense.getAmount());
            return ResponseEntity.ok(saved);
        }
    }

    private Expense balancing(Expense expense, 
    Optional<Event> event, Expense newExpese, double amount) {
        Participant payer = participantRepository
                .findById(expense.getPayer().getParticipantID()).orElse(null);
        newExpese.setPayer(payer);
        List<Participant> participants = new ArrayList<>();
        eventRepository.save(event.get());
        participants.addAll(event.get().getParticipants());
        participants.remove(payer);
        payer.setBalance(payer.getBalance() + (amount));
        participantRepository.save(payer);
        participants.add(payer);
        List<Participant> newOwers = new ArrayList<>();
        for (Participant pp : newExpese.getOwers()) {
            Participant p = participantRepository.findById(pp.getParticipantID()).orElse(null);
            participants.remove(p);
            p.setBalance(p.getBalance() - amount / (newExpese.getOwers().size()));
            participantRepository.save(p);
            participants.add(p);
            newOwers.add(p);
        }
        newExpese.setOwers(newOwers);
        Event newEvent = event.get();
        newEvent.toString();
        newEvent.setParticipants(participants);
        eventRepository.save(newEvent);
        Expense saved = expenseRepository.save(newExpese);
        return saved;
    }

    /**
     * Find all expenses in the repository belonging to 1 event
     * 
     * @param eventId
     * @return List of expenses
     */
    public ResponseEntity<List<Expense>> getExpenses(Long eventId) {
        return ResponseEntity.ok(expenseRepository.findExpensesByEventId(eventId));
    }

    /**
     * finds all expenses where 1 specific participant is the payer.
     * 
     * @param eventId       event which contains expenses
     * @param participantId participantId of the payer
     * @return list of expenses where the participant is the payer
     */
    public ResponseEntity<List<Expense>> getExpensesOfPayer(Long eventId, Long participantId) {
        Event event = eventService.getEventById(eventId).getBody();
        Participant payer = participantService.getParticipantById(eventId, participantId).getBody();
        List<Expense> expenses = event.getExpenses();
        for (Expense expense : expenses) {
            if (!expense.getPayer().equals(payer))
                expenses.remove(expense);
        }
        return ResponseEntity.ok(expenses);
    }

    /**
     * finds all expenses where 1 specific participant is an ower
     * 
     * @param eventId       event which contains expenses
     * @param participantId id of the ower
     * @return list of expenses where the participant is an ower
     */
    public ResponseEntity<List<Expense>> getExpensesOfOwer(Long eventId, Long participantId) {
        Event event = eventService.getEventById(eventId).getBody();
        Participant ower = participantService.getParticipantById(eventId, participantId).getBody();
        List<Expense> expenses = event.getExpenses();
        for (Expense expense : expenses) {
            if (!expense.getOwers().contains(ower))
                expenses.remove(expense);
        }
        return ResponseEntity.ok(expenses);
    }

    /**
     * get expense by its id.
     * 
     * @param eventId   id of event of this expense
     * @param expenseId id of this expense
     * @return expense belonging to this id
     */
    public ResponseEntity<Expense> getExpenseById(Long eventId, Long expenseId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        if (expense.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if (expense.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        return ResponseEntity.ok(expense.get());
    }

    /**
     * updates the expense
     * @param eventId id of event of expense to update
     * @param expenseId id of expense to update
     * @param expense changed form of expense
     * @return expense
     */
    public ResponseEntity<Expense> updateExpense(Long eventId, 
    Long expenseId, Expense expense) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> newExpesee = expenseRepository.findById(expenseId);
        if(newExpesee.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(newExpesee.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        Expense oldExpense = newExpesee.get();
        Expense newExpese = new Expense();
        newExpese.setId(expenseId);
        expense.toString();
        newExpese.setAmount(expense.getAmount());
        newExpese.setOwers(oldExpense.getOwers());
        newExpese.setTag(expense.getTag());
        newExpese.setTitle(expense.getTitle());
        newExpese.setEvent(event.get());
        newExpese.toString();
        Expense oldExpenseAfterBalancing = balancing(oldExpense, 
        event, newExpese, -oldExpense.getAmount());
        newExpese.setOwers(expense.getOwers());
        Expense realUpdatedExpense = balancing(expense, event, newExpese, expense.getAmount());
        return ResponseEntity.ok(realUpdatedExpense);
    }

    /**
     * deletes an expense
     * 
     * @param eventId
     * @param expenseId
     * @return expense
     */
    public ResponseEntity<Expense> deleteExpense(Long eventId, Long expenseId) {
        /// NEED TO REVERT BALANCES
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty())
            throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> oExpense = expenseRepository.findById(expenseId);
        if (oExpense.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        Expense expense = oExpense.get();
        if (expense.getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        expense.toString();
        Expense newExpense = new Expense();
        newExpense.setOwers(expense.getOwers());
        Expense oldExpenseAfterBalancing = balancing(expense, 
        event, newExpense, -expense.getAmount());
        expenseRepository.deleteById(expenseId);
        expenseRepository.delete(oldExpenseAfterBalancing);
        expense.toString();
        return ResponseEntity.ok(expense);
    }

}
