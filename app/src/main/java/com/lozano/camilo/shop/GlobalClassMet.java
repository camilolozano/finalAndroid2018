package com.lozano.camilo.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class GlobalClassMet {

    Context mContext;
    boolean isOnnet;
    SharedPreferences prefsUser;
    String localhost;


    public GlobalClassMet(Context mContext) {
        this.mContext = mContext;
    }

    public boolean ConectionValidate() {
        if (conectadoWifi()) {
            System.err.println("wifi: " + conectadoWifi());
            if (ValidateIntetnetOk()) {
                return true;
            } else {
                return false;
            }

        } else {
            if (conectRedMovil()) {
                System.err.println("internet: " + conectRedMovil());
                if (isOnNetNetwork()) {
                    return true;
                } else {
                    return true;
                }
            } else {
                return false;
            }

        }
    }


    protected Boolean conectadoWifi() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Boolean conectRedMovil() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean ValidateIntetnetOk() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /*public Boolean isOnlineNet() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }*/

    public boolean isOnNetNetwork() {
        prefsUser = mContext.getSharedPreferences("UserData", mContext.MODE_PRIVATE);
        localhost = (prefsUser.getString("localhost", mContext.getResources().getString(R.string.localhost)));
        new Thread(new Runnable() {
            public void run() {
                String urlvalidate = "http://" + localhost + ":8000/create-user-app/validate-email/kamilo92@live.com";
                final RequestQueue queue = Volley.newRequestQueue(mContext);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, urlvalidate, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                System.err.println("true");
                                isOnnet = true;
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.err.println("false");
                                isOnnet = false;

                            }
                        });
                queue.add(jsObjRequest);

            }
        }).start();

        return isOnnet;
    }

    public void ToastInternetNot(Context context) {
        Toast.makeText(context, "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
    }

    public void ToastProblem(Context context) {
        Toast.makeText(context, "Problemas de conexion a internet", Toast.LENGTH_SHORT).show();
    }

    public void ProblemApp(Context context) {
        Toast.makeText(context, "Ocurrio un error intentalo mas tarde", Toast.LENGTH_SHORT).show();
    }


    public double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        double distancia = radioTierra * va2;

        return distancia;
    }


}