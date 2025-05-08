@echo off
setlocal EnableDelayedExpansion
echo Running GRADAWAY with JavaFX...

REM Try to use IDE's Maven if available
set MAVEN_EXECUTABLE=mvn

REM Check if Maven is in PATH
where %MAVEN_EXECUTABLE% >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found in PATH, searching for Maven in common locations...
    
    REM Common locations where Maven might be installed by IDEs
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA*\plugins\maven\lib\maven3\bin\mvn.cmd" (
        for /d %%i in ("C:\Program Files\JetBrains\IntelliJ IDEA*\plugins\maven\lib\maven3\bin") do (
            set MAVEN_EXECUTABLE=%%i\mvn.cmd
            echo Found Maven in IntelliJ: !MAVEN_EXECUTABLE!
            goto :found_maven
        )
    )
    
    REM Check Eclipse locations
    if exist "C:\Users\%USERNAME%\.p2\pool\plugins\org.apache.maven.bin*\bin\mvn.cmd" (
        for /d %%i in ("C:\Users\%USERNAME%\.p2\pool\plugins\org.apache.maven.bin*\bin") do (
            set MAVEN_EXECUTABLE=%%i\mvn.cmd
            echo Found Maven in Eclipse plugins: !MAVEN_EXECUTABLE!
            goto :found_maven
        )
    )
    
    echo Maven not found, please install Maven or make sure it's in your PATH.
    echo Trying to run the application directly...
    goto :run_direct
) else (
    echo Found Maven in PATH
    goto :found_maven
)

:found_maven
echo Running with Maven JavaFX plugin...
%MAVEN_EXECUTABLE% clean javafx:run -Djavafx.mainClass=MainApp
goto :end

:run_direct
echo Running using direct Java command...
REM Direct run using Java
set CLASSPATH=target\classes;target\dependency\*
java --module-path ".\target\dependency" --add-modules javafx.controls,javafx.fxml MainApp

:end
endlocal
pause 