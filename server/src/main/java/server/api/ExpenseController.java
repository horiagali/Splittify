package server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import commons.Expense;
import server.service.ExpenseService;


@RestController
@RequestMapping("/api/events/{event_id}/expenses")

public class ExpenseController {
    private final ExpenseService expenseService;
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor for this service class
     * @param expenseService service to be used
     */
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * returns all expenses related to an event
     * @param eventId
     * @return list of expenses
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getAll(@PathVariable(name = "event_id") Long eventId) {
        return expenseService.getExpenses(eventId);
    }

    /**
     * get an expense by their expenseId
     * @param eventId
     * @param expenseId the id
     * @return expense
     */
    @GetMapping("/{expense_id}")
    public ResponseEntity<Expense> getById(@PathVariable(name = "event_id") Long eventId, 
    @PathVariable(name = "expense_id") Long expenseId) {
        return expenseService.getExpenseById(eventId, expenseId);
    }

    /**
     * returns all expenses where 1 participant is payer
     * @param eventId
     * @param participantId
     * @return list of expenses
     */
    @GetMapping("/payer={participant_id}")
    public ResponseEntity<List<Expense>> getExpensesOfPayer
    (@PathVariable(name = "event_id") Long eventId, 
    @PathVariable(name = "participant_id") Long participantId) {
        return expenseService.getExpensesOfPayer(eventId, participantId);
    }

    /**
     * returns all expenses where 1 participant is ower
     * @param eventId
     * @param participantId
     * @return list of expenses
     */
    @GetMapping("/ower={participant_id}")
    public ResponseEntity<List<Expense>> getExpensesOfOwer
    (@PathVariable(name = "event_id") Long eventId, 
    @PathVariable(name = "participant_id") Long participantId) {
        return expenseService.getExpensesOfOwer(eventId, participantId);
    }


    /**
     * Creates an expense
     * @param eventId event where the expense is supposed to be created in
     * @param expense the expense that needs to be created
     * @return expense
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Expense> createExpense(@PathVariable(name = "event_id")Long eventId, 
    @RequestBody Expense expense) {
        ResponseEntity<Expense> created = expenseService.createExpense(eventId, expense);
        /*if (created.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/expenses", created.getBody());
        }*/
        return created;
    }

    /**
     * Creates an expense debt
     * @param eventId event where the expense is supposed to be created in
     * @param expense the expense that needs to be created
     * @return expense
     */
    @PostMapping("/debt")
    @ResponseBody
    public ResponseEntity<Expense> createExpenseDebt(@PathVariable(name = "event_id")Long eventId,
                                                 @RequestBody Expense expense) {
        ResponseEntity<Expense> created = expenseService.createExpenseDebt(eventId, expense);
        /*if (created.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/expenses", created.getBody());
        }*/
        return created;
    }

    /**
     * updates an expense
     * @param eventId event the expense belongs to
     * @param expenseId id of expense
     * @param expense updated version of the expense
     * @return expense
     */
    @PutMapping("/{expense_id}")
    public ResponseEntity<Expense> update(@PathVariable(name = "event_id") Long eventId,
    @PathVariable(name = "expense_id") Long expenseId, @RequestBody Expense expense) {

        ResponseEntity<Expense> updated = expenseService.updateExpense(eventId, expenseId, expense);
        /*if (updated.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/expenses", updated.getBody());
        }*/
        return updated;
    }

    /**
     * deletes an expense.
     * @param eventId id of event this expense belongs to,
     * @param expenseId id of expense that needs to be deleted
     * @return expense
     */
    @DeleteMapping("/{expense_id}")
    public ResponseEntity<Expense> delete(@PathVariable(name = "event_id") Long eventId, 
    @PathVariable(name = "expense_id") Long expenseId) {
        ResponseEntity<Expense> deleted = expenseService.deleteExpense(eventId, expenseId);
        /*if (deleted.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/expenses", deleted.getBody());
        }*/
        return deleted;
    }

    /**
     * deletes an expense.
     * @param eventId id of event this expense belongs to,
     * @param expenseId id of expense that needs to be deleted
     * @return expense
     */
    @DeleteMapping("/debt/{expense_id}")
    public ResponseEntity<Expense> deleteDebt(@PathVariable(name = "event_id") Long eventId,
                                          @PathVariable(name = "expense_id") Long expenseId) {
        ResponseEntity<Expense> deleted = expenseService.deleteExpenseDebt(eventId, expenseId);
        /*if (deleted.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/expenses", deleted.getBody());
        }*/
        return deleted;
    }
}




