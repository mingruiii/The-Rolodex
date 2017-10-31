package seedu.address.logic.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.logic.ConfirmationDialog;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";
    public static final Set<String> COMMAND_WORD_ABBREVIATIONS =
            new HashSet<>(Arrays.asList(COMMAND_WORD, "quit", "close", "bye", "esc"));

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting Rolodex as requested ...";
    public static final String CONFIRMATION_MESSAGE = "Do you want to exit Rolodex?";
    private ConfirmationDialog exitConfirmationDialog;

    @Override
    public CommandResult execute() {
        Platform.runLater(() -> {
            exitConfirmationDialog = new ConfirmationDialog(COMMAND_WORD, CONFIRMATION_MESSAGE);
            if (exitConfirmationDialog.goAhead()) {
                EventsCenter.getInstance().post(new ExitAppRequestEvent());
            }
        });
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT);
    }

}
