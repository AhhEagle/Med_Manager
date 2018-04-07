package com.oladimeji.medmanager.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Oladimeji on 4/6/2018.
 */

public class PillReminderService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public PillReminderService() {
        super("PillReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        PillReminderTasks.executeTask(this, action);

    }
}
