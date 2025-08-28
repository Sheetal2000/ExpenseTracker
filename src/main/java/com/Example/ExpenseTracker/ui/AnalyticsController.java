package com.Example.ExpenseTracker.ui;

import com.Example.ExpenseTracker.db.DBUtil;
import com.Example.ExpenseTracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AnalyticsController {

    @FXML
    private PieChart pieChart;

    @FXML
    public void initialize() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String query = "SELECT c.name AS category, SUM(e.amount) AS total " +
                "FROM expenses e JOIN categories c ON e.category_id = c.id " +
                "WHERE strftime('%Y-%m', e.date) = strftime('%Y-%m', 'now') " +
                "GROUP BY c.name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                pieChartData.add(new PieChart.Data(category, total));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Expenses by Category (This Month)");
    }
}
