import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    static {
        try {
            String javaVersion = System.getProperty("java.version");
            String javafxVersion = System.getProperty("javafx.version");
            System.out.println("JavaFX " + javafxVersion + ", Java " + javaVersion);
            
            // Try to provide module info if missing
            String modules = "javafx.controls,javafx.fxml,javafx.graphics";
            System.setProperty("javafx.modules", modules);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
        primaryStage.setTitle("GRADAWAY - Système de Gestion");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 