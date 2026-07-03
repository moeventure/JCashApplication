package org.example.controller;

import org.example.model.Transaction;
import org.example.service.TransactionService;
import org.example.service.TransferService;
import org.example.service.UserService;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UserController {
    TransactionService transactionService;
    UserService userService;
    TransferService transferService;

    public UserController(TransactionService transactionService, UserService userService, TransferService transferService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.transferService = transferService;
    }

    public boolean logInUser() {
        Scanner sc = new Scanner(System.in);
        try{
            int attempts = 3;
            do{
                System.out.print("Enter mobile number (e.g. 09XXXXXXXXX) : ");
                String mobile_number = sc.nextLine().trim();
                if(mobile_number.length() != 11){
                    System.out.println("Mobile number should be 11 digits! " + (attempts-1) + " attempts remaining.");
                    --attempts;
                    continue;
                }
                if(!this.userService.checkUserByMobileNumber(mobile_number)){
                    System.out.println("Mobile number does not exist! " + (attempts-1) + " attempts remaining.");
                    --attempts;
                    continue;
                }
                System.out.print("Enter pin : ");
                String pin = sc.nextLine();
                if (pin.length() != 4){
                    System.out.println("Number of PIN digits should be 4! " + (attempts-1) + " attempts remaining.");
                    --attempts;
                    continue;
                }
                boolean check = this.userService.checkLoginCredentials(mobile_number, pin);
                if(check){
                    this.userService.getCurrentUser(mobile_number);
                    this.transactionService.getTransactionsByUserID(this.userService.getCurrentUserId());
                    System.out.println("\nSuccessfully logged in!");
                    System.out.println("\nHello, " + this.userService.getCurrentUserFullName());
                    return true;
                } else {
                    System.out.println("Invalid PIN! " + (attempts-1) + " attempts remaining!");
                    --attempts;
                }
            }while(attempts != 0);

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerUser() {
        Scanner sc = new Scanner(System.in);
        try{
            String name, mobileNumber, pin;
            while (true) {
                try {
                    System.out.print("Enter Full Name : ");
                    name = sc.nextLine();
                    if(name.isBlank() || name.length() < 4 ) {
                        throw new Error("Please enter a valid Full Name!");
                    }
                    break;
                } catch (Error e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                try {
                    System.out.print("Enter mobile number (e.g. 09XXXXXXXXX) : ");
                    mobileNumber = sc.nextLine().trim();
                    if(mobileNumber.length() != 11){
                        throw new Error("Mobile number should be 11 digits.");
                    }
                    boolean guardDupe = this.userService.checkUserByMobileNumber(mobileNumber);
                    if(guardDupe) {
                        throw new Error("Mobile number already exists!");
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid amount.");
                } catch (Error e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                try {
                    System.out.print("Enter pin : ");
                    pin = sc.nextLine();
                    if (pin.length() != 4){
                        throw new Error("Number of PIN digits should be 4 digits!");
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid amount.");
                } catch (Error e) {
                    System.out.println(e.getMessage());
                }
            }

            boolean check = this.userService.registerUser(name, mobileNumber, pin);

            if(check){
                System.out.println("\nSuccessfully registered user!");

            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void displayBalance() {
        System.out.printf("Current Balance : %.2f\n", this.userService.getCurrentUserBalance());
    }

    public void cashIn() {
        Scanner sc = new Scanner(System.in);
        double amount;
        String answer;
        try{
            while (true) {
                try {
                    System.out.print("Enter amount : ");
                    amount = sc.nextDouble();
                    if(amount <= 0) {
                        throw new Error("Amount should not be less than or equal to 0.");
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid amount.");
                    sc.nextLine();
                } catch (Error e) {
                    System.out.println(e.getMessage());
                    sc.nextLine();
                }
            }
            sc.nextLine();

            while (true) {
                try {
                    System.out.print("Are you sure you want to proceed (Y/N)? : ");
                    answer = sc.nextLine();
                    if(!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")) {
                        throw new InputMismatchException();
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Valid input should be Y/N.");
                }
            }
            if(answer.equalsIgnoreCase("Y")){
                boolean check = userService.updateCurrentUserBalance(amount);
                if(check){
                    transactionService.addTransaction("DEPOSIT", amount, "CASH-IN", Date.valueOf(LocalDate.now()), this.userService.getCurrentUserId());
                    System.out.println("Successfully added Transaction!");
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void transferMoney() {
        Scanner sc = new Scanner(System.in);
        double amount;
        String answer;
        String mobileNumber;
        displayBalance();
        try{
            while (true) {
                try {
                    if(this.userService.getCurrentUserBalance() <= 0){
                        System.out.println("You don't have enough funds to transfer money. Going back to menu...");
                        return;
                    }
                    System.out.print("Enter mobile number to transfer money to : ");
                    mobileNumber = sc.nextLine().trim();
                    if(mobileNumber.length() != 11){
                        throw new Error("Mobile number should be 11 digits.");
                    }
                    boolean guardDupe = this.userService.getCurrentUserMobileNumber().equals(mobileNumber);
                    if(guardDupe) {
                        throw new Error("You cannot send money to your own mobile number.");
                    }
                    boolean guard = this.userService.checkUserByMobileNumber(mobileNumber);
                    if(!guard){
                        throw new NullPointerException();
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid amount.");
                }
                catch (NullPointerException e) {
                    System.out.println("Mobile number does not exist.");
                } catch (Error e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                try {
                    System.out.print("Enter amount : ");
                    amount = sc.nextDouble();
                    if (amount <= 0) {
                        throw new Error("Amount should not be less than or equal to 0.");
                    }
                    boolean validateUserBalance = userService.validateNewBalance(-amount);
                    if(!validateUserBalance){
                        throw new Error("Insufficient balance to send money.");
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid amount.");
                    sc.nextLine();
                } catch (Error e) {
                    System.out.println(e.getMessage());
                    sc.nextLine();
                }
            }
            sc.nextLine();
            while (true) {
                try {
                    System.out.print("Are you sure you want to proceed (Y/N)? : ");
                    answer = sc.nextLine();
                    if(!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")) {
                        throw new InputMismatchException();
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Valid input should be Y/N.");
                }
            }
            if(answer.equalsIgnoreCase("Y")){
                this.transferService.updateSenderAndReceiverBalance(mobileNumber, amount);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void displayTransactionLogs() {
        try{
            List<Transaction> transactions = this.transactionService.getTransactions();
            System.out.println("Transaction Log List");
            System.out.println("Transaction Type | Amount | Details | Date");
            for(Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

}
