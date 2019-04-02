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
    String Urlupload="http://ampycure.herokuapp.com/upload";
    String Urlcheck="http://ampycure.herokuapp.com/done";
    String Urlresult="http://ampycure.herokuapp.com/getresult";
    //String Urlupload="http://192.168.50.201:5000/upload";
    //String Urlupload="http://192.168.50.201:5000/upload";
    //String Urlcheck="http://192.168.50.201:5000/done";
    //String Urlresult="http://192.168.50.201:5000/getresult";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //indViewById(R.id.loadingPanel).setVisiblity(View.GONE);
        //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        buttonStart = (Button) findViewById(R.id.button1);
       /* buttonStop = (Button) findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);*/
        buttonUploadAudio = (Button)findViewById(R.id.button4);

        //buttonStop.setEnabled(false);
        //buttonPlayLastRecordAudio.setEnabled(false);
        buttonUploadAudio.setEnabled(false);

        testView1=(TextView) findViewById(R.id.text1);
        testView2=(TextView) findViewById(R.id.text2);
        testView3=(TextView) findViewById(R.id.text3);
        // taking permission

        if(!checkPermission())
            requestPermission();


        //testView1.setText("opu");
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        /*buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "AudioRecording.wav";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);
                    buttonUploadAudio.setEnabled(false);
                    buttonPlayLastRecordAudio.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

            }
        });*/
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        /*buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonUploadAudio.setEnabled(true);

                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });
        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                buttonStart.setEnabled(true);
                Toast.makeText(MainActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });*/
        buttonUploadAudio.setOnClickListener(new View.OnClickListener() {
          //  findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            @Override
            public void onClick(View view) {

                //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                (new UploadtoServer()).execute();

                //findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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


        /*Request request = new Request.Builder()
                .url(Url)
                .get()
                .build();*/

        OkHttpClient client = new OkHttpClient.Builder().readTimeout(300,TimeUnit.SECONDS).build();
        //Response resp = client.newCall(request).execute();

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
        //testView1.setText("opu");
        testView2.post(new Runnable() {
            public void run() {
                testView2.setText(outputParkinson);
            }
        });
        //testView2.setText(outputParkinson);
        testView3.post(new Runnable() {
            public void run() {
                testView3.setText(outputDepression);
            }
        });
        //testView3.setText(outputDepression);
        System.out.println("78");


        /*String jsonData = uploadResponse.body().string();
        JSONObject Jobject = new JSONObject(jsonData);
        JSONArray Jarray = Jobject.getJSONArray("results");
        JSONObject object     = Jarray.getJSONObject(0);

        int check=object.getInt("autism");

        if(check==1)
        {
            output="positive";
        }
        else
        {
            output="negative";
        }*/
        //System.out.println("vjnfdvjbdjvbdjvbj : " + uploadResponse.body().string());
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
