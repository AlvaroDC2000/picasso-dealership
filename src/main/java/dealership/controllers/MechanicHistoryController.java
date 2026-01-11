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

    public void setNavigationContext(Stage stage, Scene previousScene) {
        this.stage = stage;
        this.previousScene = previousScene;
    }

    @FXML
    private void initialize() {
        repairIdColumn.setCellValueFactory(c -> c.getValue().repairIdProperty());
        vehicleColumn.setCellValueFactory(c -> c.getValue().vehicleProperty());
        statusColumn.setCellValueFactory(c -> c.getValue().statusProperty());
        dateColumn.setCellValueFactory(c -> c.getValue().dateProperty());

        historyTable.setItems(data);

        loadHistory();
    }

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

    @FXML
    private void handleBack() {
        if (stage != null && previousScene != null) {
            stage.setScene(previousScene);
            stage.show();
        }
    }
}
