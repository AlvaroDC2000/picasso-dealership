package dealership.controllers;

import dealership.dao.RepairOrderDao;
import dealership.dao.UserDao;
import dealership.model.BossRepairEditDetails;
import dealership.util.RepairSelectionContext;
import dealership.util.SessionContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class BossRepairEditController {

    @FXML
    private Label repairIdLabel;

    @FXML
    private Label vehicleLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private ComboBox<RegisterRepairController.IdName> mechanicCombo;

    @FXML
    private TextArea notesArea;

    @FXML
    private Button unassignButton;

    @FXML
    private Button saveButton;

    @FXML
    private Label errorLabel;

    private final RepairOrderDao repairOrderDao = new RepairOrderDao();
    private final UserDao userDao = new UserDao();

    private Integer repairId;
    private Integer bossId;

    @FXML
    public void initialize() {
        errorLabel.setText("");

        bossId = SessionContext.getUserId();
        repairId = RepairSelectionContext.getSelectedRepairId();

        if (bossId == null || repairId == null) {
            errorLabel.setText("Missing session data. Please go back and try again.");
            disableEditing();
            return;
        }

        loadMechanics();
        loadRepairDetails();
    }

    private void loadMechanics() {
        try {
            mechanicCombo.getItems().clear();
            mechanicCombo.getItems().add(new RegisterRepairController.IdName(-1, "(none)"));

            List<RegisterRepairController.IdName> mechanics = userDao.findActiveMechanicsForCombo();
            mechanicCombo.getItems().addAll(mechanics);

            mechanicCombo.getSelectionModel().selectFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not load mechanics.");
        }
    }

    private void loadRepairDetails() {
        try {
            BossRepairEditDetails details = repairOrderDao.findBossEditDetailsById(repairId, bossId);
            if (details == null) {
                errorLabel.setText("Repair not found (or you don't have permissions). ");
                disableEditing();
                return;
            }

            repairIdLabel.setText(String.format("%05d", details.getRepairId()));
            vehicleLabel.setText(details.getVehicleText());
            statusLabel.setText(details.getStatus());
            notesArea.setText(details.getNotes());

            selectMechanic(details.getAssignedMechanicId());

            if (!canEdit(details.getStatus())) {
                errorLabel.setText("This repair cannot be edited because its status is: " + details.getStatus());
                disableEditing();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not load repair details.");
            disableEditing();
        }
    }

    private boolean canEdit(String status) {
        if (status == null) {
            return false;
        }
        String s = status.trim().toUpperCase();
        return s.equals("PENDING") || s.equals("ASSIGNED");
    }

    private void selectMechanic(Integer mechanicId) {
        if (mechanicId == null) {
            mechanicCombo.getSelectionModel().selectFirst();
            return;
        }

        for (RegisterRepairController.IdName item : mechanicCombo.getItems()) {
            if (item != null && item.getId() == mechanicId) {
                mechanicCombo.getSelectionModel().select(item);
                return;
            }
        }
        mechanicCombo.getSelectionModel().selectFirst();
    }

    private void disableEditing() {
        mechanicCombo.setDisable(true);
        notesArea.setEditable(false);
        unassignButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        errorLabel.setText("");

        if (bossId == null || repairId == null) {
            errorLabel.setText("Session expired.");
            return;
        }

        RegisterRepairController.IdName selected = mechanicCombo.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() <= 0) {
            errorLabel.setText("Select a mechanic (or use Unassign). ");
            return;
        }

        try {
            String notes = notesArea.getText() != null ? notesArea.getText().trim() : "";
            boolean ok = repairOrderDao.assignMechanicAndUpdateNotes(repairId, bossId, selected.getId(), notes);
            if (!ok) {
                errorLabel.setText("No changes were saved (maybe status is not editable).");
                return;
            }

            goTo((Node) event.getSource(), "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not save changes.");
        }
    }

    @FXML
    private void handleUnassign(javafx.event.ActionEvent event) {
        errorLabel.setText("");

        if (bossId == null || repairId == null) {
            errorLabel.setText("Session expired.");
            return;
        }

        try {
            String notes = notesArea.getText() != null ? notesArea.getText().trim() : "";
            boolean ok = repairOrderDao.unassignMechanicAndUpdateNotes(repairId, bossId, notes);
            if (!ok) {
                errorLabel.setText("No changes were saved (maybe status is not editable).");
                return;
            }

            goTo((Node) event.getSource(), "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not unassign.");
        }
    }

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            goTo((Node) event.getSource(), "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not go back.");
        }
    }

    private void goTo(Node source, String fxmlPath) throws Exception {
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        if (getClass().getResource("/styles/app.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }
}
