@echo off
cd /d "C:\Users\sheetal\Downloads\JavaProjects\ExpenseTracker\out\artifacts\ExpenseTracker_jar"
java --module-path "C:\Users\sheetal\Downloads\openjfx-21.0.8_windows-x64_bin-sdk\javafx-sdk-21.0.8\lib" --add-modules javafx.controls,javafx.fxml -jar "ExpenseTracker.jar"
pause
