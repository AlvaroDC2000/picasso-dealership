package dealership.controllers;

import dealership.dao.RepairOrderDao;
import dealership.model.RepairTaskRow;
import dealership.util.SessionContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class MechanicTasksController {

    @FXML
    private TableView<RepairTaskRow> tasksTable;

    @FXML
    private TableColumn<RepairTaskRow, Number> repairIdColumn;

    @FXML
    private TableColumn<RepairTaskRow, String> vehicleColumn;

    @FXML
    private TableColumn<RepairTaskRow, String> statusColumn;

    @FXML
    private TableColumn<RepairTaskRow, Void> actionColumn;

    private final ObservableList<RepairTaskRow> tasks = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        repairIdColumn.setCellValueFactory(data -> data.getValue().repairIdProperty());
        vehicleColumn.setCellValueFactory(data -> data.getValue().vehicleProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        configureActionColumn();
        loadTasks();
    }

    private void loadTasks() {
        tasks.clear();

        Integer mechanicId = SessionContext.getUserId();
        if (mechanicId == null) {
            mechanicId = 1;
        }

        try {
            RepairOrderDao dao = new RepairOrderDao();
            tasks.addAll(dao.findTasksByMechanicId(mechanicId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        tasksTable.setItems(tasks);
    }

    private void configureActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<>() {

            private final Button openButton = new Button("Open");

            {
                openButton.setStyle("-fx-background-color: white; -fx-border-color: #E6EAF0; -fx-font-weight: 600;");
                openButton.setOnAction(e -> {
                    RepairTaskRow row = getTableView().getItems().get(getIndex());
                    openRepairDetails(row.getRepairId(), e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : openButton);
            }
        });
    }

    private void openRepairDetails(int repairId, javafx.event.ActionEvent event) {
        try {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();

            Scene previousScene = source.getScene();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/repair-details-view.fxml"));
            Parent root = loader.load();

            RepairDetailsController controller = loader.getController();
            controller.setNavigationContext(stage, previousScene);
            controller.setRepairId(repairId);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
        SessionContext.clear();

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/views/login-view.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            stage.setTitle("Concesionario Picasso");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleHistory(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            javafx.stage.Stage stage = (javafx.stage.Stage) source.getScene().getWindow();
            javafx.scene.Scene previousScene = source.getScene();

            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/mechanic-history-view.fxml"));

            javafx.scene.Parent root = loader.load();

            MechanicHistoryController controller = loader.getController();
            controller.setNavigationContext(stage, previousScene);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

