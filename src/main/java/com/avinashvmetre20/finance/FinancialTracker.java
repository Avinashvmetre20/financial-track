package com.avinashvmetre20.finance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FinancialTracker {
    private static final String URL = "jdbc:mysql://localhost:3306/financial_db";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = "Hsaniva@4570";  // Replace with your MySQL password

    public static void main(String[] args) {
        double bal=0;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) { //making connection
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Welcome to Financial Tracker");
                System.out.println("1. Add Money");
                System.out.println("2. Spend Money");
                System.out.println("3. View Transaction History");
                System.out.println("4. Exit");
                System.out.print("Select an option: ");
                int choice = scanner.nextInt();

                switch (choice) { // selecting the choice by entering the number
                    case 1:
                        handleTransaction(connection, scanner, "gain");//calling the method by sending the gain parametre
                        break;
                    case 2:
                        handleTransaction(connection, scanner, "spent");// calling the method by sending the spent parametre
                        break;
                    case 3:
                        displayHistory(connection); // calling for display the history
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleTransaction(Connection connection, Scanner scanner, String type) throws SQLException {
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter purpose: ");
        String purpose = scanner.nextLine();

        String query = "INSERT INTO transactions (amount, type, purpose, balance) VALUES (?, ?, ?, ?)";


        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setDouble(1, amount);
            stmt.setString(2, type);
            stmt.setString(3, purpose);

            String query1 = "SELECT balance FROM transactions WHERE id = (SELECT MAX(id) FROM transactions)";
            try (PreparedStatement stmt1 = connection.prepareStatement(query1);
                 ResultSet rs = stmt1.executeQuery()) {
                if(rs.next()) {//moving cursor to first row
                    double balance = rs.getDouble("balance"); //getting the balance amount from the table

                    if(type == "gain") {
                        balance+=amount; // adding the amount to balance
                    }
                    else {
                        balance-=amount;
                    }
                    stmt.setDouble(4, balance);
                }
            }
            stmt.executeUpdate();
            System.out.println(type.equals("gain") ? "Money added successfully!" : "Money spent successfully!");

        }
    }

    private static void displayHistory(Connection connection) throws SQLException {
        String query = "SELECT * FROM transactions ORDER BY date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Transaction History:");
            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                String purpose = rs.getString("purpose");
                String date = rs.getString("date");
                double balance = rs.getDouble("balance");
                System.out.printf("ID: %d | Amount: %.2f | Type: %s | Purpose: %s | Date: %s | Balance: %.2f%n", id, amount, type, purpose, date, balance);
            }
        }
    }
}
