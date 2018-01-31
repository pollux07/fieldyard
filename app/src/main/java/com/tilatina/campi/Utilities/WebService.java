package com.tilatina.campi.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Derechos reservados tilatina.
 */
public class WebService {

    private static String TAG = "WebServices";
    //private static String BASE_URL = "http://192.168.0.84/ws/campi/1.0";
    private static String BASE_URL = "http://ws.tilatina.com/ws/campi/1.0";
    private static int DEFAULT_TIMEOUT = 120000;

    public interface RequestListener {
        void onSuccess(String response);
        void onError();
    }

    public static void loginAction(Context context, final Map<String, String> params,
                                   final RequestListener requestListener) {

        String url = String.format("%s/loginCheck", BASE_URL);
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, String.format("Login response: %s", response));
                if (!expectedJson(response)) {
                    requestListener.onError();
                }

                requestListener.onSuccess(response);
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(loginRequest);
    }

    public static void logoutAction(Context context, final Map<String, String> params,
                                    final RequestListener requestListener) {

        String url = String.format("%s/logoutCheck", BASE_URL);
        StringRequest logoutRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, String.format("Logout response: %s", response));
                if (!expectedJson(response)) {
                    requestListener.onError();
                }
                requestListener.onSuccess(response);
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        logoutRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(logoutRequest);
    }

    public static void getServiceListAction(Context context, final Map<String, String> params,
                                            final RequestListener requestListener) {
        String url = String.format("%s/getServiceList", BASE_URL);
        Log.d(TAG, params.toString());
        StringRequest getServiceListRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }

                        requestListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestListener.onError();
                    }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };

        getServiceListRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(getServiceListRequest);
    }

    public static void getTicketTask(Context context,
                                     final String ticketId,
                                     final String userId,
                                     final RequestListener requestListener){
        String url = String.format("%s/getTicketTask", BASE_URL);
        StringRequest ticketTaskAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }

                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("ticket_id", String.format("%s", ticketId));
                params.put("user_id", String.format("%s", userId));
                return params;
            }
        };

        ticketTaskAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(ticketTaskAction);
    }

    public static void makeArrival(Context context, final Map<String, String> params,
                                   final RequestListener requestListener) {
        String url = String.format("%s/makeArrival", BASE_URL);
        StringRequest makeArrivalAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, String.format("Make arrival response: %s", response));
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestListener.onError();
                    }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        makeArrivalAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(makeArrivalAction);
    }

    public static void makeExit(Context context, final Map<String, String> params,
                                final RequestListener requestListener) {
        String url = String.format("%s/makeExit", BASE_URL);
        StringRequest makeExitAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, String.format("Make Exit response: %s", response));
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestListener.onError();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        makeExitAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(makeExitAction);
    }

    static void sendNoveltyDescriptionAction(Context context, final Map<String, String> params,
                                             final RequestListener requestListener) {
        String url = String.format("%s/novelty", BASE_URL);
        StringRequest sendNoveltyRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestListener.onError();
                    }
            }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        sendNoveltyRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(sendNoveltyRequest);
    }

    public static void createTask(Context context,
                                        final Map<String, String> params,
                                        final RequestListener requestListener) {
        String url = String.format("%s/createTask", BASE_URL);
        StringRequest createTask = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        createTask.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(createTask);
    }

    /**
     * Uploads file to server in a separated thread. If file can not be uploaded signals a
     * communications error through the onErrorListener. NOTE: As this method runs under an
     * AsynTask, listeners are required to issue a new Runnable task in order to show UI
     * messages.
     * @param fileTitle File name that will be reported to the server.
     * @param path Whole file path within the mobile equipment.
     * @param headers Headers to be sent along the request.
     * @param requestParams Request params.
     * @param requestListener request listener
     */
    public static void uploadNovelty(final String fileTitle, final String path,
                                      final Map<String, String> headers,
                                      final Map<String, String> requestParams,
                                      final RequestListener requestListener) {

        Log.d("FileUploader", String.format("path='%s'", path));
        final Handler mHandler = new Handler(Looper.getMainLooper());


        final File sourceFile = new File(path);
        List<String> paramList = new ArrayList<>();
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            try {
                paramList.add(param.getKey() + "=" + URLEncoder.encode(param.getValue(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String queryString = TextUtils.join("&", paramList);

        if (!sourceFile.isFile()) {
            Log.e("FileUploader", String.format("Source File '%s' does not exist", path));
            requestListener.onError();
        } else {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Log.d("WebService", "Intentando mandar.... antes de conexión");
                        HttpURLConnection conn;
                        DataOutputStream dos;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1024 * 1024;


                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(String.format("%s/novelty?%s",
                                BASE_URL, queryString));
                        Log.d("FileUploader", String.format("Uploading to %s", url.toString()));

                        // Open a HTTP  connection to  the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("file", fileTitle);
                        if (headers != null) {
                            for (Map.Entry<String, String> entry : headers.entrySet()) {
                                conn.setRequestProperty(entry.getKey(), entry.getValue());
                            }
                        }

                        if (null == conn.getOutputStream()) {
                            requestListener.onError();
                            return null;
                        }

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + fileTitle + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        }

                        // send multipart form data necesssary after file data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        //close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        Log.d("FileUploader", "HTTP Response is : "
                                + serverResponseMessage + ": " + serverResponseCode);

                        if (serverResponseCode == 200
                                || serverResponseCode == CommonUtilities.INVALID_TICKET
                                || serverResponseCode == CommonUtilities.ELEMENT_NOT_FOUND) {

                            //Para ver la respuesta
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String output;
                            while ((output = br.readLine()) != null) {
                                sb.append(output);
                            }
                            Log.d("SERVER RESPONSE ----- ", sb.toString());

                            requestListener.onSuccess(sb.toString());
                        } else {
                            Log.e("FileUploader", String.format("Response code %s (%s) from '%s'",
                                    serverResponseCode, serverResponseMessage, url.toString()));
                            requestListener.onError();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestListener.onError();
                            }
                        });
                    }
                    return null;
                }

            };
            task.execute();

        } // End else block
    }

    public static void getAddedParts(final Context context,
                                     final String eventId,
                                     final RequestListener requestListener) {
        String url = String.format("%s/getAddedParts", BASE_URL);
        StringRequest getAddedPartsAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }

                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("event_id", String.format("%s", eventId));
                return params;
            }
        };

        getAddedPartsAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(getAddedPartsAction);
    }

    public static void getSpareParts(final Context context,
                                     final RequestListener requestListener) {
        String url = String.format("%s/getSpareParts", BASE_URL);
        StringRequest getSparePartsAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        });

        getSparePartsAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(getSparePartsAction);
    }

    public static void setSpareParts(final Context context,
                                 final Map<String, String> params,
                                 final RequestListener requestListener) {
        String url = String.format("%s/setSpareParts", BASE_URL);
        StringRequest setSparePartsAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        setSparePartsAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(setSparePartsAction);
    }

    public static void deleteSpareParts(final Context context,
                                        final Map<String, String> params,
                                        final RequestListener requestListener) {
        String url = String.format("%s/deleteSpareParts", BASE_URL);
        StringRequest deleteSparePartsAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        deleteSparePartsAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(deleteSparePartsAction);
    }

    /**
     * Uploads file to server in a separated thread. If file can not be uploaded signals a
     * communications error through the onErrorListener. NOTE: As this method runs under an
     * AsynTask, listeners are required to issue a new Runnable task in order to show UI
     * messages.
     * @param fileTitle File name that will be reported to the server.
     * @param path Whole file path within the mobile equipment.
     * @param headers Headers to be sent along the request.
     * @param requestParams Request params.
     * @param requestListener request listener
     */
    public static void uploadSign(final String fileTitle, final String path,
                                     final Map<String, String> headers,
                                     final Map<String, String> requestParams,
                                     final RequestListener requestListener) {

        Log.d("FileUploader", String.format("path='%s'", path));
        final Handler mHandler = new Handler(Looper.getMainLooper());


        final File sourceFile = new File(path);
        List<String> paramList = new ArrayList<>();
        for (Map.Entry<String, String> param : requestParams.entrySet()) {
            try {
                paramList.add(param.getKey() + "=" + URLEncoder.encode(param.getValue(), "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String queryString = TextUtils.join("&", paramList);

        if (!sourceFile.isFile()) {
            Log.e("FileUploader", String.format("Source File '%s' does not exist", path));
            requestListener.onError();
        } else {
            AsyncTask task = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Log.d("WebService", "Intentando mandar.... antes de conexión");
                        HttpURLConnection conn;
                        DataOutputStream dos;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1024 * 1024;


                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(sourceFile);
                        URL url = new URL(String.format("%s/sign?%s",
                                BASE_URL, queryString));
                        Log.d("FileUploader", String.format("Uploading to %s", url.toString()));

                        // Open a HTTP  connection to  the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("file", fileTitle);
                        if (headers != null) {
                            for (Map.Entry<String, String> entry : headers.entrySet()) {
                                conn.setRequestProperty(entry.getKey(), entry.getValue());
                            }
                        }

                        if (null == conn.getOutputStream()) {
                            requestListener.onError();
                            return null;
                        }

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + fileTitle + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of  maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        }

                        // send multipart form data necesssary after file data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        //close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        Log.d("FileUploader", "HTTP Response is : "
                                + serverResponseMessage + ": " + serverResponseCode);

                        if (serverResponseCode == 200
                                || serverResponseCode == CommonUtilities.INVALID_TICKET
                                || serverResponseCode == CommonUtilities.ELEMENT_NOT_FOUND) {

                            //Para ver la respuesta
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String output;
                            while ((output = br.readLine()) != null) {
                                sb.append(output);
                            }
                            Log.d("SERVER RESPONSE ----- ", sb.toString());

                            requestListener.onSuccess(sb.toString());
                        } else {
                            Log.e("FileUploader", String.format("Response code %s (%s) from '%s'",
                                    serverResponseCode, serverResponseMessage, url.toString()));
                            requestListener.onError();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestListener.onError();
                            }
                        });
                    }
                    return null;
                }

            };
            task.execute();

        }
    }

    public static void sendLocationAction(final Context context,
                                          final Map<String, String> params,
                                          final RequestListener requestListener) {

        String url = String.format("%s/updatePosition?%s", BASE_URL, getUrlParams(params));
        Log.d(CommonUtilities.LOG_TAG, url);

        StringRequest sendInSite = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!expectedJson(response)) {
                    requestListener.onError();
                }
                requestListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                params.put("user", CommonUtilities.getPreference(context,
                        CommonUtilities.USERID, null));
                return params;
            }
        };

        sendInSite.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(sendInSite);
    }

    /**
     * Se envian la localizacion actual del dispositivo por medio del boton de panico haciendo
     * referencia a una emergencia
     * @param context Contexto
     * @param params se envia la localizacion y el id del telefono para saber que dispositivo
     *               realizo la llamada de panico
     * @param requestListener Callback al que se entrega el resultado
     */
    public static void panicAction(Context context,
                                   final Map<String, String> params,
                                   final RequestListener requestListener){
        String url = String.format("%s/isPanic", BASE_URL);
        StringRequest panicAction = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!expectedJson(response)) {
                            requestListener.onError();
                        }
                        requestListener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestListener.onError();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                return params;
            }
        };

        panicAction.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(panicAction);
    }

    private static boolean expectedJson(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            String code = jsonResponse.getString("code");
            if (code == null) {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Prepara que query string. Este método se creó para poder hacer requests con método
     * GET sin modificar la firma de los métodos.
     * @param params Parametros
     * @return Regresa respuesta del servidos
     */
    private static String getUrlParams(Map<String, String> params)  {
        List<String> tokens = new ArrayList<>();
        for (String key : params.keySet()) {
            try {
                tokens.add(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Random rnd = new Random();
        tokens.add(String.format("salt=%s", rnd.nextDouble()));
        return TextUtils.join("&", tokens);
    }

}
