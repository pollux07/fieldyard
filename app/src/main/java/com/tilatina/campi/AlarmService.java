package com.tilatina.campi;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tilatina.campi.Utilities.CommonUtilities;

/**
 * Derechos reservados tilatina.
 */
public class AlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(CommonUtilities.LOG_TAG, "AlarmService.onStartCommand");
        final SharedPreferences sharedPreferences =
                getSharedPreferences(CommonUtilities.LOG_TAG, MODE_PRIVATE);
        String userId = sharedPreferences.getString(CommonUtilities.USERID, null);

        if (userId != null) {
            CommonUtilities.alarmSchedule(this);
        }
        return START_STICKY;
    }
}
