package com.tilatina.campi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PreviewPhoto extends AppCompatActivity {
    public static final String IMAGE_PATH = "image_path";
    public static final String ID = "id";
    public static final String TICKET_ID = "ticket_id";
    Activity mActivity;
    Context mCtx;

    ImageView mPreviewPic;
    Uri mFile;

    static int mCurrRotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(CommonUtilities.LOG_TAG, getLocalClassName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);
        mActivity = this;
        mCtx = this;

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPreviewPic = (ImageView) findViewById(R.id.preview_picture);
        FloatingActionButton sendPic = (FloatingActionButton) findViewById(R.id.send_pic);
        final EditText descriptionPic = (EditText) findViewById(R.id.picture_description);

        //Image preview
        picture();
        sendPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descriptionPic.length() == 0){
                    descriptionPic.setError("Campo obligatorio");
                    descriptionPic.requestFocus();
                } else {
                    String pictureDesc = descriptionPic.getText().toString();
                    sendNovelty(pictureDesc, mFile);
                }
            }
        });
    }
    private void picture () {
        String picturePath = getIntent().getStringExtra("image_path");
        final Uri selectedImage = Uri.parse(picturePath);

        try {
            mPreviewPic.setImageBitmap(handleSamplingAndRotationBitmap(mCtx, selectedImage));
            mFile = getImageUri(handleSamplingAndRotationBitmap(mCtx, selectedImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        if (imageStream != null) {
            imageStream.close();
        } else {
            Toast.makeText(mCtx, R.string.camera_error, Toast.LENGTH_SHORT).show();
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private Bitmap rotateImageIfRequired(Bitmap img) throws IOException {
        Matrix matrix = new Matrix();
        switch (mCurrRotation) {
            case 0:
                matrix.postRotate(0);
                break;
            case 90:
                matrix.postRotate(90);
                break;
            case 180:
                matrix.postRotate(180);
                break;
            case 270:
                matrix.postRotate(270);
        }
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
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

    private void sendNovelty(final String pictureDesc, final Uri selectedImage) {
        final String userId = CommonUtilities
                .getPreference(mCtx, CommonUtilities.USERID, null);
        final String elementId = getIntent().getStringExtra("id");
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

            FusedLocationProviderClient fusedLocationProviderClient =
                    new FusedLocationProviderClient(mCtx);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (null != location) {
                                final double latitude = location.getLatitude();
                                final double longitude = location.getLongitude();

                                final ProgressDialog progressDialog = new ProgressDialog(mCtx);
                                progressDialog.setMessage("Enviando novedad");
                                progressDialog.show();
                                progressDialog.setCanceledOnTouchOutside(false);

                                final Map<String, String> params = new HashMap<>();
                                params.put("user", userId);
                                params.put("element", elementId);
                                params.put("ticket_id", ticketId);
                                params.put("lat", String.format("%s", latitude));
                                params.put("lng", String.format("%s", longitude));
                                params.put("phoneDate", date);
                                params.put("description", pictureDesc);

                                if (null != selectedImage) {
                                    WebService.uploadNovelty("file_picture", selectedImage.getPath(), null,
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
                                                                    Toast.makeText(mCtx, "Novedad enviada", Toast.LENGTH_SHORT).show();
                                                                    File file = new File(selectedImage.getPath());
                                                                    if (file.delete()) {
                                                                        Log.d(CommonUtilities.LOG_TAG, "File deleted");
                                                                    }
                                                                    if(progressDialog.isShowing()) {
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    mCurrRotation = 0;
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
                                                                    mCurrRotation = 0;
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
                                                                    mCurrRotation = 0;
                                                                    finish();
                                                                    Toast.makeText(mCtx, "El ticket fue cerrado o cancelado", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    } catch (JSONException e) {
                                                        if (progressDialog.isShowing()) {
                                                            progressDialog.dismiss();
                                                        }
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
                                                            dbManager.insertNovelty(userId, elementId,
                                                                    ticketId, latitude, longitude,
                                                                    date, pictureDesc,
                                                                    "file_picture", picturePath);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel, menu);
        getMenuInflater().inflate(R.menu.rotate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                mCurrRotation = 0;
                setResult(RESULT_FIRST_USER);
                finish();
                break;
            case R.id.rotate:
                mCurrRotation += 90;
                if (mCurrRotation == 360) {
                    mCurrRotation = 0;
                }
                picture();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        mCurrRotation = 0;
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        mCurrRotation = 0;
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
