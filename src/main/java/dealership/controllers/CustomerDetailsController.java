package dealership.controllers;

import dealership.model.RepairDetails;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the customer details popup/screen.
 * <p>
 * This view is used to display customer contact information related to a repair,
 * plus the vehicle involved. It also provides a simple "Notify" action and a
 * back navigation to return to the previous scene.
 * </p>
 */
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

    /**
     * Sets the navigation context needed to return back to the previous screen.
     * <p>
     * This controller does not load scenes by itself. Instead, the caller provides
     * the current stage and the scene to return to when the user presses Back.
     * </p>
     *
     * @param stage the stage where this view is being shown
     * @param previousScene the scene to restore when going back
     */
    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    /**
     * Sets the repair details to be displayed in this view.
     * <p>
     * Once details are set, the UI labels are immediately populated.
     * </p>
     *
     * @param details repair details containing customer and vehicle information
     */
    public void setDetails(RepairDetails details) {
        this.details = details;
        loadData();
    }

    /**
     * Loads data from the current {@link #details} into the UI labels.
     * <p>
     * If details are not present, this method does nothing.
     * </p>
     */
    private void loadData() {
        if (details == null) return;

        nameLabel.setText("Name\n" + details.getCustomerName());
        phoneLabel.setText("Phone\n" + details.getCustomerPhone());
        emailLabel.setText("Email\n" + details.getCustomerEmail());
        vehicleLabel.setText("Vehicle\n" + details.getVehicleText());
    }

    /**
     * Handles the notify action.
     * <p>
     * For now, it shows a confirmation information dialog indicating that the
     * notification was sent successfully.
     * </p>
     */
    @FXML
    private void handleNotify() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notify customer");
        alert.setHeaderText("Notification sent");
        alert.setContentText("The customer has been notified successfully.");
        alert.showAndWait();
    }

    /**
     * Handles the back action.
     * <p>
     * Restores the previous scene in the same stage if both were provided.
     * </p>
     */
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
