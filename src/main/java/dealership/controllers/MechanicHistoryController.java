package dealership.controllers;

import dealership.dao.RepairHistoryDao;
import dealership.model.RepairHistoryRow;
import dealership.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Controller for the mechanic repair history screen.
 * <p>
 * This view shows a table with the repairs previously handled by the current mechanic.
 * The mechanic ID is taken from {@link SessionContext}. A simple navigation context
 * can be injected so the user can return to the previous scene.
 * </p>
 */
public class MechanicHistoryController {

    @FXML
    private TableView<RepairHistoryRow> historyTable;

    @FXML
    private TableColumn<RepairHistoryRow, Number> repairIdColumn;

    @FXML
    private TableColumn<RepairHistoryRow, String> vehicleColumn;

    @FXML
    private TableColumn<RepairHistoryRow, String> statusColumn;

    @FXML
    private TableColumn<RepairHistoryRow, String> dateColumn;

    private final ObservableList<RepairHistoryRow> data = FXCollections.observableArrayList();

    private Stage stage;
    private Scene previousScene;

    /**
     * Sets the navigation context used for returning to the previous screen.
     * <p>
     * The caller provides the stage currently showing this view and the scene
     * that should be restored when the user presses Back.
     * </p>
     *
     * @param stage the stage where this view is shown
     * @param previousScene the scene to restore when going back
     */
    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It binds the table columns to {@link RepairHistoryRow} properties,
     * assigns the observable list to the table and loads the mechanic history
     * from the database.
     * </p>
     */
    @FXML
    private void initialize() {
        repairIdColumn.setCellValueFactory(c -> c.getValue().repairIdProperty());
        vehicleColumn.setCellValueFactory(c -> c.getValue().vehicleProperty());
        statusColumn.setCellValueFactory(c -> c.getValue().statusProperty());
        dateColumn.setCellValueFactory(c -> c.getValue().dateProperty());

        historyTable.setItems(data);

        loadHistory();
    }

    /**
     * Loads the repair history for the current mechanic.
     * <p>
     * The mechanic ID is read from {@link SessionContext}. If it is missing,
     * a default value is used. The results are loaded through {@link RepairHistoryDao}
     * and appended to the observable list bound to the table.
     * </p>
     */
    private void loadHistory() {
        data.clear();

        Integer mechanicId = SessionContext.getUserId();
        if (mechanicId == null) {
            mechanicId = 1;
        }

        try {
            RepairHistoryDao dao = new RepairHistoryDao();
            data.addAll(dao.findHistoryByMechanicId(mechanicId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles the back action.
     * <p>
     * If a navigation context was provided, it restores the previous scene on
     * the same stage.
     * </p>
     */
    @FXML
    private void handleBack() {
        if (stage != null && previousScene != null) {
            stage.setScene(previousScene);
            stage.show();
        }
    }
}
