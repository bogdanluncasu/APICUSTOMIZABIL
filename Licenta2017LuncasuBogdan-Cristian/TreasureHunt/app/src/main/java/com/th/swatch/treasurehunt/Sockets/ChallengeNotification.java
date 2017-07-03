package com.th.swatch.treasurehunt.Sockets;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.engineio.client.transports.Polling;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.th.swatch.treasurehunt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChallengeNotification extends Service {
    private Socket socket;
    static final String APIURL = "https://treasurehuntlive.herokuapp.com/";

    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = new JSONObject(String.valueOf(args[0])).getJSONObject("data");
                String verb = new JSONObject(String.valueOf(args[0])).getString("verb");
                String message = data.getString("message");
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notification =
                        new NotificationCompat.Builder(ChallengeNotification.this)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(verb)
                                .setContentText(message);

                notificationManager.notify((int)(Math.random()*10000), notification.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private String token="";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LBC_", "OSC");
        JSONObject requestChallenge = new JSONObject();
        JSONObject requestTasks = new JSONObject();
        if(intent!=null) {
            Bundle extras = intent.getExtras();
            Log.d("LBC__extras", String.valueOf(extras.keySet()));
            if(extras.containsKey("token"))
                token = extras.getString("token", "");
        }

        try {
            requestChallenge.put("url", "/challenge/subscribe");
            requestTasks.put("url", "/challenge/subscribeTasks?token="+token);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("LBC_", "exception");
        }

        socket.emit("get", requestChallenge, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("LBC_socket", Arrays.toString(args));
            }
        });

        socket.emit("get", requestTasks, new Ack() {
            @Override
            public void call(Object... args) {
                Log.d("LBC_socket", Arrays.toString(args));
            }
        });
        socket.on("challenge", listener);
        socket.on("task", listener);
        socket.connect();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public ChallengeNotification() {
        Log.d("LBC_socket", "challengenotigicationSOCKET");
        try {
            IO.Options options=new IO.Options();
            socket = IO.socket(APIURL + "?__sails_io_sdk_version=0.11.0", options);
            socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Transport transport = (Transport) args[0];
                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            @SuppressWarnings("unchecked")
                            Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                            String bearer = "Bearer " + token;
                            headers.put("Authorization", Arrays.asList(bearer));
                            Log.d("LBC__headers", String.valueOf(headers));
                        }
                    }).on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            Log.d("LBC_socket", "uri");
        }


    }


}
