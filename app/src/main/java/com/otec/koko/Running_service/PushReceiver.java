package com.otec.koko.Running_service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.otec.koko.R;
import com.otec.koko.UI.Notification_beam;
import com.otec.koko.utils.Constant;

import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import me.pushy.sdk.Pushy;


public class PushReceiver extends BroadcastReceiver {

    private String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1 = new Intent(context, Notification_beam.class);
        int notificationID = new Random().nextInt(3000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icons))
                .setSmallIcon(R.drawable.icons)
                .setContentTitle(intent.getStringExtra("id"))
                .setContentText(intent.getStringExtra("item"))
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        if (intent.getStringExtra("pic") != null) {
            Bitmap bitmap = get_img_url(intent.getStringExtra("pic"));
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null)).setLargeIcon(bitmap);
        }

        Pushy.setNotificationChannel(builder, context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, builder.build());

        if (intent.getStringExtra("O") != null)
            if (intent.getStringExtra("O").equals("1"))
                notificationManager.cancelAll();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    }


    private Bitmap get_img_url(String img_url) {
        try {
            URL url = new URL(Constant.BASEURL_S3 + img_url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
