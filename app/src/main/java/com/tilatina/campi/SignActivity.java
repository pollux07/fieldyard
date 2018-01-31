package com.tilatina.campi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tilatina.campi.Utilities.CommonUtilities;
import com.tilatina.campi.Utilities.DBManager;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SignActivity extends AppCompatActivity {
    public static final String ID = "element_id";
    public static final String TICKET_ID = "ticket_id";

    Activity mActivity;
    Context mCtx;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSendSignButton;

    Uri mFile;

    String mAlertTitle;
    String mMessageButton;
    String mClientName;
    int mRateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mActivity = this;
        mCtx = this;

        mAlertTitle = "Agregar";
        mMessageButton = "Ingresar";
        captureClientOptions(mAlertTitle, mMessageButton);

        mFusedLocationProviderClient = new FusedLocationProviderClient(mCtx);
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSendSignButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSendSignButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_button);
        mSendSignButton = (Button) findViewById(R.id.send_sign_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSendSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                mFile = getImageUri(signatureBitmap);
                //sendNovelty(mFile);
                mAlertTitle = "Selecciona";
                mMessageButton = "Enviar";
                captureClientOptions(mAlertTitle, mMessageButton);
            }
        });
    }

    private void captureClientOptions(String mAlertTitle, String mMessageButton) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mCtx);
        alertBuilder.setMessage(mAlertTitle);
        alertBuilder.setView(R.layout.client_options);
        alertBuilder.setPositiveButton(mMessageButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog dialog = alertBuilder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

        final TextView tvNameTitle = (TextView) dialog.findViewById(R.id.tv_client_name);
        final TextView tvQuality = (TextView) dialog.findViewById(R.id.tv_quality);
        final EditText etName = (EditText) dialog.findViewById(R.id.et_client_name);
        final LinearLayout lLOptions = (LinearLayout) dialog.findViewById(R.id.linear_options);
        final ImageView ivBad = (ImageView) dialog.findViewById(R.id.view_bad);
        final ImageView ivRegular = (ImageView) dialog.findViewById(R.id.view_regular);
        final ImageView ivGood = (ImageView) dialog.findViewById(R.id.view_good);
        assert tvNameTitle != null;
        assert etName != null;
        assert tvQuality != null;
        assert lLOptions != null;
        assert ivBad != null;
        assert ivRegular != null;
        assert ivGood != null;

        if (mAlertTitle.equals("Agregar")) {
            tvNameTitle.setVisibility(View.VISIBLE);
            etName.setVisibility(View.VISIBLE);
            tvQuality.setVisibility(View.GONE);
            lLOptions.setVisibility(View.GONE);

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etName.setError(null);
                    boolean cancel = false;
                    View focusView = null;

                    String clientName = etName.getText().toString();
                    if (TextUtils.isEmpty(clientName)) {
                        etName.setError(getString(R.string.error_field_required));
                        focusView = etName;
                        cancel = true;
                    }

                    if (cancel) {
                        focusView.requestFocus();
                    } else {
                        mClientName = clientName;
                        dialog.dismiss();
                    }
                }
            });

            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_FIRST_USER);
                    finish();
                }
            });
        } else{
            tvNameTitle.setVisibility(View.GONE);
            etName.setVisibility(View.GONE);
            tvQuality.setVisibility(View.VISIBLE);
            lLOptions.setVisibility(View.VISIBLE);

            ivBad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivBad.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_bad_filled));
                    ivRegular.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_regular));
                    ivGood.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_good));
                    mRateService = 0;
                }
            });

            ivRegular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivBad.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_bad));
                    ivRegular.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_regular_filled));
                    ivGood.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_good));
                    mRateService = 1;
                }
            });

            ivGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivBad.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_bad));
                    ivRegular.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_regular));
                    ivGood.setImageDrawable(ContextCompat.getDrawable(mCtx,
                            R.drawable.ic_good_filled));
                    mRateService = 2;
                }
            });

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(CommonUtilities.LOG_TAG, "VALOR DE LA CALIFICACION: " + mRateService);
                    dialog.dismiss();
                    sendNovelty(mFile);
                }
            });

            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

    }

    public Uri getImageUri(Bitmap inImage) {
        String dateString = CommonUtilities.getPicName();
        //create a mFile to write mBitmap data
        File f = new File(Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dateString + ".jpg");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100 , bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in mFile
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(f);
    }

    private void sendNovelty(final Uri selectedImage) {
        final String userId = CommonUtilities
                .getPreference(mCtx, CommonUtilities.USERID, null);
        final String elementId = getIntent().getStringExtra("element_id");
        final String  ticketId = getIntent().getStringExtra("ticket_id");
        final String date = CommonUtilities.getUTCDateTime();

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
                                final double latitude = location.getLatitude();
                                final double longitude = location.getLongitude();
                                final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                progressDialog.setMessage("Enviando firma");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);

                                final Map<String, String> params = new HashMap<>();
                                params.put("user", userId);
                                params.put("element", elementId);
                                params.put("ticket_id", ticketId);
                                params.put("lat", String.format("%s", latitude));
                                params.put("lng", String.format("%s", longitude));
                                params.put("phoneDate", date);
                                params.put("client_name", mClientName);
                                params.put("rate", String.valueOf(mRateService));

                                if (null != selectedImage) {
                                    WebService.uploadSign("file_picture", selectedImage.getPath(), null,
                                            params, new WebService.RequestListener() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    Log.d(CommonUtilities.LOG_TAG, "uploadNovelty#onSuccess");
                                                    try {
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        int code = jsonResponse.getInt("code");
                                                        if (code == CommonUtilities.RESPONSE_OK) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(mCtx, "Firma enviada", Toast.LENGTH_SHORT).show();
                                                                    File file = new File(selectedImage.getPath());
                                                                    if (file.delete()) {
                                                                        Log.d(CommonUtilities.LOG_TAG, "File deleted");
                                                                    }
                                                                    if (progressDialog.isShowing()) {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    setResult(RESULT_OK);
                                                                    finish();
                                                                }
                                                            });
                                                        } else if (code == CommonUtilities.ELEMENT_NOT_FOUND) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(mCtx, "Elemento no encontrado", Toast.LENGTH_SHORT).show();
                                                                    File file = new File(selectedImage.getPath());
                                                                    if (file.delete()) {
                                                                        Log.d(CommonUtilities.LOG_TAG, "File deleted");
                                                                    }
                                                                    if (progressDialog.isShowing()) {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    finish();
                                                                }
                                                            });
                                                        } else if (code == CommonUtilities.INVALID_TICKET) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (progressDialog.isShowing()) {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    File file = new File(selectedImage.getPath());
                                                                    if (file.delete()) {
                                                                        Log.d(CommonUtilities.LOG_TAG, "File deleted");
                                                                    }
                                                                    finish();
                                                                    Toast.makeText(mCtx, "El ticket fue cerrado o cancelado", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onError() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String picturePath
                                                                    = String.format("%s",
                                                                    selectedImage.getPath());
                                                            DBManager dbManager = new DBManager(mCtx);
                                                            dbManager.insertSigns(userId, elementId,
                                                                    ticketId, latitude, longitude,
                                                                    date, mClientName, String.valueOf(mRateService), "file_title",
                                                                    picturePath);
                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                            Toast.makeText(mCtx, "Error de comunicaciones", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                }
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_FIRST_USER);
        finish();
        super.onBackPressed();
    }
}
