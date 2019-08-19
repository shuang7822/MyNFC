package com.wshuang.mynfc.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.wshuang.mynfc.R;

import org.litepal.crud.DataSupport;

public class ReadHistory_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_history);
        TextView textview=findViewById(R.id.textViewRead);
        Cursor cardss=DataSupport.findBySQL("select  Date,FltNr,count(FltNr) as total from Cards where operate='F' group by Date,FltNr  order by Date desc,FltNr ");
        Log.v("OK", "库中数据量"+String.valueOf(cardss.getCount()));
        String temp="";
        int i=0;
        if(cardss!=null&&cardss.moveToFirst()){

            do{i=i+1;

                temp+="日期："+cardss.getString(0)+"  航班号："+cardss.getString(1)+"  已发数："+cardss.getString(2)+"\n";
                Log.v("ok", "日期："+cardss.getString(0)+"  航班号："+cardss.getString(1)+"  已发数："+cardss.getString(2));
                if(i>29)break;
           }while(cardss.moveToNext());
        }
        textview.setText(temp);


    }
}
