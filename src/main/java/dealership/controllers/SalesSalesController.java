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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Sales -> Sales list screen.
 *
 * <p>This controller loads completed sales from the database, displays them in a table,
 * and provides user interactions such as searching and opening a sale detail view.</p>
 */
public class SalesSalesController {

    @FXML private TextField searchField;
    @FXML private TableView<SalesSaleRow> salesTable;

    @FXML private TableColumn<SalesSaleRow, String> saleIdCol;
    @FXML private TableColumn<SalesSaleRow, String> vehicleCol;
    @FXML private TableColumn<SalesSaleRow, String> customerCol;
    @FXML private TableColumn<SalesSaleRow, String> priceCol;
    @FXML private TableColumn<SalesSaleRow, LocalDate> saleDateCol;
    @FXML private TableColumn<SalesSaleRow, SalesSaleRow> actionCol;

    private final SaleDao saleDao = new SaleDao();
    private final ObservableList<SalesSaleRow> data = FXCollections.observableArrayList();

    // Puedes cambiar el formato si el profe lo quiere en ES:
    // private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method configures the table layout, sets up column bindings and cell rendering,
     * loads the initial sales data from the database, and enables the search filter.</p>
     */
    @FXML
    private void initialize() {
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureColumns();
        loadData();
        configureSearch();
    }

    /**
     * Configures table columns, including value bindings, date formatting, and action buttons.
     *
     * <p>The sale date column is intentionally bound to a {@link LocalDate} value to preserve
     * correct sorting behavior, while the cell factory formats the date for display.</p>
     *
     * <p>The action column provides a "View" button per row to open the sale detail screen.</p>
     */
    private void configureColumns() {
        saleIdCol.setCellValueFactory(cell -> cell.getValue().saleCodeProperty());
        vehicleCol.setCellValueFactory(cell -> cell.getValue().vehicleProperty());
        customerCol.setCellValueFactory(cell -> cell.getValue().customerProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());

        // IMPORTANT: use LocalDate for correct sorting
        saleDateCol.setCellValueFactory(cell -> cell.getValue().saleDateProperty());

        // Format how the date is shown in the cell
        saleDateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("-");
                } else {
                    setText(DATE_FMT.format(item));
                }
            }
        });

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
     * Loads all sales rows from the database and updates the table data source.
     *
     * <p>This method retrieves the list of completed sales available to the Sales module
     * and replaces the current observable list content. If the data cannot be loaded,
     * an error dialog is shown.</p>
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
     * Enables dynamic filtering of sales based on the search field input.
     *
     * <p>The filter matches the user query against customer name, vehicle description,
     * sale code, price text, and a formatted sale date string. The table remains sortable
     * by binding a {@link SortedList} comparator to the table comparator.</p>
     */
    private void configureSearch() {
        FilteredList<SalesSaleRow> filtered = new FilteredList<>(data, s -> true);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String text = (newV == null) ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(row -> {
                if (text.isEmpty()) return true;

                String dateText = formatDateForSearch(row.getSaleDate()).toLowerCase();

                return safe(row.getCustomer()).contains(text)
                        || safe(row.getVehicle()).contains(text)
                        || safe(row.getSaleCode()).contains(text)
                        || safe(row.getPrice()).contains(text)
                        || dateText.contains(text);
            });
        });

        SortedList<SalesSaleRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(salesTable.comparatorProperty());
        salesTable.setItems(sorted);
    }

    /**
     * Formats a {@link LocalDate} into the same textual representation used in the table,
     * so the date can be matched consistently during searches.
     *
     * @param d the date to format
     * @return a formatted date string, or "-" if the date is {@code null}
     */
    private String formatDateForSearch(LocalDate d) {
        return (d == null) ? "-" : DATE_FMT.format(d);
    }

    /**
     * Opens the sale detail view for the selected sale row.
     *
     * <p>The selected sale ID is stored in a shared context so the detail screen
     * can load the correct sale from the database.</p>
     *
     * @param row the selected sale row
     */
    private void onView(SalesSaleRow row) {
        SelectedSaleContext.setSaleId(row.getId());
        SalesNavigation.loadCenter("/views/sales-sale-detail-view.fxml");
    }

    /**
     * Safely converts a string to lowercase, returning an empty string
     * if the input value is {@code null}.
     *
     * <p>This helper method keeps the search predicate null-safe and consistent.</p>
     *
     * @param s the input string
     * @return a lowercase string or an empty string if {@code null}
     */
    private String safe(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    /**
     * Displays a modal error dialog to the user.
     *
     * <p>This method is used to report database and loading issues
     * encountered while populating the sales list screen.</p>
     *
     * @param title the dialog title
     * @param msg the error message to display
     */
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

