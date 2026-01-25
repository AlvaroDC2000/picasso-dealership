package dealership.controllers;

import dealership.dao.CustomerDao;
import dealership.util.SalesNavigation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * Controller for the Sales new customer screen.
 * <p>
 * This controller handles the creation of a new customer in the sales
 * workflow. It collects the data entered by the user, performs basic
 * validation, and delegates the persistence logic to {@link CustomerDao}.
 * </p>
 */
public class SalesCustomerNewController {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField dniField;

    private final CustomerDao customerDao = new CustomerDao();

    /**
     * Handles the back action.
     * <p>
     * Navigates back to the sales customers list view without saving
     * any changes.
     * </p>
     */
    @FXML
    private void handleBack() {
        SalesNavigation.loadCenter("/views/sales-customers-view.fxml");
    }

    /**
     * Handles the creation of a new customer.
     * <p>
     * This method reads the form fields, validates the input (empty fields,
     * email format and DNI length), extracts first and last names from the
     * full name, and inserts the customer into the database.
     * </p>
     * <p>
     * On success, a confirmation message is shown and the user is returned
     * to the customers list. On failure, an error dialog is displayed.
     * </p>
     */
    @FXML
    private void handleCreateCustomer() {
        String fullName = safeText(fullNameField.getText());
        String phone = safeText(phoneField.getText());
        String email = safeText(emailField.getText());
        String dni = safeText(dniField.getText()).toUpperCase();

        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || dni.isBlank()) {
            showError("Validation error", "Please fill in all fields.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Validation error", "Please enter a valid email address.");
            return;
        }

        if (dni.length() < 8) {
            showError("Validation error", "Please enter a valid DNI.");
            return;
        }

        String firstName = extractFirstName(fullName);
        String lastName = extractLastName(fullName);

        try {
            customerDao.insertCustomer(dni, firstName, lastName, phone, email);
            showInfo("Customer created", "Customer was created successfully.");
            SalesNavigation.loadCenter("/views/sales-customers-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not create customer. DNI must be unique.");
        }
    }

    /**
     * Extracts the first name from a full name string.
     *
     * @param fullName the full name entered by the user
     * @return the first name part
     */
    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return (parts.length >= 1) ? parts[0] : fullName.trim();
    }

    /**
     * Extracts the last name(s) from a full name string.
     * <p>
     * Everything except the first word is considered part of the last name.
     * </p>
     *
     * @param fullName the full name entered by the user
     * @return the last name portion, or empty string if not present
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
     * Returns a trimmed, non-null string value.
     *
     * @param value the original text
     * @return a safe trimmed string, or empty string if null
     */
    private String safeText(String value) {
        return (value == null) ? "" : value.trim();
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

