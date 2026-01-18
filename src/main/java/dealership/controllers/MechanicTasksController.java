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

/**
 * Controller for the mechanic tasks screen.
 * <p>
 * This screen displays the list of repairs assigned to the current mechanic.
 * From here, the mechanic can open a repair details view, access the history view,
 * or logout back to the login screen.
 * </p>
 */
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

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It binds the table columns to {@link RepairTaskRow} properties, configures the
     * action column with an "Open" button and then loads the assigned tasks for the
     * current mechanic.
     * </p>
     */
    @FXML
    private void initialize() {
        repairIdColumn.setCellValueFactory(data -> data.getValue().repairIdProperty());
        vehicleColumn.setCellValueFactory(data -> data.getValue().vehicleProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        configureActionColumn();
        loadTasks();
    }

    /**
     * Loads the tasks assigned to the current mechanic.
     * <p>
     * The mechanic ID is retrieved from {@link SessionContext}. If the session does not
     * provide an ID, a default value is used. Tasks are fetched from the database using
     * {@link RepairOrderDao} and displayed in the table.
     * </p>
     */
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

    /**
     * Configures the action column to show an "Open" button per row.
     * <p>
     * Clicking the button opens the repair details screen for the selected repair.
     * </p>
     */
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

            /**
             * Updates the action cell graphic.
             * <p>
             * If the row is empty, the button is removed. Otherwise, the "Open" button
             * is displayed.
             * </p>
             *
             * @param item  the cell item (unused because this is a Void column)
             * @param empty whether the cell is empty
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : openButton);
            }
        });
    }

    /**
     * Opens the repair details view for the given repair ID.
     * <p>
     * It loads the repair details FXML, passes the navigation context (stage + previous scene)
     * to the controller and sets the selected repair ID to be displayed.
     * </p>
     *
     * @param repairId the repair ID to open
     * @param event the action event triggered by the open button
     */
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

    /**
     * Handles the logout action.
     * <p>
     * It clears the current session and returns the user to the login view.
     * </p>
     *
     * @param event the action event triggered by the logout button
     */
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

    /**
     * Opens the mechanic history view.
     * <p>
     * It loads the history screen and passes the navigation context to allow the user
     * to return back to this tasks view.
     * </p>
     *
     * @param event the action event triggered by the history button
     */
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
