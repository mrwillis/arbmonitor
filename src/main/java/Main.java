import org.slf4j.LoggerFactory;

/**
 * Created by julia on 7/2/2017.
 */
public class Main {
    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ArbMonitor.class);

    public static void main(String[] args) {
        try {
            ArbMonitor monitor = new ArbMonitor();
            monitor.Monitor();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }
}
