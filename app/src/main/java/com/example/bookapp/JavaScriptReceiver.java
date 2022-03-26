package com.example.bookapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class JavaScriptReceiver {
    Activity mContext;
    AlertManager alert ;
    private ProgressDialog loading;
    WebView wbview;
    /** Instantiate the receiver and set the context */
    public JavaScriptReceiver(Activity c) {
        mContext = c;
        alert = new AlertManager(c);
    }

    public JavaScriptReceiver(Activity c , WebView wbview) {
        mContext = c;
        alert = new AlertManager(c);
        this.wbview = wbview;
    }

    private String userID = "";



    @JavascriptInterface
    public void showErrMessage(String msg){
        alert.showAlertDialog(mContext, mContext.getString(R.string.info), msg, false);
    }

    @JavascriptInterface
    public void showMessage(String msg){
        alert.showAlertDialog(mContext, mContext.getString(R.string.info), msg, true);
    }


    @JavascriptInterface
    public void loadProcess() {
        loading = ProgressDialog.show(mContext, "Loading", "Please wait...", false, false);
    }

    public void dismissLoadProcess() {
        loading.dismiss();
    }

    @JavascriptInterface
    public void loadDismiss(){
        loading.dismiss();
    }


    @JavascriptInterface
    public void startChat(String seller_id, String buyer_id, String type){
        if(type.equals("te")){
            Intent i = new Intent(mContext.getApplicationContext(), com.example.bookapp.seller.chat_seller.class);
            i.putExtra("buyer_id" , buyer_id);
            mContext.startActivity(i);
            mContext.overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        }else{
            Intent i = new Intent(mContext.getApplicationContext(), com.example.bookapp.buyer.chat_buyer.class);
            i.putExtra("seller_id" , seller_id);
            mContext.startActivity(i);
            mContext.overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        }
    }



}
