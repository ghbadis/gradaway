Write-Host "Starting GRADAWAY application..." -ForegroundColor Green

# Try to find JavaFX SDK in various locations
$javafxLocations = @(
    "C:\Program Files\Java\javafx-sdk-21.0.2\lib",
    "C:\Program Files (x86)\Java\javafx-sdk-21.0.2\lib",
    "$env:USERPROFILE\javafx-sdk-21.0.2\lib",
    "C:\javafx-sdk-21.0.2\lib",
    "C:\Program Files\JavaFX\lib",
    "$env:USERPROFILE\.m2\repository\org\openjfx"
)

$javafxPath = $null
foreach ($location in $javafxLocations) {
    if (Test-Path "$location\javafx.base.jar") {
        $javafxPath = $location
        break
    }
}

if ($javafxPath -eq $null) {
    Write-Host "JavaFX SDK not found. Please download from https://gluonhq.com/products/javafx/ and extract to one of these locations:" -ForegroundColor Red
    foreach ($location in $javafxLocations) {
        Write-Host "  - $location" -ForegroundColor Yellow
    }
    Write-Host "`nAlternatively, you can manually set the correct path below:" -ForegroundColor Cyan
    $manualPath = Read-Host "Enter path to JavaFX lib directory (or press Enter to exit)"
    
    if ($manualPath -and (Test-Path "$manualPath\javafx.base.jar")) {
        $javafxPath = $manualPath
    } else {
        Read-Host "Press Enter to exit"
        exit
    }
}

Write-Host "Using JavaFX from: $javafxPath" -ForegroundColor Cyan

# Make sure target/classes exists
if (-not (Test-Path "target\classes")) {
    Write-Host "Compiling Java files..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null
    & javac -d "target\classes" $(Get-ChildItem -Path "src\main\java\tests","src\main\java\controllers" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName })
}

# Run the application with JavaFX modules
& java --module-path "$javafxPath" --add-modules=javafx.controls,javafx.fxml,javafx.graphics -cp "target\classes;src\main\resources" tests.Launcher

Write-Host "Application closed." -ForegroundColor Green
Read-Host "Press Enter to exit" 