package com.wshuang.mynfc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wshuang.mynfc.activity.MainActivity2;
import com.wshuang.mynfc.base.AESUtils3;
import com.wshuang.mynfc.base.BaseNfcActivity;
import com.wshuang.mynfc.base.CrashHandler;
import com.bjcgs.ntag21xseries.NTag213;
import com.bjcgs.ntag21xseries.NTag21x;
import com.bjcgs.ntag21xseries.NTagEventListener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends BaseNfcActivity implements View.OnClickListener, View.OnFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener, TextWatcher {

    private String TAG = "ifu25";
    private ImageButton mIbNavigationBack;
    private LinearLayout mLlLoginPull;
    private View mLlLoginLayer;
    private LinearLayout mLlLoginOptions;
    private EditText mEtLoginUsername;
    private EditText mEtLoginPwd;
    private LinearLayout mLlLoginUsername;
    private CheckBox rememberpass;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ImageView mIvLoginUsernameDel;
    private Button mBtLoginSubmit;
    private LinearLayout mLlLoginPwd;
    private ImageView mIvLoginPwdDel;
    private ImageView mIvLoginLogo;
    private LinearLayout mLayBackBar;
    private String ANDROID_ID = "000000000000";
    private TextView login_about3;
    private TextView Login_result;
    private String Userid;
    private String pwd;
    private String MyUrl;
    private String Psw = "!@c#$G%^s&*";


    //全局Toast
    private Toast mToast;

    private int mLogoHeight;
    private int mLogoWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);

        CrashHandler.getInstance().init(this);//初始化全局异常管理
        initView();
        login_about3.setText("设备ID:" + ANDROID_ID);

        //记住密码功能
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String Account = pref.getString("account", "");
            String Password = pref.getString("password", "");
            mEtLoginUsername.setText(Account);
            Password = AESUtils3.decrypt2(Password, Psw);
            mEtLoginPwd.setText(Password);
            rememberpass.setChecked(true);

        }

    }

    //初始化视图
    private void initView() {
        Log.v("ok", "登录页面加载");

        //登录层、下拉层、其它登录方式层
        mLlLoginLayer = findViewById(R.id.ll_login_layer);
        mLlLoginPull = findViewById(R.id.ll_login_pull);
        mLlLoginOptions = findViewById(R.id.ll_login_options);

        //导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar);
        mIbNavigationBack = findViewById(R.id.ib_navigation_back);

        //logo
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        //username
        mLlLoginUsername = findViewById(R.id.ll_login_username);
        mEtLoginUsername = findViewById(R.id.et_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);

        //passwd
        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);
        login_about3 = findViewById(R.id.tv_login_about3);
        Login_result = findViewById(R.id.login_result);
        rememberpass = findViewById(R.id.remember_pass);
        //提交、注册
        mBtLoginSubmit = findViewById(R.id.bt_login_submit);
        //  mBtLoginRegister = findViewById(R.id.bt_login_register);

        //忘记密码
        //  mTvLoginForgetPwd = findViewById(R.id.tv_login_forget_pwd);
        // mTvLoginForgetPwd.setOnClickListener(this);

        //注册点击事件
        mLlLoginPull.setOnClickListener(this);
        mIbNavigationBack.setOnClickListener(this);
        mEtLoginUsername.setOnClickListener(this);
        mIvLoginUsernameDel.setOnClickListener(this);
        mBtLoginSubmit.setOnClickListener(this);
        //   mBtLoginRegister.setOnClickListener(this);
        mEtLoginPwd.setOnClickListener(this);
        mIvLoginPwdDel.setOnClickListener(this);

        //注册其它事件
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mEtLoginUsername.setOnFocusChangeListener(this);
        mEtLoginUsername.addTextChangedListener(this);
        mEtLoginPwd.setOnFocusChangeListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        Log.v("ok", "登录页面加载成功");
    }

    @Override
    public void onNewIntent(Intent intent) {
        Toast.makeText(this, "请登录后再操作", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                //返回
                finish();
                break;
            case R.id.et_login_username:
                mEtLoginPwd.clearFocus();
                mEtLoginUsername.setFocusableInTouchMode(true);
                mEtLoginUsername.requestFocus();
                break;
            case R.id.et_login_pwd:
                mEtLoginUsername.clearFocus();
                mEtLoginPwd.setFocusableInTouchMode(true);
                mEtLoginPwd.requestFocus();
                break;
            case R.id.iv_login_username_del:
                //清空用户名
                mEtLoginUsername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                //清空密码
                mEtLoginPwd.setText(null);
                break;
            case R.id.bt_login_submit:
                //登录
                Userid = mEtLoginUsername.getText().toString().trim();
                pwd = mEtLoginPwd.getText().toString().trim();
                if (Userid.length() < 6 || pwd.length() < 6) {
                    Login_result.setText("输入的帐号密码太短");
                    Login_result.setTextColor(Color.parseColor("#ff0000"));
                    break;
                }
                pwd = AESUtils3.encrypt2(pwd, Psw);

                Log.v("ok", ANDROID_ID);
                Log.v("ok", Userid);
                Log.v("ok", pwd);
                MyUrl = "http://nfc.bjcgs.com:82/account/cgs?LogStr=" + Userid + "," + pwd + "," + ANDROID_ID;
                Log.v("ok", MyUrl);
                sendRequestWithHttpURLConnection();
                break;
            case R.id.ll_login_layer:
            case R.id.ll_login_pull:
                mLlLoginPull.animate().cancel();
                mLlLoginLayer.animate().cancel();

                int height = mLlLoginOptions.getHeight();
                float progress = (mLlLoginLayer.getTag() != null && mLlLoginLayer.getTag() instanceof Float) ? (float) mLlLoginLayer.getTag() : 1;
                int time = (int) (360 * progress);

                if (mLlLoginPull.getTag() != null) {
                    mLlLoginPull.setTag(null);
                    glide(height, progress, time);
                } else {
                    mLlLoginPull.setTag(true);
                    upGlide(height, progress, time);
                }
                break;
            default:
                break;
        }
    }

    //登录
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
                        Login_result.setText("用户名密码不合格");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        //返回
                        break;
                    case "notthree":
                        Login_result.setText("用户名密码不合格2");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        //返回
                        break;
                    case "devicelosttime":
                        Login_result.setText("此设备超过有效期");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));

                        //返回
                        break;
                    case "invaliddevice":
                        Login_result.setText("此设备被禁用");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        //返回
                        break;
                    case "notthisdevice":
                        Login_result.setText("此设备没登记");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        break;
                    case "logerror":
                        editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        Login_result.setText("帐号没找到或密码错误");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        break;
                    case "logfirst":
                        //
                        String[] result3 = response2.split(",");
                        editor = pref.edit();
                        if (rememberpass.isChecked()) {
                            editor.putBoolean("remember_password", true);
                            editor.putBoolean("first_login", true);
                            editor.putString("account", mEtLoginUsername.getText().toString().trim());
                            editor.putString("password", pwd);
                            editor.putString("UserName", result3[1]);//保存用户姓名
                            editor.putString("Station", result3[2]);//保存用户场站
                        } else {
                            editor.clear();
                        }
                        editor.apply();
                        //成功
                        Intent intent2 = new Intent();
                        intent2.putExtra("UserName", result3[1]);//设置航班日期
                        intent2.putExtra("Station", result3[2]);//设置航班号
                        intent2.setClass(MainActivity.this, MainActivity2.class);//从哪里跳到哪里
                        startActivity(intent2);
                        break;
                    case "logok":
                        //
                        editor = pref.edit();
                        String[] result4 = response2.split(",");
                        editor = pref.edit();
                        if (rememberpass.isChecked()) {
                            editor.putBoolean("remember_password", true);
                            editor.putBoolean("first_login", false);
                            editor.putString("account", mEtLoginUsername.getText().toString().trim());
                            editor.putString("password", pwd);
                            editor.putString("UserName", result4[1]);//保存用户姓名
                            editor.putString("Station", result4[2]);//保存用户场站
                        } else {
                            editor.clear();
                        }
                        editor.apply();
                        //成功
                        Intent intent3 = new Intent();
                        intent3.putExtra("UserName", result4[1]);//设置航班日期
                        intent3.putExtra("Station", result4[2]);//设置航班号
                        intent3.setClass(MainActivity.this, MainActivity2.class);//从哪里跳到哪里
                        startActivity(intent3);
                        break;
                    default:
                        Login_result.setText("未知原因失败");
                        Login_result.setTextColor(Color.parseColor("#ff0000"));
                        break;
                }
            }
        });
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
                    Login_result.setText("网络异常");
                    Login_result.setTextColor(Color.parseColor("#ff0000"));
                    Log.v("ok", e.toString());

                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            Login_result.setText("登录失败-异常");
                            Login_result.setTextColor(Color.parseColor("#ff0000"));
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

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }

    /**
     * menu glide
     *
     * @param height   height
     * @param progress progress
     * @param time     time
     */
    private void glide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height - height * progress)
                .translationY(height)
                .setDuration(time)
                .start();

        mLlLoginLayer.animate()
                .alphaBy(1 * progress)
                .alpha(0)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                        mLlLoginLayer.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * menu up glide
     *
     * @param height   height
     * @param progress progress
     * @param time     time
     */
    private void upGlide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height * progress)
                .translationY(0)
                .setDuration(time)
                .start();
        mLlLoginLayer.animate()
                .alphaBy(1 - progress)
                .alpha(1)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mLlLoginLayer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }
                })
                .start();
    }

    //显示或隐藏logo
    @Override
    public void onGlobalLayout() {
        final ImageView ivLogo = this.mIvLoginLogo;
        Rect KeypadRect = new Rect();

        mLayBackBar.getWindowVisibleDisplayFrame(KeypadRect);

        int screenHeight = mLayBackBar.getRootView().getHeight();
        int keypadHeight = screenHeight - KeypadRect.bottom;

        //隐藏logo
        if (keypadHeight > 300 && ivLogo.getTag() == null) {
            final int height = ivLogo.getHeight();
            final int width = ivLogo.getWidth();
            this.mLogoHeight = height;
            this.mLogoWidth = width;

            ivLogo.setTag(true);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
        //显示logo
        else if (keypadHeight < 300 && ivLogo.getTag() != null) {
            final int height = mLogoHeight;
            final int width = mLogoWidth;

            ivLogo.setTag(null);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                }
            });

            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }
            valueAnimator.start();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //用户名密码输入事件
    @Override
    public void afterTextChanged(Editable s) {
        String username = mEtLoginUsername.getText().toString().trim();
        String pwd = mEtLoginPwd.getText().toString().trim();

        //是否显示清除按钮
        if (username.length() > 0) {
            mIvLoginUsernameDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
        }
        if (pwd.length() > 0) {
            mIvLoginPwdDel.setVisibility(View.VISIBLE);
        } else {
            mIvLoginPwdDel.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.white, getTheme()));

        } else {
            mBtLoginSubmit.setBackgroundResource(R.drawable.bg_login_submit_lock);
            mBtLoginSubmit.setTextColor(getResources().getColor(R.color.account_lock_font_color, getTheme()));
        }
    }


    /**
     * 显示Toast
     *
     * @param msg 提示信息内容
     */
    private void showToast(int msg) {
        if (null != mToast) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
            mToast.show();
        }

        mToast.show();
    }
}
