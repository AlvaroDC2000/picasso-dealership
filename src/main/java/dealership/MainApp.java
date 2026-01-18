package dealership;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point of the application.
 * <p>
 * This class is responsible for launching the JavaFX application and
 * initializing the primary stage. It loads the login view, applies the
 * global stylesheet and configures the main window properties such as
 * title, size and minimum dimensions.
 * </p>
 */
public class MainApp extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method loads the main FXML view used for user login, attaches
     * the application stylesheet, sets up the stage configuration and
     * finally displays the main window.
     * </p>
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws Exception if the FXML file or resources cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/views/login-view.fxml"));
        Scene scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Concesionario Picasso");

        stage.setWidth(1100);
        stage.setHeight(800);
        stage.setMinWidth(1100);
        stage.setMinHeight(800);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Application main method.
     * <p>
     * This method launches the JavaFX runtime and delegates control
     * to the {@link #start(Stage)} method.
     * </p>
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}

