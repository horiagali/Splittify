package commons;

import java.util.Objects;

public class Debt {
    private final Integer id;
    private final User owed;
    private final User indebted;
    private final Double amount;


    /**
     * Constructs a Debt object with the specified parameters.
     *
     * @param id        The ID of the debt.
     * @param owed      The user who is owed the debt.
     * @param indebted  The user who owes the debt.
     * @param amount    The amount of the debt.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public Debt(Integer id, User owed, User indebted, Double amount) {
        this.id = id;
        this.owed = owed;
        this.indebted = indebted;

        if (amount < 0) {
            throw new IllegalArgumentException("Debt can not be negative.");
        }
        this.amount = amount;

    }

    /**
     * Gets the ID of the debt.
     *
     * @return The ID of the debt.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the user who is owed the debt.
     *
     * @return The user who is owed the debt.
     */
    public User getOwed() {
        return owed;
    }

    /**
     * Gets the user who owes the debt.
     *
     * @return The user who owes the debt.
     */
    public User getIndebted() {
        return indebted;
    }

    /**
     * Gets the amount of the debt.
     *
     * @return The amount of the debt.
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o The reference object with which to compare.
     * @return true if this object is the same as the o argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Objects.equals(id, debt.id)
                && Objects.equals(owed, debt.owed)
                && Objects.equals(indebted, debt.indebted)
                && Objects.equals(amount, debt.amount);
    }

    /**
     * Returns a hash code value for the Debt object.
     *
     * @return A hash code value for this Debt object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, owed, indebted, amount);
    }

    /**
     * Returns a string representation of the Debt object.
     *
     * @return A string representation of the Debt object.
     */
    @Override
    public String toString() {
        return "Debt{" +
                "id=" + id +
                ", owed=" + owed +
                ", indebted=" + indebted +
                ", amount=" + amount +
                '}';
    }
}
