@echo off
echo GradAway - Lanceur Principal
echo ==========================
echo.
echo 1. Gestion des Candidatures
echo 2. Gestion des Universites
echo 3. Soumettre une Candidature (Liste des Universites)
echo.
set /p choix=Choisissez une option (1, 2 ou 3): 

if "%choix%"=="1" (
    call launch_candidatures.bat
) else if "%choix%"=="2" (
    call launch_universites.bat
) else if "%choix%"=="3" (
    echo Lancement de l'interface de soumission de candidature...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.UniversiteCardsApplication
) else (
    echo Option invalide. Veuillez choisir 1, 2 ou 3.
    pause
    exit /b
)
pause 