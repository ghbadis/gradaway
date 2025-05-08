@echo off
setlocal EnableDelayedExpansion
echo Starting GRADAWAY Application...

REM Find Java installation
where java > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java not found in PATH. Please install Java and add it to your PATH.
    pause
    exit /b 1
)

REM Set classpath for target/classes and resources
set CLASSPATH=target\classes;src\main\resources

REM Check if the target/classes directory exists
if not exist "target\classes" (
    echo Building the application...
    call gradlew.bat clean build 2>nul
    if %ERRORLEVEL% NEQ 0 (
        echo Gradle build failed. Trying Maven...
        call mvn clean compile 2>nul
        if %ERRORLEVEL% NEQ 0 (
            echo Maven build failed. Compiling manually...
            if not exist "target\classes" mkdir "target\classes"
            javac -d target\classes src\main\java\*.java 2>nul
        )
    )
)

REM Check common JavaFX locations as separate if statements
if exist "C:\Program Files\Java\javafx-sdk-21.0.2\lib\javafx.controls.jar" (
    set "JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-21.0.2\lib"
    goto :found_javafx
)

if exist "C:\Program Files\Java\javafx-sdk-21\lib\javafx.controls.jar" (
    set "JAVAFX_PATH=C:\Program Files\Java\javafx-sdk-21\lib"
    goto :found_javafx
)

if exist "C:\javafx-sdk-21.0.2\lib\javafx.controls.jar" (
    set "JAVAFX_PATH=C:\javafx-sdk-21.0.2\lib"
    goto :found_javafx
)

if exist "C:\javafx-sdk-21\lib\javafx.controls.jar" (
    set "JAVAFX_PATH=C:\javafx-sdk-21\lib"
    goto :found_javafx
)

if exist "%USERPROFILE%\javafx-sdk-21.0.2\lib\javafx.controls.jar" (
    set "JAVAFX_PATH=%USERPROFILE%\javafx-sdk-21.0.2\lib"
    goto :found_javafx
)

echo JavaFX not found in common locations. 
echo Please download JavaFX SDK from https://gluonhq.com/products/javafx/
echo and extract it to C:\javafx-sdk-21.0.2 or set JAVAFX_PATH manually.
pause
exit /b 1

:found_javafx
echo Found JavaFX at: %JAVAFX_PATH%

:run_app
echo Running MainApp with JavaFX...
java --module-path "%JAVAFX_PATH%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp "%CLASSPATH%" MainApp

echo Application closed.
endlocal
pause 