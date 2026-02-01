package dealership.controllers;

import dealership.dao.ProposalDao;
import dealership.model.SalesProposalRow;
import dealership.util.SalesNavigation;
import dealership.util.SelectedProposalContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for Sales -> Proposals list screen.
 * <p>
 * It loads proposals from the database, displays them in a table, and provides:
 * - a search filter (customer, vehicle, proposal code, price)
 * - navigation to proposal detail
 * - navigation to create a new proposal
 * </p>
 */
public class SalesProposalsController {

    @FXML private TextField searchField;
    @FXML private TableView<SalesProposalRow> proposalsTable;

    @FXML private TableColumn<SalesProposalRow, String> proposalIdCol;
    @FXML private TableColumn<SalesProposalRow, String> vehicleCol;
    @FXML private TableColumn<SalesProposalRow, String> customerCol;
    @FXML private TableColumn<SalesProposalRow, String> priceCol;
    @FXML private TableColumn<SalesProposalRow, SalesProposalRow> actionCol;

    private final ProposalDao proposalDao = new ProposalDao();
    private final ObservableList<SalesProposalRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It configures table columns, loads initial data, and enables searching.
     * </p>
     */
    @FXML
    private void initialize() {
        proposalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureColumns();
        loadData();
        configureSearch();
    }

    /**
     * Configures table columns and the action column (View button).
     * <p>
     * The action column uses a custom cell with a button that navigates
     * to the proposal detail screen for the selected row.
     * </p>
     */
    private void configureColumns() {
        proposalIdCol.setCellValueFactory(cell -> cell.getValue().proposalCodeProperty());
        vehicleCol.setCellValueFactory(cell -> cell.getValue().vehicleProperty());
        customerCol.setCellValueFactory(cell -> cell.getValue().customerProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());

        actionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(e -> {
                    SalesProposalRow row = getItem();
                    if (row != null) onView(row);
                });
            }

            @Override
            protected void updateItem(SalesProposalRow item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || item == null) ? null : viewButton);
            }
        });
    }

    /**
     * Loads proposals from the database and fills the table data list.
     * <p>
     * If the database query fails, an error dialog is displayed.
     * </p>
     */
    private void loadData() {
        try {
            List<SalesProposalRow> list = proposalDao.findAllProposalsForSales();
            data.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error", "Could not load proposals.");
        }
    }

    /**
     * Configures the live search filter for the proposals table.
     * <p>
     * Filtering is done locally on the loaded list using a {@link FilteredList}
     * and a {@link SortedList} bound to the table comparator.
     * </p>
     */
    private void configureSearch() {
        FilteredList<SalesProposalRow> filtered = new FilteredList<>(data, p -> true);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            String text = (newV == null) ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(row -> {
                if (text.isEmpty()) return true;
                return safe(row.getCustomer()).contains(text)
                        || safe(row.getVehicle()).contains(text)
                        || safe(row.getProposalCode()).contains(text)
                        || safe(row.getPrice()).contains(text);
            });
        });

        SortedList<SalesProposalRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(proposalsTable.comparatorProperty());
        proposalsTable.setItems(sorted);
    }

    /**
     * Opens the New Proposal screen.
     */
    @FXML
    private void handleNewProposal() {
        SalesNavigation.loadCenter("/views/sales-new-proposal-view.fxml");
    }

    /**
     * Navigates to the proposal detail screen for the selected row.
     * <p>
     * The proposal id is stored in {@link SelectedProposalContext} to be read
     * by the detail controller.
     * </p>
     *
     * @param row the selected proposal row
     */
    private void onView(SalesProposalRow row) {
        SelectedProposalContext.setProposalId(row.getId());
        SalesNavigation.loadCenter("/views/sales-proposal-detail-view.fxml");
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