package com.Example.ExpenseTracker.test;

import com.Example.ExpenseTracker.dao.CategoryDAO;
import com.Example.ExpenseTracker.dao.ExpenseDAO;
import com.Example.ExpenseTracker.model.Expense;

import java.time.LocalDate;
import java.util.List;

public class DAOTest {
    public static void main(String[] args) {

        // 1. Add categories
        CategoryDAO.addCategory("Food");
        CategoryDAO.addCategory("Transport");
        CategoryDAO.addCategory("Shopping");

        System.out.println("✅ Categories Added!");

        // 2. Show all categories
        List<String> categories = CategoryDAO.getAllCategories();
        System.out.println("\nAll Categories:");
        for (String cat : categories) {
            System.out.println(cat);
        }

        // 3. Add expenses (now with date)
        String today = LocalDate.now().toString(); // yyyy-MM-dd
        ExpenseDAO.addExpense("Lunch", 250.50, today, 1);       // categoryId = 1 (Food)
        ExpenseDAO.addExpense("Bus Ticket", 50.00, today, 2);   // categoryId = 2 (Transport)

        // 4. Show all expenses
        List<Expense> expenses = ExpenseDAO.getAllExpenses();
        System.out.println("\nAll Expenses:");
        for (Expense exp : expenses) {
            System.out.println(exp);
        }
    }
}
