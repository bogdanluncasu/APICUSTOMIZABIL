package com.th.swatch.treasurehunt.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.th.swatch.treasurehunt.Adapters.ChallengesListAdapter;
import com.th.swatch.treasurehunt.Adapters.TasksListAdapter;
import com.th.swatch.treasurehunt.ChallengeActivity;
import com.th.swatch.treasurehunt.Models.ChallengeModel;
import com.th.swatch.treasurehunt.Models.TaskModel;
import com.th.swatch.treasurehunt.Models.UserModel;
import com.th.swatch.treasurehunt.R;
import com.th.swatch.treasurehunt.SolveTaskActivity;
import com.th.swatch.treasurehunt.TaskActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by swatch on 01.06.2017.
 */

public class RetrieveDataTask<T> extends AsyncTask<Void, Void, List<T>> {
    SyncHttpClient client;
    Activity context;
    String apiurl;
    String token;
    SharedPreferences shared;
    private final Class<T> type;

    public RetrieveDataTask(String apiurl, Activity activity, Class<T> type) {
        this.apiurl = apiurl;
        this.context = activity;
        this.client = new SyncHttpClient();
        this.type = type;

    }

    @Override
    protected void onPreExecute() {
        shared = context.getSharedPreferences("authpreference", Context.MODE_PRIVATE);
        token = (shared.getString("token", ""));
    }

    @Override
    protected List<T> doInBackground(Void... params) {

        RequestParams rp = new RequestParams();
        client.addHeader("Authorization", "Bearer " + token);
        final List<JSONObject> myobjects = new LinkedList<>();
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("LBC", Integer.toString(statusCode));
                if (statusCode == 200) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            myobjects.add(response.getJSONObject(i));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        client.get(apiurl, responseHandler);
        Log.d("LBC", String.valueOf(myobjects));
        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        List<T> listOfObjects = new LinkedList<>();
        for (JSONObject obj : myobjects) {
            JsonElement mJson = parser.parse(obj.toString());
            listOfObjects.add(gson.fromJson(mJson, type));
        }
        return listOfObjects;

    }

    @Override
    protected void onPostExecute(List<T> t) {
        Log.d("LBC", t.getClass().getName());

        if (context instanceof ChallengeActivity && type.equals(ChallengeModel.class)) {
            String username = (shared.getString("username", ""));

            List<ChallengeModel> challenges = (List<ChallengeModel>) t;

            List<ChallengeModel> accepted = new LinkedList<>();
            List<ChallengeModel> active = new LinkedList<>();

            for (ChallengeModel cm : challenges) {
                if (cm.isAccepted())
                    accepted.add(cm);
                else
                    active.add(cm);
            }

            ListView activeChallengesView = ((ListView) context.findViewById(R.id.active_challenges_list));
            activeChallengesView.setAdapter(new ChallengesListAdapter(context.getLayoutInflater(), active, apiurl + "/../", context));

            final ListView acceptedChallengesView = ((ListView) context.findViewById(R.id.challenges_list));
            acceptedChallengesView.setAdapter(new ChallengesListAdapter(context.getLayoutInflater(), accepted, apiurl + "/../", context));

            acceptedChallengesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("id",((ChallengeModel) acceptedChallengesView.getAdapter().getItem(position)).getId());
                    intent.putExtra("description",((ChallengeModel) acceptedChallengesView.getAdapter().getItem(position)).getDescription());
                    intent.putExtra("title",((ChallengeModel) acceptedChallengesView.getAdapter().getItem(position)).getTitle());
                    context.startActivity(intent);
                }
            });
        }else if (context instanceof TaskActivity && type.equals(TaskModel.class)){
            final ListView tasksView= (ListView) context.findViewById(R.id.tasks);

            tasksView.setAdapter(new TasksListAdapter(context, (List<TaskModel>) t));
            tasksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TaskModel task= (TaskModel) tasksView.getAdapter().getItem(position);
                    if(!task.isActive()) {
                        Toast.makeText(context, "Task already done",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(context,SolveTaskActivity.class);
                    intent.putExtra("id",task.getId());
                    intent.putExtra("challengeId",task.getChallengeId());
                    intent.putExtra("title",task.getTitle());
                    intent.putExtra("description",task.getDescription());
                    context.startActivity(intent);
                }
            });


        }
    }
}
