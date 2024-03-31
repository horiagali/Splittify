package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant payer;
    @ManyToMany
    private List<Participant> owers; //the people who owe money

    @ManyToOne
    @JsonIgnore
    private Event event;

    private double amount;
    @ManyToOne
    private Tag tag;

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
     * @param tag tag for expense
     * @throws IllegalArgumentException if the amount is negative.
     *
     */
    public Expense(String title, double amount, 
    Participant payer, List<Participant> owers, Tag tag) {
        this.title = title;
        this.owers = owers;
        this.payer = payer;
        this.tag = tag;

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
    public Long getId() {
        return id;
    }

    /**
     * returns event of expense
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * sets the event of expense
     * @param event to set expense to
     */
    public void setEvent(Event event) {
        this.event = event;
    }


    /**
     * Sets the ID of the expense.
     * @param id The ID to set.
     */
    public void setId(Long id) {
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
     * retrieve tag of this expense.
     * @return Tag tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * change the tag of this expense.
     * @param tag
     */
    public void setTag(Tag tag) {
        this.tag = tag;
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
    public void setOwers(List<Participant> owers) {
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
            ower.setBalance((ower.getBalance()) + amount / owers.size());
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

    /**
     * equals
     * @param o object to compare to
     * @return true iff same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(amount, expense.amount) == 0
                && Objects.equals(id, expense.id)
                && Objects.equals(title, expense.title)
                && Objects.equals(payer, expense.payer)
                && Objects.equals(owers, expense.owers)
                && Objects.equals(event, expense.event)
                && Objects.equals(tag, expense.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, amount);
    }

    @Override
    public String toString() {
        String owersList = owers != null ? owers.toString() : null;

        return "Expense{" +
                " id=" + id + "\n" +
                "  title=" + title + "\n" +
                "  amount=" + amount + "\n" +
                "  payer=" + payer + "\n" +
                "  owers=" + owersList + "\n" +
                "  tag=" + tag + "\n" +
                "]";
    }


}

