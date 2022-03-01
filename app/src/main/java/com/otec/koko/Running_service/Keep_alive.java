package com.otec.koko.Running_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.otec.koko.R;
import com.otec.koko.utils.Constant;
import java.util.Timer;
import java.util.TimerTask;

public class Keep_alive extends Service {

    public int count = 0;
    public String TAG = "Keep_alive";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            start_Custom_For_ground();
            Log.d(TAG, "Loop service Started ! ");
        } else
            startForeground(0, new Notification());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void start_Custom_For_ground() {
        NotificationChannel chau = new NotificationChannel( Constant.ADMIN_CHANNEL_ID,  Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
        chau.setLightColor(Color.BLUE);
        chau.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chau.setDescription("Grelot channel");
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(chau);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, Constant.ADMIN_CHANNEL_ID);
        Notification notification = noBuilder.setOngoing(true)
                .setContentTitle("Grelot is running")
                .setSmallIcon(R.drawable.icons)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(10, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_NOT_STICKY;

    }


    @Override
    public void onDestroy() {
        stopTimer();
        Intent intent = new Intent();
        intent.setAction("restartservice");
        intent.setClass(this, PushReceiver.class);
        intent.putExtra("O", "1");
        this.sendBroadcast(intent);
        super.onDestroy();
    }


    private Timer timer;
    private TimerTask timerTask;

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }


    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
