package commons;

import java.util.Objects;

/**
 * BankAccount class to store banking information
 */
public class BankAccount {
    private String owner;
    private String iban;
    private String bic;

    public BankAccount(String owner, String iban, String bic) {
        this.owner = owner;
        this.iban = iban;
        this.bic = bic;
    }
    // Empty constructor
    public BankAccount(){}

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

    public String getBic() {return bic;}

    public void setBic(String bic) {this.bic = bic;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(getOwner(), that.getOwner()) && Objects.equals(getIban(), that.getIban()) && Objects.equals(getBic(), that.getBic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getIban(), getBic());
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "owner='" + owner + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                '}';
    }
}
