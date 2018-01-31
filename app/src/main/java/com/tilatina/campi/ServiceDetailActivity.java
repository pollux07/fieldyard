package com.tilatina.campi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tilatina.campi.Utilities.CommonUtilities;
import com.tilatina.campi.Utilities.DBManager;
import com.tilatina.campi.Utilities.TicketAdapter;
import com.tilatina.campi.Utilities.TicketObjects;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ServiceDetailActivity extends AppCompatActivity {
    SwipeRefreshLayout mSwipeRefresh;

    private FloatingActionButton mOptions;
    private FloatingActionButton mNoveltyPic;
    private FloatingActionButton mComments;
    private FloatingActionButton mSign;
    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    private Boolean isFabOpen = false;

    Uri mCurrentPhotoPath;
    File picture;
    Activity mActivity;
    Context mCtx;
    FusedLocationProviderClient mFusedLocationProviderClient;
    double mLatitude;
    double mLongitude;
    Bundle extras;

    String mTicketId;
    int mElementType;

    List<TicketObjects> ticketObjectList = new ArrayList<>();
    TicketAdapter ticketAdapter = new TicketAdapter(ticketObjectList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        mActivity = this;
        mCtx = this;
        mFusedLocationProviderClient = new FusedLocationProviderClient(mCtx);
        int SITE = 6;
        int GPS = 4;

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refresh_details);
        final ImageButton panicAction = (ImageButton) findViewById(R.id.panicAction);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        restorePanicButton(panicAction);

        extras = getIntent().getExtras();
        final String element_name = extras.getString("element_name");
        final char color = extras.getChar("color");
        mElementType = extras.getInt("element_type");
        mTicketId = extras.getString("ticket_id");
        final String ticketDetail = extras.getString("ticket_detail");

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.taskListView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ticketAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),getRequestedOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        final TextView tv_ticketId = (TextView) findViewById(R.id.no_ticket);
        final TextView tv_nameTicket = (TextView) findViewById(R.id.name);
        final TextView tv_tikcetD = (TextView) findViewById(R.id.ticket_des);
        final ImageView iv_elementState = (ImageView) findViewById(R.id.stateIcon);
        mOptions = (FloatingActionButton) findViewById(R.id.fab_options);
        mNoveltyPic = (FloatingActionButton) findViewById(R.id.noveltyPictAction);
        mComments = (FloatingActionButton) findViewById(R.id.noveltyCommentAction);
        mSign = (FloatingActionButton) findViewById(R.id.signAction);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        final String userId = CommonUtilities.getPreference(getApplicationContext(),
                CommonUtilities.USERID, null);

        getTicketTask(mTicketId, userId);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTicketTask(mTicketId, userId);
            }
        });


        if ('G' == color) {
            if (mElementType == SITE) {
                iv_elementState.setBackgroundResource(R.drawable.ic_site_green);
            } else if (mElementType == GPS) {
                iv_elementState.setBackgroundResource(R.drawable.ic_gps_green);
            } else {
                iv_elementState.setBackgroundResource(R.drawable.ic_gm_green);
            }
        } else if ('Y' == color) {
            if (mElementType == SITE) {
                iv_elementState.setBackgroundResource(R.drawable.ic_site_yellow);
            } else if (mElementType == GPS) {
                iv_elementState.setBackgroundResource(R.drawable.ic_gps_yellow);
            } else {
                iv_elementState.setBackgroundResource(R.drawable.ic_gm_yellow);
            }
        } else if ('R' == color){
            if (mElementType == SITE) {
                iv_elementState.setBackgroundResource(R.drawable.ic_site_red);
            } else if (mElementType == GPS) {
                iv_elementState.setBackgroundResource(R.drawable.ic_gps_red);
            } else {
                iv_elementState.setBackgroundResource(R.drawable.ic_gm_red);
            }
        }

        tv_ticketId.setText(String.format("#%s", mTicketId));
        tv_nameTicket.setText(element_name);
        tv_tikcetD.setText(ticketDetail);
        invalidateOptionsMenu();

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
                } else {
                    mFusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (null != location) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
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
                                                restorePanicButton(panicAction);
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
                switch (event.getAction()){
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

        mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        mNoveltyPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comentAction(mCtx, mTicketId);
            }
        });

        mSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = new Intent(ServiceDetailActivity.this, SignActivity.class);
                signIntent.putExtra(SignActivity.ID, extras.getString("element_id"));
                signIntent.putExtra(SignActivity.TICKET_ID, extras.getString("ticket_id"));
                startActivityForResult(signIntent, 2);
            }
        });

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

    public void animateFAB(){

        if(isFabOpen){

            mOptions.startAnimation(rotate_backward);
            mNoveltyPic.startAnimation(fab_close);
            mComments.startAnimation(fab_close);
            mSign.startAnimation(fab_close);
            mNoveltyPic.setClickable(false);
            mComments.setClickable(false);
            mSign.setClickable(false);
            isFabOpen = false;

        } else {

            mOptions.startAnimation(rotate_forward);
            mNoveltyPic.startAnimation(fab_open);
            mComments.startAnimation(fab_open);
            mSign.startAnimation(fab_open);
            mNoveltyPic.setClickable(true);
            mComments.setClickable(true);
            mSign.setClickable(true);
            isFabOpen = true;

        }
    }

    private void takePicture(){
        if (!CommonUtilities.validateCanCaptureAndWritePicture(ServiceDetailActivity.this)) {
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String dateString = CommonUtilities.getPicName();

        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long total;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            total = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong() );
        } else {
            total = (statFs.getAvailableBlocks() * statFs.getBlockSize() );
        }

        if (total < 2e+7) {
            Toast.makeText(mCtx, "Lo siento, necesitas liberar espacio para poder capturar fotografías.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            picture = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dateString + ".jpg");
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCurrentPhotoPath = FileProvider.getUriForFile(mCtx, CommonUtilities.AUTHORITY, picture);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,mCurrentPhotoPath);
            } else {
                mCurrentPhotoPath = Uri.fromFile(picture);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
            }
            startActivityForResult(takePictureIntent, 1);
        }
    }

    private void comentAction(Context mCtx, final String ticketId) {
        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(mCtx);
        alerBuilder.setMessage("Comentario");
        alerBuilder.setView(R.layout.comment);
        alerBuilder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = alerBuilder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commentText = (EditText) dialog.findViewById(R.id.comment_input);
                if (commentText != null) {
                    String comment = commentText.getText().toString();
                    sendComment(ticketId, comment);
                } else {
                    commentText.setError("");
                }
                dialog.dismiss();
            }
        });
    }

    private void sendComment(final String ticketId, final String comment) {
        final ProgressDialog progressDialog = new ProgressDialog(mCtx);
        progressDialog.setMessage("Enviando comentario");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        if (comment!= null ) {

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
                                    final String userId = CommonUtilities
                                            .getPreference(mCtx, CommonUtilities.USERID, null);
                                    final double latitude = location.getLatitude();
                                    final double longitude = location.getLongitude();
                                    final String date = CommonUtilities.getUTCDateTime();
                                    final Map<String, String> params = new HashMap<>();
                                    params.put("ticket_id", ticketId);
                                    params.put("user_id", userId);
                                    params.put("description", comment);
                                    params.put("lat", String.format("%s", latitude));
                                    params.put("lng", String.format("%s", longitude));
                                    params.put("date", date);

                                    WebService.createTask(mCtx, params, new WebService.RequestListener() {
                                        @Override
                                        public void onSuccess(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                int code = jsonResponse.getInt("code");
                                                if (code == CommonUtilities.RESPONSE_OK) {
                                                    Toast.makeText(mCtx, "Comentario enviado", Toast.LENGTH_SHORT).show();
                                                    String userId = CommonUtilities.getPreference(getApplicationContext(),
                                                            CommonUtilities.USERID, null);
                                                    getTicketTask(ticketId, userId);
                                                    progressDialog.dismiss();
                                                } else if (code == CommonUtilities.ELEMENT_NOT_FOUND) {
                                                    progressDialog.cancel();
                                                    Toast.makeText(mCtx, "Elemento no encontrado", Toast.LENGTH_SHORT).show();
                                                } else if (code == CommonUtilities.INVALID_TICKET) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(ServiceDetailActivity.this,
                                                            MainActivity.class));
                                                    finish();
                                                    Toast.makeText(mCtx, "El ticket fue cerrado o cancelado", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError() {
                                            DBManager dbManager = new DBManager(mCtx);
                                            dbManager.insertComments(userId, ticketId, latitude,
                                                    longitude, date, comment);
                                            progressDialog.dismiss();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int TASK_SENT = 2;
        int NOVELTY_ACTIVITY = 100;
        if (requestCode == NOVELTY_ACTIVITY) {
            if (null != data) {
                if (data.getBooleanExtra("elementNotFound", false)) {
                    Toast.makeText(mCtx, "El elemento no existe o está dado de baja",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("needRefresh", true);
                    setResult(1, intent);
                    finish();
                }
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];

            try {
                CommonUtilities.downSize(this, mCurrentPhotoPath, 380, picture);
                Intent preview = new Intent(ServiceDetailActivity.this, PreviewPhoto.class);
                preview.putExtra(PreviewPhoto.IMAGE_PATH, mCurrentPhotoPath.toString());
                preview.putExtra(PreviewPhoto.ID, extras.getString("element_id"));
                preview.putExtra(PreviewPhoto.TICKET_ID, extras.getString("ticket_id"));
                startActivityForResult(preview, TASK_SENT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == 0 && requestCode == 2) {
            takePicture();
        }

            Log.d(CommonUtilities.LOG_TAG, "ENTRANDO A LA RESPUESTA CON CODIGO: " + resultCode + ", " + requestCode + "RESULT_OK: " + RESULT_OK);
        if (resultCode == -1 && requestCode == 2) {
            mSwipeRefresh.setRefreshing(true);
            String userId = CommonUtilities.getPreference(getApplicationContext(),
                    CommonUtilities.USERID, null);
            getTicketTask(extras.getString("ticket_id"), userId);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final double lat = extras.getDouble("lat");
        final double lng = extras.getDouble("lng");
        MenuItem item = menu.findItem(R.id.ride_me);
        if (0 == lat || 0 == lng) {
            item.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrival, menu);
        getMenuInflater().inflate(R.menu.finish, menu);
        getMenuInflater().inflate(R.menu.ride_me, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                break;
            case R.id.arrival:
                arrival();
                break;
            case R.id.finish:
                finishArrival();
                break;
            case R.id.ride_me:
                rideMe();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getTicketTask(String ticketId, String userId) {
        mSwipeRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mSwipeRefresh.setRefreshing(true);

        WebService.getTicketTask(mCtx, ticketId, userId, new WebService.RequestListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    ticketObjectList.clear();
                    ticketAdapter.notifyDataSetChanged();
                    JSONArray jsonArray = jsonResponse.getJSONArray("tasks");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String linkPhoto = null;
                        if (!object.getString("link_photo").equals("null")) {
                            linkPhoto = object.getString("link_photo").replaceAll("\\\\", "");
                        }
                        TicketObjects tTasks = new TicketObjects();
                            tTasks.setDate(object.getString("the_date"))
                                    .setTask(object.getString("description"))
                                    .setUser(object.getString("email"))
                                    .setElementType(mElementType)
                                    .setEventId(object.getString("event_id"))
                                    .setEventCanAdd(object.getString("event_can_add"))
                                    .setLinkPhoto(linkPhoto);
                        ticketObjectList.add(tTasks);
                        mSwipeRefresh.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mSwipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError() {
                mSwipeRefresh.setRefreshing(false);
                Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void arrival(){
        final String elementId = extras.getString("element_id");
        new AlertDialog.Builder(mCtx)
                .setTitle("Arribo")
                .setMessage("¿Confirma enviar arribo al servicio?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

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
                                                mLatitude = location.getLatitude();
                                                mLongitude = location.getLongitude();
                                                final String userId = CommonUtilities
                                                        .getPreference(mCtx, CommonUtilities.USERID, null);
                                                final String date = CommonUtilities.getUTCDateTime();

                                                final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                                progressDialog.setMessage("Enviando arribo");
                                                progressDialog.show();
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                Map<String, String> params = new HashMap<>();
                                                params.put("user", userId);
                                                params.put("element", String.format("%s", elementId));
                                                params.put("lat", String.format("%s", mLatitude));
                                                params.put("lng", String.format("%s", mLongitude));
                                                params.put("phoneDate", date);

                                                WebService.makeArrival(mCtx, params, new WebService.RequestListener() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        try {
                                                            processResponse(response);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        String userId = CommonUtilities.getPreference(getApplicationContext(),
                                                                CommonUtilities.USERID, null);
                                                        getTicketTask(mTicketId, userId);
                                                        progressDialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        DBManager dbManager = new DBManager(mCtx);
                                                        dbManager.insertArrival(userId, elementId,
                                                                mLatitude, mLongitude, date);
                                                        progressDialog.dismiss();
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
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void finishArrival() {
        final String elementId = extras.getString("element_id");
        new AlertDialog.Builder(mCtx)
                .setTitle("Salida")
                .setMessage("¿Confirma enviar Salida al servicio?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

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
                                                final String userId = CommonUtilities
                                                        .getPreference(mCtx, CommonUtilities.USERID, null);
                                                final String date = CommonUtilities.getUTCDateTime();
                                                mLatitude = location.getLatitude();
                                                mLongitude = location.getLongitude();
                                                final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                                progressDialog.setMessage("Enviando salida");
                                                progressDialog.show();
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                Map<String, String> params = new HashMap<>();
                                                params.put("user", userId);
                                                params.put("element", String.format("%s", elementId));
                                                params.put("lat", String.format("%s", mLatitude));
                                                params.put("lng", String.format("%s", mLongitude));
                                                params.put("phoneDate", date);

                                                WebService.makeExit(mCtx, params, new WebService.RequestListener() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        try {
                                                            JSONObject object = new JSONObject(response);
                                                            int code = object.getInt("code");
                                                            if (code == CommonUtilities.RESPONSE_OK) {
                                                                Toast.makeText(mCtx, "Salida enviada", Toast.LENGTH_SHORT).show();
                                                            } else if (code == CommonUtilities.ELEMENT_NOT_FOUND) {
                                                                Toast.makeText(mCtx, "El elemente no existe o está dado de baja",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent();
                                                                intent.putExtra("needRefresh", true);
                                                                setResult(1, intent);
                                                                finish();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        String userId = CommonUtilities.getPreference(mCtx,
                                                                CommonUtilities.USERID, null);
                                                        getTicketTask(mTicketId, userId);
                                                        progressDialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onError() {
                                                        DBManager dbManager = new DBManager(mCtx);
                                                        dbManager.insertExit(userId, elementId,
                                                                mLatitude, mLongitude, date);
                                                        progressDialog.dismiss();
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
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void rideMe() {
        final double lat = extras.getDouble("lat");
        final double lng = extras.getDouble("lng");

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
                                mLatitude = location.getLatitude();
                                mLongitude = location.getLongitude();
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

        new AlertDialog.Builder(mCtx)
                .setTitle("Llévame")
                .setMessage("¿Quieres navegar hacia esa ubicación?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkPackageExist(mCtx, "com.waze")) {

                            String uri = String.format("waze://?ll=%s, %s&navigate=yes", lat, lng);
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                            } catch (Exception e) {
                                new android.app.AlertDialog.Builder(mCtx)
                                        .setTitle("")
                                        .setMessage("Necesitas instalar Waze para ser llevado al destino.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        } else {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse(String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",
                                            mLatitude, mLongitude, lat, lng
                                    )));
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void processResponse(String response) throws JSONException {
        JSONObject object = new JSONObject(response);
        int code = object.getInt("code");
        switch (code) {
            case CommonUtilities.RESPONSE_OK :
                Toast.makeText(mCtx, "Arribo enviado", Toast.LENGTH_SHORT).show();
                break;
            case CommonUtilities.ELEMENT_NOT_FOUND :
                Toast.makeText(mCtx, "El elemente no existe o está dado de baja",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("needRefresh", true);
                setResult(1, intent);
                finish();
                break;
            default :
                CommonUtilities.forceLogout(ServiceDetailActivity.this);
                break;
        }
    }

    private boolean checkPackageExist(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            return false;
        }
    }
}
