package com.example.bookapp.buyer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.bookapp.adapters.bookAdapter;
import com.example.bookapp.adapters.categoryAdapter;
import com.example.bookapp.adapters.spinnerCategoryAdapter;
import com.example.bookapp.models.Book;
import com.example.bookapp.models.BookCopy;
import com.example.bookapp.models.Category;
import com.example.bookapp.recycleview.ClickListener;
import com.example.bookapp.recycleview.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SearchActivity extends BaseActivity {

    ArrayList<Book> bookArrayList = new ArrayList<>();
    public ProgressDialog progressDialog = null;
    RecyclerView listview;
    private SessionManager session;
    private AlertManager alert ;
    private bookAdapter adapter = null;
    private TextView interface_title;
    private String type = "";
    private Category category;


    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private ImageView micButton;
    private Spinner categorySpinner;


    String search_word = "";
    String priceFrom = "0";
    String priceTo = "0";
    String categoryId = "0";
    private EditText priceToEdt ,priceFromEdt;
    private Button searchBtn;
    private Spinner bookTypeSpinner;
    private String book_type = "New";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.search_activity, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        session = new SessionManager(SearchActivity.this);
        alert = new AlertManager(SearchActivity.this);


        interface_title = (TextView) findViewById(R.id.interface_title);
        interface_title.setText(R.string.list_of_books);
        listview = findViewById(R.id.listview);
        priceFromEdt = (EditText)findViewById(R.id.priceFrom);
        priceToEdt = (EditText)findViewById(R.id.priceTo);
        searchBtn = (Button)findViewById(R.id.searchBtn);

        listview.setHasFixedSize(true);
        listview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1  ));


        adapter = new bookAdapter( bookArrayList , SearchActivity.this);
        listview.setAdapter(adapter);


        bookTypeSpinner = (Spinner) findViewById(R.id.newUsedSpinner);
        categorySpinner = (Spinner) findViewById(R.id.categSpinner);
        ArrayList CategoriesList = new ArrayList(URLs.categoriesList);
        CategoriesList.add(0, new Category("0" , "---"));
        spinnerCategoryAdapter categAdapter = new spinnerCategoryAdapter(this, R.layout.support_simple_spinner_dropdown_item,
                android.R.id.text1,
                CategoriesList);
        categorySpinner.setAdapter(categAdapter);
        categAdapter.notifyDataSetChanged();

        SearchView search_input = (SearchView)findViewById(R.id.search_input);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if(extras != null)
        {
            if(extras.containsKey("search_word")){
                search_word = extras.getString("search_word");
            }
            if(extras.containsKey("priceFrom")){
                priceFrom = extras.getString("priceFrom");
            }
            if(extras.containsKey("priceTo")){
                priceTo = extras.getString("priceTo");
            }
            if(extras.containsKey("category_id")){
                categoryId = extras.getString("category_id");
            }
            if(extras.containsKey("category_id")){
                book_type = extras.getString("book_type");
            }
            priceFromEdt.setText(priceFrom);
            priceToEdt.setText(priceTo);
            categorySpinner.setSelection(Integer.parseInt(categoryId));
            if(book_type.equals("New")) {
                bookTypeSpinner.setSelection(0);
            }else{
                bookTypeSpinner.setSelection(1);
            }
            toolbar.setTitle(getString(R.string.search_for)+search_word+"]");
            interface_title.setText(getString(R.string.search_for)+search_word+"]");
            search_input.setQuery(search_word , false);
            loadBooks(categoryId , search_word , priceFrom, priceTo);
            type = "search";
        }else {
            loadBooks("0" , "", priceFrom, priceTo);
        }
        search_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("search_word",  s );
                bundle.putString("priceFrom",  priceFromEdt.getText().toString() );
                bundle.putString("priceTo",  priceToEdt.getText().toString() );
                String category_id = ((Category)categorySpinner.getSelectedItem()).getId();
                bundle.putString("category_id", category_id);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }


        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if(session.getLang().equals("en")){
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        }else{
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA");

        }

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                Bundle bndle = new Bundle();
                bndle.putString("search_word",  data.get(0) );
                bndle.putString("priceFrom",  priceFromEdt.getText().toString() );
                bndle.putString("priceTo",  priceToEdt.getText().toString() );
                String category_id = ((Category)categorySpinner.getSelectedItem()).getId();
                bndle.putString("category_id", category_id);
                String book_type = bookTypeSpinner.getSelectedItem().toString();
                bndle.putString("book_type", book_type);

                intent.putExtras(bndle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    micButton.setImageResource(R.drawable.ic_mic_black_off);
                    speechRecognizer.stopListening();
                    //System.out.println("bye");
                }

                System.out.println("Action -> "+motionEvent.getAction());
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return true;
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("search_word",  search_word );
                bundle.putString("priceFrom",  priceFromEdt.getText().toString() );
                bundle.putString("priceTo",  priceToEdt.getText().toString() );
                String category_id = ((Category)categorySpinner.getSelectedItem()).getId();
                bundle.putString("category_id", category_id);
                String book_type = bookTypeSpinner.getSelectedItem().toString();
                bundle.putString("book_type", book_type);

                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }
    private void loadBooks(String category_id , String search_word, String from, String to) {

        String request = "";
        request = URLs.SEARCH_BOOKS;

        final ProgressDialog loading = ProgressDialog.show(this, "Loading", getString(R.string.waiting), false, false);
        //Toast.makeText(this, request, Toast.LENGTH_LONG).show();

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        System.out.println(response);
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
                params.put("search_word" , search_word);
                params.put("category_id" , category_id);
                params.put("priceFrom" , from);
                params.put("priceTo" , to);
                params.put("book_type" , bookTypeSpinner.getSelectedItem().toString());
                params.put("buyer_id" , session.getUserID());
                return params;
            }

        };

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onLoadError() {
        //alert.showAlertDialog(BuyerMainActivity.this,"Error","No data Available",false);

        if(type.equals("search")) {
            interface_title.setText(interface_title.getText()+"\n No results found ");

        }else if(type.equals("category")){
            interface_title.setText(getString(R.string.no_books_found)+category.getName()+"]");
        }
    }

    private JSONArray booksJSONlist = null;

    public void doAction(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            booksJSONlist = jsonObject.getJSONArray("result");

            JSONObject jo = booksJSONlist.getJSONObject(0);
            String success = jo.getString("success");

            if(success.equals("1")){
                for(int i=1;i<booksJSONlist.length();i++){
                    jo = booksJSONlist.getJSONObject(i);

                    String imgRUL= jo.getString("image");
                    char slash[] = new char[1];
                    slash[0] = (char)92;
                    imgRUL = URLs.SERVER + imgRUL.replace( new String(slash), "" );


                    String id = jo.getString("id");
                    String bookName = jo.getString("bookName");
                    String authorName = jo.getString("authorName");
                    String publisherName = jo.getString("publisherName");
                    String summary = jo.getString("summary");
                    String seller_id = jo.getString("seller_id");
                    String addDate = jo.getString("addDate");
                    String bookType = jo.getString("bookType");
                    String category_id = jo.getString("category_id");

                    Book b = new Book(id, bookName,  authorName, publisherName, summary, seller_id, addDate, bookType,imgRUL, category_id);

                    JSONArray copiesJSONlist = jo.getJSONArray("copies");
                    ArrayList<BookCopy> copiesList = new ArrayList<>();
                    for(int j=0; j<copiesJSONlist.length();j++){
                        JSONObject jsob = copiesJSONlist.getJSONObject(j);
                        String copy_id = jsob.getString("id");
                        String book_id = jsob.getString("book_id");
                        String isbn = jsob.getString("isbn");
                        String price = jsob.getString("price");
                        String numberOfCopies = jsob.getString("numberOfCopies");
                        String numberOfPages = jsob.getString("numberOfPages");
                        String copyAddDate = jsob.getString("addDate");
                        BookCopy copy = new BookCopy(copy_id, book_id,  isbn, price, numberOfCopies, numberOfPages, copyAddDate );
                        copiesList.add(copy);
                    }
                    b.setCopyArrayList(copiesList);
                    bookArrayList.add(b);

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
}
