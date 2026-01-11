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

public class BossMechanicSkillsEditController {

    @FXML
    private TextArea skillsArea;

    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    private Integer bossId;
    private Integer mechanicId;

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

    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            goTo((Node) event.getSource(), "/views/boss-mechanics-skills-view.fxml");
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
