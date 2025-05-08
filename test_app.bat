@echo off
echo Running Simple Test App...
java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.SimpleTestApp
pause 