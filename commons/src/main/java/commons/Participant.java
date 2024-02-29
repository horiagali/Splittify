package commons;


import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Entity
@Table(indexes = {
        @Index(columnList = "Nickname"),
        @Index(columnList = "email")
})
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long participantID;

    private String nickname; // Changed name to nickname
    private String email;
    private double balance;

    @ManyToOne
    private Event event;
    private String BIC;
    private String IBAN;

    /**
     * Creates a participant
     *
     * @param nickname nickname of participant
     * @param email    email of participant
     * @param BIC      BIC of participant
     * @param IBAN     IBAN of participant
     * @param balance  initial balance of participant
     */
    public Participant(String nickname, String email, String BIC, String IBAN, double balance) {
        this.nickname = nickname;
        this.email = email;
        this.balance = 0;
        this.BIC = BIC;
        this.IBAN = IBAN;
        this.balance = balance;
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
    public String getBIC() {
        return BIC;
    }

    /**
     * Setter
     * @param BIC BIC
     */
    public void setBIC(String BIC) {
        this.BIC = BIC;
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
     * @return IBAN
     */
    public String getIBAN() {
        return IBAN;
    }

    /**
     * Setter
     * @param IBAN
     */
    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
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
                .append("BIC", BIC)
                .append("IBAN", IBAN)
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
        return Double.compare(balance, that.balance) == 0 && Objects.equals(participantID, that.participantID) && Objects.equals(nickname, that.nickname) && Objects.equals(email, that.email) && Objects.equals(BIC, that.BIC) && Objects.equals(IBAN, that.IBAN);
    }

    /**
     * Hash code method
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(participantID, nickname, email, BIC, IBAN, balance);
    }




    
}
