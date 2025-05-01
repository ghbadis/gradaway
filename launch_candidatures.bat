@echo off
echo GradAway - Lanceur d'Applications Candidatures
echo =============================================
echo.
echo 1. Interface Admin des Candidatures
echo 2. Récupérer les Candidatures directement
echo.
set /p choix=Choisissez une option (1 ou 2): 

if "%choix%"=="1" (
    echo Lancement de l'interface admin des candidatures...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.CandidatureApplication
) else if "%choix%"=="2" (
    echo Lancement de l'interface de récupération des candidatures...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.RecupererCandidaturesApplication
) else (
    echo Option invalide. Veuillez choisir 1 ou 2.
    pause
    exit /b
)
pause 