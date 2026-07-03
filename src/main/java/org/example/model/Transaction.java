package org.example.model;

import java.util.Date;

public class Transaction {
    private String transactionType;
    private double amount;
    private String details;
    private Date date;
    private int userId;

    public Transaction(String transactionType, double amount, String details, Date date, int userId) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.details = details;
        this.date = date;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return
                transactionType +
                " | " + amount +
                " | " + details +
                " | " + date ;
    }
}
