package com.example.bookapp.buyer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.AlertManager;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.URLs;
import com.example.bookapp.adapters.InterestAdapter;
import com.example.bookapp.models.Category;
import com.example.bookapp.models.Interest;
import com.google.android.material.navigation.NavigationView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class interestActivity extends BaseActivity {
    private SessionManager session;
    public InterestAdapter adapterInterest;
    public static ArrayList<Interest> listInterest = new ArrayList<>();

    AlertManager alert ;
    RecyclerView rv_label;
    String userID = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.interest_categories, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        session = new SessionManager(getApplicationContext());

        userID = session.getUserID();
        listInterest.clear();
        alert = new AlertManager(interestActivity.this);

        rv_label = findViewById(R.id.rv_label);


        Interest all = new Interest("All", 0,  false);
        listInterest.add(all);
        for(int i = 0; i<URLs.categoriesList.size(); i++)
        {
            Category c = URLs.categoriesList.get(i);
            listInterest.add(new Interest(c.getName(), Integer.parseInt(c.getId()), false));
        }
        adapterInterest = new InterestAdapter(listInterest, this);

        rv_label.setHasFixedSize(true);
        rv_label.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        rv_label.setAdapter(adapterInterest);

        Button save = (Button)findViewById(R.id.saveIntrstBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.showMessageOKCancel(getString(R.string.update_intersts_q), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==-1){
                            doPostRequest();
                        }
                    }
                });
            }
        });

        loaddMyInterests();
    }

    private void readResult(String response) {
        JSONObject jsonObject = null;
        try {
            System.out.println(response);
            jsonObject = new JSONObject(response);
            users = jsonObject.getJSONArray("result");

            JSONObject jo = users.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                for(int i=1;i<users.length();i++){
                    jo = users.getJSONObject(i);
                    String categID = jo.getString("id");
                    String name = jo.getString("name");
                    for(int j=0; j<listInterest.size(); j++)
                    {
                        if(name.equals(listInterest.get(j).getName()))
                        {
                            listInterest.get(j).setSelected(true);
                        }
                    }
                }
                adapterInterest.notifyDataSetChanged();
            }else{
            }
        } catch (JSONException e) {
        }
    }

    private void loaddMyInterests(){
        String request = URLs.LOAD_MY_INTEREST;
        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, true);
        Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        readResult(response);
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        alert.showAlertDialog(interestActivity.this,getString(R.string.error),error.toString(),false);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id" , userID);
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    String myInterests = "";
    private void doPostRequest() {

        for(int i = 0; i< listInterest.size(); i++)
        {
            if(listInterest.get(i).isSelected()){
                myInterests = myInterests + listInterest.get(i).getCategID() + ";";
            }
        }
        //myInterests = myInterests.replaceAll(" ","+");

        String request = URLs.UPDATE_INTEREST;
        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        System.out.println(response);
                        doAction(response);
                        Toast.makeText(interestActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        alert.showAlertDialog(interestActivity.this,getString(R.string.error),error.toString(),false);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("buyer_id" , userID);
                params.put("interest" , myInterests);
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed(){
        finish();
    }



    private JSONArray users = null;

    public void doAction(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            users = jsonObject.getJSONArray("result");

            JSONObject jo = users.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                alert.showAlertDialog(interestActivity.this,"","Data updated successfully",true);
                Intent intent = new Intent(getApplicationContext(), BuyerMainActivity.class);
                startActivity(intent);
            }else{
                alert.showAlertDialog(interestActivity.this, "", "Could'nt update data", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
