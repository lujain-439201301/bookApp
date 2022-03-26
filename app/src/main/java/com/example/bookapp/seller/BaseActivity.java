package com.example.bookapp.seller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookapp.AlertManager;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.example.bookapp.buyer.chat_buyer_list;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    public SessionManager session;
    private JSONObject jsonObject;
    private AlertManager alert;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if(session.getLang() !=null && session.getLang().equals("ar"))
            changeLanguage("ar");
        else
            changeLanguage("en");
        setContentView(R.layout.activity_base);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        alert = new AlertManager(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                final String appPackageName = getPackageName();

                switch (item.getItemId()) {

                    case R.id.home:
                        Intent i = new Intent(getApplicationContext(), booksList.class);
                        startActivity( i);
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.add_item:
                        startActivity(new Intent(getApplicationContext(), addBookActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.list_items:
                        startActivity(new Intent(getApplicationContext(), booksList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.my_orders:
                        startActivity(new Intent(getApplicationContext(), ordersList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.chat_with_admin:
                        Intent intent_ = new Intent(getApplicationContext(), chat_seller.class);
                        intent_.putExtra("admin" , "admin");
                        startActivity( intent_);
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;





                    case R.id.messages:
                        startActivity(  new Intent(getApplicationContext(), chat_seller_list.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.changeLang:
                        startActivity(  new Intent(getApplicationContext(), SellerchangLangActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.profile:
                        startActivity(  new Intent(getApplicationContext(), SProfileActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;




                    case R.id.logout:
                        session.logoutUser();
                        drawerLayout.closeDrawers();
                        finish();
                        break;

                }
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1 = intent.getStringExtra("msgs_count");
            //text.setText(s1);
            //Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();

            // get menu from navigationView
            Menu menu = navigationView.getMenu();

            // find MenuItem you want to change
            MenuItem msgItem = menu.findItem(R.id.messages);

            // set new title to the MenuItem
            msgItem.setTitle("Messages ("+s1+" New)");


        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        if(!session.getServiceRun()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.example.bookapp.seller");
            registerReceiver(broadcastReceiver, intentFilter);


            Intent service = new Intent(this, com.example.bookapp.notificationService.class);
            startService(service);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(!session.getServiceRun()) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    private void changeLanguage(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources =  getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}