package org.example.service;

import org.example.model.User;
import org.example.util.AuthUtil;
import org.example.util.DatabaseConnection;

import java.sql.SQLException;

public class UserService {
    private User user;
    private final DatabaseConnection db;
    private AuthUtil authUtil;

    public UserService(DatabaseConnection db, AuthUtil authUtil) throws SQLException {
        this.db = db;
        this.authUtil = authUtil;
    }

    public Double getCurrentUserBalance(){
        return this.user.getBalance();
    }

    public int getCurrentUserId(){
        return this.user.getUserId();
    }

    public String getCurrentUserMobileNumber(){
        return this.user.getMobileNumber();
    }

    public String getCurrentUserFullName(){
        return this.user.getFullName();
    }

    public void getCurrentUser(String mobileNumber) throws SQLException {
        this.user = this.db.getUserByMobileNumber(mobileNumber);
    }

    public User getUserByMobileNumber(String mobileNumber) throws SQLException {
        return this.db.getUserByMobileNumber(mobileNumber);
    }

    public void setCurrentUserBalance(double balance){
        this.user.setBalance(balance);
    }

    public boolean checkLoginCredentials(String mobileNumber, String pin) throws SQLException {
        User account = this.db.getUserByMobileNumber(mobileNumber);
        if (account == null) {
            return false;
        }

        if (this.authUtil.authenticatePIN(pin, account.getPIN())) {
            return true;
        }

        return false;
    }

    public boolean updateCurrentUserBalance(Double amount) throws SQLException {
        double finalAmount = this.user.getBalance() + amount;
        boolean check = db.updateBalance(this.getCurrentUserId(), finalAmount);
        if(check) {
            this.user.setBalance(finalAmount);
            return true;
        }
        return false;
    }

    public boolean validateNewBalance(double amount){
        double finalAmount = this.user.getBalance() + amount;
        return !(finalAmount < 0);
    }

    public boolean checkUserByMobileNumber(String mobileNumber) throws SQLException {
        User account = this.db.getUserByMobileNumber(mobileNumber);
        return account != null;
    }

    public boolean registerUser(String name, String mobileNumber, String pin) throws SQLException {
        String hashPin = this.authUtil.encryptPin(pin);
        return this.db.registerUser(name, mobileNumber, hashPin);
    }

    public int getUserIdByMobileNumber(String mobileNumber) throws SQLException {
        return this.db.getUserByMobileNumber(mobileNumber).getUserId();
    }

}
