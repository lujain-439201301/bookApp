package com.example.bookapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.buyer.BuyerMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


@RequiresApi(api = Build.VERSION_CODES.O)
public class notificationService extends Service {
    public static final int notify = (1000 * 3 );  //interval between two services(Here Service run every 30 seconds)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    private SessionManager session;
    private JSONObject jsonObject;
    private String msgs_count;
    private Intent intent1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        session = new SessionManager(getApplicationContext());

        intent1 = new Intent();
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        try {
            mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
        }catch (Exception ex){

        }
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onCreate() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
        //Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    //Toast.makeText(checkNewNotificationService.this, "Service is running", Toast.LENGTH_SHORT).show();
                    try {
                        doPostRequest();
                    }catch (Exception e){

                    }

                }
            });
        }
    }

    private void doPostRequest() {
        String userType = "";
        String seller_id = "";
        String buyer_id = "";
        String para = "";
        if(session.getUserType().equals("Seller")) {
            para = "?userType=te&buyer_id=0&seller_id="+session.getUserID();
        }else{
            para = "?userType=st&seller_id=0&buyer_id="+session.getUserID();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                                    URLs.CHECK_MSGS+para  ,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doAction(response);
                        //Toast.makeText(itemsListActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(checkNewNotificationService.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                return params;
            }
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                /*
                if(session.getUserType().equals("Seller")) {
                    params.put("userType",  "te");
                    params.put("buyer_id", "0");
                    params.put("seller_id", session.getUserID());
                }else{
                    params.put("userType",  "st");
                    params.put("buyer_id", session.getUserID());
                    params.put("seller_id", "0");
                }
                */
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private JSONArray users = null;
    private String json;

    public void doAction(String response){
        try {

            //System.out.println(response);
            jsonObject = new JSONObject(response);
            users = jsonObject.getJSONArray("result");

            JSONObject jo = users.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                for(int i=1;i<users.length();i++){
                    jo = users.getJSONObject(i);
                    msgs_count = jo.getString("msgs_count");
                    if(!session.getMsgCount().equals(msgs_count)){
                        new Notification(this, "You have (" + msgs_count + ") new messages");
                        session.setMsgCount(msgs_count);
                    }

                    if(session.getUserType().equals("Seller")) {
                        intent1.setAction("com.example.bookapp.seller");
                        intent1.putExtra("msgs_count", msgs_count);
                        sendBroadcast(intent1);
                    }else{
                        intent1.setAction("com.example.bookapp.buyer");
                        intent1.putExtra("msgs_count", msgs_count);
                        sendBroadcast(intent1);

                    }
                }
            }else{
                session.setMsgCount("0");
                if(session.getUserType().equals("Seller")) {
                    intent1.setAction("com.example.bookapp.seller");
                    intent1.putExtra("msgs_count", "0");
                    sendBroadcast(intent1);
                }else{
                    intent1.setAction("com.example.bookapp.buyer");
                    intent1.putExtra("msgs_count", "0");
                    sendBroadcast(intent1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}