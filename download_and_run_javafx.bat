@echo off
setlocal EnableDelayedExpansion
echo GRADAWAY JavaFX Setup and Launcher

REM Check if JavaFX is already installed
set JAVAFX_PATH=C:\javafx-sdk-21.0.2\lib
if exist "%JAVAFX_PATH%\javafx.controls.jar" (
    echo JavaFX SDK already installed at %JAVAFX_PATH%
    goto :run_app
)

REM Ask user if they want to download JavaFX
echo JavaFX SDK not found. You need to install it to run the application.
echo Would you like to:
echo 1. Open the download page for JavaFX SDK
echo 2. Exit
set /p choice="Enter your choice (1 or 2): "

if "%choice%"=="1" (
    echo Opening JavaFX download page...
    start https://gluonhq.com/products/javafx/
    
    echo.
    echo After downloading the JavaFX SDK:
    echo 1. Extract the ZIP file to C:\javafx-sdk-21.0.2
    echo 2. Run this script again to launch the application
    echo.
    pause
    exit /b 0
) else (
    echo Exiting setup
    exit /b 1
)

:run_app
REM Set classpath for target/classes and resources
set CLASSPATH=target\classes;src\main\resources

REM Check if compiled classes exist
if not exist "target\classes\MainApp.class" (
    echo Application not compiled. Compiling now...
    
    REM First check if target/classes directory exists
    if not exist "target\classes" mkdir "target\classes"
    
    REM Try to compile
    javac -d target\classes src\main\java\*.java 2>nul
    if %ERRORLEVEL% NEQ 0 (
        echo Compilation failed. Please make sure you have Java JDK installed.
        pause
        exit /b 1
    )
)

echo Running application with JavaFX...
java --module-path "%JAVAFX_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp "%CLASSPATH%" MainApp

echo Application closed.
endlocal
pause 