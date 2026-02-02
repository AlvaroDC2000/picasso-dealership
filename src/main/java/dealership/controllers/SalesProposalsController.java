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
 *
 * <p>It loads proposals from the database, displays them in a table, and provides:
 * - a search filter (customer, vehicle, proposal code, price, status)
 * - navigation to proposal detail
 * - navigation to create a new proposal
 * </p>
 *
 * <p>This controller acts as the main entry point for managing and browsing
 * sales proposals within the Sales module.</p>
 */
public class SalesProposalsController {

    @FXML private TextField searchField;
    @FXML private TableView<SalesProposalRow> proposalsTable;

    @FXML private TableColumn<SalesProposalRow, String> proposalIdCol;
    @FXML private TableColumn<SalesProposalRow, String> vehicleCol;
    @FXML private TableColumn<SalesProposalRow, String> customerCol;
    @FXML private TableColumn<SalesProposalRow, String> priceCol;
    @FXML private TableColumn<SalesProposalRow, String> statusCol;
    @FXML private TableColumn<SalesProposalRow, SalesProposalRow> actionCol;

    private final ProposalDao proposalDao = new ProposalDao();
    private final ObservableList<SalesProposalRow> data = FXCollections.observableArrayList();

    /**
     * Initializes the controller after the FXML has been loaded.
     *
     * <p>This method configures the table layout, initializes column bindings,
     * applies row styling rules, loads proposal data from the database,
     * and sets up the search filtering behavior.</p>
     */
    @FXML
    private void initialize() {
        proposalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configureColumns();
        configureRowHighlight();
        loadData();
        configureSearch();
    }

    /**
     * Configures all table columns, including value bindings and custom cell factories.
     *
     * <p>This method defines how proposal data is displayed, including
     * formatting of status values and creation of action buttons
     * for navigating to proposal details.</p>
     */
    private void configureColumns() {
        proposalIdCol.setCellValueFactory(cell -> cell.getValue().proposalCodeProperty());
        vehicleCol.setCellValueFactory(cell -> cell.getValue().vehicleProperty());
        customerCol.setCellValueFactory(cell -> cell.getValue().customerProperty());
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                String s = item.trim().toUpperCase();
                setText("ACTIVE".equals(s) ? "Active" : "Inactive");

                if ("ACTIVE".equals(s)) {
                    setStyle("-fx-text-fill: #16A34A; -fx-font-weight: 700;");
                } else {
                    setStyle("-fx-text-fill: #6B7280; -fx-font-weight: 700;");
                }
            }
        });

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
     * Applies visual highlighting to table rows based on proposal status.
     *
     * <p>Rows representing active proposals are styled differently
     * from inactive ones using predefined CSS classes.</p>
     */
    private void configureRowHighlight() {
        proposalsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(SalesProposalRow row, boolean empty) {
                super.updateItem(row, empty);

                getStyleClass().removeAll("proposal-active-row", "proposal-inactive-row");

                if (empty || row == null) return;

                String s = (row.getStatus() == null) ? "" : row.getStatus().trim().toUpperCase();
                if ("ACTIVE".equals(s)) {
                    getStyleClass().add("proposal-active-row");
                } else {
                    getStyleClass().add("proposal-inactive-row");
                }
            }
        });
    }

    /**
     * Loads proposal data from the database into the table.
     *
     * <p>This method retrieves all proposals available to the Sales module
     * and updates the underlying observable list used by the table.</p>
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
     * Configures the search field to filter proposals dynamically.
     *
     * <p>The filter applies to customer name, vehicle description,
     * proposal code, price, and status, and updates the table
     * as the user types.</p>
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
                        || safe(row.getPrice()).contains(text)
                        || safe(row.getStatus()).contains(text);
            });
        });

        SortedList<SalesProposalRow> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(proposalsTable.comparatorProperty());
        proposalsTable.setItems(sorted);
    }

    /**
     * Handles navigation to the new proposal creation screen.
     *
     * <p>This method is triggered by the "New Proposal" action
     * in the Sales proposals view.</p>
     */
    @FXML
    private void handleNewProposal() {
        SalesNavigation.loadCenter("/views/sales-new-proposal-view.fxml");
    }

    /**
     * Opens the proposal detail view for the selected proposal.
     *
     * <p>The selected proposal ID is stored in a shared context
     * before navigating to the detail screen.</p>
     *
     * @param row the selected proposal row
     */
    private void onView(SalesProposalRow row) {
        SelectedProposalContext.setProposalId(row.getId());
        SalesNavigation.loadCenter("/views/sales-proposal-detail-view.fxml");
    }

    /**
     * Safely converts a string to lowercase, returning an empty string
     * if the input value is {@code null}.
     *
     * <p>This helper method is used to simplify null-safe filtering logic.</p>
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
     * <p>This method is used to report database or navigation errors
     * occurring within the proposals list screen.</p>
     *
     * @param title the dialog title
     * @param msg the error message
     */
    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
