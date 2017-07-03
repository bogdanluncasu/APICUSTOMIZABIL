package com.th.swatch.treasurehunt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.th.swatch.treasurehunt.Services.LoginTask;
import com.th.swatch.treasurehunt.Services.RegisterTask;

public class MainActivity extends AppCompatActivity {

    static final String APIURL = "https://treasurehuntlive.herokuapp.com/";
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.login);
        Button register = (Button) findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                new LoginTask(APIURL, username, password, MainActivity.this).execute();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = ((EditText) findViewById(R.id.username)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Email");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        new RegisterTask(APIURL, username, password, email, MainActivity.this).execute();

                    }
                });

                builder.show();


            }
        });
    }
}



