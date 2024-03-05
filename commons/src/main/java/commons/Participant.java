package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long participantID;
    private String nickname; // Changed name to nickname
    private String email;
    private double balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Event event;
    private String bic;
    private String iban;


    /**
     * Creates a participant
     *
     * @param nickname nickname of participant
     * @param email    email of participant
     * @param bic      BIC of participant
     * @param iban     IBAN of participant
     * @param balance  initial balance of participant
     */
    public Participant(String nickname, String email, String bic, String iban, double balance) {
        this.nickname = nickname;
        this.email = email;
        this.bic = bic;
        this.iban = iban;
        this.balance = balance;
    }

    /**
     *
     * @param participantID
     * @param nickname
     * @param email
     * @param balance
     * @param event
     * @param bic
     * @param iban
     */
    public Participant(Long participantID, String nickname, String email,
                       double balance, Event event, String bic, String iban) {
        this.participantID = participantID;
        this.nickname = nickname;
        this.email = email;
        this.balance = balance;
        this.event = event;
        this.bic = bic;
        this.iban = iban;
    }

    /**
     * Default constructor required by JPA
     */
    public Participant() {
        // Default constructor required by JPA
    }

    // Getters and setters

    /**
     * Getter
     * @return participantID
     */
    public Long getParticipantID() {
        return participantID;
    }

    /**
     * Setter
     * @param participantID participantID
     */
    public void setParticipantID(Long participantID) {
        this.participantID = participantID;
    }

    /**
     * Getter
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter
     * @param nickname nickname of participant
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter
     * @return BIC
     */
    public String getBic() {
        return bic;
    }

    /**
     * Setter
     * @param bic BIC
     */
    public void setBic(String bic) {
        this.bic = bic;
    }


    /**
     * Getter
     * @return Balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Setter
     * @param balance balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Getter
     * @return iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * Setter
     * @param iban iban number
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     *
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /**
     *
     * @param event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * toString method
     *
     * @return String in a human friendly format
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("participantID", participantID)
                .append("nickname", nickname)
                .append("email", email)
                .append("BIC", bic)
                .append("IBAN", iban)
                .append("balance", balance)
                .toString();
    }


    /**
     * Equals method
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Double.compare(balance, that.balance) == 0
                && Objects.equals(participantID, that.participantID)
                && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) 
                && Objects.equals(bic, that.bic) && Objects.equals(iban, that.iban);
    }

    /**
     * Hash code method
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(participantID, nickname, email, bic, iban, balance);
    }
}
