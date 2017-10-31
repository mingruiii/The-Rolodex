package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT;

import org.junit.Rule;
import org.junit.Test;

import guitests.GuiRobot;
import guitests.RolodexGuiTest;
import javafx.scene.input.KeyCode;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.ui.testutil.EventsCollectorRule;

public class ExitCommandTest extends RolodexGuiTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();
    private GuiRobot guiRobot = new GuiRobot();

    @Test
    public void executeExitSuccess() {
        CommandResult result = new ExitCommand().execute();
        assertEquals(MESSAGE_EXIT_ACKNOWLEDGEMENT, result.feedbackToUser);
        guiRobot.push(KeyCode.ENTER);
        assertTrue(eventsCollectorRule.eventsCollector.getMostRecent() instanceof ExitAppRequestEvent);
        assertTrue(eventsCollectorRule.eventsCollector.getSize() == 1);
    }
}
