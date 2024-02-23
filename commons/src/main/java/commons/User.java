package commons;

import java.util.Objects;

public class User {
    private Integer id;
    private String name;
    private String email;
    private BankAccount bankAccount;

    public User(Integer id, String name, String email, BankAccount bankAccount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bankAccount = bankAccount;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BankAccount getBank() {return bankAccount; }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getName(), user.getName()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(bankAccount, user.bankAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getEmail(), bankAccount);
    }

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
