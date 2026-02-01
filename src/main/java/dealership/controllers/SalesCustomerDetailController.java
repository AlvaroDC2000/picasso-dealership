package dealership.controllers;

import dealership.dao.CustomerDao;
import dealership.model.CustomerDetail;
import dealership.util.SalesNavigation;
import dealership.util.SelectedCustomerContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

/**
 * Controller for the Sales customer detail screen.
 * <p>
 * This screen shows customer information and supports basic CRUD actions:
 * read the data, edit and save changes, or delete the customer.
 * Navigation is handled through {@link SalesNavigation} and the selected
 * customer is provided by {@link SelectedCustomerContext}.
 * </p>
 */
public class SalesCustomerDetailController {

    // View labels
    @FXML private Label fullNameValue;
    @FXML private Label phoneValue;
    @FXML private Label emailValue;

    // Edit fields
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    // Buttons
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button deleteButton;

    private final CustomerDao customerDao = new CustomerDao();

    private Integer customerId;
    private CustomerDetail currentCustomer;
    @SuppressWarnings("unused")
	private boolean editMode = false;

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It reads the selected customer id from {@link SelectedCustomerContext},
     * loads the customer information from the database, and starts in view mode.
     * </p>
     */
    @FXML
    private void initialize() {
        customerId = SelectedCustomerContext.getCustomerId();

        if (customerId == null) {
            showError("Navigation error", "No customer selected.");
            SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
            return;
        }

        loadCustomer(customerId);
        setEditMode(false);
    }

    /**
     * Loads the customer information for the given id and updates the UI.
     * <p>
     * It fills both the read-only labels and the editable text fields.
     * If the customer is not found, the user is redirected back to the list.
     * </p>
     *
     * @param id the customer identifier
     */
    private void loadCustomer(int id) {
        try {
            currentCustomer = customerDao.findCustomerDetailById(id);

            if (currentCustomer == null) {
                showError("Not found", "Customer not found (ID: " + id + ").");
                SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
                return;
            }

            fullNameValue.setText(safeText(currentCustomer.getFullName()));
            phoneValue.setText(safeText(currentCustomer.getPhone()));
            emailValue.setText(safeText(currentCustomer.getEmail()));

            // Pre-fill edit fields
            fullNameField.setText(safeTextForEdit(currentCustomer.getFullName()));
            phoneField.setText(safeTextForEdit(currentCustomer.getPhone()));
            emailField.setText(safeTextForEdit(currentCustomer.getEmail()));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load customer details.");
        }
    }

    /**
     * Handles the back action.
     * <p>
     * Clears the selected customer context and navigates back to the customers list.
     * </p>
     */
    @FXML
    private void handleBack() {
        SelectedCustomerContext.clear();
        SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
    }

    /**
     * Enables edit mode for the customer details screen.
     * <p>
     * This will show the text fields and the save/cancel buttons.
     * </p>
     */
    @FXML
    private void handleEdit() {
        setEditMode(true);
    }

    /**
     * Cancels edit mode and restores the original customer values.
     * <p>
     * The editable fields are reset from the last loaded {@code currentCustomer}
     * and the screen returns to view mode.
     * </p>
     */
    @FXML
    private void handleCancel() {
        // Restore fields from current customer
        if (currentCustomer != null) {
            fullNameField.setText(safeTextForEdit(currentCustomer.getFullName()));
            phoneField.setText(safeTextForEdit(currentCustomer.getPhone()));
            emailField.setText(safeTextForEdit(currentCustomer.getEmail()));
        }
        setEditMode(false);
    }

    /**
     * Saves changes made to the customer.
     * <p>
     * It validates the fields, splits the full name into first and last name,
     * and calls the DAO update. On success, it reloads the customer and exits edit mode.
     * </p>
     */
    @FXML
    private void handleSave() {
        String fullName = safeTextForEdit(fullNameField.getText());
        String phone = safeTextForEdit(phoneField.getText());
        String email = safeTextForEdit(emailField.getText());

        if (fullName.isBlank() || phone.isBlank() || email.isBlank()) {
            showError("Validation error", "Please fill in all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Validation error", "Please enter a valid email address.");
            return;
        }

        String firstName = extractFirstName(fullName);
        String lastName = extractLastName(fullName);

        try {
            boolean updated = customerDao.updateCustomer(customerId, firstName, lastName, phone, email);

            if (!updated) {
                showError("Update failed", "Customer could not be updated.");
                return;
            }

            showInfo("Customer updated", "Customer data was updated successfully.");
            loadCustomer(customerId);
            setEditMode(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not update customer.");
        }
    }

    /**
     * Deletes the current customer after user confirmation.
     * <p>
     * A confirmation dialog is shown first. If the delete succeeds, the context
     * is cleared and the user is redirected back to the customers list.
     * </p>
     */
    @FXML
    private void handleDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete customer");
        confirm.setHeaderText("Are you sure you want to delete this customer?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = customerDao.deleteCustomerById(customerId);

            if (!deleted) {
                showError("Delete failed", "Customer could not be deleted.");
                return;
            }

            showInfo("Customer deleted", "Customer was deleted successfully.");
            SelectedCustomerContext.clear();
            SalesNavigation.loadCenter("/views/sales-customers-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not delete customer. It may be referenced in other records.");
        }
    }

    /**
     * Enables or disables edit mode and updates UI visibility accordingly.
     * <p>
     * When edit mode is enabled, the labels are hidden and the fields are shown.
     * The delete button is disabled while editing to avoid risky actions mid-edit.
     * </p>
     *
     * @param enabled true to enable edit mode, false to return to view mode
     */
    private void setEditMode(boolean enabled) {
        editMode = enabled;

        // Toggle view vs edit controls
        fullNameValue.setVisible(!enabled);
        fullNameValue.setManaged(!enabled);
        phoneValue.setVisible(!enabled);
        phoneValue.setManaged(!enabled);
        emailValue.setVisible(!enabled);
        emailValue.setManaged(!enabled);

        fullNameField.setVisible(enabled);
        fullNameField.setManaged(enabled);
        phoneField.setVisible(enabled);
        phoneField.setManaged(enabled);
        emailField.setVisible(enabled);
        emailField.setManaged(enabled);

        // Buttons
        editButton.setVisible(!enabled);
        editButton.setManaged(!enabled);

        saveButton.setVisible(enabled);
        saveButton.setManaged(enabled);

        cancelButton.setVisible(enabled);
        cancelButton.setManaged(enabled);

        // Optional: disable delete while editing (safer UX)
        deleteButton.setDisable(enabled);
    }

    /**
     * Extracts the first name from a full name string.
     *
     * @param fullName the full name entered by the user
     * @return the first name portion
     */
    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return (parts.length >= 1) ? parts[0] : fullName.trim();
    }

    /**
     * Extracts the last name(s) from a full name string.
     * <p>
     * Everything except the first token is treated as last name.
     * </p>
     *
     * @param fullName the full name entered by the user
     * @return the last name portion, or empty string if missing
     */
    private String extractLastName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length <= 1) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i]);
            if (i < parts.length - 1) sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Returns a safe text value for read-only labels.
     * <p>
     * If the value is null or blank, "-" is returned.
     * </p>
     *
     * @param value the original text
     * @return a safe display value
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    /**
     * Returns a safe text value for edit fields.
     * <p>
     * It trims the value and converts "-" back to an empty string
     * so the user can edit it normally.
     * </p>
     *
     * @param value the original text
     * @return a safe editable text value
     */
    private String safeTextForEdit(String value) {
        if (value == null) return "";
        String t = value.trim();
        return t.equals("-") ? "" : t;
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

    /**
     * Shows an information dialog with the given title and message.
     *
     * @param title the dialog title
     * @param message the message to display
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
