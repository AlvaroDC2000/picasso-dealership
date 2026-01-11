package dealership.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class BossMenuController {

    @FXML
    private void handleRegisterNewRepair(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/register-repair-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open Register new repair screen.");
        }
    }

    @FXML
    private void handleViewRepairs(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open View repairs screen.");
        }
    }

    @FXML
    private void handleMechanicsAndSkills(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/boss-mechanics-skills-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open Mechanics and skills screen.");
        }
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        try {
            goTo(event, "/views/login-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not logout.");
        }
    }

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


