package dealership.controllers;

import dealership.dao.SaleDao;
import dealership.model.SalesSaleRow;
import dealership.util.SalesNavigation;
import dealership.util.SelectedSaleContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for Sales -> Sales list screen.
 * <p>
 * It loads sales from the database and shows them in a table with:
 * - live search filtering (customer, vehicle, sale code, price)
 * - navigation to the Sale detail screen
 * </p>
 */
public class SalesSalesController {

    @FXML private TextField searchField;
    @FXML private TableView<SalesSaleRow> salesTable;

    @FXML private TableColumn<SalesSaleRow, String> saleIdCol;
    @FXML private TableColumn<SalesSaleRow, String> vehicleCol;
    @FXML private TableColumn<SalesSaleRow, String> customerCol;
    @FXML private TableColumn<SalesSaleRow, String> priceCol;
    @FXML private TableColumn<SalesSaleRow, SalesSaleRow> actionCol;

    private final SaleDao saleDao = new SaleDao();
    private final ObservableList<SalesSaleRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It configures table columns, loads the initial sales list,
     * and enables the search filtering.
     * </p>
     */
    @FXML
    private void initialize() {
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureColumns();
        loadData();
        configureSearch();
    }

    /**
     * Configures the table columns and adds the action column (View button).
     * <p>
     * The action column uses a custom cell with a button that opens the sale detail view.
     * </p>
     */
    private void configureColumns() {
        saleIdCol.setCellValueFactory(cell -> cell.getValue().saleCodeProperty());
        vehicleCol.setCellValueFactory(cell -> cell.getValue().vehicleProperty());
        customerCol.setCellValueFactory(cell -> cell.getValue().customerProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());

        actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(e -> {
                    SalesSaleRow row = getItem();
                    if (row != null) onView(row);
                });
            }

            @Override
            protected void updateItem(SalesSaleRow item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || item == null) ? null : viewButton);
            }
        });
    }

    /**
     * Loads sales from the database and updates the table data list.
     * <p>
     * If the query fails, an error dialog is displayed.
     * </p>
     */
    private void loadData() {
        try {
            List<SalesSaleRow> list = saleDao.findAllSalesForSales();
            data.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load sales.");
        }
    }

    /**
     * Configures the search field to filter the table rows in real time.
     * <p>
     * Filtering runs locally on the loaded list using a {@link FilteredList}
     * and is sorted using a {@link SortedList} bound to the table comparator.
     * </p>
     */
    private void configureSearch() {
        FilteredList<SalesSaleRow> filtered = new FilteredList<>(data, s -> true);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String text = (newV == null) ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(row -> {
                if (text.isEmpty()) return true;
                return safe(row.getCustomer()).contains(text)
                        || safe(row.getVehicle()).contains(text)
                        || safe(row.getSaleCode()).contains(text)
                        || safe(row.getPrice()).contains(text);
            });
        });

        SortedList<SalesSaleRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(salesTable.comparatorProperty());
        salesTable.setItems(sorted);
    }

    /**
     * Opens the Sale detail screen for the selected row.
     * <p>
     * The sale id is stored in {@link SelectedSaleContext} for the detail controller.
     * </p>
     *
     * @param row the selected sale row
     */
    private void onView(SalesSaleRow row) {
        SelectedSaleContext.setSaleId(row.getId());
        SalesNavigation.loadCenter("/views/sales-sale-detail-view.fxml");
    }

    /**
     * Returns a safe lowercase string for search comparisons.
     *
     * @param s the original string
     * @return lowercase value, or empty string if null
     */
    private String safe(String s) {
        return (s == null) ? "" : s.toLowerCase();
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

