package org.example.mainclass;

import java.util.Scanner;
import org.example.model.User;
import org.example.services.BankService;
import org.example.util.*;

public class MainClass {
    private static BankService bank = new BankService();
    private static Scanner sc = new Scanner(System.in);
    private static User user;
    private static String mobileNumber;
    private static String pin;
    private static String fullName;

    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== C_CASH ACCOUNTS! =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("============================");
            System.out.print("Choice: ");

            int option;

            // Loops for attempts when the user input their account

            while (true) {
                if (sc.hasNextInt()) {
                    option = sc.nextInt();
                    if (option < 4) {
                        break;
                    } else {
                        sc.nextLine();
                        System.out.println("=======================================");
                        System.out.println("Invalid choice! Please try again.");
                        System.out.println("Invalid input! Please enter a number.");
                        System.out.println("=======================================");
                    }
                } else {
                    sc.nextLine();
                }
            }

            switch (option) { // loops that show database

                case 1: // Registration account
                    register();
                    break;

                case 2: // Login accounts
                    login();
                    break;

                case 3: // Once the user choose number 3 to exit this loop of a program explain the scenario
                    exit();
                    break;

                default:
                    System.out.println("Invalid Choice!");
                    break;
            }
        }
    }

    // Registration for user account
    public static void register () {
        Scanner sc = new Scanner(System.in);
        System.out.print("Full Name: ");

        String name = sc.nextLine();
        System.out.print("Mobile Number: ");

        String mobile_number = sc.next();
        System.out.print("PIN: ");

        String pin = sc.next();
        bank.register(mobile_number, pin, name);
    }

    // Login user account
    public static void login() {

        int tries = 0;
        final int MAX_ATTEMPTS = 3;

        System.out.print("Mobile Number: ");
        String mobile = sc.next();

        while (tries < MAX_ATTEMPTS) {
            System.out.print("PIN: ");
            String pin = sc.next();

            user = bank.login(mobile, pin);

            if (user != null) {

                System.out.println("==============================");
                System.out.println("Login Successful!");
                System.out.println("Welcome " + user.getFullName());
                System.out.println("==============================");

                transaction();
                return;
            }

            tries++;

            if (tries < MAX_ATTEMPTS) {
                System.out.println("==============================");
                System.out.println("Invalid PIN!");
                System.out.println("Remaining Attempts: " + (MAX_ATTEMPTS - tries));
                System.out.println("==============================");
            } else {
                System.out.println("Maximum login attempts reached.");
            }
        }
    }

    // Output of the user choices
    public static void transaction () {
        int choice;

        do {

            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1.Balance");
            System.out.println("2.Deposit");
            System.out.println("3.Transfer");
            System.out.println("4.Transaction History");
            System.out.println("5.Log Out");
            System.out.println("======================");
            System.out.print("Choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println(user.getBalance());
                    break;

                case 2:
                    System.out.print("Amount: ");
                    double cash = InputValidator.getDouble(sc);
                    bank.cashIn(user, cash);
                    break;

                case 3:
                    System.out.print("Receiver Mobile: ");
                    String receiver = sc.next();

                    System.out.print("Amount: ");
                    double amount = InputValidator.getDouble(sc);
                    boolean success = bank.transfer(user, receiver, amount);

                    if (success) { // Users prefer to continue or not
                        while (true) {

                            System.out.println("\n=========================================");
                            System.out.print("Return to C_CASH ACCOUNTS? (Yes/No): ");
                            System.out.println("\n=========================================");
                            String answer = sc.next();

                            if (answer.equalsIgnoreCase("Yes")) {

                                bank.logout(user);

                                // Clear the logged-in user
                                user = null;

                                // Exit the transaction menu and return to the main menu
                                return;

                            } else if (answer.equalsIgnoreCase("No")) {

                                System.out.println("\n=========================================");
                                System.out.println("You may continue using your account.");
                                System.out.println("=========================================");
                                break;

                            } else {

                                System.out.println("=========================================");
                                System.out.println("Invalid input! Please enter Yes or No.");
                                System.out.println("=========================================");
                            }
                        }
                    }
                    break;
                case 4:
                    bank.showTransactions(user);
                    break;

                case 5:
                    bank.logout(user);
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

        } while (choice != 5);
    }
    
    public static void exit() {
        System.out.println("\n=========================================");
        System.out.print("Are you sure you want to exit? (Yes/No): ");
        char exitChoice = sc.next().toUpperCase().charAt(0);

        if (exitChoice == 'Y') {
            System.out.println("Thank you for using C_CASH ACCOUNTS!");
            System.out.println("=========================================");
            sc.close();
            System.exit(0);
        } else {
            System.out.println("Returning to Main Menu...");
        }
    } 
}