package utilities;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Utility class for navigation and view management in JavaFX applications.
 */
public class NavigationUtils {

    /**
     * Opens a new window with the specified FXML layout.
     * @param fxmlPath Path to the FXML file.
     * @param title Title of the new window.
     * @param controller Controller instance to be used (can be null).
     * @param wait If true, the method will wait until the window is closed.
     */
    public void openNewWindow(String fxmlPath, String title, Object controller, boolean wait) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle(title);
            if (wait) {
                newStage.showAndWait();
            } else {
                newStage.show();
                newStage.centerOnScreen();
            }
        } catch (IOException e) {
            handleError("Error abriendo nueva ventana: " + fxmlPath, e);
        }
    }

    /**
     * Changes the scene of the given stage to a new FXML layout.
     * @param stage The stage to change the scene of.
     * @param fxmlPath Path to the FXML file.
     * @param title Title of the stage.
     * @param controller Controller instance to be used.
     */
    public void changeScene(Stage stage, String fxmlPath, String title, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
            stage.centerOnScreen();

        } catch (IOException e) {
            handleError("Error changing the scene " + fxmlPath, e);
        }
    }

    /**
     * Loads an internal view into the specified AnchorPane container.
     * @param container The AnchorPane to load the view into.
     * @param fxmlPath Path to the FXML file.
     * @param controller Controller instance to be used.
     */
    public void loadInternalView(AnchorPane container, String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            Node newContent = loader.load();

            container.getChildren().clear();
            container.getChildren().add(newContent);

            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
        } catch (IOException e) {
            handleError("Error cargando vista interna: " + fxmlPath, e);
        }
    }

    /**
     * Loads a component from an FXML file.
     * @param fxmlPath Path to the FXML file.
     * @param controller Controller instance to be used.
     * @return
     */
    public Parent loadComponent(String fxmlPath, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controller != null) {
                loader.setController(controller);
            }
            return loader.load();
        } catch (IOException e) {
            handleError("Error cargando componente: " + fxmlPath, e);
            return null;
        }
    }

    /**
     * Loads an internal component into the specified Pane container.
     * @param container The Pane to load the component into.
     * @param fxmlPath Path to the FXML file.
     * @param controller Controller instance to be used.
     */
    public void loadInternalComponent(Pane container, String fxmlPath, Object controller) {
        Parent component = loadComponent(fxmlPath, controller);
        if (component != null) {
            container.getChildren().add(component);
        }
    }

    /**
     * Handles errors by printing the message and stack trace.
     * @param message Error message to display.
     * @param e The exception that was thrown.
     */
    private void handleError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}
