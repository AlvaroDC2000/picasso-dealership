package dealership.controllers;

import dealership.dao.ProposalDao;
import dealership.dao.SaleDao;
import dealership.model.ProposalDetail;
import dealership.util.SalesNavigation;
import dealership.util.SelectedProposalContext;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller for Sales -> Proposal detail screen.
 * <p>
 * This screen displays proposal information and allows the user to:
 * - View proposal details
 * - Edit price/notes (if not accepted)
 * - Delete the proposal (only if it is not already accepted)
 * - Accept the proposal and register a sale
 * </p>
 */
public class SalesProposalDetailController {

    // Read-only display
    @FXML private Label customerValue;
    @FXML private Label vehicleValue;

    // Editable (but stable sizes)
    @FXML private TextField priceField;
    @FXML private TextArea notesArea;

    @FXML private Label statusValue;

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button acceptButton;

    private final ProposalDao proposalDao = new ProposalDao();
    private final SaleDao saleDao = new SaleDao();

    private Integer proposalId;
    private ProposalDetail current;

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It reads the selected proposal id from {@link SelectedProposalContext},
     * loads proposal data, and starts in read-only mode.
     * </p>
     */
    @FXML
    private void initialize() {
        proposalId = SelectedProposalContext.getProposalId();
        if (proposalId == null) {
            showError("Navigation error", "No proposal selected.");
            SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");
            return;
        }

        setEditMode(false);
        loadProposal(proposalId);
    }

    /**
     * Loads proposal data from the database and updates the UI.
     * <p>
     * If the proposal is already accepted, editing is blocked and the accept action
     * is disabled.
     * </p>
     *
     * @param id proposal identifier
     */
    private void loadProposal(int id) {
        try {
            current = proposalDao.findProposalDetailById(id);
            if (current == null) {
                showError("Not found", "Proposal not found (ID: " + id + ").");
                SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");
                return;
            }

            customerValue.setText(safeText(current.getCustomerName()));
            vehicleValue.setText(safeText(current.getVehicleText()));
            priceField.setText(current.getPrice() != null ? current.getPrice().stripTrailingZeros().toPlainString() : "");
            notesArea.setText(current.getNotes() == null ? "" : current.getNotes());
            statusValue.setText(safeText(current.getStatus()));

            // If already accepted -> block edits
            boolean accepted = "ACCEPTED".equalsIgnoreCase(current.getStatus());
            acceptButton.setDisable(accepted);
            editButton.setDisable(accepted);
            saveButton.setDisable(true);
            setEditMode(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load proposal details.");
        }
    }

    /**
     * Handles the back action.
     * <p>
     * Clears the selected proposal context and navigates back to the proposals list.
     * </p>
     */
    @FXML
    private void handleBack() {
        SelectedProposalContext.clear();
        SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");
    }

    /**
     * Enables edit mode so the user can modify price and notes.
     * <p>
     * Save is enabled and edit is disabled until the user finishes.
     * </p>
     */
    @FXML
    private void handleEdit() {
        setEditMode(true);
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }

    /**
     * Saves the edited proposal data.
     * <p>
     * It validates the price value, keeps the current status (default ACTIVE),
     * and updates the proposal through the DAO.
     * </p>
     */
    @FXML
    private void handleSave() {
        if (proposalId == null) return;

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

        String notes = notesArea.getText();

        try {
            // keep status as ACTIVE unless you want other states
            String status = (current != null && current.getStatus() != null) ? current.getStatus() : "ACTIVE";
            proposalDao.updateProposal(proposalId, price, notes, status);

            showInfo("Success", "Proposal updated.");
            setEditMode(false);
            saveButton.setDisable(true);
            editButton.setDisable(false);
            loadProposal(proposalId);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not update proposal.");
        }
    }

    /**
     * Deletes the current proposal after user confirmation.
     * <p>
     * If the proposal has already been accepted and converted into a sale,
     * deletion may fail depending on database constraints/business rules.
     * </p>
     */
    @FXML
    private void handleDelete() {
        if (proposalId == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete this proposal?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            boolean deleted = proposalDao.deleteProposalById(proposalId);

            if (!deleted) {
                showError(
                    "Cannot delete proposal",
                    "This proposal cannot be deleted because it has already been accepted and registered as a sale."
                );
                return;
            }

            showInfo("Success", "Proposal deleted.");
            SelectedProposalContext.clear();
            SalesNavigation.loadCenter("/views/sales-proposals-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not delete proposal.");
        }
    }


    /**
     * Accepts the current proposal and creates a sale from it.
     * <p>
     * The process is:
     * 1) Create a sale record linked to the proposal (proposal_id is unique).
     * 2) Update proposal status to ACCEPTED.
     * After success, the user is redirected to the sales screen.
     * </p>
     */
    @FXML
    private void handleAccept() {
        if (proposalId == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm accept");
        confirm.setHeaderText(null);
        confirm.setContentText("Accept this proposal and register the sale?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            // 1) create sale (proposal_id unique)
            saleDao.createSaleFromProposal(proposalId, LocalDate.now());

            // 2) mark proposal accepted
            proposalDao.setProposalStatus(proposalId, "ACCEPTED");

            showInfo("Success", "Sale created from proposal.");
            SelectedProposalContext.clear();
            SalesNavigation.loadCenter("/views/sales-sales-view.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not accept proposal (maybe it was already accepted).");
        }
    }

    /**
     * Enables or disables edit mode for the editable fields.
     * <p>
     * This method only switches editability and style classes to keep the UI stable
     * (no font size jump). It does not change navigation or button state.
     * </p>
     *
     * @param enabled true to enable editing, false to restore read-only behavior
     */
    private void setEditMode(boolean enabled) {
        // IMPORTANT: do not change font sizes on edit, only enable fields
        priceField.setEditable(enabled);
        notesArea.setEditable(enabled);

        // Optional: keep same background to avoid “jump”
        if (enabled) {
            priceField.getStyleClass().remove("readonly-field");
            notesArea.getStyleClass().remove("readonly-area");
        } else {
            if (!priceField.getStyleClass().contains("readonly-field")) priceField.getStyleClass().add("readonly-field");
            if (!notesArea.getStyleClass().contains("readonly-area")) notesArea.getStyleClass().add("readonly-area");
        }
    }

    /**
     * Returns a safe display text value for labels.
     *
     * @param v the original value
     * @return "-" if null/blank, otherwise the original value
     */
    private String safeText(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
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

