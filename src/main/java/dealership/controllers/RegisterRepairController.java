package dealership.controllers;

import dealership.dao.CustomerDao;
import dealership.dao.RepairOrderDao;
import dealership.dao.UserDao;
import dealership.dao.VehicleDao;
import dealership.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the "Register repair" screen (boss flow).
 * <p>
 * This controller allows the boss to create a new repair order by selecting:
 * a vehicle, a customer and an assigned mechanic, plus writing some notes.
 * The required combo data is loaded from the database on initialization.
 * </p>
 */
public class RegisterRepairController {

    @FXML
    private ComboBox<IdName> vehicleCombo;

    @FXML
    private ComboBox<IdName> customerCombo;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label assignedMechanicLabel;

    @FXML
    private Label errorLabel;

    private IdName selectedMechanic;

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It loads vehicles and customers from the database and sets initial UI values.
     * If any database load fails, a message is shown on the screen.
     * </p>
     */
    @FXML
    private void initialize() {
        errorLabel.setText("");

        try {
            VehicleDao vehicleDao = new VehicleDao();
            List<IdName> vehicles = vehicleDao.findAllVehiclesForCombo();
            vehicleCombo.setItems(FXCollections.observableArrayList(vehicles));

            CustomerDao customerDao = new CustomerDao();
            List<IdName> customers = customerDao.findAllCustomersForCombo();
            customerCombo.setItems(FXCollections.observableArrayList(customers));

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error loading data from database.");
        }

        selectedMechanic = null;
        assignedMechanicLabel.setText("Assigned mechanic: (none)");
        notesArea.setText("");
    }

    /**
     * Handles the back action.
     * <p>
     * Returns to the boss menu screen. If the navigation fails, an error message
     * is displayed in the view.
     * </p>
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        errorLabel.setText("");
        try {
            goTo(event, "/views/boss-menu-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error going back.");
        }
    }

    /**
     * Opens a dialog to select and assign a mechanic to the new repair.
     * <p>
     * The mechanics list is loaded from the database. If the user selects a mechanic,
     * the selection is stored and the UI label is updated.
     * </p>
     *
     * @param event the action event triggered by the assign mechanic button
     */
    @FXML
    private void handleAssignMechanic(javafx.event.ActionEvent event) {
        errorLabel.setText("");

        try {
            UserDao userDao = new UserDao();
            List<IdName> mechanics = userDao.findActiveMechanicsForCombo();

            if (mechanics.isEmpty()) {
                errorLabel.setText("No mechanics found.");
                return;
            }

            ChoiceDialog<IdName> dialog = new ChoiceDialog<>(mechanics.get(0), mechanics);
            dialog.setTitle("Assign to mechanic");
            dialog.setHeaderText("Select a mechanic");
            dialog.setContentText("Mechanic:");

            Optional<IdName> result = dialog.showAndWait();
            if (result.isPresent()) {
                selectedMechanic = result.get();
                assignedMechanicLabel.setText("Assigned mechanic: " + selectedMechanic.getName());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error loading mechanics.");
        }
    }

    /**
     * Creates a new repair order with the selected data.
     * <p>
     * This method validates that a vehicle, customer and mechanic are selected,
     * verifies the session boss ID, checks notes content and then calls the DAO
     * layer to persist the repair order.
     * </p>
     *
     * @param event the action event triggered by the create repair button
     */
    @FXML
    private void handleCreateRepair(javafx.event.ActionEvent event) {
        errorLabel.setText("");

        IdName selectedVehicle = vehicleCombo.getValue();
        IdName selectedCustomer = customerCombo.getValue();

        if (selectedVehicle == null) {
            errorLabel.setText("Select a car.");
            return;
        }
        if (selectedCustomer == null) {
            errorLabel.setText("Select a customer.");
            return;
        }
        if (selectedMechanic == null) {
            errorLabel.setText("Assign the repair to a mechanic first.");
            return;
        }

        int bossId = SessionContext.getUserId();
        if (bossId <= 0) {
            errorLabel.setText("Session not found. Please login again.");
            return;
        }

        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();
        if (notes.isEmpty()) {
            errorLabel.setText("Write some notes before creating the repair.");
            return;
        }

        try {
            RepairOrderDao repairOrderDao = new RepairOrderDao();
            repairOrderDao.createRepairOrder(
                    selectedVehicle.getId(),
                    selectedCustomer.getId(),
                    bossId,
                    selectedMechanic.getId(),
                    notes
            );

            goTo(event, "/views/boss-repairs-view.fxml");

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error creating the repair.");
        }
    }

    /**
     * Navigates to a given FXML view by replacing the current scene.
     * <p>
     * This method is used by this controller to move between screens.
     * If the main stylesheet exists, it is applied to the loaded scene.
     * </p>
     *
     * @param event the action event that triggered navigation
     * @param fxmlPath the target FXML path
     * @throws Exception if the FXML file or resources cannot be loaded
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
     * Small helper model used to display an ID + name pair inside combo boxes and dialogs.
     * <p>
     * The {@link #toString()} method returns the name so JavaFX controls show the name
     * by default while keeping the ID available for database operations.
     * </p>
     */
    public static class IdName {
        private final int id;
        private final String name;

        /**
         * Creates a new ID/name pair.
         *
         * @param id the database identifier
         * @param name the display name to show in the UI
         */
        public IdName(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Returns the identifier of this item.
         *
         * @return the ID value
         */
        public int getId() { return id; }

        /**
         * Returns the display name of this item.
         *
         * @return the name value
         */
        public String getName() { return name; }

        /**
         * Returns the string representation used by JavaFX controls.
         *
         * @return the name value
         */
        @Override
        public String toString() { return name; }
    }
}
