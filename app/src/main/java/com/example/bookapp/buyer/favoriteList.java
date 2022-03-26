package com.example.bookapp.buyer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
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
import com.example.bookapp.URLs;
import com.example.bookapp.adapters.favoriteAdapter;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.Cart;
import com.example.bookapp.models.Dispute;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class favoriteList extends BaseActivity {

    ArrayList<Book> booksList = new ArrayList<>();
    public ProgressDialog progressDialog = null;
    RecyclerView listview;
    private AlertManager alert ;
    private favoriteAdapter adapter = null;
    private TextView interface_title;
    private JSONObject jsonObject;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.orders_list_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        alert = new AlertManager(favoriteList.this);


        interface_title = (TextView) findViewById(R.id.interface_title);
        interface_title.setText("My Favorite Books");
        listview = findViewById(R.id.listview);

        listview.setHasFixedSize(true);
        listview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1  ));


        adapter = new favoriteAdapter(booksList , favoriteList.this);
        listview.setAdapter(adapter);



        loadFavBooks();
    }

    private void loadFavBooks() {

        String user_id = session.getUserID();
        String request = URLs.GET_FAV_BOOKS;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        doAction(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        onLoadError();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user_id" , user_id);
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onLoadError() {
        alert.showAlertDialog(favoriteList.this,"Error","No data Available",false);
    }

    private JSONArray users = null;

    public void doAction(String response){
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


                    String id = jo.getString("book_id");

                    String imgRUL= jo.getString("image");
                    char slash[] = new char[1];
                    slash[0] = (char)92;
                    imgRUL = URLs.SERVER + imgRUL.replace( new String(slash), "" );
                    String bookName = jo.getString("bookName");
                    String authorName = jo.getString("authorName");
                    String publisherName = jo.getString("publisherName");
                    String summary = jo.getString("summary");
                    String seller_id = jo.getString("seller_id");
                    String addDate = jo.getString("addDate");
                    String bookType = jo.getString("bookType");
                    String category_id = jo.getString("category_id");

                    Book book = new Book(id, bookName,  authorName, publisherName, summary, seller_id, addDate, bookType,imgRUL, category_id);


                    booksList.add(book);


                    onLoadSucess();
                }
            }else{
                onLoadError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onLoadSucess() {

        adapter.notifyDataSetChanged();

    }



    public void removeItem(Book book) {
        alert.showMessageOKCancel("Are you sure to remove this item from favorite list?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(DialogInterface.BUTTON_POSITIVE == i){
                    doDelettion(book);
                }
            }
        });
    }


    private void doDelettion(Book book){
        String user_id = session.getUserID();
        String request = URLs.FAV_REMOVE_ITEM;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        //Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            System.out.println(response);
                            jsonObject = new JSONObject(response);
                            users = jsonObject.getJSONArray("result");

                            JSONObject jo = users.getJSONObject(0);
                            String success = jo.getString("success");

                            if(success.equals("1")){

                                alert.showAlertDialog( favoriteList.this,
                                        "",
                                        getString(R.string.item_fav_removed_success),
                                        true);
                                booksList.remove(book);
                                onLoadSucess();
                            }else{
                                // email / password doesn't match
                                alert.showAlertDialog( favoriteList.this,
                                        getString(R.string.error),
                                        getString(R.string.couldnt_remove_fav_item),
                                        false);
                            }
                        } catch (JSONException e) {
                            alert.showAlertDialog( favoriteList.this,
                                    getString(R.string.error),
                                    getString(R.string.couldnt_remove_fav_item),
                                    false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        onLoadError();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("user_id" , user_id);
                params.put("book_id" , book.getID());

                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
