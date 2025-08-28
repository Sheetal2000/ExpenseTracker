package com.Example.ExpenseTracker.ui;

import com.Example.ExpenseTracker.dao.ExpenseDAO;
import com.Example.ExpenseTracker.db.DBUtil;
import com.Example.ExpenseTracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {

    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> categoryComboBox;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> descriptionColumn;
    @FXML private TableColumn<Expense, Double> amountColumn;
    @FXML private TableColumn<Expense, String> categoryColumn;

    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private ExpenseDAO expenseDAO = new ExpenseDAO();

    @FXML
    public void initialize() {
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        amountColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAmount()));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        loadCategories();
        loadExpenses();
    }

    // Load categories into ComboBox
    private void loadCategories() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories");
             ResultSet rs = ps.executeQuery()) {

            categoryComboBox.getItems().clear();
            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all expenses into TableView
    private void loadExpenses() {
        expenses.clear();
        expenses.addAll(expenseDAO.getAllExpenses());
        expenseTable.setItems(expenses);
    }

    @FXML
    public void addExpense() {
        String description = descriptionField.getText();
        String amountText = amountField.getText();
        String category = categoryComboBox.getValue();

        if (!description.isEmpty() && !amountText.isEmpty() && category != null && !category.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountText);
                int categoryId = getCategoryId(category);
                String date = java.time.LocalDate.now().toString();

                ExpenseDAO.addExpense(description, amount, date, categoryId);
                expenses.add(new Expense(description, amount, category));

                clearFields();
                loadCategories();

            } catch (NumberFormatException e) {
                showAlert("Invalid amount. Please enter a valid number.");
            } catch (Exception e) {
                showAlert("Error adding expense: " + e.getMessage());
            }
        } else {
            showAlert("Please fill all fields and select a category.");
        }
    }

    @FXML
    public void updateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String description = descriptionField.getText();
            String amountText = amountField.getText();
            String category = categoryComboBox.getValue();

            if (!description.isEmpty() && !amountText.isEmpty() && category != null && !category.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountText);
                    int categoryId = getCategoryId(category);
                    int expenseId = getExpenseId(selected);

                    selected.setDescription(description);
                    selected.setAmount(amount);
                    selected.setCategory(category);

                    expenseDAO.updateExpense(selected, categoryId, expenseId);
                    loadExpenses();
                    clearFields();
                    loadCategories();

                } catch (NumberFormatException e) {
                    showAlert("Invalid amount. Please enter a number.");
                } catch (Exception e) {
                    showAlert("Error updating expense: " + e.getMessage());
                }
            } else {
                showAlert("Please fill all fields and select a category.");
            }
        } else {
            showAlert("No expense selected for update.");
        }
    }

    @FXML
    public void deleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int expenseId = getExpenseId(selected);
            expenseDAO.deleteExpense(expenseId);
            loadExpenses();
            clearFields();
        } else {
            showAlert("No expense selected for deletion.");
        }
    }

    @FXML
    public void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Expenses CSV");
        fileChooser.setInitialFileName("expenses.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(expenseTable.getScene().getWindow());
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Description,Amount,Category");
                writer.newLine();
                for (Expense expense : expenses) {
                    writer.write(expense.getDescription() + "," + expense.getAmount() + "," + expense.getCategory());
                    writer.newLine();
                }
                showAlertInfo("CSV exported successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error exporting CSV");
            }
        }
    }

    @FXML
    public void openAnalytics() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/Example/ExpenseTracker/ui/analytics-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Monthly Analytics");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openMonthlyChart() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/Example/ExpenseTracker/ui/monthly-bar-chart.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Monthly Expenses Chart");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getCategoryId(String categoryName) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM categories WHERE name=?")) {
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return insertCategory(categoryName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int insertCategory(String name) throws SQLException {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO categories(name) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private int getExpenseId(Expense expense) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id FROM expenses WHERE description=? AND amount=? AND category_id=(SELECT id FROM categories WHERE name=?)")) {
            ps.setString(1, expense.getDescription());
            ps.setDouble(2, expense.getAmount());
            ps.setString(3, expense.getCategory());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void clearFields() {
        descriptionField.clear();
        amountField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showAlertInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
