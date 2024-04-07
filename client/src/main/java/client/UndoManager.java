package client;

import commons.Expense;
import commons.Participant;
import commons.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class UndoManager<T> {
    private static final UndoManager<Expense> instance = new UndoManager<>();
    private Stack<ExpenseSnapshot> undoStack = new Stack<>();

    private UndoManager() {
    }

    /**
     * Returns the singleton instance of UndoManager.
     *
     * @return the UndoManager instance
     */
    public static UndoManager<Expense> getInstance() {
        return instance;
    }

    /**
     * Captures the current state of an Expense object for potential undo operations.
     *
     * @param expense the Expense object to capture the state from
     */
    public void captureState(Expense expense) {
        // Create a snapshot of the expense and push it onto the undo stack
        ExpenseSnapshot snapshot = new ExpenseSnapshot(expense);
        undoStack.push(snapshot);
    }

    /**
     * Undoes the last operation by restoring the previous state of the Expense object.
     *
     * @return the Expense object representing the state before the last operation,
     * or null if the undo stack is empty
     */
    public ExpenseSnapshot undo() {
        if (!undoStack.isEmpty()) {
            // Pop the last snapshot from the undo stack and restore the state
            ExpenseSnapshot snapshot = undoStack.pop();
            return snapshot;
        } else {
            System.out.println("Undo stack is empty.");
            return null;
        }
    }

    /**
     * Inner class to represent a snapshot of an Expense object.
     */
    public static class ExpenseSnapshot {
        private final String title;
        private final double amount;
        private final Participant payer;
        private final List<Participant> owers;
        private final Date date;
        private final Tag tag;
        private Long id;

        /**
         * Constructs an ExpenseSnapshot from an Expense object.
         *
         * @param expense the Expense object to create a snapshot from
         */
        public ExpenseSnapshot(Expense expense) {
            this.title = expense.getTitle();
            this.amount = expense.getAmount();
            this.payer = expense.getPayer();
            this.owers = new ArrayList<>(expense.getOwers());
            this.date = new Date(expense.getDate().getTime());
            this.tag = expense.getTag();
            this.id = expense.getId();
        }


        /**
         * Restores the state of an Expense object from this snapshot.
         *
         * @param restoredExpense the Expense object to restore the state to
         * @return the restored Expense object
         */
        public Expense restore(Expense restoredExpense) {
            restoredExpense.setTitle(this.title);
            restoredExpense.setAmount(this.amount);
            restoredExpense.setPayer(this.payer);
            restoredExpense.setOwers(new ArrayList<>(this.owers));
            restoredExpense.setDate(new Date(this.date.getTime()));
            restoredExpense.setTag(this.tag);
            return restoredExpense;
        }

        /**
         * Retrieves the ID of the Expense associated with this snapshot.
         *
         * @return the ID of the Expense
         */
        public Long getExpenseId() {
            return this.id;
        }
    }
}
