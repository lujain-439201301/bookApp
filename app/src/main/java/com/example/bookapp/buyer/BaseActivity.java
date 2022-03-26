package com.example.bookapp.buyer;


import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookapp.AlertManager;
import com.example.bookapp.R;
import com.example.bookapp.SessionManager;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    public Toolbar toolbar;
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


        setContentView(R.layout.buyer_activity_base);
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

                    case R.id.buyer_home:
                        Intent i = new Intent(getApplicationContext(), BuyerMainActivity.class);
                        startActivity( i);
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.my_cart:
                        startActivity( new Intent(getApplicationContext(), cart.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.my_orders:
                        startActivity( new Intent(getApplicationContext(), ordersList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.my_disputes:
                        startActivity( new Intent(getApplicationContext(), disputeList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.chat_with_admin:
                        Intent intent_ = new Intent(getApplicationContext(), chat_buyer.class);
                        intent_.putExtra("admin" , "admin");
                        startActivity( intent_);
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.messages:
                        startActivity(  new Intent(getApplicationContext(), chat_buyer_list.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.faveList:
                        startActivity(  new Intent(getApplicationContext(), favoriteList.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.intersts:
                        startActivity(  new Intent(getApplicationContext(), interestActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.changeLang:
                        startActivity(  new Intent(getApplicationContext(), BuyerchangLangActivity.class));
                        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.profile:
                        startActivity(  new Intent(getApplicationContext(), BProfileActivity.class));
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

            Menu menu =  navigationView.getMenu();

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
            session.setServiceRun(true);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.example.bookapp.buyer");
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