package com.tilatina.campi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.tilatina.campi.Utilities.CommonUtilities;
import com.tilatina.campi.Utilities.DBManager;
import com.tilatina.campi.Utilities.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Derechos reservador tilatina.
 */

public class NetworkStatusConnection extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            final DBManager dbManager = new DBManager(context);

            final Cursor cursorNovelties = dbManager.getAllNovelties();

            if (cursorNovelties.moveToFirst()) {
                do {
                    final String idNovelty = cursorNovelties.getString(0);
                    final String userIdNovelty = cursorNovelties.getString(1);
                    final String elementIdNovelty = cursorNovelties.getString(2);
                    final String ticketIdNovelty = cursorNovelties.getString(3);
                    final String latNovelty = cursorNovelties.getString(4);
                    final String lngNovelty = cursorNovelties.getString(5);
                    final String dateNovelty = cursorNovelties.getString(6);
                    final String descriptionNovelty = cursorNovelties.getString(7);
                    final String fileTitleNovelty = cursorNovelties.getString(8);
                    final String filePathNovelty = cursorNovelties.getString(9);

                    Uri savedImage = Uri.parse(filePathNovelty);

                    Map<String, String> params = new HashMap<>();
                    params.put("user", userIdNovelty);
                    params.put("element", elementIdNovelty);
                    params.put("ticket_id", ticketIdNovelty);
                    params.put("lat", latNovelty);
                    params.put("lng", lngNovelty);
                    params.put("phoneDate", dateNovelty);
                    params.put("description", descriptionNovelty);

                    WebService.uploadNovelty(fileTitleNovelty, savedImage.getPath(), null, params,
                            new WebService.RequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int code = jsonResponse.getInt("code");
                                if (code == CommonUtilities.RESPONSE_OK) {
                                    dbManager.deleteNovelty(idNovelty);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError() {

                        }
                    });

                } while (cursorNovelties.moveToNext());
                dbManager.deleteNovelties();
            }

            final Cursor cursorComments = dbManager.getAllComments();
            if (cursorComments.moveToFirst()) {
                do {
                    final String idComment = cursorComments.getString(0);
                    final String userIdComment = cursorComments.getString(1);
                    final String ticketIdComment = cursorComments.getString(2);
                    final String latComment = cursorComments.getString(3);
                    final String lngComment = cursorComments.getString(4);
                    final String dateComment = cursorComments.getString(5);
                    final String commentComment = cursorComments.getString(6);

                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", userIdComment);
                    params.put("ticket_id", ticketIdComment);
                    params.put("lat", latComment);
                    params.put("lng", lngComment);
                    params.put("date", dateComment);
                    params.put("description", commentComment);

                    WebService.createTask(context, params, new WebService.RequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int code = jsonResponse.getInt("code");
                                if (code == CommonUtilities.RESPONSE_OK) {
                                    dbManager.deleteComment(idComment);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

                } while (cursorComments.moveToNext());
                dbManager.deleteComments();
            }

            final Cursor cursorSigns = dbManager.getAllSigns();
            if (cursorSigns.moveToFirst()) {
                do {
                    final String idSign = cursorSigns.getString(0);
                    final String userIdSign = cursorSigns.getString(1);
                    final String elementIdSign = cursorSigns.getString(2);
                    final String ticketIdSign = cursorSigns.getString(3);
                    final String latSign = cursorSigns.getString(4);
                    final String lngSign = cursorSigns.getString(5);
                    final String dateSign = cursorSigns.getString(6);
                    final String clientNameSign = cursorSigns.getString(7);
                    final String rateSign = cursorSigns.getString(8);
                    final String fileTitleSign = cursorSigns.getString(9);
                    final String filePathSign = cursorSigns.getString(10);

                    Uri savedSign = Uri.parse(filePathSign);

                    Map<String, String> params = new HashMap<>();
                    params.put("user", userIdSign);
                    params.put("element", elementIdSign);
                    params.put("ticket_id", ticketIdSign);
                    params.put("lat", latSign);
                    params.put("lng", lngSign);
                    params.put("phoneDate", dateSign);
                    params.put("client_name", clientNameSign);
                    params.put("rate", rateSign);

                    WebService.uploadSign(fileTitleSign, savedSign.getPath(), null, params,
                            new WebService.RequestListener() {
                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        int code = jsonResponse.getInt("code");
                                        if (code == CommonUtilities.RESPONSE_OK) {
                                            dbManager.deleteSign(idSign);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });

                } while (cursorSigns.moveToNext());
                dbManager.deleteSigns();
            }

            final Cursor cursosArrivals = dbManager.getAllArrivals();
            if (cursosArrivals.moveToFirst()) {
                do {
                    final String idArrival = cursosArrivals.getString(0);
                    final String userIdArrival = cursosArrivals.getString(1);
                    final String elementIdArrival = cursosArrivals.getString(2);
                    final String latArrival = cursosArrivals.getString(3);
                    final String lngArrival = cursosArrivals.getString(4);
                    final String dateArrival = cursosArrivals.getString(5);

                    Map<String, String> params = new HashMap<>();
                    params.put("user", userIdArrival);
                    params.put("element", elementIdArrival);
                    params.put("lat", latArrival);
                    params.put("lng", lngArrival);
                    params.put("phoneDate", dateArrival);

                    WebService.makeArrival(context, params, new WebService.RequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int code = jsonResponse.getInt("code");
                                if (code == CommonUtilities.RESPONSE_OK) {
                                    dbManager.deleteArrival(idArrival);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

                } while (cursosArrivals.moveToNext());
                dbManager.deleteArrivals();
            }

            final Cursor cursorExits = dbManager.getAllExits();
            if (cursorExits.moveToFirst()) {
                do {
                    final String idExit = cursorExits.getString(0);
                    final String userIdExit = cursorExits.getString(1);
                    final String elementIdExit = cursorExits.getString(2);
                    final String latExit = cursorExits.getString(3);
                    final String lngExit = cursorExits.getString(4);
                    final String dateExit = cursorExits.getString(5);

                    Map<String, String> params = new HashMap<>();
                    params.put("user", userIdExit);
                    params.put("element", elementIdExit);
                    params.put("lat", latExit);
                    params.put("lng", lngExit);
                    params.put("phoneDate", dateExit);

                    WebService.makeExit(context, params, new WebService.RequestListener() {
                        @Override
                        public void onSuccess(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int code = jsonResponse.getInt("code");
                                if (code == CommonUtilities.RESPONSE_OK) {
                                    dbManager.deleteExit(idExit);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

                } while (cursorExits.moveToNext());
                dbManager.deleteExits();
            }
        }
    }
}
