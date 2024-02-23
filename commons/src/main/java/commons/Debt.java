package commons;

import java.util.Objects;

public class Debt {
    private Integer id;
    private User owed;
    private User indebted;
    private Double amount;

    public Debt(Integer id, User owed, User indebted, Double amount) {
        this.id = id;
        this.owed = owed;
        this.indebted = indebted;

        if (amount < 0) {
            throw new IllegalArgumentException("Expense can not be negative.");
        }
        this.amount = amount;

    }

    public Integer getId() {
        return id;
    }

    public User getOwed() {
        return owed;
    }

    public User getIndebted() {
        return indebted;
    }

    public Double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Debt debt = (Debt) o;
        return Objects.equals(id, debt.id) && Objects.equals(owed, debt.owed) && Objects.equals(indebted, debt.indebted) && Objects.equals(amount, debt.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owed, indebted, amount);
    }

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
