package dealership.controllers;

import dealership.dao.UserDao;
import dealership.model.MechanicSkillRow;
import dealership.util.MechanicSelectionContext;
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
 * Controller for the boss mechanic skills overview screen.
 * <p>
 * This screen lists all mechanics from the boss dealership together with their
 * current skills and status. From the table, the boss can open the edit screen
 * for a specific mechanic using the "Edit" action link.
 * </p>
 */
public class BossMechanicsSkillsController {

    @FXML
    private Button backButton;

    @FXML
    private TableView<MechanicSkillRow> mechanicsTable;

    @FXML
    private TableColumn<MechanicSkillRow, String> mechanicColumn;

    @FXML
    private TableColumn<MechanicSkillRow, String> skillsColumn;

    @FXML
    private TableColumn<MechanicSkillRow, String> statusColumn;

    @FXML
    private TableColumn<MechanicSkillRow, Void> actionColumn;

    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    /**
     * Initializes the controller after the FXML has been loaded.
     * <p>
     * It binds the table columns to the {@link MechanicSkillRow} properties,
     * configures the action column to display an "Edit" link, and finally
     * loads the mechanics list for the current boss dealership.
     * </p>
     */
    @FXML
    public void initialize() {
        mechanicColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicName"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupActionColumn();
        loadMechanics();
    }

    /**
     * Configures the action column in the table.
     * <p>
     * Each row shows an "Edit" button styled as a link. When clicked, it stores
     * the selected mechanic ID in {@link MechanicSelectionContext} and navigates
     * to the mechanic skills edit screen.
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
                    MechanicSkillRow row = getTableView().getItems().get(getIndex());
                    if (row == null) {
                        return;
                    }

                    MechanicSelectionContext.setSelectedMechanicId(row.getMechanicId());
                    try {
                        goTo(editButton, "/views/boss-mechanic-skills-edit-view.fxml");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        errorLabel.setText("Could not open Edit mechanic skills screen.");
                    }
                });
            }

            /**
             * Updates the cell content depending on whether the row is empty.
             * <p>
             * When the row is not empty, the edit button is shown. Otherwise, the
             * cell is cleared.
             * </p>
             *
             * @param item  the cell item (unused because this is a Void column)
             * @param empty whether the cell is empty
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
    }

    /**
     * Loads mechanics for the boss dealership and fills the table.
     * <p>
     * The boss user ID is read from {@link SessionContext}. If the session has
     * expired, an error is displayed. Otherwise, mechanics and their skills are
     * retrieved from the database using {@link UserDao}.
     * </p>
     */
    private void loadMechanics() {
        errorLabel.setText("");

        Integer bossId = SessionContext.getUserId();
        if (bossId == null) {
            errorLabel.setText("Session expired. Please login again.");
            return;
        }

        try {
            ObservableList<MechanicSkillRow> data = FXCollections.observableArrayList(
                    userDao.findMechanicsWithSkillsForBossDealership(bossId)
            );
            mechanicsTable.setItems(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not load mechanics.");
        }
    }

    /**
     * Handles the back action from this screen.
     * <p>
     * It clears the mechanic selection context and navigates back to the boss menu.
     * </p>
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            MechanicSelectionContext.clear();
            goTo((Node) event.getSource(), "/views/boss-menu-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not go back.");
        }
    }

    /**
     * Navigates to a different view by replacing the current scene.
     * <p>
     * It loads the given FXML, applies the main stylesheet if available,
     * and sets the scene on the current stage.
     * </p>
     *
     * @param source   the node that triggered the navigation
     * @param fxmlPath the path to the target FXML view
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
