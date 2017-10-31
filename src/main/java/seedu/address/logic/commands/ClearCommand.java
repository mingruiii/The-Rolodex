package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ClearPersonDetailPanelRequestEvent;
import seedu.address.logic.ConfirmationDialog;
import seedu.address.model.Rolodex;

/**
 * Clears the rolodex.
 */
public class ClearCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "clear";
    public static final Set<String> COMMAND_WORD_ABBREVIATIONS =
            new HashSet<>(Arrays.asList(COMMAND_WORD, "c"));
    public static final String COMMAND_HOTKEY = "Ctrl+Shift+C";
    public static final String CONFIRMATION_MESSAGE = "Are you sure you want to clear all contacts "
            + "in the current Rolodex?";

    public static final String MESSAGE_SUCCESS = "Rolodex has been cleared!";
    private ConfirmationDialog clearConfirmationDialog;


    @Override
    public CommandResult executeUndoableCommand() {
        requireNonNull(model);
        Platform.runLater(() -> {
            clearConfirmationDialog = new ConfirmationDialog(ClearCommand.COMMAND_WORD,
                    ClearCommand.CONFIRMATION_MESSAGE);
            if (clearConfirmationDialog.goAhead()) {
                model.resetData(new Rolodex());
                EventsCenter.getInstance().post(new ClearPersonDetailPanelRequestEvent());
            }
        });
        return new CommandResult(MESSAGE_SUCCESS);

    }
}
