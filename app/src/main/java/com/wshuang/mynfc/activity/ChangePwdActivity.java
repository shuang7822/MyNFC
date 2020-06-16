package com.wshuang.mynfc.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wshuang.mynfc.R;
import com.wshuang.mynfc.base.AESUtils3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChangePwdActivity extends AppCompatActivity {
    // Content View Elements

    private EditText mEt_login_username;
    private EditText mEt_old_pwd;
    private EditText mEt_reset_pwd;
    private EditText mEt_reset_pwd2;
    private Button mBt_reset_submit;
    private SharedPreferences pref;
    private TextView textView1;
    private SharedPreferences.Editor editor;
    private String Userid;
    private String OldPwd;
    private String NewPwd;
    private String MyUrl;
    private String NewPwd2;
    private String Psw = "!@c#$G%^s&*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("ok", "修改密码页面开始加载");

        setContentView(R.layout.activity_main_reset_pwd);
        mEt_login_username = findViewById(R.id.et_login_username);
        mEt_old_pwd = findViewById(R.id.et_old_pwd);
        mEt_reset_pwd = findViewById(R.id.et_reset_pwd);
        mEt_reset_pwd2 = findViewById(R.id.et_reset_pwd2);
        mBt_reset_submit = findViewById(R.id.bt_reset_submit);
        textView1 = findViewById(R.id.textView1);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String Account = pref.getString("account", "");
        mEt_login_username.setText(Account);
        mEt_login_username.setEnabled(false);

        // findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        Log.v("ok", "修改密码页面加载成功");

    }

    public void clickChange(View view) {

        Log.v("ok", "开始密码！");
        Userid = mEt_login_username.getText().toString().trim();
        OldPwd = mEt_old_pwd.getText().toString().trim();
        NewPwd = mEt_reset_pwd.getText().toString().trim();
        NewPwd2 = mEt_reset_pwd2.getText().toString().trim();
        if (!NewPwd.equals(NewPwd2)) {
            Log.v("ok", NewPwd);
            Log.v("ok", NewPwd2);

            mEt_reset_pwd.setText(null);
            mEt_reset_pwd2.setText(null);
            textView1.setText("新密码不一致");
            return;
        }
        //     if (isDGCheckUKEYPinPwd(NewPwd)) {
        if (!isPassword(NewPwd)) {
            Log.v("ok", NewPwd);
            textView1.setText("新密码太简单");
            return;
        }
        String OldPwd2 = pref.getString("password", "");
        OldPwd = AESUtils3.encrypt2(OldPwd, Psw);

        if (!OldPwd.equals(OldPwd2)) {
            Log.v("ok", OldPwd);
            Log.v("ok", OldPwd2);

            textView1.setText("旧密码不正确");
            return;
        }
        NewPwd = AESUtils3.encrypt2(NewPwd, Psw);

        MyUrl = "http://nfc.bjcgs.com:82/account/CGP?ChStr=" + Userid + "," + OldPwd + "," + NewPwd;
        Log.v("ok", MyUrl);
        sendRequestWithHttpURLConnection();
        Log.v("ok", "密码修改结束！");
    }

    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(MyUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    Log.v("ok", "1");

                    InputStream in = connection.getInputStream();
                    Log.v("ok", "2");

                    // 下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));

                    StringBuilder response = new StringBuilder();
                    Log.v("ok", "3");

                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.v("ok", "4");
                        response.append(line);
                    }
                    Log.v("ok", response.toString());
                    Log.v("ok", "取到值");

                    loginRequest(response.toString());


                } catch (Exception e) {
                    textView1.setText("网络异常！");
                    textView1.setTextColor(Color.parseColor("#ff0000"));
                    Log.v("ok", e.toString());

                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            textView1.setText("登录失败-异常");
                            textView1.setTextColor(Color.parseColor("#ff0000"));
                            Log.v("ok", e.toString());
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    private void loginRequest(final String response) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                Log.v("ok", "loginRequest-------------------");

                String restitch;//str1.indexOf(str2) == -1
                Log.v("ok", response + "返回-------");

                //去掉前后的双引号
                String response2 = response.substring(1, response.length() - 1);
                Log.v("ok", response2);
                if (response2.contains(",")) {
                    String[] result2 = response2.split(",");
                    restitch = result2[0];
                } else {
                    restitch = response2;
                }
                switch (restitch) {
                    case "tooshort":
                        textView1.setText("用户名密码不合格");
                        textView1.setTextColor(Color.parseColor("#ff0000"));
                        //返回
                        break;
                    case "notthree":
                        textView1.setText("用户名密码不合格2");
                        textView1.setTextColor(Color.parseColor("#ff0000"));
                        //返回
                        break;
                    case "changerror":
                        textView1.setText("用户名密码不正确");
                        textView1.setTextColor(Color.parseColor("#ff0000"));
                        break;
                    case "changok":
                        //
                        editor = pref.edit();
                        String[] result4 = response2.split(",");
                        editor = pref.edit();
                        editor.putString("password", NewPwd);
                        editor.apply();
                        //成功
                        Log.v("ok", "密码修改成功！");
                        textView1.setText("修改成功,请退出重新登录!");
                        textView1.setTextColor(Color.parseColor("#00ff00"));
                        break;
                    default:
                        textView1.setText("未知原因失败");
                        textView1.setTextColor(Color.parseColor("#ff0000"));
                        break;
                }
            }
        });
    }

    //6-16位数字字母混合,不能全为数字,不能全为字母,首位不能为数字
    public boolean isPassword(String password) {
        String regex = "^(?![0-9])(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        boolean isMatch = m.matches();
        Log.i("ok", "isPassword: 是否密码正则匹配" + isMatch);
        return isMatch;
    }
}

