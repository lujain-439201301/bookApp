package com.example.bookapp.buyer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookapp.AlertManager;
import com.example.bookapp.R;
import com.example.bookapp.URLs;
import com.example.bookapp.loginActivity;
import com.example.bookapp.models.Order;
import com.example.bookapp.models.User;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BProfileActivity extends BaseActivity {

    private Button save;
    private EditText uname;
    private EditText mail;
    private EditText pass;
    private EditText address;
    private EditText country;
    ProgressDialog loading;

    AlertManager alert ;
    ImageButton backBtn;
    private Spinner userType;
    private Spinner sellerType;


    String email, password, nickName;
    private JSONArray users = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.profile_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        alert = new AlertManager(BProfileActivity.this);
        uname = (EditText) findViewById(R.id.uname);
        mail = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.pass);
        address = (EditText) findViewById(R.id.address);
        country = (EditText) findViewById(R.id.country);
        backBtn = (ImageButton) findViewById(R.id.backBtn);

        save = (Button) findViewById(R.id.registerBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });


        userType = (Spinner) findViewById(R.id.userType);
        sellerType = (Spinner) findViewById(R.id.sellerType);
        sellerType.setVisibility(View.GONE);
        country.setVisibility(View.GONE);
        getData();
    }

    private void update() {

        if (validation()) {
            loading = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.UPDATE_PROFILE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            updateAction(response);
                            Toast.makeText(BProfileActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            alert.showAlertDialog(BProfileActivity.this,
                                    "",
                                    error.toString(),false);
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    String userName = uname.getText().toString().trim();
                    String email = mail.getText().toString().trim();
                    String pass_txt = pass.getText().toString().trim();
                    String country_txt = country.getText().toString().trim();
                    String address_txt = address.getText().toString().trim();

                    params.put("nickName" , userName);
                    params.put("email" ,email);
                    params.put("pass" , pass_txt );
                    params.put("country" , country_txt );
                    params.put("address" , address_txt );
                    params.put("userType", "Buyer" );
                    params.put("sellerType", "0" );
                    params.put("user_id", session.getUserID() );

                    return params;
                }

            };

            //Adding the request to request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } else {
            alert.showAlertDialog(this, getString(R.string.update_failes), getString(R.string.check_your_inputes), false);
        }
    }

    public void updateAction(String response){
        JSONObject jsonObject = null;
        try {
            System.out.println(response);
            jsonObject = new JSONObject(response);
            users = jsonObject.getJSONArray("result");

            JSONObject jo = users.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                alert.showAlertDialog(BProfileActivity.this,
                        "",
                        getString(R.string.profile_update_success),
                        true,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getApplicationContext(), BuyerMainActivity.class));
                                    finish();
                            }
                });
            }else{
                alert.showAlertDialog(BProfileActivity.this, "", getString(R.string.update_profile_failed), false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean validation(){
        boolean validData = true;
        nickName = uname.getText().toString().trim();
        email = mail.getText().toString().trim();
        password = pass.getText().toString().trim();
        password = pass.getText().toString().trim();

        //validations
        if (TextUtils.isEmpty(nickName)) {
            uname.setError(getString(R.string.nickname_required));
            uname.requestFocus();
            validData = false;
        }



        if (TextUtils.isEmpty(email)) {
            mail.setError(getString(R.string.email_required));
            mail.requestFocus();
            validData = false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mail.setError(getString(R.string.write_valid_email));
            mail.requestFocus();
            validData = false;
        }

        Pattern PASSWORD_PATTERN =
                Pattern.compile("^" +
                        "(?=.*[@#$%^&+=])" +     // at least 1 special character
                        "(?=\\S+$)" +            // no white spaces
                        ".{6,}" +                // at least 6 characters
                        "$");
        if (TextUtils.isEmpty(password)) {
            pass.setError(getString(R.string.pass_required));
            pass.requestFocus();
            validData = false;
        }
        else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            pass.setError(getString(R.string.pass_is_week));
            validData = false;
        } else {
            pass.setError(null);
        }

        return validData;
    }


    private void getData() {
            loading = ProgressDialog.show(this, "Loading", "Please wait...", false, false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_USER_DATA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            readAction(response);
                            Toast.makeText(BProfileActivity.this, response, Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loading.dismiss();
                            alert.showAlertDialog(BProfileActivity.this,
                                    "",
                                    error.toString(),false);
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("user_id" , session.getUserID() );
                    params.put("userType", "Buyer" );

                    return params;
                }

            };

            //Adding the request to request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }


    public void readAction(String response){
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
                    String nickName = jo.getString("nickName");
                    String email = jo.getString("email");
                    String addressT = jo.getString("address");
                    uname.setText(nickName);
                    mail.setText(email);
                    address.setText(addressT);
                }
            }else{

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
