import java.util.Timer;
import java.util.TimerTask;

public class NotificationService {
    private Timer timer = new Timer();

    public void planifierNotification(Notification notification, long delayMillis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notification.envoyer();
            }
        }, delayMillis);
    }
}
