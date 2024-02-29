package commons;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
-ExpenseID (long) (ID)
- Title (String)
- Payer (Participant)
- Amount (double)
- owers (List<Participants>) (Many to Many?)
        - Date of transaction (figure out another name, maybe just date) (Date)
        - Tag (tag)
*/
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    @ManyToOne
    private Participant payer;
    @OneToMany(targetEntity = Participant.class)
    private List<Participant> owers; //the people who owe money

    private double amount;

    /**
     * Constructors
     */
    public Expense() {
        // Default constructor for JPA
    }
    // creates an expense and modifies everyone's balance accordingly

    /**
     * Constructs a new Expense object with the given title, amount, payer, and owers.
     * @param title The title of the expense.
     * @param amount The amount of the expense.
     * @param payer The participant who paid the expense.
     * @param owers The list of participants who owe money for the expense.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public Expense(String title, double amount, Participant payer, ArrayList<Participant> owers) {
        this.title = title;
        this.owers = owers;
        this.payer = payer;

        if (amount < 0) {
            throw new IllegalArgumentException("Expense can not be negative.");
        }
        this.amount = amount;

        payer.setBalance(payer.getBalance() + amount);
        //increase the balance of the person who paid
        for (Participant ower : owers) {
            ower.setBalance(ower.getBalance() - amount / owers.size());
            //decrease the balance of person who now settles their balance to payee
        }

    }

    /**
     * Returns the ID of the expense.
     * @return The ID of the expense.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the expense.
     * @param id The ID to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the title of the expense.
     * @return The title of the expense.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the expense.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the amount of the expense.
     * @return The amount of the expense.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the expense.
     * @param amount The amount to set.
     * @throws IllegalArgumentException if the amount is negative.
     */
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }


    /**
     * Returns the payer of the expense.
     * @return The payer of the expense.
     */
    public Participant getPayer() {
        return payer;
    }

    /**
     * Sets the payer of the expense.
     * @param payer The payer to set.
     */
    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    /**
     * Returns the list of participants who owe money for the expense.
     * @return The list of participants who owe money for the expense.
     */
    public List<Participant> getOwers() {
        return owers;
    }

    /**
     * Sets the list of participants who owe money for the expense.
     * @param owers The list of participants to set.
     */
    public void setOwers(ArrayList<Participant> owers) {
        this.owers = owers;
    }

    /**
     * When this method is called the balance of
     * all people involved in the expense is adjusted as if everyone
     * paid what they owe.
     */
    public void settleBalance() {
        payer.setBalance(payer.getBalance() - amount);
        // payer is no longer owed the money
        for (Participant ower : owers) {
            ower.setBalance((ower.getBalance()) + amount * 1.0 / owers.size());
            // each ower no longers owes his part of the expense
        }
    }

    /**
     * this method does the same as the previous one in reverse.
     * This is needed when editing an expense or deleting it alltogether.
     * when editing an expense, you remove the balances of the old one and add the new one.
     */
    public void reverseSettleBalance() {
        payer.setBalance(payer.getBalance() + amount);
        //increase the balance of the person who paid
        for (Participant ower : owers) {
            ower.setBalance(ower.getBalance() - amount / owers.size());
            //decrease the balance of person who now settles their balance to payee
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
        String payerName = payer != null ? payer.getNickname() : null;
        String owersList = owers != null ? owers.toString() : null;

        return "Expense{" +
                " id=" + id + "\n" +
                "  title=" + title + "\n" +
                "  amount=" + amount + "\n" +
                "  payer=" + payerName + "\n" +
                "  owers=" + owersList + "\n" +
                "]";
    }


}

