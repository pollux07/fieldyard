package com.tilatina.campi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Derechos reservados tilatina.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
                && intent.getData() != null
                && intent.getData().toString().equals("package:" + "com.tilatina.campi")
        )) {
            context.startService(new Intent(context, AlarmService.class));
        }
    }
}
