package com.th.swatch.treasurehunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.th.swatch.treasurehunt.Models.TaskModel;
import com.th.swatch.treasurehunt.Services.RetrieveDataTask;

public class TaskActivity extends AppCompatActivity {
    static final String APIURL = "https://treasurehuntlive.herokuapp.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        ((TextView)findViewById(R.id.description)).setText(getIntent().getStringExtra("description"));
        int id=getIntent().getIntExtra("id",0);
        new RetrieveDataTask<TaskModel>(APIURL+"challenge/"+id+"/task",this,TaskModel.class).execute();
    }
}
