package com.Example.ExpenseTracker.dao;

import com.Example.ExpenseTracker.db.DBUtil;
import com.Example.ExpenseTracker.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public static void addExpense(String description, double amount, String date, int categoryId) {
        String sql = "INSERT INTO expenses(description, amount, date, category_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setDouble(2, amount);
            ps.setString(3, date);
            ps.setInt(4, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.description, e.amount, c.name as category FROM expenses e JOIN categories c ON e.category_id = c.id";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                expenses.add(new Expense(description, amount, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }


    public void updateExpense(Expense expense, int categoryId, int expenseId) {
        String update = "UPDATE expenses SET description=?, amount=?, category_id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(update)) {

            ps.setString(1, expense.getDescription());
            ps.setDouble(2, expense.getAmount());
            ps.setInt(3, categoryId);
            ps.setInt(4, expenseId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpense(int expenseId) {
        String delete = "DELETE FROM expenses WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(delete)) {

            ps.setInt(1, expenseId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
