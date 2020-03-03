package com.example.android.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button_notify, button_cancel, button_update;

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private NotificationManager mNotifyManager;

    private static final int NOTIFICATION_ID = 0;

    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.example.android.notifyme.ACTION_UPDATE_NOTIFICATION";

    private static final String ACTION_CANCEL_NOTIFICATION =
            "com.example.android.notifyme.ACTION_CANCEL_NOTIFICATION ";

    private NotificationReceiver mReceiver = new NotificationReceiver();

    private ClearNotificationReceiver cReceiver = new ClearNotificationReceiver();

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(cReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(mReceiver,new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(cReceiver,new IntentFilter(ACTION_CANCEL_NOTIFICATION ));
        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });
        createNotificationChannel();

        button_update = findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the notification
                updateNotification();
            }
        });

        button_cancel = findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel the notification
                cancelNotification();
            }
        });
        setNotificationButtonState(true, false, false);
    }

    public void sendNotification() {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, true, true);
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentTitle("You've been notified!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setDeleteIntent(createDeleteIntent());
        return notifyBuilder;
    }

    public void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(),R.drawable.mascot_1);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"));
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        setNotificationButtonState(false, false, true);
    }
    public void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
        setNotificationButtonState(true, false, false);
    }

    void setNotificationButtonState(Boolean isNotifyEnabled,
                                    Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    private PendingIntent createDeleteIntent() {
        Intent intent = new Intent(ACTION_CANCEL_NOTIFICATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals(ACTION_UPDATE_NOTIFICATION)) { return; }
            // Update the notification
            updateNotification();
        }
    }

    public class ClearNotificationReceiver extends BroadcastReceiver {

        public ClearNotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null || !action.equals(ACTION_CANCEL_NOTIFICATION)) { return; }
            cancelNotification();
        }
    }
}
