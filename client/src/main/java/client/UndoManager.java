package client;

import commons.Expense;

import java.util.*;

public class UndoManager {
    private static final UndoManager instance = new UndoManager();
    private Map<Expense, Map<String, Stack<Object>>> undoHistories = new HashMap<>();

    private UndoManager() {
    }

    public static UndoManager getInstance() {
        return instance;
    }

    public void captureState(Expense expense, String field, Object value) {
        // Log arguments passed to captureState
        System.out.println("Capturing state for expense: " + expense + ", field: " + field + ", value: " + value);

        // Check if an undo history already exists for this expense
        Map<String, Stack<Object>> expenseUndoHistory = undoHistories.computeIfAbsent(expense, k -> new HashMap<>());
        // Log retrieved expense undo history
        System.out.println("Retrieved expense undo history: " + expenseUndoHistory);

        // Check if an undo history already exists for this field
        Stack<Object> fieldUndoHistory = getFieldHistory(expenseUndoHistory, field);
        System.out.println("kill myself if followed by null " + fieldUndoHistory);
        if (fieldUndoHistory == null) {
            fieldUndoHistory = new Stack<>();
            expenseUndoHistory.put(field, fieldUndoHistory);
        }
        // Push the new value onto the undo history stack for this field
        System.out.println("Pushing value " + value + " onto undo history for field " + field);
        fieldUndoHistory.push(value);
    }

    public Object undo(Expense expense, String field) {
        // Retrieve the undo history for the specified expense and field
        Map<String, Stack<Object>> expenseUndoHistory = getExpenseUndoHistory(expense);

        if (expenseUndoHistory != null) {
            Stack<Object> fieldUndoHistory = expenseUndoHistory.get(field);

            if (fieldUndoHistory != null && !fieldUndoHistory.isEmpty()) {
                // Pop the previous state from the undo history stack for this field and return it
                Object previousState = fieldUndoHistory.pop();
                System.out.println("Previous state popped for field " + field + ": " + previousState);

                // Log the updated undo history after popping
                System.out.println("Updated Undo History for Expense " + expense + ": " + expenseUndoHistory);

                return previousState;
            } else {
                System.out.println("No undo history available for field: " + field);
            }
        } else {
            System.out.println("No undo history available for expense: " + expense);
        }
        return null;
    }

    private Map<String, Stack<Object>> getExpenseUndoHistory(Expense expense) {
        for (Map.Entry<Expense, Map<String, Stack<Object>>> entry : undoHistories.entrySet()) {
            if (entry.getKey().getId().equals(expense.getId())) {
                System.out.println("slay");
                return entry.getValue();
            }
        }
        System.out.println("antislay");
        return null;
    }

    private Stack<Object> getFieldHistory(Map<String, Stack<Object>> expenseUndoHistory, String field) {
        for (Map.Entry<String, Stack<Object>>  entry : expenseUndoHistory.entrySet()) {
            if (entry.getKey().equals(field)) {
                System.out.println("slay");
                return entry.getValue();
            }
        }
        System.out.println("antislay");
        return null;
    }
}