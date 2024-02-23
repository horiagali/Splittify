package commons;

import java.util.Objects;

/**
 * BankAccount class to store banking information
 */
public class BankAccount {
    private String owner;
    private String iban;

    public BankAccount(String owner, String iban) {
        this.owner = owner;
        this.iban = iban;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getIban(), that.getIban());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getIban());
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "owner='" + owner + '\'' +
                ", iban='" + iban + '\'' +
                '}';
    }
}
