package dealership.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class responsible for loading and switching JavaFX scenes.
 * <p>
 * This helper centralizes the logic required to load an FXML file,
 * apply the main application stylesheet and display it on a given stage.
 * It helps reduce duplicated navigation code across controllers.
 * </p>
 */
public class SceneLoader {

    /**
     * Loads an FXML view into the given stage and displays it.
     * <p>
     * The method creates a new {@link Scene} from the provided FXML path,
     * applies the global stylesheet, sets the window title and shows
     * the stage.
     * </p>
     *
     * @param stage the stage where the scene will be displayed
     * @param fxmlPath the path to the FXML file to load
     * @param title the title to set on the stage window
     * @throws Exception if the FXML or resources cannot be loaded
     */
    public static void load(Stage stage, String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(SceneLoader.class.getResource(fxmlPath));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneLoader.class.getResource("/styles/app.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
