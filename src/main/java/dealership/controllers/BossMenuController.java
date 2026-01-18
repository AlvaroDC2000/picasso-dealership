package dealership.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Controller for the boss main menu screen.
 * <p>
 * This controller handles navigation actions available for the boss role,
 * such as registering a new repair, viewing existing repairs, managing
 * mechanics and their skills, and logging out of the application.
 * </p>
 */
public class BossMenuController {

    /**
     * Opens the "Register new repair" screen.
     * <p>
     * If the view cannot be loaded, an error alert is shown to the user.
     * </p>
     *
     * @param event the action event triggered by the corresponding menu button
     */
    @FXML
    private void handleRegisterNewRepair(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/register-repair-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open Register new repair screen.");
        }
    }

    /**
     * Opens the "View repairs" screen for the boss.
     * <p>
     * If the view cannot be loaded, an error alert is shown.
     * </p>
     *
     * @param event the action event triggered by the corresponding menu button
     */
    @FXML
    private void handleViewRepairs(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open View repairs screen.");
        }
    }

    /**
     * Opens the "Mechanics and skills" screen.
     * <p>
     * This screen allows the boss to review mechanics and access the
     * skills edit flow. If the view cannot be loaded, an error alert is shown.
     * </p>
     *
     * @param event the action event triggered by the corresponding menu button
     */
    @FXML
    private void handleMechanicsAndSkills(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/boss-mechanics-skills-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open Mechanics and skills screen.");
        }
    }

    /**
     * Logs the user out by returning to the login screen.
     * <p>
     * This method simply navigates back to the login view. If navigation fails,
     * an error alert is shown.
     * </p>
     *
     * @param event the action event triggered by the logout button
     */
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/login-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not logout.");
        }
    }

    /**
     * Navigates to the given FXML view, replacing the current scene.
     * <p>
     * It loads the FXML, applies the application stylesheet if present,
     * and displays the new scene in the current stage.
     * </p>
     *
     * @param event    the action event that triggered the navigation
     * @param fxmlPath the path to the FXML view to load
     * @throws Exception if the FXML view or resources cannot be loaded
     */
    private void goTo(javafx.event.ActionEvent event, String fxmlPath) throws Exception {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

        Scene scene = new Scene(root);
        if (getClass().getResource("/styles/app.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Shows an error dialog to the user.
     * <p>
     * This is used as a simple, consistent way to report navigation or loading
     * problems from this menu.
     * </p>
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
