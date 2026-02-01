package dealership.controllers;

import dealership.dao.SaleDao;
import dealership.model.SaleDetail;
import dealership.util.SalesNavigation;
import dealership.util.SelectedSaleContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

/**
 * Controller for Sales -> Sale detail screen.
 * <p>
 * It loads a single sale from the database and displays its information
 * (customer, vehicle, price, date and notes). The selected sale id is read
 * from {@link SelectedSaleContext}.
 * </p>
 */
public class SalesSaleDetailController {

    @FXML private Label customerValue;
    @FXML private Label vehicleValue;
    @FXML private Label priceValue;
    @FXML private Label saleDateValue;
    @FXML private Label notesValue;

    private final SaleDao saleDao = new SaleDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It reads the selected sale id from {@link SelectedSaleContext} and loads
     * the sale data. If there is no selected id, it navigates back to the sales list.
     * </p>
     */
    @FXML
    private void initialize() {
        Integer saleId = SelectedSaleContext.getSaleId();
        if (saleId == null) {
            showError("Navigation error", "No sale selected.");
            SalesNavigation.loadCenter("/views/sales-sales-view.fxml");
            return;
        }

        loadSale(saleId);
    }

    /**
     * Loads sale details from the database and updates the UI.
     * <p>
     * If the sale does not exist, the user is redirected back to the sales list screen.
     * </p>
     *
     * @param saleId the sale identifier
     */
    private void loadSale(int saleId) {
        try {
            SaleDetail s = saleDao.findSaleDetailById(saleId);
            if (s == null) {
                showError("Not found", "Sale not found (ID: " + saleId + ").");
                SalesNavigation.loadCenter("/views/sales-sales-view.fxml");
                return;
            }

            customerValue.setText(safeText(s.getCustomerName()));
            vehicleValue.setText(safeText(s.getVehicleText()));
            priceValue.setText(s.getPrice() != null ? s.getPrice().stripTrailingZeros().toPlainString() : "-");

            if (s.getSaleDate() != null) {
                saleDateValue.setText(s.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                saleDateValue.setText("-");
            }

            notesValue.setText((s.getNotes() == null || s.getNotes().isBlank()) ? "-" : s.getNotes());

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load sale details.");
        }
    }

    /**
     * Handles the back action.
     * <p>
     * Clears the selected sale context and navigates back to the sales list screen.
     * </p>
     */
    @FXML
    private void handleBack() {
        SelectedSaleContext.clear();
        SalesNavigation.loadCenter("/views/sales-sales-view.fxml");
    }

    /**
     * Returns a safe display value for labels.
     *
     * @param v the original value
     * @return "-" if the value is null/blank, otherwise the original value
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
}
