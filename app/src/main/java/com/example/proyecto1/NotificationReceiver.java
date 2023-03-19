package com.example.proyecto1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Crear notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "IdCanal")
                .setSmallIcon(R.drawable.notif)
                .setContentTitle("50/50")
                .setContentText(context.getString(R.string.vota_notif))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //Mostrar notificación
        notificationManager.notify(notificationId, builder.build());
    }
}
