package commons;

import java.util.Objects;

/**
 * BankAccount class to store banking information
 */
public class BankAccount {
    private String owner;
    private String iban;
    private String bic;


    /**
     * Constructs a new BankAccount object with the specified owner, IBAN, and BIC.
     * @param owner The owner or holder of the bank account.
     * @param iban The International Bank Account Number (IBAN) associated with the bank account.
     * @param bic The Bank Identifier Code (BIC) associated with the bank account.
     */
    public BankAccount(String owner, String iban, String bic) {
        this.owner = owner;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * Empty constructor
     */
    public BankAccount(){}

    /**
     * Gets the owner of the bank account.
     * @return The owner of the bank account.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the bank account.
     * @param owner The new owner of the bank account.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }


    /**
     * Gets the IBAN associated with the bank account.
     * @return The IBAN associated with the bank account.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the IBAN associated with the bank account.
     * @param iban The new IBAN associated with the bank account.
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Gets the BIC associated with the bank account.
     * @return The BIC associated with the bank account.
     */
    public String getBic() {return bic;}

    /**
     * Sets the BIC associated with the bank account.
     * @param bic The new BIC associated with the bank account.
     */
    public void setBic(String bic) {this.bic = bic;}

    /**
     * Checks if this bank account is equal to another object.
     * @param o The object to compare with this bank account.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(getOwner(), that.getOwner())
                && Objects.equals(getIban(), that.getIban())
                && Objects.equals(getBic(), that.getBic());
    }

    /**
     * Computes the hash code of this bank account.
     * @return The hash code of this bank account.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getIban(), getBic());
    }

    /**
     * Returns a string representation of this bank account.
     * @return A string representation of this bank account.
     */
    @Override
    public String toString() {
        return "BankAccount{" +
                "owner='" + owner + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                '}';
    }
}
