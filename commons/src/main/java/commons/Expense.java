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
    private Tag tag;


    // Constructors
    public Expense() {
        // Default constructor for JPA
    }
    // creates an expense and modifies everyone's balance accordingly


    public Expense(String title, double amount, Participant payer, ArrayList<Participant> owers, Tag tag) {
        this.title = title;
        this.owers = owers;
        this.payer = payer;
        this.tag = tag;

        if (amount < 0) {
            throw new IllegalArgumentException("Expense can not be negative.");
        }
        this.amount = amount;

        payer.setBalance(payer.getBalance() + amount); //increase the balance of the person who paid
        for (Participant ower : owers) {
            ower.setBalance(ower.getBalance() - amount / owers.size()); //decrease the balance of person who now settles their balance to payee
        }

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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Participant getPayer() {
        return payer;   
    }

    public void setPayer(Participant payer) {
        this.payer = payer;
    }

    public List<Participant> getOwers() {
        return owers;
    }

    public void setOwers(ArrayList<Participant> owers) {
        this.owers = owers;
    }

    /*

   When this method is called the balance of all people involved in the expense is adjusted as if everyone
   paid what they owe.

     */


    public void settleBalance() {
        payer.setBalance(payer.getBalance() - amount);  // payer is no longer owed the money
        for (Participant ower : owers) {
            ower.setBalance((ower.getBalance()) + amount * 1.0 / owers.size());  // each ower no longers owes his part of the expense
        }
    }


    //this method does the same as the previous one in reverse. This is needed when editing an expense or deleting it alltogether.
    //when editing an expense, you remove the balances of the old one and add the new one.
    public void reverseSettleBalance() {
        payer.setBalance(payer.getBalance() + amount); //increase the balance of the person who paid
        for (Participant ower : owers) {
            ower.setBalance(ower.getBalance() - amount / owers.size()); //decrease the balance of person who now settles their balance to payee
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

