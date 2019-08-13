package com.wshuang.mynfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wshuang.mynfc.activity.About_Activity;
import com.wshuang.mynfc.activity.ReadHistory_Activity;
import com.wshuang.mynfc.activity.ReadTextActivity;
import com.wshuang.mynfc.activity.WriteHistory_Activity;
import com.wshuang.mynfc.activity.WriteTextActivity;
import com.wshuang.mynfc.base.BaseNfcActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MainActivity extends BaseNfcActivity {
    private TextView ifo_NFC;
    private EditText editFltNr;
    private EditText editFltDate;
    //private String flt;

    private static final String[] strs = new String[]{
            "收  卡",
            "发  卡"
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.PutInHistory:
                Toast.makeText(this,"收卡", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this,WriteHistory_Activity.class);
                Log.v("ok", "加载收卡页面");

                startActivity(intent1);

                break;
            case R.id.PutOutHistory:
                Toast.makeText(this,"发牌", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this,ReadHistory_Activity.class);
                Log.v("ok", "加载发牌页面");

                startActivity(intent2);

                break;
            case R.id.AboutMe:
                Toast.makeText(this,"关于", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this,About_Activity.class);
                Log.v("ok", "加载关于页面");

               startActivity(intent3);
                break;
            default:
              return super.onOptionsItemSelected(item);
        }
        return  true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ifo_NFC = findViewById(R.id.ifo_NFC);
        editFltNr = findViewById(R.id.editFlt);
        editFltDate = findViewById(R.id.editFltDate);
        Button buttonputout = findViewById(R.id.buttonPutout);
        Button buttonputin = findViewById(R.id.buttonPutin);
        editFltDate.setText("当前日期");
        Date date = new Date();
        //第二个mm要大写,不然月份会有错误
        SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");//(“yyy-MM-dd”);
        editFltDate.setText(sdf.format(date));
        //设置有效期
        Date date1 = new Date(119, 9, 25);
        Date now;
        Calendar c = Calendar.getInstance();
        now =c.getTime();// new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        Log.v("ok", "当前时间为："+now.toString());
        Log.v("ok", "过期时间为："+date1.toString());

        if (date1.before(now)) {
            editFltDate.setText("证书过期");
            buttonputout.setEnabled(false);
            buttonputin.setEnabled(false);

        }
        Log.v("ok", "主页面加载成功");
        Log.v("ok", "创建数据库成功！");

        // NFC适配器，所有的关于NFC的操作从该适配器进行
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!ifNFCUse()) {
            return;
        }


    }

    public void clickPutin(View view) {
        if (editFltDate.getText().length() < 8 | editFltNr.getText().length() < 5) {
            makeText(this, "输入不正确！", LENGTH_LONG).show();
            return;
        }
        String FltData = String.valueOf(editFltDate.getText());
        String  FltNr = String.valueOf(editFltNr.getText());
        FltNr=FltNr.toUpperCase();
        Intent intent = new Intent();
        intent.putExtra("FltData", FltData);//设置航班日期
        intent.putExtra("FltNr", FltNr);//设置航班号
        intent.setClass(this, ReadTextActivity.class);//从哪里跳到哪里
        startActivity(intent);

    }

    public void clickPutout(View view) {
        if (editFltDate.getText().length() < 8 | editFltNr.getText().length() < 5) {
            makeText(this, "输入不正确！", LENGTH_LONG).show();
            return;
        }
        String FltData = String.valueOf(editFltDate.getText());
        String  FltNr = String.valueOf(editFltNr.getText());
        FltNr=FltNr.toUpperCase();
        Intent intent2 = new Intent();
        intent2.putExtra("FltData", FltData);//设置航班日期
        intent2.putExtra("FltNr", FltNr);//设置航班号
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

