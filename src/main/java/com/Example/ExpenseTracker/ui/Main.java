package com.Example.ExpenseTracker.ui;

import com.Example.ExpenseTracker.db.DBUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        DBUtil.initializeDatabase();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/Example/ExpenseTracker/ui/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Expense Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
