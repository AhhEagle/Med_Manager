package com.oladimeji.medmanager.utilities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Oladimeji on 4/7/2018.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName componentName = new ComponentName(context, AlarmBroadcastReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
