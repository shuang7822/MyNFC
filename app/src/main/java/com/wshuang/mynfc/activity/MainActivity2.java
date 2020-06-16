package com.wshuang.mynfc.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wshuang.mynfc.R;
import com.wshuang.mynfc.base.BaseNfcActivity;

import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class MainActivity2 extends BaseNfcActivity {
    private static final String[] strs = new String[]{
            "收  卡",
            "发  卡"
    };
    private TextView ifo_NFC;
    private EditText editFltNr;
    private SharedPreferences pref;
    private EditText editFltDate;
    private boolean isExit = false;

    //private String flt;
    private Timer timer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.PutInHistory:
                // Toast.makeText(this,"收卡", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, WriteHistory_Activity.class);
                Log.v("ok", "加载收卡页面");

                startActivity(intent1);

                break;
            case R.id.PutOutHistory:
                // Toast.makeText(this,"发牌", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, ReadHistory_Activity.class);
                Log.v("ok", "加载发牌页面");

                startActivity(intent2);

                break;
            case R.id.ModifyPass:
                //Toast.makeText(this,"关于", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(this, ChangePwdActivity.class);
                Log.v("ok", "加载修改密码页面");

                startActivity(intent3);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("ok", "操作主界面2");
        setContentView(R.layout.activity_main);
        ifo_NFC = findViewById(R.id.ifo_NFC);
        editFltNr = findViewById(R.id.editFlt);
        editFltDate = findViewById(R.id.editFltDate);
        Button buttonputout = findViewById(R.id.buttonPutout);
        Button buttonputin = findViewById(R.id.buttonPutin);
        Button buttonclear = findViewById(R.id.buttonClear);

        editFltDate.setText("当前日期");
        Date date = new Date();
        //第二个mm要大写,不然月份会有错误
        SimpleDateFormat sdf = new SimpleDateFormat("yyyMMdd");//(“yyy-MM-dd”);
        editFltDate.setText(sdf.format(date));
        //设置有效期
        Date date1 = new Date(120, 12, 31);
        Log.v("ok", date1.toString());
        Date now;
        Calendar c = Calendar.getInstance();
        now = c.getTime();// new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        Log.v("ok", "当前时间为：" + now.toString());
        Log.v("ok", "过期时间为：" + date1.toString());

        if (date1.before(now)) {
            editFltDate.setText("证书过期");
            buttonputout.setEnabled(false);
            buttonputin.setEnabled(false);
            buttonputin.setEnabled(false);


        }
        Log.v("ok", "主页面加载成功");
        Connector.getDatabase();
        Log.v("ok", "数据库创建成功");
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean FirstLog = pref.getBoolean("first_login", false);
        if (FirstLog) {
            buttonputout.setEnabled(false);
            buttonputin.setEnabled(false);
            buttonclear.setEnabled(false);

            ifo_NFC.setText("首次登录，请修改密码！");
            ifo_NFC.setTextColor(getResources().getColor(R.color.red_text, getTheme()));
            Log.v("ok", "首次登录");

            return;
        }

        // NFC适配器，所有的关于NFC的操作从该适配器进行
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!ifNFCUse()) {
            buttonputout.setEnabled(false);
            buttonputin.setEnabled(false);
            buttonclear.setEnabled(false);

        }


    }

    @Override
    public void onNewIntent(Intent intent) {

        Toast.makeText(this, "请输入航班号选择操作", Toast.LENGTH_SHORT).show();


    }

    //重写onKeyDown()方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回键调用方法
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    //点击返回键调用的方法
    private void exit() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //2000ms内按第二次则退出
            finish();
            System.exit(0);
        }
    }

    public void clickPutin(View view) {
        String FltData = editFltDate.getText().toString().trim();
        String FltNr = editFltNr.getText().toString().trim();

        if (FltData.length() < 8 | FltNr.length() < 5) {
            makeText(this, "输入不正确！", LENGTH_LONG).show();
            return;
        }
        FltNr = FltNr.toUpperCase();
        Intent intent = new Intent();
        intent.putExtra("FltData", FltData);//设置航班日期
        intent.putExtra("FltNr", FltNr);//设置航班号
        intent.setClass(this, ReadTextActivity.class);//从哪里跳到哪里
        startActivity(intent);

    }

    public void clickPutout(View view) {
        if (editFltDate.getText().toString().trim().length() < 8 | editFltNr.getText().length() < 5) {
            makeText(this, "输入不正确！", LENGTH_LONG).show();
            return;
        }
        String FltData = String.valueOf(editFltDate.getText().toString().trim());
        String FltNr = String.valueOf(editFltNr.getText().toString().trim());
        FltNr = FltNr.toUpperCase();
        Intent intent2 = new Intent();
        intent2.putExtra("FltData", FltData);//设置航班日期
        intent2.putExtra("FltNr", FltNr);//设置航班号
        intent2.setClass(this, WriteTextActivity.class);//从哪里跳到哪里
        startActivity(intent2);

    }

    public void clickClear(View view) {
        if (editFltDate.getText().toString().trim().length() < 8 | editFltNr.getText().length() < 5) {
            makeText(this, "输入不正确！", LENGTH_LONG).show();
            return;
        }
        String FltData = String.valueOf(editFltDate.getText().toString().trim());
        String FltNr = String.valueOf(editFltNr.getText().toString().trim());
        FltNr = FltNr.toUpperCase();
        Intent intent3 = new Intent();
        intent3.putExtra("FltData", FltData);//设置航班日期
        intent3.putExtra("FltNr", FltNr);//设置航班号
        intent3.setClass(this, ClearTextActivity.class);//从哪里跳到哪里
        Log.v("OK", "核销页面开始加载");

        startActivity(intent3);

    }


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

