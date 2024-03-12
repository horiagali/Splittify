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
     * @param expenseRepository repo for expenses
     * @param eventRepository repo for events
     * @param participantRepository repo for participants
     */
    public ExpenseService(ExpenseRepository expenseRepository, 
    EventRepository eventRepository, ParticipantRepository participantRepository){
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        eventService = new EventService(eventRepository);
        participantService = new ParticipantService(participantRepository, eventRepository);

    }

    /**
     * creates an expense
     * @param eventId id of event where the expense is created
     * @param expense created expense
     * @return expense
     */
    @Transactional
    public ResponseEntity<Expense> createExpense(Long eventId, Expense expense) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        else {
            Expense newExpese = new Expense();
            newExpese.setAmount(expense.getAmount());
            newExpese.setOwers(expense.getOwers());
            newExpese.setPayer(expense.getPayer());
            newExpese.setTag(expense.getTag());
            newExpese.setTitle(expense.getTitle());
            newExpese.setEvent(event.get());
            double amount = expense.getAmount();
            Participant payer = expense.getPayer();
            payer.setBalance(payer.getBalance() + amount);
            participantRepository.save(payer);
            for (Participant p : newExpese.getOwers()){
                p.setBalance(p.getBalance() - amount / newExpese.getOwers().size());
                participantRepository.save(p);
            }
            Expense saved = expenseRepository.save(newExpese);
            return ResponseEntity.ok(saved);
        }
    }

    /**
     * Find all expenses in the repository belonging to 1 event
     * @param eventId
     * @return List of expenses
     */
    public List<Expense> getExpenses(Long eventId) {
        return expenseRepository.findExpensesByEventId(eventId);
    }


    /**
     * finds all expenses where 1 specific participant is the payer.
     * @param eventId event which contains expenses
     * @param participantId participantId of the payer
     * @return list of expenses where the participant is the payer
     */
    public List<Expense> getExpensesOfPayer(Long eventId, Long participantId) {
        Event event = eventService.getEventById(eventId).getBody();
        Participant payer = participantService.getParticipantById(eventId, participantId).getBody();
        List<Expense> expenses = event.getExpenses();
        for(Expense expense : expenses) {
            if(!expense.getPayer().equals(payer))
            expenses.remove(expense);
        }
        return expenses;
    }

    /**
     * finds all expenses where 1 specific participant is an ower
     * @param eventId event which contains expenses
     * @param participantId id of the ower
     * @return list of expenses where the participant is an ower
     */
    public List<Expense> getExpensesOfOwer(Long eventId, Long participantId) {
        Event event = eventService.getEventById(eventId).getBody();
        Participant ower = participantService.getParticipantById(eventId, participantId).getBody();
        List<Expense> expenses = event.getExpenses();
        for(Expense expense : expenses) {
            if(!expense.getOwers().contains(ower))
            expenses.remove(expense);
        }
        return expenses;
    }

    /**
     * get expense by its id.
     * @param eventId id of event of this expense
     * @param expenseId id of this expense
     * @return expense belonging to this id
     */
    public ResponseEntity<Expense> getExpenseById(Long eventId, Long expenseId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        if(expense.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(expense.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        return ResponseEntity.ok(expense.get());
    }

    /**
     * updates the expense
     * @param eventId id of event of expense to update
     * @param expenseId id of expense to update
     * @param changedExpense changed form of expense
     * @return expense
     */
    public ResponseEntity<Expense> updateExpense(Long eventId, 
    Long expenseId, Expense changedExpense) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        if(expense.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(expense.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        
        expense.get().setAmount(changedExpense.getAmount());
        expense.get().setOwers(changedExpense.getOwers());
        expense.get().setPayer(changedExpense.getPayer());
        expense.get().setTag(changedExpense.getTag());
        expense.get().setTitle(changedExpense.getTitle());
        expense.get().setEvent(event.get());
        Expense saved = expenseRepository.save(expense.get());
        return ResponseEntity.ok(saved);
    }


    /**
     * deletes an expense
     * @param eventId
     * @param expenseId
     * @return expense
     */
    public ResponseEntity<Expense> deleteExpense(Long eventId, Long expenseId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Expense> oExpense = expenseRepository.findById(expenseId);
        if(oExpense.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        Expense expense = oExpense.get();
        if(expense.getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        expense.toString();
        expenseRepository.deleteById(expenseId);
        expense.toString();
        return ResponseEntity.ok(expense);
    }

    
}
