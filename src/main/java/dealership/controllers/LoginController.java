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
 * This controller validates user input, authenticates the user through {@link UserDao},
 * stores session data in {@link SessionContext}, and redirects the user to the correct
 * view depending on the role returned by the authentication process.
 * </p>
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    /**
     * Handles the login button action.
     * <p>
     * It reads the username and password fields, performs basic validation,
     * calls the authentication method, stores session information and then
     * navigates to the correct view depending on the resolved role.
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

            // Guarda la sesión
            SessionContext.setUserId(user.getId());
            SessionContext.setRoleName(user.getRoleName());

            // Resuelve el rol
            String role = normalizeRole(user.getRoleName());

            // Navigación
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
     * Normalizes a role name to a safe comparable value.
     * <p>
     * This method trims the input and converts it to uppercase. If the role name
     * is null, it returns an empty string.
     * </p>
     *
     * @param roleName the original role name coming from the database/user
     * @return the normalized role name in uppercase (or empty string if null)
     */
    private String normalizeRole(String roleName) {
        if (roleName == null) {
            return "";
        }
        return roleName.trim().toUpperCase();
    }

    /**
     * Loads an FXML view into the given stage.
     * <p>
     * The view is loaded using {@link FXMLLoader}. If the main stylesheet exists,
     * it is applied to the new scene as well.
     * </p>
     *
     * @param stage the current application stage
     * @param fxmlPath the FXML path to be loaded
     * @throws Exception if the FXML file or resources cannot be loaded
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
