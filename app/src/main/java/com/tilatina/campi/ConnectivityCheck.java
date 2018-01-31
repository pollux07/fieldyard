package com.tilatina.campi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.tilatina.campi.Utilities.CommonUtilities;

/**
 * Derechos reservados tilatina.
 */

public class ConnectivityCheck extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!checkConnection()) {
            CommonUtilities.enableNotify(this);
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean checkConnection() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
}
