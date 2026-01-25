package dealership.controllers;

import dealership.dao.CustomerDao;
import dealership.model.CustomerDetail;
import dealership.util.SalesNavigation;
import dealership.util.SelectedCustomerContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Controller for the Sales customer detail screen.
 * <p>
 * This controller is responsible for displaying detailed information
 * about a selected customer in the sales workflow. It retrieves the
 * customer ID from {@link SelectedCustomerContext}, loads the data
 * from the database and fills the view labels.
 * </p>
 */
public class SalesCustomerDetailController {

    @FXML private Label fullNameValue;
    @FXML private Label phoneValue;
    @FXML private Label emailValue;

    private final CustomerDao customerDao = new CustomerDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It checks whether a customer ID is available in the navigation
     * context. If not, the user is redirected back to the customer list
     * and an error message is shown.
     * </p>
     */
    @FXML
    private void initialize() {
        Integer customerId = SelectedCustomerContext.getCustomerId();

        if (customerId == null) {
            showError("Navigation error", "No customer selected.");
            SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
            return;
        }

        loadCustomer(customerId);
    }

    /**
     * Loads the customer details from the database and updates the UI.
     * <p>
     * If the customer does not exist or cannot be loaded, an error
     * message is shown and the user is returned to the customers list.
     * </p>
     *
     * @param customerId the identifier of the customer to load
     */
    private void loadCustomer(int customerId) {
        try {
            CustomerDetail customer = customerDao.findCustomerDetailById(customerId);

            if (customer == null) {
                showError("Not found", "Customer not found (ID: " + customerId + ").");
                SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
                return;
            }

            fullNameValue.setText(safeText(customer.getFullName()));
            phoneValue.setText(safeText(customer.getPhone()));
            emailValue.setText(safeText(customer.getEmail()));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load customer details.");
        }
    }

    /**
     * Handles the back action from the customer detail screen.
     * <p>
     * Clears the selected customer context and navigates back
     * to the customers list view.
     * </p>
     */
    @FXML
    private void handleBack() {
        SelectedCustomerContext.clear();
        SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
    }

    /**
     * Returns a safe text value for UI display.
     * <p>
     * If the provided value is null or blank, a dash ("-") is returned
     * instead to avoid empty labels.
     * </p>
     *
     * @param value the original text value
     * @return a safe, non-empty text for display
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    /**
     * Shows an error dialog with the given title and message.
     *
     * @param title the dialog title
     * @param message the message to display
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
