package commons;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    @ManyToOne
    private Participant payee; //payee is person who should get money from payors
    @OneToMany
    private ArrayList<Participant> payors; //the people who owe money

    private double amount;

    @ManyToOne
    private Event event;

    // Constructors
    public Expense() {
        // Default constructor for JPA
    }

    
    public Expense(String title, double amount, Participant payee, ArrayList<Participant> payors) {
        this.title = title;
        this.payee = payee;
        this.payors = payors;

        if (amount < 0) {
            throw new IllegalArgumentException("Expense can not be negative.");
        }
        this.amount = amount;

    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Participant getPayee() {
        return payee;
    }

    public void setPayee(Participant payee) {
        this.payee = payee;
    }

    public ArrayList<Participant> getPayors() {
        return payors;
    }

    public void setPayors(ArrayList<Participant> payors) {
        this.payors = payors;
    }

    //method for settling debts of event. When this is called, people matched to this expense will pay up (in debt, not real time)
//    public void settleDebts() {
//        payee.setDebt(payee.getDebt() + amount); //increase the debt of the person who paid (positive is surplus)
//        for(Participant payor : payors) {
//            payor.setDebt(payor.getDebt() - amount/payors.size()); //decrease the debt of person who now settles their debt to payee
//        }
//    }
    public void settleDebts() {
        payee.setDebt(payee.getDebt() + amount); // Increase the debt of the payee by the full amount

        // Calculate the individual share for each payor
        double individualShare = amount / payors.size();

        // Distribute the amount among payors without rounding errors
        for (Participant payor : payors) {
            double updatedDebt = payor.getDebt() - individualShare;
            payor.setDebt(updatedDebt);
        }

        // Handle any remaining amount due to rounding errors by distributing it to the first payor
        double remainingAmount = amount - (individualShare * payors.size());
        if (!payors.isEmpty()) {
            double firstPayorDebt = payors.get(0).getDebt();
            payors.get(0).setDebt(firstPayorDebt - remainingAmount);
        }
    }


    //this method does the same as the previous one in reverse. This is needed when editing an expense or deleting it alltogether.
    //when editing an expense, you remove the debts of the old one and add the new one.
    public void reverseSettleDebts() {
        payee.setDebt(payee.getDebt() - amount); //decrease the debt of the person who paid (positive is surplus)
        for(Participant payor : payors) {
            payor.setDebt(payor.getDebt() + amount/payors.size()); //increase the debt of person who now settles their debt to payee
        }
    }

    // Equals, HashCode, ToString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id &&
                Double.compare(expense.amount, amount) == 0 &&
                Objects.equals(title, expense.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, amount);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", id)
                .append("title", title)
                .append("amount", amount)
                .toString();
    }
}

