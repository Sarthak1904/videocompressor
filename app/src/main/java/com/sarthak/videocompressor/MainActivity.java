package com.sarthak.videocompressor;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    Button btselect;
    VideoView videoView1,videoView2;
    TextView textView1,textView2,textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btselect=findViewById(R.id.bt_select);
        videoView1=findViewById(R.id.video_view);
        videoView2=findViewById(R.id.video_view2);
        textView1=findViewById(R.id.text_view1);
        textView2=findViewById(R.id.text_view2);
        textView3=findViewById(R.id.text_view3);

        btselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                    selectVideo();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }

            }
        });

    }

    private void selectVideo() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Video"),100);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selectVideo();
        }else {
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&& resultCode==RESULT_OK && data !=null){
            Uri uri =data.getData();
            videoView1.setVideoURI(uri);
            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            new CompressVideo().execute("false",uri.toString(),file.getPath());

        }
    }

    private class CompressVideo extends AsyncTask<String,String,String> {
        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=ProgressDialog.show(MainActivity.this,"","Compressing...");
        }

        @Override
        protected String doInBackground(String... strings) {
           String videoPath=null;
           Uri uri=Uri.parse((strings[1]));
           try{
               videoPath= SiliCompressor.with(MainActivity.this)
                       .compressVideo(uri,strings[2]);

           }catch (URISyntaxException e){
               e.printStackTrace();
           }
           return videoPath;

        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            videoView1.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
            videoView2.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);

            File file=new File(s);

            Uri uri =Uri.fromFile(file);

            videoView1.start();
            videoView2.start();

            float size=file.length()/1024f;

            textView3.setText(String.format("Size : %.2f KB",size));
        }
    }
}