@echo off
echo Running Candidature Diagnostics...
echo.

REM Compile the needed classes if they don't exist
if not exist "target\classes\util\CandidatureDiagnostics.class" (
    echo Compiling diagnostic utility...
    javac -d target\classes -cp src\main\java src\main\java\util\CandidatureDiagnostics.java
)

REM Run the diagnostics
echo Starting diagnostics...
java -cp target\classes util.CandidatureDiagnostics

echo.
echo Diagnostics complete. Check the output above for errors.
echo.
pause 