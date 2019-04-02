package com.example.paras.amypcure;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deskode.recorddialog.RecordDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Thread.sleep;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonUploadAudio ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    TextView testView1,testView2,testView3;
    Random random ;
    public static final int RequestPermissionCode = 1;
    RecordDialog recordDialog;
    MediaPlayer mediaPlayer ;
    private ProgressBar spinner;
    String Urlupload="http://ampycure.herokuapp.com/upload";
    String Urlcheck="http://ampycure.herokuapp.com/done";
    String Urlresult="http://ampycure.herokuapp.com/getresult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonStart = (Button) findViewById(R.id.button1);

        buttonUploadAudio = (Button)findViewById(R.id.button4);

        buttonUploadAudio.setEnabled(false);

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        testView1=(TextView) findViewById(R.id.text1);
        testView2=(TextView) findViewById(R.id.text2);
        testView3=(TextView) findViewById(R.id.text3);

        if(!checkPermission())
            requestPermission();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testView1.setText("N/A");
                testView2.setText("N/A");
                testView3.setText("N/A");
                buttonUploadAudio.setEnabled(true);
                recordDialog = RecordDialog.newInstance("Record Audio");
                recordDialog.setMessage("Press for record");
                recordDialog.show(MainActivity.this.getFragmentManager(),"TAG");
                recordDialog.setPositiveButton("Save", new RecordDialog.ClickListener() {
                    @Override
                    public void OnClickListener(String path) {
                        AudioSavePathInDevice=path;
                        Toast.makeText(MainActivity.this,"Save audio: " + path, Toast.LENGTH_LONG).show();
                    }
                });
            }


        });
        buttonUploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spinner.setVisibility(View.VISIBLE);
                (new UploadtoServer()).execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
    public void test_Upload() throws Exception {

        File file = new File(AudioSavePathInDevice);

        MultipartBody.Builder data = new MultipartBody.Builder();
        data.setType(MultipartBody.FORM);
        data.addFormDataPart("file","AudioRecordin.wav", RequestBody.create(MediaType.parse("media/type"), file));
        RequestBody requestBody = data.build();

        Request uploadRequest = new Request.Builder()
                .url(Urlupload)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(300,TimeUnit.SECONDS).build();


        Response uploadResponse = client.newCall(uploadRequest).execute();
        System.out.println("Upload : " + uploadResponse.body().string());
        uploadResponse.body().close();
        System.out.println("After Upload");
        boolean requestReceived = false;
        while(true)
        {
            System.out.println("Inside loop");
            Request checkRequest=new Request.Builder().
                    url(Urlcheck).
                    get().
                    build();
            Response checkResponse = client.newCall(checkRequest).execute();
            String temp=checkResponse.body().string();
            System.out.println("Done : " + temp);
            checkResponse.body().close();
            if(temp.equals("true"))
            {

                //testView1.setText("1236");
                requestReceived=true;
                break;
            }
            sleep(1000);
        }

        System.out.println("not requesting for result");
        Request receiveRequest=new Request.Builder().
                url(Urlresult).
                get().
                build();
        Response resultResponse = client.newCall(receiveRequest).execute();
        String result= resultResponse.body().string();
        resultResponse.body().close();
        System.out.println("Resp : " + result);
        String outputAutism,outputParkinson,outputDepression;
        if(result.charAt(1)=='0')
        {
            outputAutism="negative";
        }
        else
        {
            outputAutism="positive";
        }
        if(result.charAt(3)=='0')
        {
            outputParkinson="negative";
        }
        else
        {
            outputParkinson="positive";
        }
        if(result.charAt(5)=='0')
        {
            outputDepression="negative";
        }
        else
        {
            outputDepression="positive";
        }
        testView1.post(new Runnable() {
            public void run() {
                testView1.setText(outputAutism);
            }
        });
        testView2.post(new Runnable() {
            public void run() {
                testView2.setText(outputParkinson);
            }
        });
        testView3.post(new Runnable() {
            public void run() {
                testView3.setText(outputDepression);
            }
        });
        System.out.println("78");
        spinner.setVisibility(View.GONE);
        file.delete();
        buttonUploadAudio.setEnabled(false);
    }
    private class UploadtoServer extends AsyncTask<Void, Void, Void> {


        @Override

        protected Void doInBackground(Void... voids) {
            try {
                test_Upload();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
