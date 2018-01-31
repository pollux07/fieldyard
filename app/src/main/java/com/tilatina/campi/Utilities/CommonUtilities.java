package com.tilatina.campi.Utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.tilatina.campi.LocationAcquirerReceiver;
import com.tilatina.campi.LoginActivity;
import com.tilatina.campi.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Derechos reservados TIlatina
 */
public class CommonUtilities {
    /**
     * For app
     */
    public static final String LOG_TAG = "CAMPI";
    public static final String USERID = "user_id";
    public static final String DOMAIN = "domain";
    public static final String USERNAME = "username";
    public static final String ONLY_WIFI = "only_wifi";
    public static final String AUTHORITY = "com.tilatina.campi.fileProvider";

    /**
     * Status codes for server interaction
     */
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static final int RESPONSE_OK = 200;
    public static final int ELEMENT_NOT_FOUND = 406;
    public static final int INVALID_TICKET = 407;
    private static final int QUALITY = 100;

    private final static int ALARM_CODE = 1992;
    private final static String DEFAULT_SENT_TIME_SECS_TAG = "default_sent_time";
    private final static String DEFAULT_SENT_TIME_SECS = "60";


    public static void putPreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOG_TAG,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getPreference(Context context, String key, String defaultPrefer) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOG_TAG,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultPrefer);
    }

    public static void deletePreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOG_TAG,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(key);
        edit.commit();
    }

    public static String getUTCDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    public static String getPicName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss",
                Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    public static void downSize(Context context, Uri uri, int maxHeight, File file) throws IOException {

        InputStream is;
        /* Primero obtenemos datos de la imagen sin cargar en memoria **/
        try {
            is = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException fnfe) {
            throw new IOException(String.format("File %s not found", uri.toString()));
        }
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();


        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(uri);
        /*
         * Comparamos contra el ancho máximo permitido
         */


        float ratio;
        if (dbo.outHeight > dbo.outWidth) {
            ratio = ((float) dbo.outWidth) / ((float) maxHeight);
        } else {
            ratio = ((float) dbo.outHeight) / ((float) maxHeight);
        }

        // Create the bitmap from file
        BitmapFactory.Options options = new BitmapFactory.Options();

        /*
         * Obtener muestreo combinando cada n bits, sin perder dimensiones:
         */
        options.inSampleSize = (int) ratio;
        srcBitmap = BitmapFactory.decodeStream(is, null, options);

        /*Log.d("ImageUtil", "Imagen sin comprimir");
        srcBitmap = BitmapFactory.decodeStream(is);
        */
        is.close();

        /*
         * Preparar el archivo para sobre-escribirlo
         */
        OutputStream stream = new FileOutputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        srcBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, byteArrayOutputStream);
        byte[] bsResized = byteArrayOutputStream.toByteArray();

        stream.write(bsResized);
        stream.close();
    }


    public static void forceLogout(Activity activity) {
        deletePreference(activity, USERID);
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Toast.makeText(activity, "Credenciales invalidas",
                Toast.LENGTH_SHORT).show();
        activity.startActivity(intent);
        activity.finish();
    }

    /*public static Date transformStringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date returningDate;
        try {
            returningDate= dateFormat.parse(date);
            return returningDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static boolean validateCanCaptureAndWritePicture(Activity activity) {
        if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            ||
            (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(activity,
                    new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);

            return false;
        }

        return true;
    }

    /**
     * Se manda a llamar al servicio de alarma interna del telefono para reprogramarla
     * cada cierto tiempo
     *
     * @param context Contexto
     */
    public static void alarmSchedule(Context context) {

        final SharedPreferences sharedPreferences =
                context.getSharedPreferences(LOG_TAG, MODE_PRIVATE);
        int timeSecs = Integer.parseInt(
                sharedPreferences.getString(DEFAULT_SENT_TIME_SECS_TAG,
                        DEFAULT_SENT_TIME_SECS));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmReceiver = new Intent(context, LocationAcquirerReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, ALARM_CODE, alarmReceiver,
                0);
        Log.d(CommonUtilities.LOG_TAG, "alarmSchedule()");

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + timeSecs * 1000, sender);
        Log.d(CommonUtilities.LOG_TAG, "alarmSchedule(). Alarma programada");
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void sendLocation(final Context context,
                                    final double latitude,
                                    final double longitude) {
        final String userId = getPreference(context, USERID, null);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("lat", String.format("%s", latitude));
        params.put("lng", String.format("%s", longitude));

        WebService.sendLocationAction(context, params, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {

            }
            @Override
            public void onError() {
            }
        });
    }

    public static void notifyGeoPermissionProblem(Context context){
        final int color = ContextCompat.getColor(context, R.color.redForPanic);

        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent iIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, iIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_permissions_denied)
                .setColor(color)
                .setContentTitle(context.getString(R.string.need_access_location))
                .setContentText(context.getString(R.string.need_access_location_explain))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{700, 1000, 1200})
                .setSound(alarmSound)
                .setAutoCancel(true)
                ;

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public static void enableNotify(Context context){
        final int color = ContextCompat.getColor(context, R.color.redForPanic);
        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent iIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, iIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = context.getString(R.string.app_requires_location_title);
        String content = context.getString(R.string.app_requires_location_content);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_location_off)
                .setColor(color)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{700, 1000, 1200})
                .setSound(alarmSound)
                .setAutoCancel(true)
                ;
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
