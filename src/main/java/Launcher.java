public class Launcher {
    public static void main(String[] args) {
        // Check if JavaFX is available in the classpath
        try {
            Class.forName("javafx.application.Application");
            System.out.println("JavaFX is in the classpath");
        } catch (ClassNotFoundException e) {
            System.err.println("JavaFX is not in the classpath");
        }
        
        // Launch the actual application
        MainApp.main(args);
    }
} 