package org.example.main;

import org.example.controller.UserController;
import org.example.service.TransactionService;
import org.example.service.TransferService;
import org.example.service.UserService;
import org.example.util.AuthUtil;
import org.example.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection db = new DatabaseConnection();
        startApp(db);
    }

    public static void startApp(DatabaseConnection db) throws SQLException {
        Scanner sc = new Scanner(System.in);

        while (true) {
            AuthUtil auth = new AuthUtil();
            UserService userService = new UserService(db, auth);
            TransactionService transactionService = new TransactionService(db);
            TransferService transferService = new TransferService(userService,transactionService,db);
            UserController uc = new UserController(transactionService, userService, transferService);

            boolean userStatus = setUpAccount(uc, sc);
            if(!userStatus){
                break;
            }

            boolean loggedIn = displayMenu(uc, sc);

            if (!loggedIn) {
                break;
            }
        }
        sc.close();
    }

    public static boolean setUpAccount(UserController uc, Scanner sc) {
        int choice;
        try{
            do{
                System.out.println("===LOGIN PORTAL===");
                System.out.println("1. Log-in");
                System.out.println("2. Register");
                System.out.println("Any number to Close Program");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        return uc.logInUser();
                    case 2:
                        uc.registerUser();
                        break;
                    default:
                        System.out.println("Thank you for using JCash Application");
                        return false;
                }
            }while(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean displayMenu(UserController uc, Scanner sc) {
        int choice;
        System.out.println("Welcome to the J-Cash Application");
        try{
            do{
                System.out.println("=============================");
                System.out.println("========J-Cash Portal========");
                System.out.println("1. Display Balance");
                System.out.println("2. Cash-in");
                System.out.println("3. Transfer Money");
                System.out.println("4. Display Transaction Logs");
                System.out.println("5. Logout");
                System.out.println("Any number to Close Program");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        uc.displayBalance();
                        break;
                    case 2:
                        uc.cashIn();
                        break;
                    case 3:
                        uc.transferMoney();
                        break;
                    case 4:
                        uc.displayTransactionLogs();
                        break;
                    case 5:
                        System.out.println();
                        return true;
                    default:
                        System.out.println("Thank you for using JCash Application");
                        return false;
                }
            }while(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}