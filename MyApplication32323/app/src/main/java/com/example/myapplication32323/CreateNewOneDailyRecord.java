package com.example.myapplication32323;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CreateNewOneDailyRecord extends AppCompatActivity {

    private static final String Pat="yyyy-MM-dd-HH-mm-ss";
    private ImageButton imageButtonCreateNewMedia;
    private ImageButton imageButtonSaveText;
    private ImageButton imageButtonBack,imageButtonWatchAll;
    private EditText editText;
    public String name;//name只是时间，没有后缀
    private Context context;
    private TextView textViewTitle,textViewTime,textViewMediaHint;
    private boolean flagMedia=false,flagEdit=false;
    private String NowTime;
    private int H=0,M=0,S=0;
    private String hour,minute,second;
    public static final String SOUND_FILE="UI_NUC_DailyRecordSound"
            ,TEXT_FILE="UI_NUC_DailyRecordText";

    private String getNowTime(){
        Date date = new Date(System.currentTimeMillis());
        String time=date.toLocaleString();
        SimpleDateFormat dateFormat=new SimpleDateFormat(Pat);
        return dateFormat.format(date);
    }

    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_one_daily_record);
        context=CreateNewOneDailyRecord.this;
        imageButtonBack=(ImageButton)findViewById(R.id.imageButton_createNewOneBack);
        imageButtonWatchAll=(ImageButton)findViewById
                (R.id.imageButton_Record_create_watch_all);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent=getIntent();

        textViewTitle=(TextView)findViewById(R.id.textView_Create_New_Record_Title) ;
        name="在此页记录下你今日的故事吧！";
        textViewTitle.setText(name);
        textViewTime=(TextView)findViewById(R.id.textViewRememberTime);
        textViewTime.setVisibility(View.INVISIBLE);
        textViewMediaHint=(TextView)findViewById(R.id.textViewMediaHint);
        textViewMediaHint.setVisibility(View.INVISIBLE);

        imageButtonCreateNewMedia=(ImageButton)findViewById(R.id.imageButton_Record_Media);
        imageButtonCreateNewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagMedia==false){
                    name=getNowTime();
                    textViewTitle.setText(name);
                    imageButtonSaveText.setClickable(false);
                    imageButtonWatchAll.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.INVISIBLE);
                    textViewTime.setText("00:00:00");
                    textViewTime.setVisibility(View.VISIBLE);
                    textViewMediaHint.setVisibility(View.VISIBLE);
                    timer=new Timer();
                    H=0;M=0;S=0;
                    TimerTask timerTask=new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    S++;
                                    if(S==60){
                                        M++;
                                        S=0;
                                    }
                                    if(M==60){
                                        H++;
                                        M=0;
                                    }
                                    hour=(H>=10)?String.valueOf(H):"0"+String.valueOf(H);
                                    minute=(M>=10)? String.valueOf(M):"0"+ String.valueOf(M);
                                    second=(S>=10)?String.valueOf(S):"0"+String.valueOf(S);
                                    textViewTime.setText(hour+":"+minute+":"+second);
                                }
                            });
                        }
                    };
                    timer.schedule(timerTask,1000,1000);
                    startRecord(name+".amr");
                    Toast.makeText(CreateNewOneDailyRecord.this,
                            "开始录制",Toast.LENGTH_SHORT).show();
                    flagMedia=true;
                }else{
                    timer.cancel();
                    editText.setVisibility(View.VISIBLE);
                    imageButtonWatchAll.setVisibility(View.VISIBLE);
                    imageButtonSaveText.setClickable(true);
                    textViewTime.setVisibility(View.INVISIBLE);
                    textViewMediaHint.setVisibility(View.INVISIBLE);
                    flagMedia=false;
                    stopRecord(name+".amr");
                }
                //这里设置传输该文件到读取页面
            }
        });

        editText=(EditText)findViewById(R.id.editText_Create_New_one_text);
        editText.setSaveEnabled(true);
        imageButtonWatchAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager=
                        (InputMethodManager)context.
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow
                        (editText.getWindowToken(),0);
            }
        });

        imageButtonSaveText=(ImageButton)findViewById(R.id.imageButton_save_text) ;

        imageButtonSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=getNowTime();
                textViewTitle.setText(name);
                String ans= String.valueOf(editText.getText());
                SaveTextFile(name+".txt",ans);
            }
        });

    }
    private void SaveTextFile(String name,String ans){
        if(ans.equals("")||ans==null){
            Toast.makeText(this, "无法储存空文件", Toast.LENGTH_SHORT).show();
        }
        else{
            File dir=new File(Environment.getExternalStorageDirectory(),TEXT_FILE);
            if(!dir.exists())dir.mkdirs();
            File text_File=new File(dir,name);
            boolean flag=false,res=false;
            if(!text_File.exists()){
                try{
                    text_File.createNewFile();
                    flag=true;
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(this,
                            "无法对该日记进行储存", Toast.LENGTH_SHORT).show();
                }
            }

            if(flag==true){
                try(OutputStream outputStream=new FileOutputStream(text_File)){
                    outputStream.write(ans.getBytes(StandardCharsets.UTF_8));
                    res=true;
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(this,
                            "无法对该日记进行储存", Toast.LENGTH_SHORT).show();
                }
            }
            if(res==true){
                Log.d("data","储存了"+name);
                RecordDailyActivity.dailyRecordAdapter
                        .AddData(name,CreateNewOneDailyRecord.this,1);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
    //-----------------------
    private MediaRecorder mediaRecorder=null;
    private void startRecord(String name) {
        if (mediaRecorder == null) {
            File dir = new File(Environment.getExternalStorageDirectory(), SOUND_FILE);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            /**
             * 此处也可以选择上传到服务器上面
             */
            File soundFile=new File(dir,name);
            if(!soundFile.exists()){
                try{
                    soundFile.createNewFile();
                    //Log.d("data","create file success");
                }catch (IOException e){e.printStackTrace();}
            }
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mediaRecorder.setOutputFile(soundFile.getAbsolutePath());
            try{
                //Log.d("data","open");
                mediaRecorder.prepare();
                mediaRecorder.start();
                imageButtonCreateNewMedia.setBackgroundResource
                        (R.drawable.record_is_speaking);
            }catch (IOException e){e.printStackTrace();}
        }
    }

    private void stopRecord(String name){
        if(mediaRecorder!=null){
            try{
                mediaRecorder.stop();
            }catch (IllegalStateException e){
                mediaRecorder=null;
                mediaRecorder=new MediaRecorder();
            }
            mediaRecorder.release();
            //Log.d("data","stop");
            mediaRecorder=null;
            imageButtonCreateNewMedia.setBackgroundResource(R.drawable.record_media);
            //这里设置传输该文件到读取页面
            RecordDailyActivity.dailyRecordAdapter
                    .AddData(name,CreateNewOneDailyRecord.this,2);
            Toast.makeText(this, "录制成功", Toast.LENGTH_SHORT).show();
        }
    }

    private MediaPlayer mediaPlayer=null;

    private void ShowRecord(String name){
        File dir=new File(Environment.getExternalStorageDirectory(),SOUND_FILE);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File soundFile=new File(dir,name);

        if(!soundFile.exists()){
            //没有这个文件
            Log.d("data",soundFile.getName());
            //return;
        }
        try{
            Log.d("data", String.valueOf(soundFile.length()));
        }catch (Exception e){e.printStackTrace();}


        mediaPlayer=new MediaPlayer();
        try{
            mediaPlayer.setDataSource(soundFile.getAbsolutePath()+"");
            mediaPlayer.prepare();
            Log.d("data","open1");
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                    Log.d("data","stop1");
                }
            });
        }catch (Exception e){e.printStackTrace();}

    }
}