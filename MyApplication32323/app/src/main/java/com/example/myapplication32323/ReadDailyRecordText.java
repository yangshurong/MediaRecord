package com.example.myapplication32323;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadDailyRecordText extends AppCompatActivity {
    public static final String FILE_NAME="READ_FILE_NAME";
    private String name;
    private ImageButton imageButton;
    private TextView textViewTitle,textViewContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_daily_record_text);
        Intent intent=getIntent();
        name=intent.getStringExtra(FILE_NAME);

        textViewTitle=(TextView)findViewById(R.id.textView_read_record_text_title);
        textViewContent=(TextView)findViewById(R.id.textView_read_record_content);
        imageButton=(ImageButton)findViewById(R.id.imageButton_read_record_text_back);
        textViewTitle.setText(name.substring(0,name.length()-4));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DealWithText(name);
    }
    private void DealWithText(String name){
        File dir=new File(Environment.getExternalStorageDirectory()
                ,CreateNewOneDailyRecord.TEXT_FILE);
        if(!dir.exists()){
            Toast.makeText(this, "没有找到文件，打开失败", Toast.LENGTH_SHORT).show();
            return;
        }
        File textFile=new File(dir,name);
        if(!textFile.exists()){
            Toast.makeText(this, "没有找到文件，打开失败", Toast.LENGTH_SHORT).show();
            return;
        }
        String res="";
        try(InputStream inputStream=new FileInputStream(textFile)){
            BufferedReader bufferedReader=new
                    BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8));
            String line="";
            while ((line=bufferedReader.readLine())!=null){res+=line+"\n";}
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(this, "文件打开失败", Toast.LENGTH_SHORT).show();
        }
        textViewContent.setText(res);
    }
}