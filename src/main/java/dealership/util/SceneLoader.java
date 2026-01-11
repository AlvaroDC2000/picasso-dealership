package dealership.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {

    public static void load(Stage stage, String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(SceneLoader.class.getResource(fxmlPath));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneLoader.class.getResource("/styles/app.css").toExternalForm());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
