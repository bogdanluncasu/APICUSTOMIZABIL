package com.th.swatch.treasurehunt;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.th.swatch.treasurehunt.Services.SolveTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.th.swatch.treasurehunt.MainActivity.APIURL;

public class SolveTaskActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView takenPhoto;
    private Uri mImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solve_task);
        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        ((TextView)findViewById(R.id.description)).setText(getIntent().getStringExtra("description"));
        takenPhoto = (ImageView) findViewById(R.id.image);
        takenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File photo;
                try
                {
                    File tempDir= Environment.getExternalStorageDirectory();
                    tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
                    if(!tempDir.exists())
                    {
                        tempDir.mkdirs();
                    }
                    photo=File.createTempFile("picture", ".jpg", tempDir);
                    mImageUri = Uri.fromFile(photo);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                catch(IOException e)
                {
                    Log.v("LBC_PHOTO", "Can't create file to take picture!");
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            int taskId=getIntent().getIntExtra("id",0);
            int challengeId=getIntent().getIntExtra("challengeId",0);
            String url=APIURL+"challenge/"+challengeId+"/task/"+taskId+"/solve";
            new SolveTask(url,mImageUri,takenPhoto,this,this.getContentResolver()).execute();
        }
    }
}
