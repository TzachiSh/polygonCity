package com.map.polygon.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by zahi on 19/09/2017.
 */

public class VolleyUtils {

    private RequestQueue requestQueue;
    private String stringResponse;


    public VolleyUtils(Context context) {
        requestQueue = Volley.newRequestQueue(context);

    }

    public void executeRequest(String url,final VolleyCallback callback) {
        //  introduced to make networking calls
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stringResponse = response;
                Log.e("RES", " res::" + stringResponse);
                callback.getResponse(stringResponse);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);

    }

    public interface VolleyCallback
    {
        void getResponse(String response);
    }

}
