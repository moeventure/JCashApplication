package org.example.service;

import org.example.model.User;
import org.example.util.DatabaseConnection;

import java.sql.SQLException;

public class TransferService {
    private UserService userService;
    private TransactionService transactionService;
    private DatabaseConnection db;

    public TransferService(UserService userService, TransactionService transactionService, DatabaseConnection db) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.db = db;
    }
    public void updateSenderAndReceiverBalance(String mobileNumberReceiver, Double amount) throws SQLException {
        User receiverUser = this.userService.getUserByMobileNumber(mobileNumberReceiver);
        double receiverBalance = receiverUser.getBalance() + amount;
        int receiverUserId = receiverUser.getUserId();
        double senderBalance = this.userService.getCurrentUserBalance() - amount;
        int senderUserId = this.userService.getCurrentUserId();

        this.db.updateSenderAndTransferBalanceAndTransaction(senderUserId, receiverUserId, senderBalance, receiverBalance, amount, mobileNumberReceiver, this.userService.getCurrentUserMobileNumber());
        this.transactionService.getTransactionsByUserID(senderUserId);
        this.userService.setCurrentUserBalance(senderBalance);
        System.out.println("Successfully sent to " + mobileNumberReceiver + "!");
    }
}
