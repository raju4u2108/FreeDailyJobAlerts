package in.freedailyjobalert.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID   = "fdja_jobs";
    private static final String CHANNEL_NAME = "Job Alerts";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "New Govt Job Alert";
        String body  = "Tap to view latest government jobs";
        String url   = "https://freedailyjobalert.in";

        // Get notification payload
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();
            if (remoteMessage.getNotification().getBody() != null)
                body = remoteMessage.getNotification().getBody();
        }

        // Get data payload (overrides notification if present)
        if (remoteMessage.getData().containsKey("title"))
            title = remoteMessage.getData().get("title");
        if (remoteMessage.getData().containsKey("body"))
            body  = remoteMessage.getData().get("body");
        if (remoteMessage.getData().containsKey("url"))
            url   = remoteMessage.getData().get("url");

        sendNotification(title, body, url);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Token refresh — you can send this to your server if needed
        // For now, Firebase handles device targeting via topics
    }

    private void sendNotification(String title, String body, String url) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("launch_url", url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int flags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Daily government job notifications from FreeDailyJobAlert.in");
            manager.createNotificationChannel(channel);
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
