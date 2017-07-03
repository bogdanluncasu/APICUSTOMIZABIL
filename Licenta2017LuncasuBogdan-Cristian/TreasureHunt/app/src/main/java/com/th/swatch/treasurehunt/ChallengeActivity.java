package com.th.swatch.treasurehunt;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.th.swatch.treasurehunt.Models.ChallengeModel;
import com.th.swatch.treasurehunt.Models.UserModel;
import com.th.swatch.treasurehunt.Services.RetrieveDataTask;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ChallengeActivity extends AppCompatActivity {
    private RetrieveDataTask rdt;
    static final String APIURL = "https://treasurehuntlive.herokuapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        Log.d("LBC","getData");
        new RetrieveDataTask<ChallengeModel>(APIURL+"challenge",this,ChallengeModel.class).execute();
    }
}
