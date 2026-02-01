package dealership.controllers;

import dealership.controllers.RegisterRepairController.IdName;
import dealership.dao.CustomerDao;
import dealership.dao.ProposalDao;
import dealership.dao.VehicleDao;
import dealership.util.SalesNavigation;
import dealership.util.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for Sales -> New proposal screen.
 * <p>
 * This screen lets the seller create a new proposal by selecting a customer and a vehicle,
 * entering the offered price, and optionally adding notes.
 * Data is loaded from the database using DAOs and the result is saved as a new proposal record.
 * </p>
 */
public class SalesNewProposalController {

    @FXML private ComboBox<IdName> customerCombo;
    @FXML private ComboBox<IdName> vehicleCombo;
    @FXML private TextField priceField;
    @FXML private TextArea notesArea;

    private final CustomerDao customerDao = new CustomerDao();
    private final VehicleDao vehicleDao = new VehicleDao();
    private final ProposalDao proposalDao = new ProposalDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It loads combo box data for customers and vehicles.
     * </p>
     */
    @FXML
    private void initialize() {
        loadCombos();
    }

    /**
     * Loads the customers and vehicles into the combo boxes.
     * <p>
     * If something fails while reading from the database, an error dialog is shown.
     * </p>
     */
    private void loadCombos() {
        try {
            List<IdName> customers = customerDao.findAllCustomersForCombo();
            customerCombo.getItems().setAll(customers);

            List<IdName> vehicles = vehicleDao.findAllVehiclesForCombo();
            vehicleCombo.getItems().setAll(vehicles);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load customers/vehicles.");
        }
    }

    /**
     * Handles the back action and returns to the proposals list screen.
     */
    @FXML
    private void handleBack() {
        SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");
    }

    /**
     * Creates a new proposal after validating the form fields.
     * <p>
     * It validates selected customer and vehicle, parses the price as {@link BigDecimal},
     * checks session data (user and dealership), and inserts the proposal using {@link ProposalDao}.
     * If session data is missing, the user is redirected to login.
     * </p>
     */
    @FXML
    private void handleCreateProposal() {
        IdName customer = customerCombo.getValue();
        IdName vehicle = vehicleCombo.getValue();

        if (customer == null) {
            showError("Validation", "Please select a customer.");
            return;
        }
        if (vehicle == null) {
            showError("Validation", "Please select a vehicle.");
            return;
        }

        BigDecimal price;
        try {
            String raw = (priceField.getText() == null) ? "" : priceField.getText().trim();
            raw = raw.replace(",", ".");
            price = new BigDecimal(raw);

            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Validation", "Price must be greater than 0.");
                return;
            }
        } catch (Exception ex) {
            showError("Validation", "Invalid price.");
            return;
        }

        Integer sellerUserId = SessionContext.getUserId();
        Integer dealershipId = SessionContext.getDealershipId();

        if (sellerUserId == null || dealershipId == null) {
            showError("Session error", "Missing session data (user/dealership). Please login again.");
            SessionContext.clear();
            // IMPORTANT: replace full scene, do NOT load login in the center stackpane
            SalesNavigation.goToLogin(customerCombo);
            return;
        }

        try {
            proposalDao.insertProposal(
                    customer.getId(),
                    vehicle.getId(),
                    sellerUserId,
                    dealershipId,
                    price,
                    notesArea.getText()
            );

            showInfo("Success", "Proposal created.");
            SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not create proposal.");
        }
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title the dialog title
     * @param msg the message to display
     */
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Shows an information dialog with the given title and message.
     *
     * @param title the dialog title
     * @param msg the message to display
     */
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
