package dealership.controllers;

import dealership.dao.RepairOrderDao;
import dealership.model.RepairTaskRow;
import dealership.util.RepairSelectionContext;
import dealership.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the boss repairs list screen.
 * <p>
 * This screen shows the repairs registered in the boss dealership and allows
 * the boss to open the edit view for a specific repair. The selected repair ID
 * is stored in {@link RepairSelectionContext} before navigating to the edit screen.
 * </p>
 */
public class BossRepairsController {

    @FXML
    private Button backButton;

    @FXML
    private TableView<RepairTaskRow> repairsTable;

    @FXML
    private TableColumn<RepairTaskRow, Integer> repairIdColumn;

    @FXML
    private TableColumn<RepairTaskRow, String> vehicleColumn;

    @FXML
    private TableColumn<RepairTaskRow, String> statusColumn;

    @FXML
    private TableColumn<RepairTaskRow, Void> actionColumn;

    @FXML
    private Label errorLabel;

    private final RepairOrderDao repairOrderDao = new RepairOrderDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It binds table columns to {@link RepairTaskRow} properties, configures the
     * action column with an "Edit" link and loads the repairs list for the
     * current boss user.
     * </p>
     */
    @FXML
    public void initialize() {
        repairIdColumn.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicle"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupActionColumn();
        loadRepairs();
    }

    /**
     * Configures the action column to show an "Edit" button per row.
     * <p>
     * The button is styled as a link. When clicked, it stores the repair ID
     * in the {@link RepairSelectionContext} and navigates to the repair edit view.
     * </p>
     */
    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-underline: true;" +
                        "-fx-text-fill: #2563EB;" +
                        "-fx-font-weight: 700;"
                );

                editButton.setOnAction(e -> {
                    RepairTaskRow row = getTableView().getItems().get(getIndex());
                    if (row == null) {
                        return;
                    }
                    RepairSelectionContext.setSelectedRepairId(row.getRepairId());
                    try {
                        goTo(editButton, "/views/boss-repair-edit-view.fxml");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        errorLabel.setText("Could not open Edit repair screen.");
                    }
                });
            }

            /**
             * Updates the cell content depending on whether the row is empty.
             * <p>
             * If the row is empty, the graphic is cleared. Otherwise, the edit
             * button is displayed.
             * </p>
             *
             * @param item  the cell item (unused because this is a Void column)
             * @param empty whether the cell is empty
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }

    /**
     * Loads repairs from the database and fills the table.
     * <p>
     * The boss user ID is retrieved from {@link SessionContext}. If the session
     * is missing/expired, an error message is shown. Otherwise, repairs are
     * queried through {@link RepairOrderDao}.
     * </p>
     */
    private void loadRepairs() {
        errorLabel.setText("");

        Integer bossId = SessionContext.getUserId();
        if (bossId == null) {
            errorLabel.setText("Session expired. Please login again.");
            return;
        }

        try {
            ObservableList<RepairTaskRow> data = FXCollections.observableArrayList(
                    repairOrderDao.findRepairsByBossId(bossId)
            );
            repairsTable.setItems(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not load repairs from database.");
        }
    }

    /**
     * Handles the back action from this screen.
     * <p>
     * It clears the current repair selection and returns to the boss menu view.
     * </p>
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            RepairSelectionContext.clear();
            goTo((Node) event.getSource(), "/views/boss-menu-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not go back.");
        }
    }

    /**
     * Navigates to a different view by loading an FXML file and replacing the scene.
     * <p>
     * If the main stylesheet exists, it is applied to the new scene as well.
     * </p>
     *
     * @param source   the node that triggered navigation
     * @param fxmlPath the path to the FXML view to load
     * @throws Exception if the FXML file or resources cannot be loaded
     */
    private void goTo(Node source, String fxmlPath) throws Exception {
        Stage stage = (Stage) source.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        if (getClass().getResource("/styles/app.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }
}
