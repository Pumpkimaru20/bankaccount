package org.example.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import org.example.model.*;
import org.example.util.Encrypt;

public class BankService {
    private Connection cn;

    public BankService() {

        try {
            String url = "jdbc:mysql://localhost:3306/bankaccount";
            String username = "root";
            String password = "";

            cn = DriverManager.getConnection(url, username, password);
            System.out.println("Database Connected!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //REGISTRATION
    public boolean register(String mobile_number, String pin, String full_name) {

        if (!mobile_number.matches("\\d{11}")) {
            System.out.println("Mobile number must be exactly 11 digits.");
            return false;
        }

        if (!pin.matches("\\d{4}")) {
            System.out.println("PIN must be exactly 4 digits.");
            return false;
        }

        try {

            // Check if mobile number already exists
            String check = "SELECT * FROM users WHERE mobile_number=?";
            PreparedStatement ps = cn.prepareStatement(check);
            ps.setString(1, mobile_number);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Mobile Number already registered.");
                return false;
            }

            // Encrypt the PIN
            String hashedPin = Encrypt.hashPin(pin);

            // Generate Account Number
            String accountNumber = "ACC" + System.currentTimeMillis();

            String sql = "INSERT INTO users(full_name, mobile_number, pin) VALUES(?,?,?)";

            ps = cn.prepareStatement(sql);

            ps.setString(1, full_name);
            ps.setString(2, mobile_number);
            ps.setString(3, hashedPin);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Registration Successfully!");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // LOGIN
    public User login(String mobile_number, String pin) {

        try {

            String sql = "SELECT * FROM users WHERE mobile_number=?";

            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, mobile_number);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String hashedPin = rs.getString("pin");

                if (Encrypt.verifyPin(pin, hashedPin)) {

                    User user = new User(
                            rs.getString("mobile_number"),
                            hashedPin,
                            rs.getString("full_name"),
                            rs.getDouble("balance")
                    );
                    user.setAccountNumber(rs.getString("account_number"));

                    return user;

                } else {

                    System.out.println("Incorrect PIN.");
                }

            } else {

                System.out.println("Mobile number not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // BALANCE
    public void showBalance(User User) {
        try {
            String sql = "SELECT balance FROM users WHERE mobile_number=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, User.getMobileNumber());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                User.setBalance(balance);
                System.out.println("\nCurrent Balance: ₱" + balance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DEPOSIT
    public void cashIn(User user, double amount) {

        if (amount <= 0) {
            System.out.println("Invalid Amount.");
            return;
        }

        try {
            String sql = "UPDATE users SET balance = balance + ? WHERE mobile_number=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDouble(1, amount);
            ps.setString(2, user.getMobileNumber());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                user.setBalance(user.getBalance() + amount);
                saveTransaction(user, "Deposit", amount, "Cash In");
                System.out.println("Deposit Successfully!");
                System.out.println("Current Balance: ₱" + user.getBalance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTransaction(User user, String type, double amount, String details) {

        try {
            String sql = "INSERT INTO transaction(mobile_number,transaction_type,amount,details) VALUES(?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user.getMobileNumber());
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, details);
            ps.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Check if the mobile number exists
    public User getData(String mobile) {
        User user = new User();

        try {
            String sql = "SELECT * FROM users WHERE mobile_number = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, mobile);

            ResultSet rs = ps.executeQuery();
            if(!rs.isBeforeFirst()) {
                System.out.println("No Data");
                return null;
            } else {
                while (rs.next()) {
                    user.setMobileNumber(rs.getString("mobile_number"));
                    user.setPin(rs.getString("pin"));
                    user.setBalance(rs.getDouble("balance"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Check if the PIN matches the given mobile number
    public boolean pinMatches(String mobile, String pin) {

        try {
            String sql = "SELECT * FROM users WHERE mobile_number = ? AND pin = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, mobile);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // TRANSFER
    public boolean transfer(User sender, String receiverMobile, double amount) {
        try {
            User receiver = getData(receiverMobile);
            if (receiver == null) {
                System.out.println("Receiver not found.");
                return false;
            }

            if (sender.getMobileNumber().equals(receiverMobile)) {
                System.out.println("Cannot transfer to yourself.");
                return false;
            }

            if (amount <= 0) {
                System.out.println("Invalid amount.");
                return false;
            }

            if (sender.getBalance() < amount) {
                System.out.println("Insufficient Balance.");
                return false;
            }

            cn.setAutoCommit(false);

            // Deduct sender
            String deduct = "UPDATE users SET balance = balance - ? WHERE mobile_number=?";
            PreparedStatement ps1 = cn.prepareStatement(deduct);

            ps1.setDouble(1, amount);
            ps1.setString(2, sender.getMobileNumber());
            ps1.executeUpdate();

            // Add receiver
            String add = "UPDATE users SET balance = balance + ? WHERE mobile_number=?";
            PreparedStatement ps2 = cn.prepareStatement(add);

            ps2.setDouble(1, amount);
            ps2.setString(2, receiverMobile);
            ps2.executeUpdate();
            cn.commit();
            saveTransaction(sender, "Transfer", amount, "Transferred to " + receiver.getFullName());
            saveTransaction(receiver, "Received", amount, "Received from " + sender.getFullName());
            sender.setBalance(sender.getBalance() - amount);
            System.out.println("Transfer Successfully!");
            return true;

        } catch (Exception e) {
            try {
                cn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }

    // TRANSACTION HISTORY
    public void showTransactions(User user) {

        try {
            String sql = "SELECT * FROM transaction WHERE mobile_number=? ORDER BY transaction_date DESC";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user.getMobileNumber());
            ResultSet rs = ps.executeQuery();

            System.out.println("\n====================================================");
            System.out.println("              TRANSACTION HISTORY");
            System.out.println("====================================================");

            if (!rs.isBeforeFirst()) {
                System.out.println("No transaction history.");
                return;
            }

            while (rs.next()) {
                System.out.println("====================================================");
                System.out.println("Mobile Number : " + rs.getString("mobile_number"));
                System.out.println("Type           : " + rs.getString("transaction_type"));
                System.out.println("Amount         : ₱" + rs.getDouble("amount"));
                System.out.println("Details        : " + rs.getString("details"));
                System.out.println("Date & Time    : " + rs.getTimestamp("transaction_date"));
                System.out.println("====================================================");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // LOG OUT
    public void logout(User currentUser) {
        System.out.println("================================");
        System.out.println("Logged Out Successfully!");
        System.out.println("Thank you for using C_CASH ACCOUNTS!");
        System.out.println("See you!, " + currentUser.getFullName());
        System.out.println("================================");
    }
}