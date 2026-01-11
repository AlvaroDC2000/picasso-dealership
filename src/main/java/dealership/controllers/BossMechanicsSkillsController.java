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

    @FXML
    public void initialize() {
        mechanicColumn.setCellValueFactory(new PropertyValueFactory<>("mechanicName"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupActionColumn();
        loadMechanics();
    }

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

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editButton);
            }
        });
    }

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
