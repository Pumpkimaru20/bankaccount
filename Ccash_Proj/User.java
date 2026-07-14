package org.example.model;
import java.util.ArrayList;
public class User {

    private String mobileNumber;
    private String pin;
    private String fullName;
    private double balance;
    private String accountNumber;
    private ArrayList<transaction> transactions;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public User() {
        transactions = new ArrayList<>();
    }

    public User(String mobileNumber, String pin, String fullName, double balance) {
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.fullName = fullName;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(transaction transaction) {
        transactions.add(transaction);
    }
}
