package dealership.controllers;

import dealership.dao.CustomerDao;
import dealership.model.SalesCustomerRow;
import dealership.util.SalesNavigation;
import dealership.util.SelectedCustomerContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for the Sales customers list screen.
 * <p>
 * This controller loads customers from the database and shows them in a table.
 * It also provides a simple search by name, and actions to view a customer
 * or create a new one.
 * </p>
 */
public class SalesCustomersController {

    @FXML private TextField searchField;

    @FXML private TableView<SalesCustomerRow> customersTable;
    @FXML private TableColumn<SalesCustomerRow, String> nameCol;
    @FXML private TableColumn<SalesCustomerRow, String> emailCol;
    @FXML private TableColumn<SalesCustomerRow, String> phoneCol;
    @FXML private TableColumn<SalesCustomerRow, SalesCustomerRow> actionCol;

    private final CustomerDao customerDao = new CustomerDao();
    private final ObservableList<SalesCustomerRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It configures the table layout, sets up the columns, loads initial data
     * from the database and enables the search filtering.
     * </p>
     */
    @FXML
    private void initialize() {
        customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configureColumns();
        loadData();
        configureSearch();
    }

    /**
     * Configures the table columns and the action column behavior.
     * <p>
     * The action column renders a "View" button per row, which opens the
     * customer detail screen for the selected customer.
     * </p>
     */
    private void configureColumns() {
        nameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(e -> {
                    SalesCustomerRow row = getItem();
                    if (row != null) {
                        onViewCustomer(row);
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
            protected void updateItem(SalesCustomerRow item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || item == null) ? null : viewButton);
            }
        });
    }

    /**
     * Loads customers from the database and fills the table data list.
     * <p>
     * If the load fails, an error dialog is shown and the table remains
     * with whatever data it had before.
     * </p>
     */
    private void loadData() {
        try {
            List<SalesCustomerRow> list = customerDao.findAllCustomersForSales();
            data.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load customers from database.");
        }
    }

    /**
     * Configures the search field to filter the customers table by name.
     * <p>
     * Filtering is performed in memory using a {@link FilteredList} and a
     * {@link SortedList} so table sorting still works.
     * </p>
     */
    private void configureSearch() {
        FilteredList<SalesCustomerRow> filtered = new FilteredList<>(data, v -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String queryText = (newValue == null) ? "" : newValue.trim().toLowerCase();

            filtered.setPredicate(row -> {
                if (queryText.isEmpty()) return true;

                String nameText = (row.getFullName() != null) ? row.getFullName().toLowerCase() : "";
                return nameText.contains(queryText);
            });
        });

        SortedList<SalesCustomerRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(customersTable.comparatorProperty());
        customersTable.setItems(sorted);
    }

    /**
     * Opens the customer detail screen for the given row.
     * <p>
     * It stores the selected customer ID in {@link SelectedCustomerContext}
     * and navigates to the detail view in the sales center area.
     * </p>
     *
     * @param row the selected customer row
     */
    private void onViewCustomer(SalesCustomerRow row) {
        SelectedCustomerContext.setCustomerId(row.getId());
        SalesNavigation.loadCenter("/views/sales-customer-detail-view.fxml");
    }

    /**
     * Handles the "New customer" action.
     * <p>
     * Navigates to the new customer form screen.
     * </p>
     */
    @FXML
    private void handleNewCustomer() {
        SalesNavigation.loadCenter("/views/sales-customer-new-view.fxml");
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
