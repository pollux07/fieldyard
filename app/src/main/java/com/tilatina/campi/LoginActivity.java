package com.tilatina.campi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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
import com.tilatina.campi.Utilities.VolleySingleton;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LoginActivity extends AppCompatActivity {

    RelativeLayout mRelative;

    Activity mActivity;
    Context mCtx = null;

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        mCtx = this;
        super.onCreate(savedInstanceState);
        if (null != getSupportActionBar()) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        if (!checkPermission()) {
            requestPermission();
        }
        VolleySingleton.getInstance(mCtx);

        startLocationUpdates();

        mRelative = (RelativeLayout) findViewById(R.id.relative_login);

        /* Before at all, If the user id preference is stored, just needs jump into main activity */
        String userId = CommonUtilities.getPreference(mCtx, CommonUtilities.USERID, null);
        if (null != userId) {
            startActivity(new Intent(mCtx, MainActivity.class));
            finish();
        }

        TextView versions = (TextView) findViewById(R.id.versions);
        assert null != versions;
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            versions.setText(String.format("%s/1.0", info.versionName));
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* UI instance */
        final EditText domain = (EditText) findViewById(R.id.domain);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.loginButton);

        /* When the user is logged in this two fields will stored for next login */
        if (null != CommonUtilities.getPreference(mCtx, CommonUtilities.DOMAIN, null)) {
            domain.setText(CommonUtilities.getPreference(mCtx, CommonUtilities.DOMAIN, null));
        }
        if (null != CommonUtilities.getPreference(mCtx, CommonUtilities.USERNAME, null)) {
            username.setText(CommonUtilities.getPreference(mCtx, CommonUtilities.USERNAME, null));
        }

        /* Just for handle enter and make the login */
        password.setImeActionLabel("Aceptar", KeyEvent.KEYCODE_ENTER);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendRequest(domain, username, password);
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest(domain, username, password);
            }
        });
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {


    }

    private void sendRequest(final EditText domain, final EditText username, final EditText password) {
        if (domain.getText().toString().trim().length() == 0) {
            domain.setError("El campo es obligatorio");
            return;
        }
        if (username.getText().toString().trim().length() == 0) {
            username.setError("El campo es obligatorio");
            return;
        }
        if (password.getText().toString().trim().length() == 0) {
            password.setError("El campo es obligatorio");
            return;
        }

        if (!checkPermission()) {
            requestPermission();
        } else if (!CommonUtilities.isLocationEnabled(mCtx)) {
            CommonUtilities.enableNotify(mCtx);
        } else {
            // Create the location request to start receiving updates
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);

            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            FusedLocationProviderClient fusedLocationProviderClient =
                                    new FusedLocationProviderClient(mCtx);
                            if (!checkPermission()) {
                                requestPermission();
                            } else if (!CommonUtilities.isLocationEnabled(mCtx)) {
                                CommonUtilities.enableNotify(mCtx);
                            } else {
                                fusedLocationProviderClient.getLastLocation()
                                        .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                if (null != location) {
                                                    final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                                    progressDialog.setMessage("Cargando");
                                                    double latitude = location.getLatitude();
                                                    double longitude = location.getLongitude();
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("_domain", domain.getText().toString());
                                                    params.put("_username", username.getText().toString());
                                                    params.put("_password", password.getText().toString());
                                                    params.put("lat", String.format("%s", latitude));
                                                    params.put("lng", String.format("%s", longitude));
                                                    params.put("phoneDate", CommonUtilities.getUTCDateTime());

                                /* Web service call */
                                                    progressDialog.show();
                                                    WebService.loginAction(mCtx, params, new WebService.RequestListener() {
                                                        @Override
                                                        public void onSuccess(String response) {
                                                            progressDialog.cancel();
                                                            try {
                                                                JSONObject jsonResponse = new JSONObject(response);
                                                                verifyResponse(jsonResponse, domain, username);
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            progressDialog.cancel();
                                                            Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                new AlertDialog.Builder(mCtx)
                                                        .setTitle("Campi no encontró posición")
                                                        .setMessage("No se ha encontrado posición, inténtelo de nuevo más tarde")
                                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        });
                            }
                        }
                    },
                    Looper.myLooper());

        }
    }

    private void verifyResponse(JSONObject response, EditText domain, EditText username) throws JSONException{
        int code = response.getInt("code");
        switch (code) {
            case CommonUtilities.RESPONSE_OK :
                int user = response.getInt("user");
                if (-1 == user) {
                    Toast.makeText(mCtx, "Credenciales inválidas", Toast.LENGTH_LONG).show();
                    break;
                }
                CommonUtilities.putPreference(mCtx, CommonUtilities.USERID, String.format("%s", user));
                CommonUtilities.putPreference(mCtx, CommonUtilities.DOMAIN, domain.getText().toString());
                CommonUtilities.putPreference(mCtx, CommonUtilities.USERNAME, username.getText().toString());
                mCtx.startActivity(new Intent(mCtx, MainActivity.class));
                finish();
                break;
            default:
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * Revisa que la aplicacion tenga permisos de localización
     * @return regresa si tiene los permisos o no
     */
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_COARSE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                INTERNET);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WAKE_LOCK);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECEIVE_BOOT_COMPLETED);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(),
                VIBRATE);

        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED
                && result5 == PackageManager.PERMISSION_GRANTED
                && result6 == PackageManager.PERMISSION_GRANTED
                && result7 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Si no tiene los permisos lo pide para el acceso a la localización
     */
    public void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
                INTERNET, WAKE_LOCK, WRITE_EXTERNAL_STORAGE, CAMERA, RECEIVE_BOOT_COMPLETED,
                VIBRATE}, CommonUtilities.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults){
        switch (requestCode){
            case CommonUtilities.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0){

                    boolean fineLocAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean corseLocAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean internetAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeLokeAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean writeESAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean bootAccepted = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean vibrateAccepted = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                    if (!fineLocAccepted && !corseLocAccepted && !internetAccepted && !wakeLokeAccepted
                            && !writeESAccepted && !cameraAccepted && !bootAccepted
                            && !vibrateAccepted){
                        showSnackBar();
                    } else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)
                                    || shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)
                                    || shouldShowRequestPermissionRationale(INTERNET)
                                    || shouldShowRequestPermissionRationale(WAKE_LOCK)
                                    || shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)
                                    || shouldShowRequestPermissionRationale(CAMERA)
                                    || shouldShowRequestPermissionRationale(RECEIVE_BOOT_COMPLETED)
                                    || shouldShowRequestPermissionRationale(VIBRATE)) {
                                showMessageOKCancel("Necesitas habilitar todos los permisos",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(
                                                            new String[]{ACCESS_FINE_LOCATION,
                                                                    ACCESS_COARSE_LOCATION,
                                                                    INTERNET, WAKE_LOCK,
                                                                    WRITE_EXTERNAL_STORAGE, CAMERA,
                                                                    RECEIVE_BOOT_COMPLETED,
                                                                    VIBRATE},
                                                            CommonUtilities.PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        }, new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which){
                                                showSnackBar();
                                            }
                                        });
                            }
                        }
                    }
                }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,
                                     DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }


    private void showSnackBar() {

        Snackbar.make(mRelative, R.string.permissions,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                })
                .show();
    }


    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
