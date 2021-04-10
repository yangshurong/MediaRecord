package com.example.myapplication32323;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ReadDailyRecordSound extends AppCompatActivity {
    private TextView textViewTitle;
    private ImageButton imageButtonStart,imageButtonStop;
    private ImageButton imageButtonBack;
    private MediaPlayer mediaPlayer;
    private String name;
    private SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_daily_record_sound);
        textViewTitle=(TextView)findViewById(R.id.textView_read_record_sound_title);
        imageButtonBack=(ImageButton)findViewById(R.id.imageButton_read_record_sound_back);
        imageButtonStart=(ImageButton)findViewById(R.id.imageButton_record_media_start);
        imageButtonStop=(ImageButton)findViewById(R.id.imageButton_record_media_stop);
        seekBar=(SeekBar)findViewById(R.id.seekBar_read_record_sound);
        imageButtonStop.setClickable(false);
        seekBar.setClickable(false);
        seekBar.setOnSeekBarChangeListener(new MySeekBar());

        imageButtonStart.setBackgroundResource(R.drawable.record_finish_pause);

        Intent intent=getIntent();
        name=intent.getStringExtra(ReadDailyRecordText.FILE_NAME);
        textViewTitle.setText(name.substring(0,name.length()-4));

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DealWithMedia(name);

    }
    private boolean flagPause=false,flagChange=false,flagStart=false;

    private Timer timer;
    private void DealWithMedia(String name){
        Log.d("data","要播放"+name);
        File dir=new File(Environment.getExternalStorageDirectory()
                ,CreateNewOneDailyRecord.SOUND_FILE);
        if(!dir.exists()){
            Toast.makeText(this, "找不到源文件夹"
                    , Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        File soundFile=new File(dir,name);
        if(!soundFile.exists()){
            Toast.makeText(this, "找不到文件"
                    , Toast.LENGTH_SHORT).show();
            Log.d("data",name);
            finish();
            return;
        }

        imageButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagStart==false){
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setOnCompletionListener
                            (new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer media_Player) {
                                    timer.cancel();
                                    media_Player.release();
                                    media_Player=null;
                                    imageButtonStop.setClickable(false);
                                    seekBar.setProgress(0);
                                    seekBar.setClickable(false);
                                    flagStart=false;
                                    flagPause=false;
                                    imageButtonStart.setBackgroundResource
                                            (R.drawable.record_finish_pause);
                                    Log.d("data","自己播放完了");
                                }
                            });
                    try{
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(soundFile.getAbsolutePath());
                        mediaPlayer.prepareAsync();

                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer media_Player) {
                                seekBar.setMax(media_Player.getDuration());
                                //Log.d("data","duration="+mediaPlayer.getDuration());
                                media_Player.start();
                                timer=new Timer();
                                TimerTask timerTask=new TimerTask() {
                                    @Override
                                    public void run() {
                                        if(flagChange==true){return;}
                                        seekBar.setProgress
                                                (media_Player.getCurrentPosition());
                                    }
                                };
                                timer.schedule(timerTask,0,10);
                                Log.d("data","开始了");
                                imageButtonStop.setClickable(true);
                                imageButtonStart.setBackgroundResource
                                        (R.drawable.record_will_pause);
                                flagStart=true;
                                flagPause=false;
                                seekBar.setClickable(true);
                            }
                        });

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(ReadDailyRecordSound.this
                                , "播放失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(mediaPlayer!=null){
                        if(flagPause==false){
                            mediaPlayer.pause();
                            imageButtonStart.setBackgroundResource
                                    (R.drawable.record_finish_pause);
                            flagPause=true;
                        }else{
                            flagPause=false;
                            mediaPlayer.start();
                            imageButtonStart.setBackgroundResource
                                    (R.drawable.record_will_pause);
                        }
                    }

                }

            }
        });

        imageButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagStart==true){
                    timer.cancel();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                    seekBar.setProgress(0);
                    imageButtonStop.setClickable(false);
                    imageButtonStart.setBackgroundResource
                            (R.drawable.record_finish_pause);
                    seekBar.setClickable(false);
                    Log.d("data","强制直接结束了");
                    flagPause=false;
                    flagStart=false;
                }

            }
        });

    }
    class MySeekBar implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            flagChange=true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(flagStart)mediaPlayer.seekTo(seekBar.getProgress());
            flagChange=false;
        }
    }
    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null){
            if(flagPause==false&&flagStart==true){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
            }

        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(mediaPlayer!=null){
            if(flagStart==true&&flagPause==true){
                mediaPlayer.start();
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mediaPlayer!=null){
            if(flagPause==false&&flagStart==true){
                mediaPlayer.pause();
            }
        }
        super.onPause();
    }

}