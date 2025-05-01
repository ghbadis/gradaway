@echo off
echo Running GRADAWAY with Maven JavaFX plugin...

mvn clean javafx:run -Djavafx.mainClass=MainApp

pause 