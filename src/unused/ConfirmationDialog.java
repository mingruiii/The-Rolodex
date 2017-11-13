//@@author mingruiii
package seedu.address.logic;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Creates a confirmation dialog, to be used before executing delete, clear and exit commands.
 */
public class ConfirmationDialog {
    private Alert alert;
    private Optional<ButtonType> clickedButton;

    public ConfirmationDialog(String headerText, String contentText) {
        alert = new Alert(Alert.AlertType.CONFIRMATION, contentText, ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().getStylesheets().add("view/LightTheme.css");
        alert.setTitle("Rolodex");
        alert.setHeaderText(headerText);
    }

    /**
     * show the alert message and execute corresponding results when user clicks yes or cancel or close the dialog
     */
    public boolean goAhead() {
        clickedButton = alert.showAndWait();
        return clickedButton.isPresent() && clickedButton.get() == ButtonType.YES;
    }
}
