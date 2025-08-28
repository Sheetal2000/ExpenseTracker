package com.Example.ExpenseTracker.ui;

import com.Example.ExpenseTracker.db.DBUtil;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MonthlyBarChartController {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    public void initialize() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Expenses");

        String query = "SELECT strftime('%Y-%m', date) AS month, SUM(amount) AS total " +
                "FROM expenses " +
                "GROUP BY month " +
                "ORDER BY month";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                series.getData().add(new XYChart.Data<>(month, total));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        barChart.getData().add(series);
    }
}
