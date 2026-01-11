package dealership.controllers;

import dealership.model.RepairDetails;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CustomerDetailsController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label vehicleLabel;

    private RepairDetails details;

    private Stage stage;
    private Scene previousScene; 

    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    public void setDetails(RepairDetails details) {
        this.details = details;
        loadData();
    }

    private void loadData() {
        if (details == null) return;

        nameLabel.setText("Name\n" + details.getCustomerName());
        phoneLabel.setText("Phone\n" + details.getCustomerPhone());
        emailLabel.setText("Email\n" + details.getCustomerEmail());
        vehicleLabel.setText("Vehicle\n" + details.getVehicleText());
    }

    @FXML
    private void handleNotify() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notify customer");
        alert.setHeaderText("Notification sent");
        alert.setContentText("The customer has been notified successfully.");
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            if (stage != null && previousScene != null) {
                stage.setScene(previousScene);
                stage.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
