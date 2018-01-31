package com.tilatina.campi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tilatina.campi.Utilities.CommonUtilities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Derechos reservados tilatina.
 */

public class LocationAcquirerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String userId = CommonUtilities.getPreference(context, CommonUtilities.USERID, null);
        Log.d(CommonUtilities.LOG_TAG, "LocationAcquirerReceiver.onReceive()");
        /*
         * 1) Reprogramar llamada de despertador
         */
        CommonUtilities.alarmSchedule(context);

        /*
         * 2) Wakelock y adquirir posición:
         */
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                CommonUtilities.LOG_TAG);
        wakeLock.acquire();

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LoginActivity login = new LoginActivity();
            login.requestPermission();
            return;
        }

        if (!CommonUtilities.isLocationEnabled(context)) {
            CommonUtilities.enableNotify(context);
        } else {
            if (userId != null) {
                final FusedLocationProviderClient fusedLocationProviderClient =
                        new FusedLocationProviderClient(context);
                LocationRequest locationRequest = new LocationRequest();

                // Create the location request to start receiving updates
                long UPDATE_INTERVAL = 10 * 1000;
                long FASTEST_INTERVAL = 2000;

                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);

                // Create LocationSettingsRequest object using location request
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(locationRequest);
                LocationSettingsRequest locationSettingsRequest = builder.build();

                // Check whether location settings are satisfied
                // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
                SettingsClient settingsClient = LocationServices.getSettingsClient(context);
                settingsClient.checkLocationSettings(locationSettingsRequest);

                getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (ActivityCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(context,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    LoginActivity login = new LoginActivity();
                                    login.requestPermission();
                                    return;
                                }
                                fusedLocationProviderClient.getLastLocation()
                                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                if (null != location) {
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    CommonUtilities.sendLocation(context, latitude, longitude);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                CommonUtilities.notifyGeoPermissionProblem(context);
                                            }
                                        });
                            }
                        },
                        Looper.myLooper());
            }
        }

        /*
         * 3) Liberamos el wakelok
         */
        wakeLock.release();
    }
}
