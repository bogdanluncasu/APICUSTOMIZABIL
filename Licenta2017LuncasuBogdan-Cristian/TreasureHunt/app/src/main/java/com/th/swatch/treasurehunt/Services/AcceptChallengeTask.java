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
import com.th.swatch.treasurehunt.Models.ChallengeModel;
import com.th.swatch.treasurehunt.R;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by swatch on 28.05.2017.
 */

public class AcceptChallengeTask extends AsyncTask<Void, Void, String> {
    SyncHttpClient client;
    String apiUrl;
    Activity context;
    ProgressDialog progressDialog;
    int challengeId;
    SharedPreferences shared;
    String token;
    public AcceptChallengeTask(String apiurl, int challengeId, Activity context) {
        this.apiUrl = apiurl;
        this.client = new SyncHttpClient();
        this.context = context;
        this.challengeId=challengeId;
    }

    @Override
    protected void onPreExecute() {

        progressDialog = new ProgressDialog(context,
                R.style.AlertDialogStyle);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Accepting challenge...");
        progressDialog.show();
        shared = context.getSharedPreferences("authpreference", Context.MODE_PRIVATE);
        token = (shared.getString("token", ""));
    }

    @Override
    protected String doInBackground(Void... _) {
        final String[] msg = {"Challenge can not be accepted."};
        client.addHeader("Authorization", "Bearer "+token);
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("LBC", responseString);
                msg[0] = "Challenge accepted.";
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("LBC", Integer.toString(statusCode));
                if (statusCode == 200) {
                    msg[0] = "Challenge accepted.";
                }
            }
        };
        client.post(apiUrl+"challenge/"+challengeId+"/accept", null, responseHandler);

        return msg[0];
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
        Toast.makeText(context,s,Toast.LENGTH_LONG).show();
        new RetrieveDataTask<ChallengeModel>(apiUrl+"challenge",context,ChallengeModel.class).execute();
    }
}
