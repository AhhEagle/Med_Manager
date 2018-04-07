package com.oladimeji.medmanager.Utilities;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import com.oladimeji.medmanager.sync.PillReminderTasks;

/**
 * Created by Oladimeji on 4/7/2018.
 */

public class PillReminderFirebaseJobService extends JobService {
    private AsyncTask mBackground;
    @Override
    public boolean onStartJob(final JobParameters params) {
        mBackground = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = PillReminderFirebaseJobService.this;
                PillReminderTasks.executeTask(context, PillReminderTasks.ACTION_TAKE_PILL_REMINDER);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };
        mBackground.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mBackground != null) mBackground.cancel(true);
        return true;
    }
}
