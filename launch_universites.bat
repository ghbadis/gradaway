@echo off
echo GradAway - Lanceur d'Applications Universites
echo =============================================
echo.
echo 1. Interface Admin des Universites
echo 2. Ajouter une Universite
echo 3. Liste des Universites
echo.
set /p choix=Choisissez une option (1, 2 ou 3): 

if "%choix%"=="1" (
    echo Lancement de l'interface admin des universites...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.UniversiteApplication
) else if "%choix%"=="2" (
    echo Lancement de l'interface d'ajout d'universite...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.AjouterUniversiteApplication
) else if "%choix%"=="3" (
    echo Lancement de l'interface de liste des universites...
    java --module-path "C:\Program Files\Java\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp "./target/classes;./target/dependency/*" application.RecupererUniversiteApplication
) else (
    echo Option invalide. Veuillez choisir 1, 2 ou 3.
    pause
    exit /b
)
pause 