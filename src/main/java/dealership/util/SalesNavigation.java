package dealership.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

/**
 * Simple navigator utility for the Sales module.
 * <p>
 * This class is responsible for handling navigation inside the Sales area.
 * Instead of changing the entire scene, it replaces only the center content
 * inside the {@code SalesMainController} by loading different FXML views
 * into a shared {@link StackPane}.
 * </p>
 */
public class SalesNavigation {

    private static StackPane contentHolder;

    /**
     * Private constructor to prevent instantiation.
     * <p>
     * This class is intended to be used only through its static methods.
     * </p>
     */
    private SalesNavigation() {
    }

    /**
     * Registers the StackPane that will hold the center content.
     * <p>
     * This method must be called once from {@code SalesMainController}
     * during initialization, before any navigation is performed.
     * </p>
     *
     * @param holder the StackPane used as content holder
     */
    public static void setContentHolder(StackPane holder) {
        contentHolder = holder;
    }

    /**
     * Loads an FXML view into the center content holder.
     * <p>
     * The loaded view replaces any existing content inside the holder.
     * Basic validation is performed to ensure the holder is set and the
     * FXML resource exists.
     * </p>
     *
     * @param fxmlPath the path to the FXML file to load
     */
    public static void loadCenter(String fxmlPath) {
        try {
            if (contentHolder == null) {
                showError("Navigation error", "Sales contentHolder is not set.");
                return;
            }

            if (SalesNavigation.class.getResource(fxmlPath) == null) {
                showError("FXML not found", "Missing file: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(SalesNavigation.class.getResource(fxmlPath));
            Node view = loader.load();
            contentHolder.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error", "Could not load: " + fxmlPath);
        }
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title the dialog title
     * @param message the message to display
     */
    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
