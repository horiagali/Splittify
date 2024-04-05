package client;

import commons.Expense;

import java.util.*;

public class UndoManager {
    private static final UndoManager instance = new UndoManager();
    private Map<Long, Map<String, Stack<Object>>> undoHistories = new HashMap<>();

    private UndoManager() {
    }

    public static UndoManager getInstance() {
        return instance;
    }

    public void captureState(Expense expense, String field, Object value) {
        // Retrieve the expense ID
        Long expenseId = expense.getId();

        // Retrieve or create the undo history map for the expense ID
        Map<String, Stack<Object>> expenseUndoHistory = undoHistories.computeIfAbsent(expenseId, k -> new HashMap<>());

        // Retrieve or create the stack for the specified field
        Stack<Object> fieldUndoHistory = expenseUndoHistory.computeIfAbsent(field, k -> new Stack<>());

        // Push the new value onto the undo history stack for this field
        fieldUndoHistory.push(value);
    }

    public Object undo(Expense expense, String field) {
        // Retrieve the expense ID
        Long expenseId = expense.getId();

        // Retrieve the undo history for the specified expense ID and field
        Map<String, Stack<Object>> expenseUndoHistory = undoHistories.get(expenseId);

        if (expenseUndoHistory != null) {
            Stack<Object> fieldUndoHistory = expenseUndoHistory.get(field);

            if (fieldUndoHistory != null && !fieldUndoHistory.isEmpty()) {
                // Pop the previous state from the undo history stack for this field and return it
                Object previousState = fieldUndoHistory.pop();
                System.out.println("Previous state popped for field " + field + ": " + previousState);

                return previousState;
            } else {
                System.out.println("No undo history available for field: " + field);
            }
        } else {
            System.out.println("No undo history available for expense ID: " + expenseId);
        }
        return null;
    }

    public Object peek(Expense expense, String field) {
        // Retrieve the expense ID
        Long expenseId = expense.getId();

        // Retrieve the undo history for the specified expense ID and field
        Map<String, Stack<Object>> expenseUndoHistory = undoHistories.get(expenseId);

        if (expenseUndoHistory != null) {
            Stack<Object> fieldUndoHistory = expenseUndoHistory.get(field);

            if (fieldUndoHistory != null && !fieldUndoHistory.isEmpty()) {
                // Peek at the previous state from the undo history stack for this field and return it
                Object previousState = fieldUndoHistory.peek();
                System.out.println("Previous state peeked for field " + field + ": " + previousState);

                return previousState;
            } else {
                System.out.println("No undo history available for field: " + field);
            }
        } else {
            System.out.println("No undo history available for expense ID: " + expenseId);
        }
        return null;
    }
}
