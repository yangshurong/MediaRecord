package com.example.myapplication32323.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication32323.CreateNewOneDailyRecord;
import com.example.myapplication32323.Data.DailyRecord;
import com.example.myapplication32323.R;
import com.example.myapplication32323.ReadDailyRecordSound;
import com.example.myapplication32323.ReadDailyRecordText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DailyRecordAdapter extends
        RecyclerView.Adapter<DailyRecordAdapter.ViewHolder>{
    private List<DailyRecord> list=new ArrayList<>();
    private Context context;

    public DailyRecordAdapter(List<DailyRecord> list, Context context){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_record_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=viewHolder.getAdapterPosition();
                DailyRecord tmp=list.get(pos);
                if(tmp.getType()==1){
                    Intent intent=new Intent(context
                            , ReadDailyRecordText.class);
                    intent.putExtra(ReadDailyRecordText.FILE_NAME,tmp.getName());
                    context.startActivity(intent);
                }
                else if(tmp.getType()==2){
                    Intent intent=new Intent(context
                            ,ReadDailyRecordSound.class);
                    intent.putExtra(ReadDailyRecordText.FILE_NAME,tmp.getName());
                    context.startActivity(intent);
                }
            }
        });

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=viewHolder.getAdapterPosition();
                DailyRecord tmp=list.get(pos);
                DealClose(tmp.getName(),tmp.getType());
            }
        });
        return viewHolder;
    }

    private void DealClose(String name,int type){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setTitle("是否删除"+name);
        dialog.setMessage("请注意，删除后将无法恢复！");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteData(name,context,type);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyRecord dailyRecord=list.get(position);
        String nowName=dailyRecord.getName();
        holder.textView.setText(nowName.substring(0,nowName.length()-4));
        holder.imageView.setBackgroundResource
                (dailyRecord.getId());
        holder.underline.setVisibility(View.INVISIBLE);
        if(dailyRecord.getType()==3){
            holder.imageButton.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.underline.setVisibility(View.VISIBLE);
            holder.textView.setText("     "+nowName.substring(0,10));
            holder.textView.setTextColor(Color.GRAY);
            holder.textView.setGravity(Gravity.BOTTOM);
            holder.textView.setTextSize(15);
            //holder.textView.setGravity(Gravity.LEFT);
            holder.imageView.setMaxWidth(0);
        }
    }

    public void AddData(String name,Context NowContext,int id){
        boolean flag=false;
        for(DailyRecord i:list){
            if(i.getName().equals(name)){
                flag=true;
                Toast.makeText(NowContext, "此日记已经存在", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(flag==false){
            DailyRecord tmp=new DailyRecord();
            tmp.setName(name);
            tmp.setType(id);
            if(id==1)tmp.setId(R.drawable.record_show_text_item);
            else tmp.setId(R.drawable.record_show_media_item);
            list.add(tmp);
            notifyDataSetChanged();
        }
    }
    private boolean flag=false;
    public void deleteData(String name,Context NowContext,int type){
        flag=false;
        for(int i=0;i<list.size();i++){
            DailyRecord tmp=list.get(i);
            if(tmp.getName().equals(name)){
                list.remove(i);
                if(tmp.getType()==1)removeText(name,NowContext,i);
                else removeSound(name,NowContext,i);
                break;
            }
        }
        if(flag==false){
            Toast.makeText(NowContext, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeSound(String name,Context NowContext,int i){
        File dir = new File(Environment.getExternalStorageDirectory()
                , CreateNewOneDailyRecord.SOUND_FILE);
        File soundFile=new File(dir,name);
        if(soundFile.exists())soundFile.delete();
        notifyItemRemoved(i);
        if(i!=getItemCount()){
            notifyItemRangeChanged(i,getItemCount());
        }
        if(!soundFile.exists()){
            flag=true;
            Toast.makeText(NowContext, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeText(String name,Context NowContext,int i){
        Log.d("data","delete name="+name);
        File dir = new File(Environment.getExternalStorageDirectory()
                , CreateNewOneDailyRecord.TEXT_FILE);
        File soundFile=new File(dir,name);
        if(soundFile.exists())soundFile.delete();
        notifyItemRemoved(i);
        if(i!=getItemCount()){
            notifyItemRangeChanged(i,getItemCount());
        }
        if(!soundFile.exists()){
            flag=true;
            Toast.makeText(NowContext, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView textView;
        ImageButton imageButton;
        ImageView imageView;
        View underline;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.CardView_daily_record_item);
            textView=(TextView)itemView.findViewById(R.id.textView_daily_record_item);
            imageButton=(ImageButton)itemView
                    .findViewById(R.id.imageButton_record_item_close);
            imageView=(ImageView)itemView.findViewById(R.id.imageView_record_show_type_item);
            underline=(View)itemView.findViewById(R.id.daily_record_item_underline);
        }
    }
}
