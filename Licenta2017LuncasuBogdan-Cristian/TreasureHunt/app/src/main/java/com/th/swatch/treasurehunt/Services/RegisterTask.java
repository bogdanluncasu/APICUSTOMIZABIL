package com.th.swatch.treasurehunt.Services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.th.swatch.treasurehunt.ChallengeActivity;
import com.th.swatch.treasurehunt.MainActivity;
import com.th.swatch.treasurehunt.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by swatch on 28.05.2017.
 */

public class RegisterTask extends AsyncTask<Void, Void, String> {
    SyncHttpClient client;
    String apiurl,username,password,email;
    Activity context;
    Button _loginButton;
    Button _registerButton;
    ProgressDialog progressDialog;
    public RegisterTask(String apiurl, String username, String password, String email, Activity activity) {
        this.apiurl=apiurl;
        this.username=username;
        this.password=password;
        this.email=email;
        this.context=activity;
        this.client = new SyncHttpClient();
        _loginButton = (Button) this.context.findViewById(R.id.login);
        _registerButton = (Button) this.context.findViewById(R.id.register);
    }

    @Override
    protected void onPreExecute() {
        _loginButton.setEnabled(false);
        _registerButton.setEnabled(false);


        progressDialog = new ProgressDialog(context,
                R.style.AlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating account...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        final String[] message = {""};
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);
        rp.add("emailAddress", email);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("LBC", responseString);
                message[0] = "ok";
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("LBC", Integer.toString(statusCode));
                Log.d("LBC", String.valueOf(response));
                Toast.makeText(context,statusCode,Toast.LENGTH_LONG).show();
                if(statusCode==302) {
                    message[0] = "ok";
                }
            }
        };
        client.post(apiurl + "user", rp, responseHandler);

        return message[0];

    }

    protected void onPostExecute(String s) {
        Log.d("LBC", "postExecute");
        progressDialog.dismiss();
        if(!s.equals("")){
            Log.d("LBC","registered");
        }else{
            Toast.makeText(context, "Username or email already in use.", Toast.LENGTH_LONG).show();
        }
        _loginButton.setEnabled(true);
        _registerButton.setEnabled(true);

    }
}
