package commons;

import java.util.Objects;

public class User {
    private Integer id;
    private String name;
    private String email;
    private BankAccount bankAccount;

    /**
     * Constructs a new User object with the specified attributes.
     * @param id The ID of the user.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param bankAccount The bank account associated with the user.
     */
    public User(Integer id, String name, String email, BankAccount bankAccount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bankAccount = bankAccount;
    }


    /**
     * Retrieves the ID of the user.
     * @return The ID of the user.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retrieves the name of the user.
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the email address of the user.
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the bank account associated with the user.
     * @return The bank account associated with the user.
     */
    public BankAccount getBank() {return bankAccount; }


    /**
     * Sets the ID of the user.
     * @param id The ID of the user.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the name of the user.
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the email address of the user.
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the bank account associated with the user.
     * @param bankAccount The bank account associated with the user.
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param o The reference object with which to compare.
     * @return True if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId())
                && Objects.equals(getName(), user.getName())
                && Objects.equals(getEmail(), user.getEmail())
                && Objects.equals(bankAccount, user.bankAccount);
    }

    /**
     * Returns a hash code value for the object.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), bankAccount);
    }

    /**
     * Returns a string representation of the object.
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bankAccount=" + bankAccount +
                '}';
    }
}
