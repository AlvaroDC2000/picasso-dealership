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

            goTo(event, "/views/boss-menu-view.fxml");

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error creating the repair.");
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

    public static class IdName {
        private final int id;
        private final String name;

        public IdName(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        public String getName() { return name; }

        @Override
        public String toString() { return name; }
    }
}

