package seedu.address.ui;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * API of UI component
 */
public interface Ui {

    /** Starts the UI (and the App).  */
    void start(Stage primaryStage);

    /** Stops the UI. */
    void stop();

    Optional<ButtonType> showAlertDialogAndGetResult(Alert.AlertType type, String title, String headerText,
                                                     String contentText);

}
