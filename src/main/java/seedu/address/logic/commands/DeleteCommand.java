package seedu.address.logic.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.ConfirmationDialog;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Deletes a person identified using it's last displayed index from the rolodex.
 */
public class DeleteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "delete";
    public static final Set<String> COMMAND_WORD_ABBREVIATIONS =
            new HashSet<>(Arrays.asList(COMMAND_WORD, "d", "-"));
    public static final String COMMAND_HOTKEY = "Ctrl+Shift+D";
    public static final String FORMAT = "delete INDEX";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String CONFIRMATION_MESSAGE = "Are you sure you want to delete this contact?";

    private final Index targetIndex;
    private ConfirmationDialog deleteConfirmationDialog;

    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getLatestPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToDelete = lastShownList.get(targetIndex.getZeroBased());

        Platform.runLater(() -> {
            deleteConfirmationDialog = new ConfirmationDialog(COMMAND_WORD, CONFIRMATION_MESSAGE);
            if (deleteConfirmationDialog.goAhead()) {
                try {
                    model.deletePerson(personToDelete);
                } catch (PersonNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, personToDelete));

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCommand // instanceof handles nulls
                && this.targetIndex.equals(((DeleteCommand) other).targetIndex)); // state check
    }
}
