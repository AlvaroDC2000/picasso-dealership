package dealership.controllers;

import dealership.util.SalesNavigation;
import dealership.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main controller for the Sales role.
 * <p>
 * This controller manages the sales main layout, where the sidebar remains fixed
 * and only the center content changes. It uses {@link SalesNavigation} to load
 * different screens inside the central content holder.
 * </p>
 */
public class SalesMainController {

    @FXML
    private StackPane contentHolder;

    @FXML
    private Button vehiclesButton;

    @FXML
    private Button customersButton;

    @FXML
    private Button proposalsButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button logoutButton;

    /**
     * Called automatically when the view is loaded.
     * <p>
     * It registers the content holder for navigation and loads the default
     * sales screen.
     * </p>
     */
    @FXML
    private void initialize() {
        // Register holder for navigation
        SalesNavigation.setContentHolder(contentHolder);

        // Default screen
        SalesNavigation.loadCenter("/views/sales-vehicles-view.fxml");
    }

    /**
     * Handles navigation to the vehicles screen.
     *
     * @param event the button action event
     */
    @FXML
    private void handleVehicles(ActionEvent event) {
        SalesNavigation.loadCenter("/views/sales-vehicles-view.fxml");
    }

    /**
     * Handles navigation to the customers screen.
     *
     * @param event the button action event
     */
    @FXML
    private void handleCustomers(ActionEvent event) {
        SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
    }

    /**
     * Handles navigation to the proposals screen.
     *
     * @param event the button action event
     */
    @FXML
    private void handleProposals(ActionEvent event) {
        SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");
    }

    /**
     * Handles navigation to the sales screen.
     *
     * @param event the button action event
     */
    @FXML
    private void handleSales(ActionEvent event) {
        SalesNavigation.loadCenter("/views/sales-sales-view.fxml");
    }

    /**
     * Handles logout action for the sales user.
     * <p>
     * It clears the current {@link SessionContext} and loads the login screen
     * into the same stage.
     * </p>
     *
     * @param event the button action event
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionContext.clear();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/login-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            if (getClass().getResource("/styles/app.css") != null) {
                scene.getStylesheets().add(
                        getClass().getResource("/styles/app.css").toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Logout error", "Could not load login screen.");
        }
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title the dialog title
     * @param message the message to display
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
