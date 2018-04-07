package com.oladimeji.medmanager.Utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.oladimeji.medmanager.CatalogActivity;
import com.oladimeji.medmanager.R;
import com.oladimeji.medmanager.sync.PillReminderService;
import com.oladimeji.medmanager.sync.PillReminderTasks;

/**
 * Created by Oladimeji on 4/6/2018.
 */

public class NotificationUtils {
     /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1000 is in no way significant.
     */
     private static final int REMINDER_NOTIFICATION_ID = 1000;

     //This pending intent id used to uniquely reference the pending intent
     private static final int FREQUENCY_REMINDER_PENDING_INTENT_ID = 2000;
    private static final int ACTION_TAKEN_PENDING_INTENT_ID = 100;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 101;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    //This method will create our notification
    public static void usePillReminder(Context context){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle(context.getString(R.string.pill_notification_title))
                .setContentText(context.getString(R.string.pill_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.pill_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(takepillAction(context))
                .addAction(ignorepillreminderAction(context))
                .setAutoCancel(true);
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* WATER_REMINDER_NOTIFICATION_ID allows you to update or cancel the notification later on */
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build());

    }

    //If the user decides to ignore the notification
    private static NotificationCompat.Action ignorepillreminderAction(Context context){
        Intent ignoreReminderIntent = new Intent(context, PillReminderService.class);
        ignoreReminderIntent.setAction(PillReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
       NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(0,"I Will Take it Later",ignoreReminderPendingIntent);
       return ignoreReminderAction;
    }

    private static NotificationCompat.Action takepillAction(Context context) {
        Intent incrementWaterCountIntent = new Intent(context, PillReminderService.class);
        incrementWaterCountIntent.setAction(PillReminderTasks.ACTION_PILL_TAKEN);
        PendingIntent takenPillPendingIntent = PendingIntent.getService(
                context,
               ACTION_TAKEN_PENDING_INTENT_ID,
                incrementWaterCountIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action pillTakenAction = new NotificationCompat.Action(0,
                "I did it!",
                takenPillPendingIntent);
        return pillTakenAction;
    }


     //Should return a PendingIntent. This method will create the pending intent
    //This will be triggered when the notification is pressed. The pending intent should open the CatalogActivity
    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, CatalogActivity.class);
        return PendingIntent.getActivity(context, FREQUENCY_REMINDER_PENDING_INTENT_ID, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
