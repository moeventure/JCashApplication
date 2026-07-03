package org.example.util;

import org.example.model.Transaction;
import org.example.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/final_mnnoor";
    private String user = "root";

    public DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(url, user, null);
    }

    public boolean registerUser(String name, String mobileNumber, String pin) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO `user` (`full_name`, `mobile_number`, `pin`, `balance`) VALUES (?, ?, ?, ?)");
        ps.setString(1, name);
        ps.setString(2, mobileNumber);
        ps.setString(3, pin);
        ps.setDouble(4, 0.0);

        int rs = ps.executeUpdate();
        if (rs > 0) {
            return true;
        }
        return false;
    }

    public User getUserByMobileNumber(String mobileNumber) throws SQLException {
        User user = null;
        PreparedStatement ps = this.connection.prepareStatement("Select * from user Where `mobile_number` = ?");
        ps.setString(1, mobileNumber);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            user = new User(rs.getInt("user_id"), rs.getString("mobile_number"), rs.getString("pin"), rs.getString("full_name"), rs.getDouble("balance"));
        }
        return user;
    }

    public String getPinByMobileNumber(String mobileNumber) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("Select pin from user Where `mobile_number` = ?");
        ps.setString(1, mobileNumber);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("pin");
        }
        return null;
    }

    public List<Transaction> getTransactionsByUserID(int userId) throws SQLException {
        List<Transaction> trans = new ArrayList<Transaction>();
        PreparedStatement ps = this.connection.prepareStatement("Select * from transaction WHERE `user_id` = ?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            trans.add(new Transaction(rs.getString("transaction_type"), rs.getDouble("amount"), rs.getString("details"), rs.getDate("date"), rs.getInt("user_id")));
        }
        return trans;
    }

    public Transaction addTransactionByUserId(String type, double amount, String details, Date date, int userId) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO transaction (`transaction_type`, `amount`, `details`, `date`, `user_id`) VALUES (?,?,?,?,?)");
        ps.setString(1, type);
        ps.setDouble(2, amount);
        ps.setString(3, details);
        ps.setDate(4, date);
        ps.setInt(5, userId);

        int rs = ps.executeUpdate();
        if (rs > 0) {
            return new Transaction(type, amount, details, date, userId);
        }
        return null;
    }

    public boolean updateBalance(int userId, double balance) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("UPDATE USER SET `balance` = ? WHERE `user_id` = ?");
        ps.setDouble(1, balance);
        ps.setInt(2, userId);

        int rs = ps.executeUpdate();
        if (rs > 0) {
            return true;
        }
        return false;
    }

    public void updateSenderAndTransferBalanceAndTransaction(int senderUserId, int receiverUserId, double senderBalance, double receiverBalance, double amount, String mobileNumberReceiver, String mobileNumberSender) throws SQLException {
        try{
            connection.setAutoCommit(false);
            PreparedStatement psSender = this.connection.prepareStatement("UPDATE USER SET `balance` = ? WHERE `user_id` = ?");
            psSender.setDouble(1, senderBalance);
            psSender.setInt(2, senderUserId);
            int rsSender = psSender.executeUpdate();
            if (rsSender < 0) {
                throw new Exception("Failed to send money. Please try again later.");
            }

            PreparedStatement psSenderTrans = this.connection.prepareStatement("INSERT INTO transaction (`transaction_type`, `amount`, `details`, `date`, `user_id`) VALUES (?,?,?,?,?)");
            psSenderTrans.setString(1, "TRANSFER_OUT");
            psSenderTrans.setDouble(2, -amount);
            psSenderTrans.setString(3, "SENT TO " + mobileNumberReceiver);
            psSenderTrans.setDate(4, Date.valueOf(LocalDate.now()));
            psSenderTrans.setInt(5, senderUserId);

            int rsSenderTrans = psSenderTrans.executeUpdate();
            if (rsSenderTrans < 0) {
                throw new Exception("Failed to send money. Please try again later.");
            }

            PreparedStatement psReceiver = this.connection.prepareStatement("UPDATE USER SET `balance` = ? WHERE `user_id` = ?");
            psReceiver.setDouble(1, receiverBalance);
            psReceiver.setInt(2, receiverUserId);
            int rsReceiver = psReceiver.executeUpdate();
            if (rsReceiver < 0) {
                throw new Exception("Failed to send money. Please try again later.");
            }

            PreparedStatement psReceiverTrans = this.connection.prepareStatement("INSERT INTO transaction (`transaction_type`, `amount`, `details`, `date`, `user_id`) VALUES (?,?,?,?,?)");
            psReceiverTrans.setString(1, "TRANSFER_IN");
            psReceiverTrans.setDouble(2, amount);
            psReceiverTrans.setString(3, "RECEIVED FROM " + mobileNumberSender);
            psReceiverTrans.setDate(4, Date.valueOf(LocalDate.now()));
            psReceiverTrans.setInt(5, receiverUserId);
            int rsReceiverTrans = psReceiverTrans.executeUpdate();
            if (rsReceiverTrans < 0) {
                throw new Exception("Failed to send money. Please try again later.");
            }

            connection.commit();
        }catch(Exception e){
            connection.rollback();
            System.out.println(e.getMessage());
            e.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }
    }
}
