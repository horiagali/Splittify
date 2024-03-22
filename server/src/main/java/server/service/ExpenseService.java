package server.service;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final ParticipantRepository participantRepository;

    private final EventService eventService;
    private final ParticipantService participantService;

    /**
     * Constructor for service
     * 
     * @param expenseRepository     repo for expenses
     * @param eventRepository       repo for events
     * @param participantRepository repo for participants
     * @param tagRepository repo for tags
     */
    public ExpenseService(ExpenseRepository expenseRepository,
                          EventRepository eventRepository,
                          ParticipantRepository participantRepository,
                          TagRepository tagRepository) {

        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.tagRepository = tagRepository;
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
        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Expense newExpense = new Expense();
        newExpense.setAmount(expense.getAmount());
        newExpense.setOwers(expense.getOwers());
        Tag tag = tagRepository
                .findById(expense.getTag().getId()).orElse(null);
        newExpense.setTag(tag);
        newExpense.setTitle(expense.getTitle());
        newExpense.setEvent(event);
        newExpense.toString();
        Expense saved = balancing(expense, event, newExpense, expense.getAmount());
        return ResponseEntity.ok(saved);

    }

    /**
     * (To be written)
     * @param expense
     * @param event
     * @param newExpense
     * @param amount
     * @return returns new expense i guess
     */
    private Expense balancing(Expense expense,
                              Event event,
                              Expense newExpense,
                              double amount) {
        Participant payer = participantRepository
                .findById(expense.getPayer().getParticipantID()).orElse(null);
        newExpense.setPayer(payer);
        List<Participant> participants = new ArrayList<>();
        eventRepository.save(event);
        participants.addAll(event.getParticipants());
        participants.remove(payer);
        payer.setBalance(payer.getBalance() + (amount));
        participantRepository.save(payer);
        participants.add(payer);
        List<Participant> newOwers = new ArrayList<>();
        for (Participant pp : newExpense.getOwers()) {
            Participant p = participantRepository.findById(pp.getParticipantID()).orElse(null);
            participants.remove(p);
            p.setBalance(p.getBalance() - amount / (newExpense.getOwers().size()));
            participantRepository.save(p);
            participants.add(p);
            newOwers.add(p);
        }
        newExpense.setOwers(newOwers);
        Event newEvent = event;
        newEvent.toString();
        newEvent.setParticipants(participants);
        eventRepository.save(newEvent);
        Expense saved = expenseRepository.save(newExpense);
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

        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!expenseRepository.findById(expenseId).isPresent()) {
            System.out.println("Expense with given ID not found");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Expense expense = expenseRepository.findById(expenseId).get();

        if (expense.getEvent() != event) {
            System.out.println("Expense does not belong to event");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(expense);
    }

    /**
     * updates the expense
     * @param eventId id of event of expense to update
     * @param expenseId id of expense to update
     * @param expense changed form of expense
     * @return expense
     */
    public ResponseEntity<Expense> updateExpense(Long eventId,
                                                 Long expenseId,
                                                 Expense expense) {

        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!expenseRepository.findById(expenseId).isPresent()) {
            System.out.println("Expense with given ID not found");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Expense oldExpense = expenseRepository.findById(expenseId).get();

        if(expense.getEvent() != event) {
            System.out.println("Expense does not belong to event");
            return ResponseEntity.notFound().build();
        }

        Expense newExpense = new Expense();

        newExpense.setId(expenseId);
        newExpense.setAmount(expense.getAmount());
        newExpense.setOwers(oldExpense.getOwers());
        Tag tag = tagRepository
                .findById(expense.getTag().getId()).orElse(null);
        newExpense.setTag(tag);
        newExpense.setTitle(expense.getTitle());
        newExpense.setEvent(event);

        Expense oldExpenseAfterBalancing = balancing(
                oldExpense,
                event,
                newExpense,
                -oldExpense.getAmount());

        newExpense.setOwers(expense.getOwers());
        Expense realUpdatedExpense = balancing(expense, event, newExpense, expense.getAmount());

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
        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!expenseRepository.findById(expenseId).isPresent()) {
            System.out.println("Expense with given ID not found");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Expense expense = expenseRepository.findById(expenseId).get();

        if(expense.getEvent() != event) {
            System.out.println("Expense does not belong to event");
            return ResponseEntity.notFound().build();
        }

        Expense newExpense = new Expense();
        newExpense.setOwers(expense.getOwers());

        Expense oldExpenseAfterBalancing = balancing(
                expense,
                event,
                newExpense,
                -expense.getAmount());

        expenseRepository.deleteById(expenseId);
        expenseRepository.delete(oldExpenseAfterBalancing);

        return ResponseEntity.ok(expense);
    }

}
