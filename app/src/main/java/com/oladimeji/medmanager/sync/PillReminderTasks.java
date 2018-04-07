package com.oladimeji.medmanager.sync;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.oladimeji.medmanager.Utilities.NotificationUtils;

/**
 * Created by Oladimeji on 4/6/2018.
 */

public class PillReminderTasks {
    public static final String ACTION_PILL_TAKEN = "pillTaken";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    public static final String ACTION_TAKE_PILL_REMINDER = "dismiss-notification";


    public static void executeTask(Context context, String action) {
        if (ACTION_PILL_TAKEN.equals(action)) {
            Toast.makeText(context, "Thank you for using your pill", Toast.LENGTH_SHORT).show();
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_TAKE_PILL_REMINDER.equals(action)){
            issuePillReminder(context);
        }
    }

    private static void issuePillReminder(Context context) {
        NotificationUtils.usePillReminder(context);
    }
}
