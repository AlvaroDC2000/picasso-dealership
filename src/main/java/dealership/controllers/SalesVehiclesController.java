package dealership.controllers;

import dealership.dao.VehicleDao;
import dealership.model.SalesVehicleRow;
import dealership.util.SalesNavigation;
import dealership.util.SelectedVehicleContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Sales vehicles list screen.
 * <p>
 * This controller loads vehicles from the database and displays them in a table.
 * It supports searching by plate or vehicle text, and allows opening a detail
 * screen for a selected vehicle using a "View" button per row.
 * </p>
 */
public class SalesVehiclesController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<SalesVehicleRow> vehiclesTable;

    @FXML
    private TableColumn<SalesVehicleRow, String> plateCol;

    @FXML
    private TableColumn<SalesVehicleRow, String> vehicleCol;

    @FXML
    private TableColumn<SalesVehicleRow, LocalDate> dateAddedCol;

    @FXML
    private TableColumn<SalesVehicleRow, SalesVehicleRow> actionCol;

    private final VehicleDao vehicleDao = new VehicleDao();
    private final ObservableList<SalesVehicleRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It configures the table columns, loads data from the database and
     * sets up the search filtering for the table.
     * </p>
     */
    @FXML
    private void initialize() {
        vehiclesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configureColumns();
        loadData();
        configureSearch();
    }

    /**
     * Configures the table columns and their cell factories.
     * <p>
     * It binds the main columns to the row properties, formats the date column,
     * and creates an action column with a "View" button per row.
     * </p>
     */
    private void configureColumns() {
        plateCol.setCellValueFactory(cellData -> cellData.getValue().plateProperty());
        vehicleCol.setCellValueFactory(cellData -> cellData.getValue().vehicleProperty());

        dateAddedCol.setCellValueFactory(cellData -> cellData.getValue().dateAddedProperty());
        dateAddedCol.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            /**
             * Updates the date cell text using dd/MM/yyyy format.
             *
             * @param item the date value
             * @param empty whether the cell is empty
             */
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.format(formatter));
            }
        });

        actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(e -> {
                    SalesVehicleRow row = getItem();
                    if (row != null) {
                        onViewVehicle(row);
                    }
                });
            }

            /**
             * Updates the action cell graphic.
             * <p>
             * Shows the "View" button only when the row is not empty.
             * </p>
             *
             * @param item the row item
             * @param empty whether the cell is empty
             */
            @Override
            protected void updateItem(SalesVehicleRow item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || item == null) ? null : viewButton);
            }
        });
    }

    /**
     * Loads vehicles from the database and fills the table data list.
     * <p>
     * If the load fails, an error dialog is shown.
     * </p>
     */
    private void loadData() {
        try {
            List<SalesVehicleRow> list = vehicleDao.findAllVehiclesForSales();
            data.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load vehicles from database.");
        }
    }

    /**
     * Configures the search field to filter the vehicles table.
     * <p>
     * Filtering is performed by checking if the query appears in the vehicle
     * text or the plate text. Sorting remains available through a {@link SortedList}.
     * </p>
     */
    private void configureSearch() {
        FilteredList<SalesVehicleRow> filtered = new FilteredList<>(data, v -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String queryText = (newValue == null) ? "" : newValue.trim().toLowerCase();

            filtered.setPredicate(row -> {
                if (queryText.isEmpty()) return true;

                String vehicleText = (row.getVehicle() != null) ? row.getVehicle().toLowerCase() : "";
                String plateText = (row.getPlate() != null) ? row.getPlate().toLowerCase() : "";

                return vehicleText.contains(queryText) || plateText.contains(queryText);
            });
        });

        SortedList<SalesVehicleRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(vehiclesTable.comparatorProperty());
        vehiclesTable.setItems(sorted);
    }

    /**
     * Opens the vehicle detail screen for the given row.
     * <p>
     * It stores the selected vehicle ID in {@link SelectedVehicleContext}
     * and navigates to the vehicle detail view.
     * </p>
     *
     * @param row the selected vehicle row
     */
    private void onViewVehicle(SalesVehicleRow row) {
        SelectedVehicleContext.setVehicleId(row.getId());
        SalesNavigation.loadCenter("/views/sales-vehicle-detail-view.fxml");
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

