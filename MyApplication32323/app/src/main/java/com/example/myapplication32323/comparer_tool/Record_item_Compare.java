package com.example.myapplication32323.comparer_tool;

import com.example.myapplication32323.Data.DailyRecord;

import java.util.Comparator;

public class Record_item_Compare implements Comparator<DailyRecord> {
    @Override
    public int compare(DailyRecord dailyRecord, DailyRecord t1) {
        return dailyRecord.getName().compareTo(t1.getName());
    }
}
