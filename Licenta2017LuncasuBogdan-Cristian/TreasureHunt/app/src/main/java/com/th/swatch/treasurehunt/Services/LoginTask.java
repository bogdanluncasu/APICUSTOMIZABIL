package com.th.swatch.treasurehunt.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.th.swatch.treasurehunt.ChallengeActivity;
import com.th.swatch.treasurehunt.R;
import com.th.swatch.treasurehunt.Sockets.ChallengeNotification;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by swatch on 28.05.2017.
 */

public class LoginTask extends AsyncTask<Void, Void, String> {
    SyncHttpClient client;
    String apiUrl, username, password;
    Activity context;
    Button _loginButton;
    Button _registerButton;
    ProgressDialog progressDialog;
    public LoginTask(String apiurl, String username, String password, Activity context) {
        this.apiUrl = apiurl;
        this.username = username;
        this.password = password;
        this.client = new SyncHttpClient();
        this.context = context;
        _loginButton = (Button) this.context.findViewById(R.id.login);
        _registerButton = (Button) this.context.findViewById(R.id.register);
    }

    @Override
    protected void onPreExecute() {
        Log.d("LBC", "preExecute");
        _loginButton.setEnabled(false);
        _registerButton.setEnabled(false);

        progressDialog = new ProgressDialog(context,
                R.style.AlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... _) {
        final String[] token = {""};
        RequestParams rp = new RequestParams();
        rp.add("username", username);
        rp.add("password", password);

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
                        token[0] = response.getString("token")+" "+response.getJSONObject("user").get("username");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        client.post(apiUrl + "auth", rp, responseHandler);

        return token[0];
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("LBC", "postExecute");
        progressDialog.dismiss();
        if(!s.equals("")){
            Log.d("LBC","logged in");
            SharedPreferences sharedpreferences
                    = context.getSharedPreferences("authpreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            String[] creditentials=s.split(" ");
            editor.putString("token", creditentials[0]);
            editor.putString("username", creditentials[1]);
            editor.apply();

            Log.d("LBC_","setservice token:"+creditentials[0]);
            Intent intentService=new Intent(context, ChallengeNotification.class);
            intentService.putExtra("token",creditentials[0]);
            context.startService(intentService);
            Log.d("LBC_","servicestarted");

            Intent intent=new Intent(context,ChallengeActivity.class);
            context.startActivity(intent);
            context.finish();
        }else{
            Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();
        }
        _loginButton.setEnabled(true);
        _registerButton.setEnabled(true);


    }
}
