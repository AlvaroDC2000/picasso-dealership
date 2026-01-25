package dealership.controllers;

import dealership.dao.VehicleDao;
import dealership.model.VehicleDetail;
import dealership.util.SalesNavigation;
import dealership.util.SelectedVehicleContext;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller for the Sales vehicle detail screen.
 * <p>
 * This controller displays detailed information about a selected vehicle in the
 * sales workflow. The vehicle ID is retrieved from {@link SelectedVehicleContext},
 * then the details are loaded from the database through {@link VehicleDao} and
 * displayed in the corresponding labels.
 * </p>
 */
public class SalesVehicleDetailController {

    // Left
    @FXML private Label plateValue;
    @FXML private Label brandValue;
    @FXML private Label modelValue;
    @FXML private Label yearValue;
    @FXML private Label colorValue;
    @FXML private Label mileageValue;

    // Right (Figma)
    @FXML private Label typeValue;
    @FXML private Label fuelValue;
    @FXML private Label transmissionValue;
    @FXML private Label doorsValue;
    @FXML private Label dateAddedValue;

    // Notes
    @FXML private Label notesValue;

    private final VehicleDao vehicleDao = new VehicleDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It checks whether a vehicle ID is available in the navigation context.
     * If not, it shows an error and returns to the vehicles list screen.
     * </p>
     */
    @FXML
    private void initialize() {
        Integer vehicleId = SelectedVehicleContext.getVehicleId();

        if (vehicleId == null) {
            showError("Navigation error", "No vehicle selected.");
            SalesNavigation.loadCenter("/views/sales-vehicles-view.fxml");
            return;
        }

        loadVehicle(vehicleId);
    }

    /**
     * Loads a vehicle detail from the database and updates the UI.
     * <p>
     * If the vehicle is not found, the user is redirected to the vehicles list screen.
     * Any database error will display an error dialog.
     * </p>
     *
     * @param vehicleId the vehicle identifier to load
     */
    private void loadVehicle(int vehicleId) {
        try {
            VehicleDetail vehicle = vehicleDao.findVehicleDetailById(vehicleId);

            if (vehicle == null) {
                showError("Not found", "Vehicle not found (ID: " + vehicleId + ").");
                SalesNavigation.loadCenter("/views/sales-vehicles-view.fxml");
                return;
            }

            // Left
            plateValue.setText(safeText(vehicle.getPlate()));
            brandValue.setText(safeText(vehicle.getBrand()));
            modelValue.setText(safeText(vehicle.getModel()));
            yearValue.setText(vehicle.getYear() != null ? String.valueOf(vehicle.getYear()) : "-");
            colorValue.setText(safeText(vehicle.getColor()));
            mileageValue.setText(formatMileageKm(vehicle.getMileage()));

            // Right
            typeValue.setText(safeText(vehicle.getType()));
            fuelValue.setText(safeText(vehicle.getFuel()));
            transmissionValue.setText(safeText(vehicle.getTransmission()));
            doorsValue.setText(vehicle.getDoors() != null ? String.valueOf(vehicle.getDoors()) : "-");
            dateAddedValue.setText(formatDate(vehicle.getEntryDate()));

            // Notes
            notesValue.setText((vehicle.getNotes() == null || vehicle.getNotes().isBlank()) ? "-" : vehicle.getNotes());

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load vehicle details.");
        }
    }

    /**
     * Handles the back action from the vehicle detail screen.
     * <p>
     * Clears the selected vehicle context and navigates back to the vehicles list view.
     * </p>
     */
    @FXML
    private void handleBack() {
        SelectedVehicleContext.clear();
        SalesNavigation.loadCenter("/views/sales-vehicles-view.fxml");
    }

    /**
     * Returns a safe text value for UI display.
     * <p>
     * If the provided value is null or blank, a dash ("-") is returned.
     * </p>
     *
     * @param value the original text value
     * @return a safe, non-empty text for display
     */
    private String safeText(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    /**
     * Formats a mileage integer as a string with thousands separators and "km" suffix.
     *
     * @param mileage the mileage value in kilometers
     * @return formatted mileage string, or "-" if null
     */
    private String formatMileageKm(Integer mileage) {
        if (mileage == null) return "-";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
        return nf.format(mileage) + " km";
    }

    /**
     * Formats a {@link LocalDate} to dd/MM/yyyy.
     *
     * @param date the date to format
     * @return formatted date string, or "-" if null
     */
    private String formatDate(LocalDate date) {
        if (date == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
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

