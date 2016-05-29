package supportclasses;

/**
 * Created by Olerdrive on 29.05.16.
 */
public class TimeHelper {
    public static void sleep(@SuppressWarnings("SameParameterValue") long period) {
        if (period <= 0) {
            return;
        }
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}