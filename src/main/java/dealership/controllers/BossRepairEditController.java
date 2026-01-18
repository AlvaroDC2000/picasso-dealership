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

/**
 * Controller for the boss repair edit screen.
 * <p>
 * This controller loads the selected repair details (based on the current session and
 * selected repair context) and allows the boss to assign/unassign a mechanic and update
 * repair notes, as long as the repair status is editable.
 * </p>
 */
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

    /**
     * Initializes the controller after the FXML is loaded.
     * <p>
     * It retrieves the current boss ID from {@link SessionContext} and the selected
     * repair ID from {@link RepairSelectionContext}. If required data is missing,
     * the screen is disabled. Otherwise, it loads mechanics for the combo box and
     * the repair details to be edited.
     * </p>
     */
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

    /**
     * Loads the list of active mechanics into the combo box.
     * <p>
     * It always adds a default "(none)" option first, then appends the list coming
     * from the database. The first option is selected by default.
     * </p>
     */
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

    /**
     * Loads the repair details that the boss wants to edit.
     * <p>
     * Details are retrieved using both the repair ID and the boss ID (to validate
     * permissions). If the repair cannot be found or is not accessible, editing is
     * disabled. Editing is also disabled when the repair status is not editable.
     * </p>
     */
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

    /**
     * Checks whether a repair with the given status can be edited.
     * <p>
     * Only repairs with status {@code PENDING} or {@code ASSIGNED} are considered editable.
     * Any other status (or null) will block editing actions in this screen.
     * </p>
     *
     * @param status the repair status to validate
     * @return true if the status allows editing, false otherwise
     */
    private boolean canEdit(String status) {
        if (status == null) {
            return false;
        }
        String s = status.trim().toUpperCase();
        return s.equals("PENDING") || s.equals("ASSIGNED");
    }

    /**
     * Selects the mechanic in the combo box that matches the given mechanic ID.
     * <p>
     * If the mechanic ID is null or not found in the current combo items, the first
     * item is selected (usually the "(none)" option).
     * </p>
     *
     * @param mechanicId the mechanic ID to select
     */
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

    /**
     * Disables all editable controls in the screen.
     * <p>
     * This is used when session data is missing, the repair cannot be accessed,
     * or the repair status does not allow edits.
     * </p>
     */
    private void disableEditing() {
        mechanicCombo.setDisable(true);
        notesArea.setEditable(false);
        unassignButton.setDisable(true);
        saveButton.setDisable(true);
    }

    /**
     * Handles the save action.
     * <p>
     * This assigns a mechanic to the repair and updates notes. The operation is only allowed
     * if the session is valid and a proper mechanic is selected. On success, it navigates
     * back to the boss repairs screen.
     * </p>
     *
     * @param event the action event triggered by the save button
     */
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

    /**
     * Handles the unassign action.
     * <p>
     * This removes the assigned mechanic from the repair (if editable) and updates notes.
     * On success, it navigates back to the boss repairs screen.
     * </p>
     *
     * @param event the action event triggered by the unassign button
     */
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

    /**
     * Handles the back navigation action.
     * <p>
     * Returns to the boss repairs screen without applying any changes.
     * </p>
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            goTo((Node) event.getSource(), "/views/boss-repairs-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not go back.");
        }
    }

    /**
     * Navigates to a different view by loading an FXML and replacing the current scene.
     * <p>
     * If the main stylesheet exists, it is applied to the new scene as well.
     * </p>
     *
     * @param source   the node that triggered navigation
     * @param fxmlPath the path to the FXML file to load
     * @throws Exception if the view cannot be loaded
     */
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
