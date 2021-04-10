package com.example.myapplication32323;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.myapplication32323.Adapter.DailyRecordAdapter;
import com.example.myapplication32323.Data.DailyRecord;
import com.example.myapplication32323.comparer_tool.Record_item_Compare;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RecordDailyActivity extends AppCompatActivity {
    private boolean flagClick=false;
    private ImageButton imageButtonAdd;

    private List<DailyRecord> list=new ArrayList<>();
    private RecyclerView recyclerView;
    public static DailyRecordAdapter dailyRecordAdapter;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_daily);
        imageButtonAdd=(ImageButton)findViewById(R.id.imageButton_Add_information);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RecordDailyActivity.this
                        ,CreateNewOneDailyRecord.class);
                startActivity(intent);
            }
        });
        context=RecordDailyActivity.this;

        recyclerView=(RecyclerView)findViewById(R.id.RecycleView_List);

        initFile();

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        dailyRecordAdapter=new DailyRecordAdapter(list,context);
        recyclerView.setAdapter(dailyRecordAdapter);

    }

    private void initFile(){
        boolean f1=false,f2=false;
        File[] fileList;
        File fileParent=new File(Environment.getExternalStorageDirectory()
                ,CreateNewOneDailyRecord.SOUND_FILE);
        if(!fileParent.exists()){fileParent.mkdirs(); }
        else f1=true;
        if(f1==true){
            fileList=fileParent.listFiles();
            for(File i:fileList){
                if(i.isFile()){

                    DailyRecord dailyRecord=new DailyRecord();
                    dailyRecord.setType(2);
                    dailyRecord.setName(i.getName());
                    dailyRecord.setId(R.drawable.record_show_media_item);
                    list.add(dailyRecord);


                   // i.delete();
                }
            }
        }

        File fileParent2=new File(Environment.getExternalStorageDirectory()
                ,CreateNewOneDailyRecord.TEXT_FILE);
        if(!fileParent2.exists())fileParent2.mkdirs();
        else f2=true;
        if(f2==true){
            fileList=fileParent2.listFiles();
            for(File i:fileList){
                if(i.isFile()){

                    DailyRecord dailyRecord=new DailyRecord();
                    dailyRecord.setName(i.getName());
                    dailyRecord.setType(1);
                    dailyRecord.setId(R.drawable.record_show_text_item);
                    list.add(dailyRecord);

                    //i.delete();
                }
            }
        }
        String pre="",now,t;
        List<DailyRecord> t_list=new ArrayList<>();
        for(DailyRecord dailyRecord:list){
            t=dailyRecord.getName();
            now=t.substring(0,4)+t.substring(5,7)+t.substring(8,10);
            if(!now.equals(pre)){
                DailyRecord TheNewOne=new DailyRecord();
                TheNewOne.setId(R.drawable.record_show_media_item);
                TheNewOne.setType(3);
                TheNewOne.setName(t.substring(0,4)+"-"
                        +t.substring(5,7)+"-"+t.substring(8,10)+"-00-00-00");
                pre=now;
                t_list.add(TheNewOne);
            }
        }
        for(DailyRecord e:t_list)list.add(e);
        Collections.sort(list,new Record_item_Compare());
    }

}