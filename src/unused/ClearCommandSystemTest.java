package systemtests;

import static seedu.address.testutil.TypicalPersons.KEYWORD_MATCHING_MEIER;

import org.junit.Test;

import guitests.GuiRobot;
import javafx.scene.input.KeyCode;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class ClearCommandSystemTest extends RolodexSystemTest {

    private GuiRobot guiRobot = new GuiRobot();

    @Test
    public void clear() {
        final Model defaultModel = getModel();

        /* Case: clear non-empty rolodex, command with leading spaces and trailing alphanumeric characters and
         * spaces -> cleared
         */
        assertCommandSuccess("   " + ClearCommand.COMMAND_WORD + " ab12   ");
        assertSelectedCardUnchanged();

        /* Case: undo clearing rolodex -> original rolodex restored */
        String command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command,  expectedResultMessage, defaultModel);
        assertSelectedCardUnchanged();

        /* Case: redo clearing rolodex -> cleared */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccessAfterPressingEnter(command, expectedResultMessage, new ModelManager());
        assertSelectedCardUnchanged();

        /* Case: selects first card in person list and clears rolodex -> cleared and no card selected */
        executeCommand(UndoCommand.COMMAND_WORD); // restores the original rolodex
        selectPerson(Index.fromOneBased(1));
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardDeselected();

        /* Case: filters the person list before clearing -> entire rolodex cleared */
        executeCommand(UndoCommand.COMMAND_WORD); // restores the original rolodex
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardUnchanged();

        /* Case: clear empty rolodex -> cleared */
        assertCommandSuccess(ClearCommand.COMMAND_WORD);
        assertSelectedCardUnchanged();
    }

    /**
     * Executes {@code command} and verifies that the command box displays an empty string, the result display
     * box displays {@code ClearCommand#MESSAGE_SUCCESS} and the model related components equal to an empty model.
     * These verifications are done by
     * {@code RolodexSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the command box has the default style class and the status bar's sync status changes.
     * @see RolodexSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(String command) {
        assertCommandSuccessAfterPressingEnter(command, ClearCommand.MESSAGE_SUCCESS, new ModelManager());
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String)} except that the result box displays
     * {@code expectedResultMessage} and the model related components equal to {@code expectedModel}.
     * @see ClearCommandSystemTest#assertCommandSuccess(String)
     */
    private void assertCommandSuccess(String command, String expectedResultMessage, Model expectedModel) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    //@@author mingruiii
    /**
     * Performs the same verification as {@code assertCommandSuccess(String, String, Model)} except that enter key is
     * pressed to select OK on the confirmation alert
     */
    private void assertCommandSuccessAfterPressingEnter(String command, String expectedResultMessage,
                                                        Model expectedModel) {
        executeCommand(command);
        guiRobot.push(KeyCode.ENTER);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }
    //@@author

    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code RolodexSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see RolodexSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
