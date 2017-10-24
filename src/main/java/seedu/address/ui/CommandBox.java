package seedu.address.ui;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Arrays;
import java.util.logging.Logger;

import org.controlsfx.control.textfield.TextFields;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.logic.ListElementPointer;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int TIME_SINCE_TYPING = 300;
    private static final int START_OF_FIRST_FIELD = 6;
    private static final int END_OF_FIRST_FIELD = 10;
    private static final int CTRL = 17;

    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private final Logic logic;
    private ListElementPointer historySnapshot;
    private Image keyboardIdle;
    private Image keyboardTyping;
    private Image keyboardError;
    private PauseTransition pause;
    private int anchorPosition;
    private String selectedText = "";
    private String input;
    private final String[] autocompleteCommandList = {"add", "a", "delete", "d", "edit", "e", "find", "f", "search",
        "list", "l", "select", "s"};
    private final String[] addCommandFieldList = {"NAME", "PHONE_NUMBER", "EMAIL", "ADDRESS", "TAG", "INDEX",
        "KEYWORD"};
    private Robot robot;



    @FXML
    private TextField commandTextField;

    @FXML
    private ImageView keyboardIcon;


    public CommandBox(Logic logic) throws AWTException {
        super(FXML);
        this.logic = logic;
        this.robot = new Robot();
        loadKeyboardIcons();
        keyboardIcon.setImage(keyboardIdle);
        pause = new PauseTransition(Duration.millis(TIME_SINCE_TYPING));
        final String[] allCommandList = {"add", "delete", "edit", "find", "search", "help", "history",
            "list", "select", "redo", "undo", "exit", "clear"};
        TextFields.bindAutoCompletion(commandTextField, allCommandList);

        // calls #processInput() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> processInput());
        historySnapshot = logic.getHistorySnapshot();
    }

    public void setFocus() {
        commandTextField.requestFocus();
    }

    public boolean isFocused() {
        return commandTextField.isFocused();
    }

    /**
     * Load images for keyboard icons in the command box.
     */
    private void loadKeyboardIcons() {
        keyboardIdle = new Image(getClass().getResourceAsStream("/images/keyboard.png"));
        keyboardTyping = new Image(getClass().getResourceAsStream("/images/keyboardTyping.png"));
        keyboardError = new Image(getClass().getResourceAsStream("/images/keyboardError.png"));
    }

    /**
     * Handles the key press event, {@code keyEvent}.
     */
    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
        case UP:
            // As up and down buttons will alter the position of the caret,
            // consuming it causes the caret's position to remain unchanged
            keyEvent.consume();

            navigateToPreviousInput();
            break;
        case DOWN:
            keyEvent.consume();
            navigateToNextInput();
            break;
        case TAB:
            autocomplete();
            //press control key to make the text selection in command box appear
            pressCtrl();
            break;
        default:
            // let JavaFx handle the keypress
        }
    }

    public Robot getRobot() {
        return this.robot;
    }

    /**
     * press control key
     */
    public void pressCtrl() {
        robot.keyPress(CTRL);
        robot.keyRelease(CTRL);
    }

    /**
     * Updates the text field with the previous input in {@code historySnapshot},
     * if there exists a previous input in {@code historySnapshot}
     */
    private void navigateToPreviousInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasPrevious()) {
            return;
        }

        replaceText(historySnapshot.previous());
    }

    /**
     * Updates the text field with the next input in {@code historySnapshot},
     * if there exists a next input in {@code historySnapshot}
     */
    private void navigateToNextInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasNext()) {
            return;
        }

        replaceText(historySnapshot.next());
    }

    /**
     * Sets {@code CommandBox}'s text field with {@code text} and
     * positions the caret to the end of the {@code text}.
     */
    protected void replaceText(String text) {
        commandTextField.setText(text);
        //commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    protected void handleCommandInputChanged() {
        try {
            CommandResult commandResult = logic.execute(commandTextField.getText());
            initHistory();
            historySnapshot.next();
            // process result of the command
            commandTextField.setText("");
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));

        } catch (CommandException | ParseException e) {
            initHistory();
            // handle command failure
            setStyleToIndicateCommandFailure();
            logger.info("Invalid command: " + commandTextField.getText());
            raise(new NewResultAvailableEvent(e.getMessage()));
        }
    }

    /**
     * Initializes the history snapshot.
     */
    private void initHistory() {
        historySnapshot = logic.getHistorySnapshot();
        // add an empty string to represent the most-recent end of historySnapshot, to be shown to
        // the user if she tries to navigate past the most-recent end of the historySnapshot.
        historySnapshot.add("");
    }

    /**
     * process input as user is typing
     */
    public void processInput() {
        input = commandTextField.getText();
        updateKeyboardIconAndStyle();
        autoSelectFirstField();
        if (Arrays.asList(addCommandFieldList).contains(selectedText)) {
            updateSelection();
        }
    }

    /**
     * if the command is add, and the next field is selected from pressing tab key, update the field selection
     */
    private void updateSelection() {
        commandTextField.selectRange(anchorPosition, anchorPosition + selectedText.length());
        selectedText = "";
    }

    /**
     * Sets the command box style to use the default style.
     * {@code keyboardTyping} icon changes to {@code keyboardIdle} when there is no change
     * to text field after some time.
     */
    private void updateKeyboardIconAndStyle() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();
        keyboardIcon.setImage(keyboardTyping);
        pause.setOnFinished(event -> {
            if (!styleClass.contains(ERROR_STYLE_CLASS)) {
                keyboardIcon.setImage(keyboardIdle);
            }
        });
        pause.playFromStart();
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * if the input matches the command format, automatically selects the first field that the user need to key in
     */
    private void autoSelectFirstField() {
        setFocus();
        switch (input) {
        case AddCommand.FORMAT:
            commandTextField.selectRange(START_OF_FIRST_FIELD, END_OF_FIRST_FIELD);
            break;
        case EditCommand.FORMAT:
        case FindCommand.FORMAT:
        case SelectCommand.FORMAT:
        case DeleteCommand.FORMAT:
            int indexOfFirstSpace = input.indexOf(" ");
            commandTextField.selectRange(indexOfFirstSpace + 1, input.length());
            break;
        default:
        }
    }

    private boolean isAutoCompleteCommand(String command) {
        return Arrays.asList(autocompleteCommandList).contains(command);
    }

    private boolean isAddCommandFormat(String input) {
        return input.startsWith("add")
                && input.contains("n/") && input.contains("p/") && input.contains("e/") && input.contains("a/");
    }

    private void changeSelectionToNextField() {
        commandTextField.selectNextWord();
        anchorPosition = commandTextField.getAnchor();
        selectedText = commandTextField.getSelectedText().trim();
    }

    /**
     * if the current input is a valid command, auto complete the full format
     * in the case of add command, if the user is trying to navigate to the next field, auto select the next field
     */
    private void autocomplete() {
        input = commandTextField.getText().trim().toLowerCase();
        if (isAutoCompleteCommand(input)) {
            displayFullFormat(input);
        } else if (isAddCommandFormat(input)) {
            int positionOfNameField = input.indexOf("n/");
            int positionOfPhoneField = input.indexOf("p/");
            int positionOfEmailField = input.indexOf("e/");
            int positionOfAddressField = input.indexOf("a/");
            int currentPosition = commandTextField.getCaretPosition();
            if (currentPosition > positionOfNameField && currentPosition < positionOfPhoneField) {
                commandTextField.positionCaret(positionOfPhoneField + 2);
                changeSelectionToNextField();
            } else if (currentPosition > positionOfPhoneField && currentPosition < positionOfEmailField) {
                commandTextField.positionCaret(positionOfEmailField + 2);
                changeSelectionToNextField();
            } else if (currentPosition > positionOfEmailField && currentPosition < positionOfAddressField) {
                commandTextField.positionCaret(positionOfAddressField + 2);
                changeSelectionToNextField();
            }
        }
    }

    /**
     * if the command input is a valid command that requires additional field(s), display the full
     * format in the textfield
     * @param command input by the user
     */
    private void displayFullFormat(String command) {
        switch (command) {
        case "add":
        case "a":
            replaceText(AddCommand.FORMAT);
            break;
        case "edit":
        case "e":
            replaceText(EditCommand.FORMAT);
            break;
        case "find":
        case "f":
        case "search":
            replaceText(FindCommand.FORMAT);
            break;
        case "select":
        case "s":
            replaceText(SelectCommand.FORMAT);
            break;
        case "delete":
        case "d":
            replaceText(DeleteCommand.FORMAT);
            break;
        default:
        }
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    public void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
        keyboardIcon.setImage(keyboardError);
    }

}
