@echo off
echo Running Database Initializer...
echo.

REM Compile the needed classes if they don't exist
if not exist "target\classes\util\DatabaseTester.class" (
    echo Compiling utility classes...
    javac -d target\classes -cp src\main\java src\main\java\util\DatabaseTester.java src\main\java\util\DatabaseInitializer.java
)

REM Run the initializer
echo Starting database setup...
java -cp target\classes util.DatabaseInitializer

echo.
echo Database setup complete. You should now be able to add candidatures using existing IDs.
echo.
pause 