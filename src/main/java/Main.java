import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by julia on 7/2/2017.
 */
public class Main {


    public static void main(String[] args) throws IOException {

        // Load configuration
        Properties prop = new Properties();
        InputStream input = null;
        String filename = "config.properties";
        input = Main.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            throw new FileNotFoundException("Unable to find file: " + filename);
        }
        prop.load(input);
        String email = prop.getProperty("email");
        String emailPassword = prop.getProperty("password");
        String[] rawNotificationRecipients = prop.getProperty("notificationRecipients").split(";");
        Logger logger = LoggerFactory.getLogger(Main.class);
        try {
            ArbMonitor monitor = new ArbMonitor(email, emailPassword, rawNotificationRecipients);
            monitor.Monitor();
        } catch (URISyntaxException | FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
