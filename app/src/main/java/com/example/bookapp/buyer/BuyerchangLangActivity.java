package com.example.bookapp.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.google.android.material.navigation.NavigationView;


public class BuyerchangLangActivity extends BaseActivity {

    private SessionManager session;
    private WebView webView = null;
    private String userID= "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.lang_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        session = new SessionManager(getApplicationContext());



        Button englishBtn = (Button) findViewById(R.id.englishBtn);
        Button arabicBtn = (Button) findViewById(R.id.arabicBtn);



        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setLang("en");
                Intent i = new Intent(getApplicationContext(), BuyerMainActivity.class);
                startActivity(i);
            }
        });
        arabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setLang("ar");
                Intent i = new Intent(getApplicationContext(), BuyerMainActivity.class);
                startActivity(i);
            }
        });
    }



}
