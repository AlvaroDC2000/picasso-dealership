package dealership.controllers;

import dealership.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main controller for Sales role.
 * The sidebar is fixed and only the center content changes.
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
     * Loads the default dashboard.
     */
    @FXML
    private void initialize() {
        loadCenter("/views/sales-dashboard-view.fxml");
    }

    /**
     * Show vehicles / stock view.
     */
    @FXML
    private void handleVehicles(ActionEvent event) {
        loadCenter("/views/sales-dashboard-view.fxml");
    }

    /**
     * Show customers list.
     */
    @FXML
    private void handleCustomers(ActionEvent event) {
        loadCenter("/views/sales-dashboard-view.fxml");
    }

    /**
     * Show proposals list.
     */
    @FXML
    private void handleProposals(ActionEvent event) {
        loadCenter("/views/sales-dashboard-view.fxml");
    }

    /**
     * Show sales list.
     */
    @FXML
    private void handleSales(ActionEvent event) {
        loadCenter("/views/sales-dashboard-view.fxml");
    }

    /**
     * Logout and return to login screen.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session data
            SessionContext.clear();

            // Get current window
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            // Load login view
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/login-view.fxml")
            );
            Scene scene = new Scene(loader.load());

            // Apply global styles
            if (getClass().getResource("/styles/app.css") != null) {
                scene.getStylesheets().add(
                        getClass().getResource("/styles/app.css").toExternalForm()
                );
            }

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a view inside the center content area.
     */
    private void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            Node view = loader.load();

            contentHolder.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

