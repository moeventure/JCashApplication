package org.example.service;

import org.example.model.Transaction;
import org.example.util.DatabaseConnection;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions;
    private DatabaseConnection db;

    public TransactionService(DatabaseConnection db) throws SQLException {
        this.db = db;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void getTransactionsByUserID(int userId) throws SQLException {
        this.transactions = this.db.getTransactionsByUserID(userId);
    }

    public void addTransaction(String type, double amount, String details, Date date, int userId) throws SQLException {
        this.transactions.add(this.db.addTransactionByUserId(type, amount, details, date, userId));
    }

    public boolean addTransactionReceiverLogs(String type, double amount, String details, Date date, int userId) throws SQLException {
        Transaction t = this.db.addTransactionByUserId(type, amount, details, date, userId);
        return t != null;
    }


}
