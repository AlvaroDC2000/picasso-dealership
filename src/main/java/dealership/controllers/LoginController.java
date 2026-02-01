package dealership.controllers;

import dealership.dao.UserDao;
import dealership.model.AuthUser;
import dealership.util.SessionContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the login screen.
 * <p>
 * It validates user input, authenticates the user against the database,
 * stores the session information and redirects the user to the correct
 * module depending on their role.
 * </p>
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    /**
     * Handles the login button action.
     * <p>
     * This method reads username and password, performs basic validation,
     * authenticates the user, stores the session (including dealership id),
     * and loads the correct view based on the resolved role.
     * </p>
     *
     * @param event the action event triggered by the login button
     */
    @FXML
    private void handleLogin(ActionEvent event) {

        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        errorLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Enter username and password.");
            return;
        }

        try {
            UserDao userDao = new UserDao();
            AuthUser user = userDao.authenticate(username, password);

            if (user == null) {
                errorLabel.setText("Invalid credentials.");
                return;
            }

            // Store session (IMPORTANT: include dealershipId)
            SessionContext.setUserId(user.getId());
            SessionContext.setDealershipId(user.getDealershipId());
            SessionContext.setRoleName(user.getRoleName());

            String role = normalizeRole(user.getRoleName());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if ("MECHANIC".equals(role)) {

                loadView(stage, "/views/mechanic-tasks-view.fxml");

            } else if ("CHIEF_MECHANIC".equals(role) || "MECHANIC_BOSS".equals(role) || "BOSS_MECHANIC".equals(role)) {

                loadView(stage, "/views/boss-menu-view.fxml");

            } else if ("SALES".equals(role)) {

                loadView(stage, "/views/sales-main-view.fxml");

            } else if ("OWNER".equals(role)) {

                errorLabel.setText("Owner role will be implemented later.");

            } else {

                errorLabel.setText("Role not supported: " + user.getRoleName());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Error during login.");
        }
    }

    /**
     * Normalizes a role name into a safe uppercase value.
     *
     * @param roleName the role name returned by the database
     * @return the normalized role string, or empty string if null
     */
    private String normalizeRole(String roleName) {
        if (roleName == null) return "";
        return roleName.trim().toUpperCase();
    }

    /**
     * Loads an FXML view into the given stage.
     * <p>
     * This method creates a new {@link Scene}, applies the main application
     * stylesheet if available, and shows the stage.
     * </p>
     *
     * @param stage the stage where the view will be displayed
     * @param fxmlPath the path to the FXML file to load
     * @throws Exception if the FXML cannot be loaded
     */
    private void loadView(Stage stage, String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        if (getClass().getResource("/styles/app.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.show();
    }
}

