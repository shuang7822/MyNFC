package com.wshuang.mynfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.wshuang.mynfc.activity.ReadTextActivity;
import com.wshuang.mynfc.activity.WriteTextActivity;

import com.wshuang.mynfc.base.BaseNfcActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseNfcActivity {
    private TextView ifo_NFC;
    private EditText editFltNr;
    private EditText editFltDate;
    private String  flt;
    private Button Buttonputout;
    private Button Buttonputin;

    private static final String[] strs = new String[]{
            "收  卡",
            "发  卡"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ifo_NFC = findViewById(R.id.ifo_NFC);
        editFltNr =  findViewById(R.id.editFlt);
        editFltDate =  findViewById(R.id.editFltDate);
        Buttonputout =  findViewById(R.id.buttonPutout);
        Buttonputin = findViewById(R.id.buttonPutin);
        editFltDate.setText("当前日期");
        Date date=new Date();
       //第二个mm要大写,不然月份会有错误
        SimpleDateFormat sdf=new SimpleDateFormat("yyyMMdd");//(“yyy-MM-dd”);
         editFltDate.setText(sdf.format(date));
        // NFC适配器，所有的关于NFC的操作从该适配器进行
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!ifNFCUse()) {
            return;
        }
    }
    public void clickPutin(View view){
        flt=editFltDate.getText()+" "+editFltNr.getText();
        flt=flt.toUpperCase();
        Intent intent=new Intent();
        intent.putExtra("FltNr", flt);//设置参数,""
        intent.setClass(this, ReadTextActivity.class);//从哪里跳到哪里
        startActivity(intent);

    }
    public void clickPutout(View view){
        flt=editFltDate.getText()+" "+editFltNr.getText();
        flt=flt.toUpperCase();
        Intent intent2=new Intent();
        intent2.putExtra("FltNr", flt);//设置参数,""
        intent2.setClass(this, WriteTextActivity.class);//从哪里跳到哪里
        startActivity(intent2);

    }

    /*    private void switchActivity(int position) {

            switch (position) {
                case 0:  //收卡
                    Intent intent=new Intent();
                    intent.putExtra("FltNr", flt);//设置参数,""
                    intent.setClass(this, ReadTextActivity.class);//从哪里跳到哪里
                    startActivity(intent);

                   // startActivity(new Intent(this, ReadTextActivity.class));
                    break;
                case 1: //发卡
                    Intent intent2=new Intent();
                    intent2.putExtra("FltNr", flt);//设置参数,""
                    intent2.setClass(this, WriteTextActivity.class);//从哪里跳到哪里
                    startActivity(intent2);

                 //   startActivity(new Intent(this, WriteTextActivity.class));
                    break;
                default:
                    break;
            }
        }*/

    /**
     * 检测工作,判断设备的NFC支持情况
     *
     * @return
     */
    protected Boolean ifNFCUse() {
        if (mNfcAdapter == null) {
            ifo_NFC.setText("设备不支持NFC！");
            return false;
        }
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            ifo_NFC.setText("请在系统设置中先启用NFC功能！");
            return false;
        }
        return true;
    }
}

