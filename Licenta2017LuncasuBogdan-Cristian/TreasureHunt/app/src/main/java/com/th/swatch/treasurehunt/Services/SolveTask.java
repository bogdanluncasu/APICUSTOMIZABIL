package com.th.swatch.treasurehunt.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.th.swatch.treasurehunt.ChallengeActivity;
import com.th.swatch.treasurehunt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.net.Uri;

import cz.msebera.android.httpclient.Header;


/**
 * Created by swatch on 28.05.2017.
 */

public class SolveTask extends AsyncTask<Void, Void, Boolean> {
    SyncHttpClient client;
    String apiUrl;
    Activity context;
    ProgressDialog progressDialog;
    SharedPreferences shared;
    String token;
    Uri imageURI;
    ImageView takenPhoto;
    ContentResolver cr;
    public SolveTask(String apiurl, Uri imageURI, ImageView takenPhoto, Activity context, ContentResolver cr) {
        this.takenPhoto=takenPhoto;
        this.cr=cr;
        this.apiUrl = apiurl;
        this.client = new SyncHttpClient();
        this.context = context;
        this.imageURI=imageURI;
    }

    @Override
    protected void onPreExecute() {
        Log.d("LBC", "preExecute");

        progressDialog = new ProgressDialog(context,
                R.style.AlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Solving task...");
        progressDialog.show();
        shared = context.getSharedPreferences("authpreference", Context.MODE_PRIVATE);
        token = (shared.getString("token", ""));
    }

    private String getStringPhoto() throws IOException {
        cr.notifyChange(imageURI, null);
        final Bitmap photo = android.provider.MediaStore.Images.Media.getBitmap(cr, imageURI);
        Log.d("LBC_widthxheight"," "+
                Integer.toString(photo.getHeight()));
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                takenPhoto.setImageBitmap(photo);
            }
        });

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


    }

    @Override
    protected Boolean doInBackground(Void... _) {
        final boolean[] reply = {false};

        String encodedImage= "";
        try {
            encodedImage = getStringPhoto();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addHeader("Authorization", "Bearer "+token);
        RequestParams rp = new RequestParams();
        rp.add("image", encodedImage);
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("LBC", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("LBC", Integer.toString(statusCode));
                if(statusCode==200) {
                    try {
                        reply[0] = response.getBoolean("done");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Log.d("LBC_json", "hhere");
        client.post(apiUrl , rp, responseHandler);

        return reply[0];
    }

    @Override
    protected void onPostExecute(Boolean s) {
        Log.d("LBC", "postExecute");
        progressDialog.dismiss();
        if(s){
            Log.d("LBC","Task done");
            Toast.makeText(context,"Task done",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context, "Task is not done yet. ", Toast.LENGTH_LONG).show();
        }

    }
}
