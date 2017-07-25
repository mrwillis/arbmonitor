import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by julia on 7/2/2017.
 */
public class Main {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ArbMonitor.class);

    public static void main(String[] args) {

        Properties prop = new Properties();
        InputStream input = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            input = classLoader.getResourceAsStream("config.properties");
            prop.load(input);

            ArbMonitor monitor = new ArbMonitor(prop.getProperty("arbitrageThreshold"));
            monitor.Monitor();
        } catch (Exception e) {
            Logger.error("A critical error occured and crashed the program. ", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Logger.error("Unable to close config.properties file", e);
                }
            }
        }
    }
}
