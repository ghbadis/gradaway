@echo off
echo Creating specific IDs in database...
echo.

REM Compile the utility
if not exist "target\classes\util\CreateSpecificIds.class" (
    echo Compiling utility...
    javac -d target\classes -cp src\main\java src\main\java\util\CreateSpecificIds.java
)

REM Run the utility
echo Creating user ID 52 and dossier ID 44...
java -cp target\classes util.CreateSpecificIds

echo.
echo Process complete. The application should now be able to use user ID 52 and dossier ID 44.
echo.
pause 