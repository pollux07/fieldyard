package com.tilatina.campi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tilatina.campi.Utilities.CommonUtilities;
import com.tilatina.campi.Utilities.ServiceAdapter;
import com.tilatina.campi.Utilities.ServiceObject;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private int SERVICE_INTENT = 1;
    String search = "";

    Activity mActivity;
    Context mCtx = null;
    FusedLocationProviderClient mFusedLocationProviderClient;
    List<ServiceObject> servicesObjectList = new ArrayList<>();
    ServiceAdapter serviceAdapter = new ServiceAdapter(servicesObjectList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        mCtx = this;
        mFusedLocationProviderClient = new FusedLocationProviderClient(mCtx);

        final SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refreshMainView);
        final ImageButton panicAction = (ImageButton) findViewById(R.id.panicAction);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.serviceListView);
        assert null != swipeRefresh;
        assert null != recyclerView;

        restorePanicButton(panicAction);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(serviceAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mCtx, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ServiceObject object = servicesObjectList.get(position);
                Intent intent = new Intent();
                intent.putExtra("element_id", object.getId());
                intent.putExtra("element_name", object.getName());
                intent.putExtra("lat", object.getLat());
                intent.putExtra("lng", object.getLng());
                intent.putExtra("color", object.getColor());
                intent.putExtra("element_type", object.getElementTypeId());
                intent.putExtra("ticket_id", object.getTicketID());
                intent.putExtra("ticket_detail", object.getTicketDetail());
                intent.setClass(mCtx, ServiceDetailActivity.class);
                startActivityForResult(intent, SERVICE_INTENT);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        final Handler handler = new Handler();
        final Runnable run = new Runnable() {
            @Override
            public void run() {
                final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                if (ActivityCompat.checkSelfPermission(
                        mActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity,
                        ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION}, 200);
                    return;
                }

                if (!CommonUtilities.isLocationEnabled(mCtx)) {
                    CommonUtilities.enableNotify(mCtx);
                    restorePanicButton(panicAction);
                } else {
                    mFusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (null != location) {
                                        final double latitude = location.getLatitude();
                                        final double longitude = location.getLongitude();

                                        Map<String, String> params = new HashMap<>();
                                        params.put("user-id", CommonUtilities.getPreference(mCtx, CommonUtilities.USERID, null));
                                        params.put("lat", String.format("%s", latitude));
                                        params.put("lng", String.format("%s", longitude));
                                        params.put("phoneTime", CommonUtilities.getUTCDateTime());

                                        WebService.panicAction(mCtx, params, new WebService.RequestListener() {
                                            @Override
                                            public void onSuccess(String response) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    int code = jsonResponse.getInt("code");
                                                    if (code == CommonUtilities.RESPONSE_OK) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new CountDownTimer(7000, 1000) {
                                                                    @Override
                                                                    public void onTick(long millisUntilFinished) {
                                                                        long timeout = millisUntilFinished / 1000;
                                                                        if (Build.VERSION.SDK_INT >= Build
                                                                                .VERSION_CODES.LOLLIPOP) {
                                                                            panicAction.setBackground(mCtx.getDrawable
                                                                                    (timeout % 2 > 0
                                                                                            ? R.drawable.oval_panic_pressed
                                                                                            : R.drawable.oval_panic_shape));

                                                                            panicAction.setImageDrawable(mCtx.getDrawable
                                                                                    (timeout % 2 > 0
                                                                                            ? R.drawable.ic_warning_black_50dp
                                                                                            : R.drawable.ic_warning_white_50dp));
                                                                        }
                                                                        if (timeout % 2 > 0) {
                                                                            vibrator.vibrate(500);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFinish() {
                                                                        restorePanicButton(panicAction);
                                                                    }
                                                                }.start();
                                                                Toast.makeText(mCtx, R.string.panic_sent,
                                                                        Toast.LENGTH_LONG).show();
                                                                Log.d(CommonUtilities.LOG_TAG, "POSICION ENVIADA: "
                                                                        + latitude
                                                                        + longitude);
                                                            }
                                                        });
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError() {
                                                Toast.makeText(mCtx, "Error de comunicaciones",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    CommonUtilities.notifyGeoPermissionProblem(mCtx);
                                }
                            });
                }
            }
        };

        panicAction.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Toast.makeText(mCtx, R.string.time_pressed, Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build
                                .VERSION_CODES.LOLLIPOP) {
                            panicAction.setBackground(mCtx
                                    .getDrawable(R.drawable.oval_panic_pressed));
                            panicAction.setImageDrawable(mCtx
                                    .getDrawable(R.drawable.ic_warning_black_50dp));
                        }
                        handler.postDelayed(run, 3000);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (Build.VERSION.SDK_INT >= Build
                                .VERSION_CODES.LOLLIPOP) {
                            panicAction.setBackground(mCtx.getDrawable(R.drawable.oval_panic_shape));
                            panicAction.setImageDrawable(mCtx
                                    .getDrawable(R.drawable.ic_warning_white_50dp));
                        }
                        handler.removeCallbacks(run);
                        return true;
                }
                return false;
            }
        });


        swipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.greenForActions);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getServiceList(swipeRefresh);
            }
        });

        getServiceList(null);

        stopService(new Intent(mCtx, AlarmService.class));
        startService(new Intent(mCtx, AlarmService.class));
    }

    private void restorePanicButton(ImageButton panicAction) {
        if (Build.VERSION.SDK_INT >= Build
                .VERSION_CODES.LOLLIPOP) {
            panicAction.setBackground(mCtx.getDrawable
                    (R.drawable.oval_panic_shape));
            panicAction.setImageDrawable(mCtx.getDrawable
                    (R.drawable.ic_warning_white_50dp));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                /* To logout */
                logoutAction();

                break;
            case R.id.refresh:
                getServiceList(null);
                break;
            case R.id.search:
                dialogForSearch(mCtx);
                break;
            case R.id.data:
                dialogForData();
                break;
            case R.id.settings:
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getPackageName()));
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SERVICE_INTENT) {
            if (data != null) {
                if (data.getBooleanExtra("needRefresh", false)) {
                    getServiceList(null);
                }
            }
        }
    }

    /** Put click listener to recycler view */
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final
        MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /** Make the web service call to get service list */
    private void getServiceList(final SwipeRefreshLayout swipeRefresh) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION}, CommonUtilities.PERMISSION_REQUEST_CODE);
            return;
        }
        if (!CommonUtilities.isLocationEnabled(mCtx)) {
            CommonUtilities.enableNotify(mCtx);
        } else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (null != location) {
                                final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                if (null == swipeRefresh) {
                                    progressDialog.setMessage("Cargando");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);
                                }

                                Map<String, String> params = new HashMap<>();
                                params.put("user", CommonUtilities.getPreference(mCtx, CommonUtilities.USERID, null));
                                params.put("lat", String.format("%s", location.getLatitude()));
                                params.put("lng", String.format("%s", location.getLongitude()));
                                params.put("search", search);

                                WebService.getServiceListAction(mCtx, params, new WebService.RequestListener() {
                                    @Override
                                    public void onSuccess(String response) {
                                        if (null != swipeRefresh) {
                                            swipeRefresh.setRefreshing(false);
                                        } else {
                                            progressDialog.dismiss();
                                        }
                                        processResponse(response);
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                                        if (null != swipeRefresh) {
                                            swipeRefresh.setRefreshing(false);
                                        } else {
                                            progressDialog.dismiss();
                                        }
                                    }

                                });

                                cleanFiltersStrings();
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
                            if (null != swipeRefresh) {
                                swipeRefresh.setRefreshing(false);
                            }
                        }
                    });
        }
    }

    private void processResponse(String response) {
        try {

            JSONObject services = new JSONObject(response);

            int code = services.getInt("code");
            switch (code) {
                case CommonUtilities.RESPONSE_OK:
                    processData(services);
                    break;
                default:
                    CommonUtilities.forceLogout(MainActivity.this);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processData(JSONObject services) throws JSONException {
        TextView ticketsAdvice = (TextView) findViewById(R.id.tickets_out);
        servicesObjectList.clear();
        serviceAdapter.notifyDataSetChanged();
        JSONArray jsonArray = services.getJSONArray("services");
        if (jsonArray.length() == 0) {
            ticketsAdvice.setVisibility(View.VISIBLE);
        } else {
            ticketsAdvice.setVisibility(View.GONE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ServiceObject service = new ServiceObject();
                service.setId(object.getString("id"))
                        .setName(object.getString("name"))
                        .setLat(object.getDouble("pos_lat"))
                        .setLng(object.getDouble("pos_lng"))
                        .setElementTypeId(object.getInt("element_type_id"))
                        .setColor(object.getString("color").charAt(0))
                        .setTicketID(object.getString("ticket_id"))
                        .setTicketDetail(object.getString("description"));
                servicesObjectList.add(service);
            }
        }

    }


    private void cleanFiltersStrings() {
        search = "";
    }

    private void dialogForSearch(Context context) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(context);
        alerBuilder.setMessage("Búsqueda");
        alerBuilder.setView(R.layout.search);
        alerBuilder.setPositiveButton("Búscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = alerBuilder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = (EditText) dialog.findViewById(R.id.searchInput);
                assert searchText != null;
                search = searchText.getText().toString().trim();
                getServiceList(null);
                dialog.dismiss();
            }
        });
    }

    private void dialogForData() {
        new AlertDialog.Builder(mCtx)
                .setTitle("Datos")
                .setMessage(
                        "Esta aplicación tiene uso de datos. Para evitar gastar dinero" +
                        " puedes activar la opción \"Solo Wifi\"" +
                        "\nRecuerda que tu arribo y primera novedad son necesarias para validar el servicio"
                )
                .setPositiveButton("Solo Wifi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtilities.putPreference(mCtx, CommonUtilities.ONLY_WIFI, "1");
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Wifi y datos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtilities.putPreference(mCtx, CommonUtilities.ONLY_WIFI, "0");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void logoutAction() {
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION}, 200);
            return;
        }

        if (!CommonUtilities.isLocationEnabled(mCtx)) {
            CommonUtilities.enableNotify(mCtx);
        } else {
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (null != location) {
                                Map<String, String> params = new HashMap<>();
                                params.put("user", CommonUtilities.getPreference(mCtx, CommonUtilities.USERID, null));
                                params.put("lat", String.format("%s", location.getLatitude()));
                                params.put("lng", String.format("%s", location.getLongitude()));
                                params.put("phoneDate", CommonUtilities.getUTCDateTime());

                                WebService.logoutAction(mCtx, params, new WebService.RequestListener() {
                                    @Override
                                    public void onSuccess(String response) {
                                        stopService(new Intent(mCtx, AlarmService.class));
                                        CommonUtilities.deletePreference(mCtx, CommonUtilities.USERID);
                                        startActivity(new Intent(mCtx, LoginActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onError() {
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
}
