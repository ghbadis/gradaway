@echo off
echo Starting GRADAWAY Main Application...

rem Find Java installation path
for /f "tokens=*" %%a in ('where java') do (
    set JAVA_PATH=%%a
    goto :found_java
)

:found_java
rem Remove \bin\java.exe from the path to get Java home
set JAVA_HOME=%JAVA_PATH:~0,-9%

rem Check for JavaFX in various locations
set PATH_TO_FX_LOCATIONS=^
%JAVA_HOME%\javafx-sdk-21.0.2\lib;^
%USERPROFILE%\javafx-sdk-21.0.2\lib;^
C:\Program Files\JavaFX\lib;^
C:\Program Files (x86)\JavaFX\lib;^
C:\javafx-sdk-21.0.2\lib;^
%USERPROFILE%\.m2\repository\org\openjfx

rem Find first existing JavaFX location
for %%i in (%PATH_TO_FX_LOCATIONS%) do (
    if exist "%%i\javafx.base.jar" (
        set PATH_TO_FX=%%i
        goto :found_fx
    )
)

:found_fx
if not exist "%PATH_TO_FX%" (
    echo JavaFX not found in any standard location.
    echo Please download JavaFX SDK from https://gluonhq.com/products/javafx/
    echo and extract it to one of these locations:
    echo - %JAVA_HOME%\javafx-sdk-21.0.2
    echo - %USERPROFILE%\javafx-sdk-21.0.2
    echo - C:\Program Files\JavaFX
    echo - C:\javafx-sdk-21.0.2
    pause
    exit /b 1
)

echo Using JavaFX from: %PATH_TO_FX%

rem Run the MainApp application with JavaFX modules
java --module-path "%PATH_TO_FX%" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp "target\classes;src\main\resources" MainApp

echo Application closed.
pause 