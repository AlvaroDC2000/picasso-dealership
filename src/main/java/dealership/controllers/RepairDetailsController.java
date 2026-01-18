package dealership.controllers;

import dealership.dao.RepairOrderDao;
import dealership.model.RepairDetails;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller for the repair details screen (mechanic flow).
 * <p>
 * This view shows the current status and notes of a repair, and provides actions
 * to start or finish the repair depending on its status. It also allows opening
 * a customer details view related to the repair.
 * </p>
 */
public class RepairDetailsController {

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button startButton;

    @FXML
    private Button finishButton;

    @FXML
    private Button customerButton;

    private int repairId;
    private RepairDetails details;

    private Stage stage;
    private Scene previousScene; 

    /**
     * Sets the navigation context used to return to the previous screen.
     * <p>
     * The caller should provide the stage and the previous scene so this controller
     * can restore it when the user presses Back.
     * </p>
     *
     * @param stage the stage where this view is being displayed
     * @param previousScene the scene to return to when going back
     */
    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    /**
     * Sets the repair ID to be displayed and loads its details.
     * <p>
     * This method is typically called right after loading the FXML.
     * </p>
     *
     * @param repairId the repair identifier to load
     */
    public void setRepairId(int repairId) {
        this.repairId = repairId;
        loadDetails();
    }

    /**
     * Loads repair details from the database and updates the UI.
     * <p>
     * If the repair does not exist, the screen is disabled and an informative message
     * is displayed. Errors are handled by showing default messages and disabling actions.
     * </p>
     */
    private void loadDetails() {
        try {
            RepairOrderDao dao = new RepairOrderDao();
            details = dao.findRepairDetailsById(repairId);

            if (details == null) {
                statusLabel.setText("Unknown");
                notesArea.setText("Repair not found.");
                startButton.setDisable(true);
                finishButton.setDisable(true);
                customerButton.setDisable(true);
                return;
            }

            statusLabel.setText(formatStatus(details.getStatus()));
            notesArea.setText(details.getNotes().isEmpty() ? "No notes." : details.getNotes());

            updateButtonsByStatus(details.getStatus());

        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error");
            notesArea.setText("Could not load repair details.");
            startButton.setDisable(true);
            finishButton.setDisable(true);
            customerButton.setDisable(true);
        }
    }

    /**
     * Enables or disables action buttons depending on the repair status.
     * <p>
     * - ASSIGNED: start enabled, finish disabled
     * - IN_PROGRESS: start disabled, finish enabled
     * - FINISHED: both disabled
     * Any unknown status disables both action buttons.
     * </p>
     *
     * @param statusRaw raw status string coming from the database
     */
    private void updateButtonsByStatus(String statusRaw) {
        String status = statusRaw == null ? "" : statusRaw.trim().toUpperCase();

        if (status.equals("ASSIGNED")) {
            startButton.setDisable(false);
            finishButton.setDisable(true);
            return;
        }

        if (status.equals("IN_PROGRESS")) {
            startButton.setDisable(true);
            finishButton.setDisable(false);
            return;
        }

        if (status.equals("FINISHED")) {
            startButton.setDisable(true);
            finishButton.setDisable(true);
            return;
        }

        startButton.setDisable(true);
        finishButton.setDisable(true);
    }

    /**
     * Formats the repair status to a more friendly label text.
     * <p>
     * Known values are converted to a nicer human readable string. Unknown values
     * are returned as-is.
     * </p>
     *
     * @param statusRaw raw status string coming from the database
     * @return formatted status string for UI display
     */
    private String formatStatus(String statusRaw) {
        if (statusRaw == null) return "Unknown";
        String s = statusRaw.trim().toUpperCase();
        return switch (s) {
            case "ASSIGNED" -> "Assigned";
            case "IN_PROGRESS" -> "In progress";
            case "FINISHED" -> "Finished";
            default -> statusRaw;
        };
    }

    /**
     * Handles the action to start the repair.
     * <p>
     * It updates the repair status in the database and refreshes the view.
     * If the operation fails, an error dialog is shown.
     * </p>
     */
    @FXML
    private void handleStartRepair() {
        try {
            RepairOrderDao dao = new RepairOrderDao();
            dao.startRepair(repairId);
            loadDetails();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not start repair.");
        }
    }

    /**
     * Handles the action to finish the repair.
     * <p>
     * It updates the repair status in the database and refreshes the view.
     * If the operation fails, an error dialog is shown.
     * </p>
     */
    @FXML
    private void handleFinishRepair() {
        try {
            RepairOrderDao dao = new RepairOrderDao();
            dao.finishRepair(repairId);
            loadDetails();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not finish repair.");
        }
    }

    /**
     * Opens the customer details view for the current repair.
     * <p>
     * It loads the customer details FXML, passes the repair details to the controller
     * and sets a navigation context so the user can return to this screen.
     * </p>
     */
    @FXML
    private void handleCustomerDetails() {
        if (details == null) return;

        try {
            if (stage == null) {
                stage = (Stage) customerButton.getScene().getWindow();
            }

            Scene repairDetailsScene = customerButton.getScene();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/customer-details-view.fxml"));
            Parent root = loader.load();

            CustomerDetailsController controller = loader.getController();
            controller.setDetails(details);
            controller.setNavigationContext(stage, repairDetailsScene);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Could not open customer details.");
        }
    }

    /**
     * Handles the back action from this view.
     * <p>
     * Restores the previous scene if it was provided through {@link #setNavigationContext(Stage, Scene)}.
     * </p>
     */
    @FXML
    private void handleBack() {
        try {
            if (stage == null) {
                stage = (Stage) customerButton.getScene().getWindow();
            }
            if (previousScene != null) {
                stage.setScene(previousScene);
                stage.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Shows a simple error dialog.
     *
     * @param msg the message to display
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
