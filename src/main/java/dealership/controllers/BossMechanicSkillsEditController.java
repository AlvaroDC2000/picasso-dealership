package dealership.controllers;

import dealership.dao.UserDao;
import dealership.util.MechanicSelectionContext;
import dealership.util.SessionContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller responsible for editing a mechanic's skills by the boss mechanic.
 * <p>
 * This controller allows the boss to view and update the skills assigned
 * to a selected mechanic, ensuring that the mechanic belongs to the same
 * dealership and that the boss has the required permissions.
 * </p>
 */
public class BossMechanicSkillsEditController {

    @FXML
    private TextArea skillsArea;

    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    private Integer bossId;
    private Integer mechanicId;

    /**
     * Initializes the controller after the FXML file has been loaded.
     * <p>
     * It retrieves the current boss user ID from the session context and
     * the selected mechanic ID from the mechanic selection context.
     * If any required data is missing, the view is disabled and an
     * error message is shown.
     * </p>
     */
    @FXML
    public void initialize() {
        errorLabel.setText("");

        bossId = SessionContext.getUserId();
        mechanicId = MechanicSelectionContext.getSelectedMechanicId();

        if (bossId == null || mechanicId == null) {
            errorLabel.setText("Missing session data. Go back and try again.");
            skillsArea.setDisable(true);
            return;
        }

        loadSkills();
    }

    /**
     * Loads the current skills of the selected mechanic.
     * <p>
     * Skills are fetched from the database using the boss and mechanic IDs.
     * If the mechanic does not exist or the boss has no permissions,
     * the input area is disabled and an error message is displayed.
     * </p>
     */
    private void loadSkills() {
        try {
            String skills = userDao.findMechanicSkillsForBossDealership(bossId, mechanicId);
            if (skills == null) {
                errorLabel.setText("Mechanic not found (or you don't have permissions).");
                skillsArea.setDisable(true);
                return;
            }
            skillsArea.setText(skills);
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not load mechanic skills.");
            skillsArea.setDisable(true);
        }
    }

    /**
     * Handles the save action for updating mechanic skills.
     * <p>
     * This method validates session data, retrieves the updated skills
     * from the text area and persists them using the data access layer.
     * On success, it navigates back to the mechanic skills overview screen.
     * </p>
     *
     * @param event the action event triggered by the save button
     */
    @FXML
    private void handleSave(javafx.event.ActionEvent event) {
        errorLabel.setText("");

        if (bossId == null || mechanicId == null) {
            errorLabel.setText("Session expired.");
            return;
        }

        try {
            String skills = skillsArea.getText() != null ? skillsArea.getText().trim() : "";
            boolean ok = userDao.updateMechanicSkillsForBossDealership(bossId, mechanicId, skills);

            if (!ok) {
                errorLabel.setText("No changes were saved (or no permissions).");
                return;
            }

            goTo((Node) event.getSource(), "/views/boss-mechanics-skills-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not save changes.");
        }
    }

    /**
     * Handles the back navigation action.
     * <p>
     * This method returns the user to the mechanic skills overview screen
     * without applying any changes.
     * </p>
     *
     * @param event the action event triggered by the back button
     */
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            goTo((Node) event.getSource(), "/views/boss-mechanics-skills-view.fxml");
        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Could not go back.");
        }
    }

    /**
     * Navigates to a different view.
     * <p>
     * This method loads the given FXML file, applies the main stylesheet
     * if available and replaces the current scene on the active stage.
     * </p>
     *
     * @param source   the node that triggered the navigation
     * @param fxmlPath the path to the FXML file to be loaded
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
