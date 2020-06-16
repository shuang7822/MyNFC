package com.wshuang.mynfc.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.wshuang.mynfc.R;

import org.litepal.crud.DataSupport;

public class WriteHistory_Activity extends Activity {
    private Cursor cursor = null;
    private SimpleCursorAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_history);
        TextView Viewtext = findViewById(R.id.textViewWrite);
        //TextView textViewtext = findViewById(R.id.textView6);
        // List<Cards> cardss=DataSupport.findAll(Cards.class);
        //  String temp="";
        //  for(Cards cards1:cardss){
        //      temp+=cards1.getCardID()+" "+cards1.getDate()+" "+cards1.getFltNr()+" "+cards1.getOperate()+" "+cards1.getOperationtime().toString()+" "+cards1.getUserID()+"\n";
//
        //     }
        //  textViewtext.setText(temp);
        Cursor cardss = DataSupport.findBySQL("select Date,FltNr,count(FltNr) as total from Cards where operate='S' group by Date,FltNr  order by Date desc,FltNr");
        Log.v("OK", "库中数据量" + String.valueOf(cardss.getCount()));
        //cursor不为空，moveToFirst为true说明有数据

        // if(cardss!=null&&cardss.moveToFirst()){
//
        //    do{
        //       Log.v("ok", "日期："+cardss.getString(0)+"  航班号："+cardss.getString(1)+"  已收数："+cardss.getString(2));
//
        //   }while(cardss.moveToNext());

        // }
        //  SimpleCursorAdapter simpleCursorAdapter = new
        //         SimpleCursorAdapter(WriteHistory_Activity.this, // 上下文
        //         R.layout.cards_item, // Item布局
        //          cardss, // Cursor 查询出来的游标 这里面有数据库里面的数据
        //        new String[]{"Date", "FltNr", "total"}, // 从哪里来，指的是 查询出数据库列名
        //        new int[]{R.id.CarsDate, R.id.CarsFltNr, R.id.CarsTotal}, // 到哪里去，指的是，把查询出来的数据，赋值给Item布局 的控件
        //       SimpleCursorAdapter.NO_SELECTION);

        // 设置ListView适配器
        //  listView.setAdapter(simpleCursorAdapter);
        String temp = "";
        int i = 0;
        if (cardss != null && cardss.moveToFirst()) {

            do {
                i = i + 1;
                temp += "日期：" + cardss.getString(0) + "  航班号：" + cardss.getString(1) + "  已收数：" + cardss.getString(2) + "\n";
                Log.v("ok", "日期：" + cardss.getString(0) + "  航班号：" + cardss.getString(1) + "  已收数：" + cardss.getString(2));
                if (i > 29) break;
//
            } while (cardss.moveToNext());


//
        }
        Viewtext.setText(temp);

    }
}
