package commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private String location;
    private Date date;
    @OneToMany(mappedBy = "event")
    private List<Expense> expenses; // List of expenses associated with the event
    @OneToMany(mappedBy = "event")
    private List<Participant> participants; // List of participants in the event'

    @OneToMany(mappedBy = "event")
    private List<Tag> tags;

    /**
     * constructor for the event
     * @param id the id
     * @param title the title as a String
     * @param description the description as a String
     * @param location the location as a String
     * @param date the date as a Date
     */
    public Event(Integer id, String title, String description, String location, Date date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.expenses = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    /**
     * empty constructor
     */
    public Event() {

    }

    // Getters and setters

    /**
     * getter for the id
     * @return an Integer, the id
     */

    public Integer getId() {
        return id;
    }
    /**
     * getter for the title
     * @return a String, the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * a getter for the description
     * @return a String, the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * a getter for the location
     * @return a String, the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * a getter for the date
     * @return a Date, the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * a getter for the expense
     * @return a list of expenses
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * a getter for the participants
     * @return a list of participants
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * a getter for the tags
     * @return a list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * a setter for the id
     * @param id an integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * a setter for the title
     * @param title a String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * a setter for the description
     * @param description a String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * a setter for the location
     * @param location a String
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * a setter for the Date
     * @param date a Date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * a setter for the expenses
     * @param expenses a list of Expenses
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    /**
     * a setter for the participants
     * @param participants a list of Participants
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * a setter for the Tags
     * @param tags a list of Tags
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * the add participant method
     * @param participant a participant
     * @return true if added, false otherwise
     */
    public boolean addParticipant(Participant participant) {
        return participants.add(participant);
    }

    /**
     * the remove participant method
     * @param participant a participant
     * @return true if removed, false otherwise
     */
    public boolean removeParticipant(Participant participant) {
        if(!participants.contains(participant)) return true;
        return participants.remove(participant);
    }

    /**
     * the edit participant method
     * @param oldParticipant the old participant
     * @param newParticipant the new participant
     * @return true if edited, false otherwise
     */
    public boolean editParticipant(Participant oldParticipant, Participant newParticipant) {
        if(!participants.contains(oldParticipant)) return false;
        int indexOfParticipant = participants.indexOf(oldParticipant);
        participants.set(indexOfParticipant, newParticipant);
        return true;
    }

    // Methods to manage expenses
    public boolean addExpense(Expense expense) {
        expense.settleDebts(); //make sure the debts of people involved are recalculated
        return expenses.add(expense);
    }

    public boolean removeExpense(Expense expense) {
        expense.reverseSettleDebts(); //recalculate debts to the state before this expense
        if(!expenses.contains(expense)) return true;
        return expenses.remove(expense);
    }

    public boolean editExpense(Expense oldExpense, Expense newExpense) {
        if(!expenses.contains(oldExpense)) return false;
        oldExpense.reverseSettleDebts(); //recover old debts
        newExpense.settleDebts(); //update debts of involved people
        int indexOfExpense = expenses.indexOf(oldExpense);
        expenses.set(indexOfExpense, newExpense);
        return true;
    }

    /**
     * filtering the list to a participant's expenses
     * @param participant the participant
     * @return the list of expenses the participant paid for
     */
    public List<Expense> myExpenses(Participant participant){
        return expenses.stream().filter(x -> x.getPayee().equals(participant)).toList();

    }

    /**
     * filtering the list to get all the expenses that involve a participant
     * @param participant the participant
     * @return the filtered list of expenses
     */
    public List<Expense> includingExpenses(Participant participant){
        return expenses.stream().filter(x -> (x.getPayors().contains(participant) || x.getPayee().equals(participant))).toList();
    }

    /**
     * the ones that owe someone money
     * @return the list of participants
     */
    public List<Participant> owers(){
        return participants.stream().filter(x -> x.getDebt() < 0).toList();
    }

    /**
     * the settling of debts
     * owe = the array of users that owe money to someone (have positive debt).
     * are_owed = the array of all users that are owed money (have negative debt).
     *So now, we sort both 'owe' and 'are_owed' in increasing order. Then, just iterate from left to right for both arrays.
     */
    public void settleDebts() {
        List<Participant> owe = participants;
        List<Participant> is_owed = participants;


        for (Participant participant : participants){
            if (participant.getDebt() > 0)
                owe.add(participant);
            if (participant.getDebt() < 0)
                is_owed.add(participant);
        }
        owe = owe.stream().sorted((a, b) -> (int) (a.getDebt() - b.getDebt())).toList();
        is_owed = is_owed.stream().sorted((b, a) -> (int) (a.getDebt() - b.getDebt())).toList();
        for (Participant p1 : owe) {
            for (Participant p2 : is_owed) {
                if (p1.getDebt() >= -p2.getDebt()) {
                    int x = (int) Math.min(p1.getDebt(), -p2.getDebt());
                    p1.setDebt(p1.getDebt() - x);
                    p2.setDebt(0);
                    is_owed.remove(p2);
                } else {
                    int x = (int) Math.min(p1.getDebt(), -p2.getDebt());
                    p1.setDebt(p1.getDebt() - x);
                    p2.setDebt(p2.getDebt() + x);
                }
            }

        }
    }
    /**
     * Equals method
     * @param obj object to compare to
     * @return true iff same
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * hash
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * To String method
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
