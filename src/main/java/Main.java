import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * Created by julia on 7/2/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            ArbMonitor monitor = new ArbMonitor();
            monitor.monitor();
        } catch (URISyntaxException | FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
