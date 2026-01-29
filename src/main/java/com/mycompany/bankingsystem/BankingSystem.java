package com.mycompany.bankingsystem;

import java.sql.*;
import java.util.Scanner;

public class BankingSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/bank_system";
    private static final String USER = "root"; // your MySQL username
    private static final String PASSWORD = "root"; // your MySQL password

    private Connection conn = null;

    // Corrected constructor
    public BankingSystem() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection failed. Please check your credentials and database.");
        }
    }

    // Create new account
    public void createAccount(String name, double initialBalance) {
        if (conn == null) {
            System.out.println("Cannot create account: No database connection.");
            return;
        }
        String sql = "INSERT INTO accounts (name, balance) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, initialBalance);
            pstmt.executeUpdate();
            System.out.println("Account created successfully!");
        } catch (SQLException e) {
        }
    }

    // Deposit money
    public void deposit(int accountId, double amount) {
        if (conn == null) {
            System.out.println("Cannot deposit: No database connection.");
            return;
        }
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Deposit successful.");
            else System.out.println("Account not found.");
        } catch (SQLException e) {
        }
    }

    // Withdraw money
    public void withdraw(int accountId, double amount) {
        if (conn == null) {
            System.out.println("Cannot withdraw: No database connection.");
            return;
        }
        try {
            conn.setAutoCommit(false); // start transaction

            String checkSql = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    String withdrawSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                    PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
                    withdrawStmt.setDouble(1, amount);
                    withdrawStmt.setInt(2, accountId);
                    withdrawStmt.executeUpdate();
                    conn.commit();
                    System.out.println("Withdrawal successful.");
                } else {
                    System.out.println("Insufficient balance.");
                    conn.rollback();
                }
            } else {
                System.out.println("Account not found.");
            }

            conn.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
            }
        }
    }

    // Check balance
    public void checkBalance(int accountId) {
        if (conn == null) {
            System.out.println("Cannot check balance: No database connection.");
            return;
        }
        String sql = "SELECT name, balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Account Holder: " + rs.getString("name"));
                System.out.println("Balance: $" + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
        }
    }

    // Main menu
    public void menu() {
        Scanner sc = new Scanner(System.in);

        if (conn == null) {
            System.out.println("Warning: Database connection is not established. Banking system cannot perform operations.");
            return;
        }

        while (true) {
            System.out.println("\n--- Banking System ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter name: ");
                    sc.nextLine(); // consume newline
                    String name = sc.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initial = sc.nextDouble();
                    createAccount(name, initial);
                }
                case 2 -> {
                    System.out.print("Enter account ID: ");
                    int depId = sc.nextInt();
                    System.out.print("Enter amount to deposit: ");
                    double depAmount = sc.nextDouble();
                    deposit(depId, depAmount);
                }
                case 3 -> {
                    System.out.print("Enter account ID: ");
                    int witId = sc.nextInt();
                    System.out.print("Enter amount to withdraw: ");
                    double witAmount = sc.nextDouble();
                    withdraw(witId, witAmount);
                }
                case 4 -> {
                    System.out.print("Enter account ID: ");
                    int balId = sc.nextInt();
                    checkBalance(balId);
                }
                case 5 -> {
                    System.out.println("Thank you for using the Banking System!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void main(String[] args) {
        BankingSystem bank = new BankingSystem();
        bank.menu();
    }
}
