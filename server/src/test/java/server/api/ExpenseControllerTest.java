package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.Event;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import commons.Expense;
import server.service.ExpenseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseControllerTest {
    private ExpenseService expenseService;
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        expenseService = mock(ExpenseService.class);
        expenseController = new ExpenseController(expenseService);
    }

    @Test
    void testGetAll() {
        List<Expense> mockExpenses = new ArrayList<>();
        List<Participant> participantsList = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        Participant participant3 = new Participant("third", "email3", "bic3", "iban3", 0);
        participantsList.add(participant1);
        participantsList.add(participant2);
        participantsList.add(participant3);
        Expense expense1 = new Expense("Expense 1", 100, new Date(), participant1, participantsList, new Tag());
        Expense expense2 = new Expense("Expense 2", 500, new Date(), participant3, participantsList, new Tag());
        Event event = new Event();
        event.setId(1L);
        List<Expense> listExpenses = new ArrayList<>();
        listExpenses.add(expense1);
        listExpenses.add(expense2);
        event.setExpenses(listExpenses);
        mockExpenses.add(expense1);
        mockExpenses.add(expense2);

        when(expenseService.getExpenses(anyLong())).thenReturn(new ResponseEntity<>(mockExpenses, HttpStatus.OK));

        ResponseEntity<List<Expense>> responseEntity = expenseController.getAll(event.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockExpenses, responseEntity.getBody());
    }

    @Test
    void testGetById() {
        Event mockEvent = new Event();
        mockEvent.setId(1L);

        List<Participant> participants = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        participants.add(participant1);
        participants.add(participant2);

        Expense expense1 = new Expense("Expense 1", 100, new Date(), participant1, participants, new Tag());
        Expense expense2 = new Expense("Expense 2", 500, new Date(), participant2, participants, new Tag());
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);

        mockEvent.setExpenses(expenses);
        expense1.setId(3L);

        when(expenseService.getExpenseById(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(expense1, HttpStatus.OK));

        ResponseEntity<Expense> responseEntity = expenseController.getById(mockEvent.getId(), expense1.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expense1, responseEntity.getBody());
    }

    @Test
    public void testGetExpensesOfPayer() {
        List<Expense> mockExpenses = new ArrayList<>();
        List<Participant> participantsList = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        participant1.setParticipantID(5L);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        Participant participant3 = new Participant("third", "email3", "bic3", "iban3", 0);
        participantsList.add(participant1);
        participantsList.add(participant2);
        participantsList.add(participant3);
        Expense expense1 = new Expense("Expense 1", 100, new Date(), participant1, participantsList, new Tag());
        expense1.setId(3L);
        Expense expense2 = new Expense("Expense 2", 500, new Date(), participant1, participantsList, new Tag());
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);
        mockExpenses.add(expense1);
        mockExpenses.add(expense2);
        Event event = new Event();
        event.setId(1L);
        event.setExpenses(expenses);
        when(expenseService.getExpensesOfPayer(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(expenses, HttpStatus.OK));

        ResponseEntity<List<Expense>> responseEntity = expenseController.getExpensesOfPayer(event.getId(), participant1.getParticipantID());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockExpenses, responseEntity.getBody());
    }

    @Test
    public void testGetExpensesOfOwer() {
        List<Expense> mockExpenses = new ArrayList<>();
        List<Participant> participantsList = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        participant1.setParticipantID(5L);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        Participant participant3 = new Participant("third", "email3", "bic3", "iban3", 0);
        participantsList.add(participant1);
        participantsList.add(participant2);
        participantsList.add(participant3);
        Expense expense1 = new Expense("Expense 1", 100, new Date(), participant1, participantsList, new Tag());
        expense1.setId(3L);

        Expense expense2 = new Expense("Expense 2", 500, new Date(), participant1, participantsList, new Tag());
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);
        mockExpenses.add(expense1);
        mockExpenses.add(expense2);

        Event event = new Event();
        event.setId(1L);
        event.setExpenses(expenses);

        when(expenseService.getExpensesOfPayer(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(mockExpenses, HttpStatus.OK));

        ResponseEntity<List<Expense>> responseEntity = expenseController.getExpensesOfPayer(event.getId(), participant1.getParticipantID());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockExpenses, responseEntity.getBody());
    }

    @Test
    public void testCreateExpense() {

        Event event = new Event();
        event.setId(1L);

        List<Participant> participants = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        participants.add(participant1);
        participants.add(participant2);

        Expense expenseToCreate = new Expense("Expense 1", 100, new Date(), participant1, participants, new Tag());
        Expense createdExpense = new Expense("Expense 1", 100, new Date(), participant1, participants, new Tag());

        when(expenseService.createExpense(anyLong(),any())).thenReturn(new ResponseEntity<>(createdExpense, HttpStatus.CREATED));

        ResponseEntity<Expense> responseEntity = expenseController.createExpense(event.getId(), expenseToCreate);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createdExpense, responseEntity.getBody());
    }

    @Test
    public void testDeletedExpense() {

        Event event = new Event();
        event.setId(1L);

        List<Participant> participants = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        participants.add(participant1);
        participants.add(participant2);

        Expense deletedExpense = new Expense("Expense 1", 100, new Date(), participant1, participants, new Tag());
        deletedExpense.setId(20L);
        event.setExpenses(new ArrayList<>());
        event.addExpense(deletedExpense);

        when(expenseService.deleteExpense(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(deletedExpense, HttpStatus.OK));

        /*ResponseEntity<Expense> responseEntity = expenseController.delete(event.getId(), deletedExpense.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedExpense, responseEntity.getBody());*/
    }

    @Test
    public void testUpdateExpense() {
        Event event = new Event();
        event.setId(1L);

        List<Participant> participants = new ArrayList<>();
        Participant participant1 = new Participant("first", "email1", "bic1", "iban1", 0);
        Participant participant2 = new Participant("second", "email2", "bic2", "iban2", 0);
        participants.add(participant1);
        participants.add(participant2);

        Expense oldExpense = new Expense("Expense 1", 100, new Date(), participant1, participants, new Tag());
        Expense updatedExpense = new Expense("Expense 1 NEW", 100, new Date(), participant1, participants, new Tag());

        when(expenseService.createExpense(anyLong(),any())).thenReturn(new ResponseEntity<>(updatedExpense, HttpStatus.OK));

        /*ResponseEntity<Expense> responseEntity = expenseController.createExpense(event.getId(), oldExpense);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedExpense, responseEntity.getBody());*/
    }
}
