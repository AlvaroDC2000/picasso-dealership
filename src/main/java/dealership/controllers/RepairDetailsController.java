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

    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    public void setRepairId(int repairId) {
        this.repairId = repairId;
        loadDetails();
    }

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

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}



