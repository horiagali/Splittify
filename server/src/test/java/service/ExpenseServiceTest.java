    package service;

    import commons.Event;
    import commons.Expense;
    import commons.Participant;
    import commons.Tag;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import server.database.ExpenseRepository;
    import server.database.EventRepository;
    import server.database.ParticipantRepository;
    import server.database.TagRepository;
    import server.service.ExpenseService;

    import java.awt.*;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.mock;
    import static org.mockito.Mockito.when;

    class ExpenseServiceTest {

        private ExpenseRepository expenseRepository;
        private EventRepository eventRepository;
        private ParticipantRepository participantRepository;
        private TagRepository tagRepository;
        private ExpenseService expenseService;
        private Long eventId;

        private Event event;
        private List<Expense> expenses;
        private List<Participant> participants;
        private List<Tag> tags;
        private Tag tag;

        private Expense testExpense;
        @BeforeEach
        void setUp() {
            expenseRepository = mock(ExpenseRepository.class);
            participantRepository = mock(ParticipantRepository.class);
            tagRepository = mock(TagRepository.class);
            eventRepository=mock(EventRepository.class);
            expenseService = new ExpenseService(expenseRepository,eventRepository, participantRepository, tagRepository);

            // Create participants
            Participant participant = new Participant("John", "john@example.com", null, null, 0);
            participants = new ArrayList<>();
            participants.add(participant);

            // Create tags
            tag = new Tag("Tag", "Red");
            tags = new ArrayList<>();
            tags.add(tag);

            // Create a test event
            eventId = 1L;
            event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", new ArrayList<>(), participants, tags);
            event.setId(eventId);

            // Create a test expense with all attributes filled
            testExpense = new Expense("Test Expense", 100.0, new Date(), participant, participants, tag);
            testExpense.setId(1L);
            expenses = new ArrayList<>();
            expenses.add(testExpense);
        }

        @Test
        void testCreateExpense() {

            // Mock behavior
            when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

            // Perform the test
            ResponseEntity<Expense> response = expenseService.createExpense(1L,testExpense);

            // Verify the result
            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());

        }

        @Test
        void testGetExpenses() {
            List<Expense> expenses = new ArrayList<>();
            expenses.add(testExpense);

            when(expenseRepository.findExpensesByEventId(eventId)).thenReturn(expenses);

            ResponseEntity<List<Expense>> response = expenseService.getExpenses(eventId);

            assertEquals(expenses, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testGetExpenseById() {
            Long expenseId = 1L;
            testExpense.setId(expenseId);

            when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));

            ResponseEntity<Expense> response = expenseService.getExpenseById(eventId, expenseId);

            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testDeleteExpense() {
            Long expenseId = 1L;
            testExpense.setId(expenseId);

            when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));

            ResponseEntity<Expense> response = expenseService.deleteExpense(eventId, expenseId);

            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testUpdateExpense() {
            Long expenseId = 1L;
            testExpense.setId(expenseId);

            when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));

            ResponseEntity<Expense> response = expenseService.updateExpense(eventId, expenseId, testExpense);

            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testGetExpensesOfPayer() {
            Long participantId = 1L;

            when(participantRepository.findById(participantId)).thenReturn(Optional.of(new Participant()));

            ResponseEntity<List<Expense>> response = expenseService.getExpensesOfPayer(eventId, participantId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testGetExpensesOfOwer() {
            Long participantId = 1L;

            when(participantRepository.findById(participantId)).thenReturn(Optional.of(new Participant()));

            ResponseEntity<List<Expense>> response = expenseService.getExpensesOfOwer(eventId, participantId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testCreateExpenseDebt() {
            ResponseEntity<Expense> response = expenseService.createExpenseDebt(eventId, testExpense);

            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        void testDeleteExpenseDebt() {
            Long expenseId = 1L;
            testExpense.setId(expenseId);

            when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(testExpense));

            ResponseEntity<Expense> response = expenseService.deleteExpenseDebt(eventId, expenseId);

            assertEquals(testExpense, response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
