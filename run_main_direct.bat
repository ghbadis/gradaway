@echo off
echo Running GRADAWAY Main App with direct JavaFX path...

rem Change this path to where you extracted JavaFX SDK
set PATH_TO_FX=C:\javafx-sdk-21.0.2\lib

java --module-path "%PATH_TO_FX%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp "target\classes;src\main\resources" MainApp

pause 