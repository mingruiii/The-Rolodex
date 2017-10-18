package seedu.address.commons.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * A star wars object that includes the streamer and telnet client source of the stream.
 * @author towel.blinkenlights.nl
 */
@SuppressWarnings("unchecked")
public class StarWars {

    private static final String server = "towel.blinkenlights.nl";

    private TelnetClient telnetClient = new TelnetClient();
    private InputStream in;

    public StarWars() {
        try {
            // Connect to the specified server
            telnetClient.connect(server, 23);

            // Get input stream reference
            in = telnetClient.getInputStream();

        } catch (Exception e) {
            // nothing needs to be done.
        }
    }

    /**
     * Disconnects the initialized telnet client on exit.
     */
    public void shutDown() {
        try {
            telnetClient.disconnect();
        } catch (IOException e) {
            // nothing needs to be done.
        }
    }

    public InputStream getIn() {
        return in;
    }
}
