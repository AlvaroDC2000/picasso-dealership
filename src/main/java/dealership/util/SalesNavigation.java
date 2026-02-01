package dealership.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Simple navigator utility for the Sales module.
 * <p>
 * This class is responsible for handling navigation inside the Sales area.
 * Instead of changing the entire scene, it replaces only the center content
 * inside the Sales main view by loading different FXML views
 * into a shared {@link StackPane}.
 * </p>
 *
 * <p>
 * The class is implemented as a static utility and maintains a reference
 * to a shared content holder used across the Sales section of the application.
 * </p>
 */
public class SalesNavigation {

    private static StackPane contentHolder;

    /**
     * Private constructor to prevent instantiation.
     *
     * <p>This class is intended to be used as a static utility and should
     * not be instantiated.</p>
     */
    private SalesNavigation() {
    }

    /**
     * Registers the {@link StackPane} that will act as the central content holder
     * for the Sales module.
     *
     * <p>This method must be called during the initialization of the Sales
     * main view. All subsequent navigation actions rely on this holder
     * to replace the visible content.</p>
     *
     * @param holder the {@code StackPane} used as content holder
     */
    public static void setContentHolder(StackPane holder) {
        contentHolder = holder;
    }

    /**
     * Loads an FXML view into the registered center content holder.
     *
     * <p>The current content of the {@link StackPane} is replaced entirely
     * by the newly loaded view. This method is intended for internal
     * navigation within the Sales area.</p>
     *
     * <p>If the content holder has not been registered or the FXML resource
     * cannot be found, an error dialog is shown to the user.</p>
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
     * Sends the user to the login screen by replacing the entire {@link javafx.scene.Scene}.
     * <p>
     * IMPORTANT: Do not use {@link #loadCenter(String)} for the login view,
     * otherwise the login screen would appear inside the Sales content area
     * instead of replacing the full window.
     * </p>
     *
     * <p>This method resolves the current {@link Stage} from any node belonging
     * to the active window and loads the login view as a fresh scene.</p>
     *
     * @param anyNode any node belonging to the current window (used to resolve the {@link Stage})
     */
    public static void goToLogin(Node anyNode) {
        try {
            if (anyNode == null || anyNode.getScene() == null) {
                showError("Navigation error", "Cannot resolve current window.");
                return;
            }

            Stage stage = (Stage) anyNode.getScene().getWindow();

            if (SalesNavigation.class.getResource("/views/login-view.fxml") == null) {
                showError("FXML not found", "Missing file: /views/login-view.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(SalesNavigation.class.getResource("/views/login-view.fxml"));
            Parent root = loader.load();

            var scene = new javafx.scene.Scene(root);

            if (SalesNavigation.class.getResource("/styles/app.css") != null) {
                scene.getStylesheets().add(
                        SalesNavigation.class.getResource("/styles/app.css").toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation error", "Could not open login screen.");
        }
    }

    /**
     * Displays a modal error dialog to the user.
     *
     * <p>This helper method centralizes error reporting for navigation-related
     * issues, ensuring consistent feedback when navigation fails due to
     * missing resources or invalid state.</p>
     *
     * @param title the dialog title
     * @param message the error message to display
     */
    private static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

