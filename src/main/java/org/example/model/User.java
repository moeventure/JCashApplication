package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String mobileNumber;
    private String PIN;
    private String fullName;
    private double balance;
    private List<Double> transactionList;

    public User(int userId, String mobileNumber, String PIN, String fullName, double balance) {
        this.userId = userId;
        this.mobileNumber = mobileNumber;
        this.PIN = PIN;
        this.fullName = fullName;
        this.balance = balance;
        this.transactionList = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public double getBalance() {
        return balance;
    }

    public String getFullName(){
        return fullName;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "mobileNumber=" + mobileNumber +
                ", PIN=" + PIN +
                ", fullName='" + fullName + '\'' +
                ", balance=" + balance +
                ", transactionList=" + transactionList +
                '}';
    }
}
