package com.wshuang.mynfc.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wshuang.mynfc.R;
import com.wshuang.mynfc.base.AESUtils3;
import com.wshuang.mynfc.base.BaseNfcActivity;
import com.wshuang.mynfc.base.Cards;
import com.wshuang.mynfc.base.SoundUtils;

import org.litepal.crud.DataSupport;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Author:Created by WuShuang 2018/10/10.
 * Email:SHUANG7822@qq.com
 * Description:
 */
public class WriteTextActivity extends BaseNfcActivity {
    private TextView TextViewFlt;
    private String mText = "NFC-CGS";
    private TextView TextViewNo;
    private TextView Tv_nfcmsg;
    private TextView editFltDate;
    List<String> CardID = new ArrayList<>();
    private String cardID;
    private String FltData;
    private String FltNr;
    private String Psw = "!@c#$G%^s&*";
    private SoundUtils mysound;


    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);
        TextViewNo = findViewById(R.id.textViewNo);
        TextViewFlt = findViewById(R.id.textViewFlt);
        editFltDate = findViewById(R.id.editFltDate);
        Tv_nfcmsg = findViewById(R.id.tv_nfcmsg);
        Intent intent = getIntent();
        FltData = intent.getStringExtra("FltData");
        FltNr = intent.getStringExtra("FltNr");
        // mText=mText.substring(0,4)+"年"+mText.substring(4,6)+"月"+mText.substring(6,8 )+"日  "+mText.substring(9,mText.length());
        editFltDate.setText("日期：" + FltData);
        TextViewFlt.setText("航班：" + FltNr + "    ");
        Log.v("ok", "发卡页面加载成功");

        //获取已经发放的数量
        String mysql = "select CardID from Cards where FltNr='" + FltNr + "' AND  Date='" + FltData + "' AND operate='F'";
        Log.v("OK", mysql);

        Cursor cardss = DataSupport.findBySQL(mysql);
        Log.v("OK", "库中数据收量" + String.valueOf(cardss.getCount()));
        i = cardss.getCount();
        TextViewNo.setText(String.format("%d", i));
        mText = FltData + FltNr;
        Log.v("ok", mText);

        mText = AESUtils3.encrypt2(mText, Psw);

        if (cardss != null && cardss.moveToFirst()) {

            do {
                Log.v("ok", "CARDS ID：" + cardss.getString(0));
                CardID.add(cardss.getString(0));

            } while (cardss.moveToNext());


        }
        mysound = new SoundUtils(this, 3);
        mysound.putSound(0, R.raw.errorpassenger);
        mysound.putSound(1, R.raw.chongfa);
        mysound.putSound(2, R.raw.chongshou);
        mysound.putSound(3, R.raw.chongshi);
        mysound.putSound(4, R.raw.cuowuka);
        mysound.putSound(5, R.raw.kongfuhe);
        mysound.putSound(6, R.raw.shibai);


    }

    @Override
    public void onNewIntent(Intent intent) {
        //新代码
        if (mText == null)
            return;

        //1.获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] cardid = detectedTag.getId();
        cardID = bytesToHexString(cardid);
        Log.v("ok", cardID);
        Log.v("ok", mText);

        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{createTextRecord(mText)});

        if (CardID.contains(cardID)) {
            Toast.makeText(this, "这张复核牌已经发放过", Toast.LENGTH_SHORT).show();
            Tv_nfcmsg.setText("重发");
            Tv_nfcmsg.setTextColor(Color.parseColor("#22ff33"));
            mysound.playSound(1, 0);

        } else {

            boolean result = writeTag(ndefMessage, detectedTag);
            if (result) {
                i = i + 1;
                TextViewNo.setText(String.format("%d", i));
                Toast.makeText(this, "复核牌发放成功", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg.setText("成功");
                Tv_nfcmsg.setTextColor(Color.parseColor("#22ff33"));

                //写入数据库
                Cards cards = new Cards();
                cards.setCardID(cardID);
                cards.setDate(FltData);
                cards.setFltNr(FltNr);
                cards.setOperationtime(Calendar.getInstance().getTime());
                cards.setUserID("707091");
                cards.setOperate("F");
                cards.save();

                Log.v("ok", "写入数据库");
                CardID.add(cardID);
            } else {
                Toast.makeText(this, "复核牌发放失败", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg.setText("失败");
                Tv_nfcmsg.setTextColor(Color.parseColor("#ff2222"));
                mysound.playSound(6, 0);


                Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                long pattern[] = {0, 300, 100, 300};
                vibrator.vibrate(pattern, -1);
                //vibrator.vibrate(1000);
            }
        }
    }

    /**
     * 创建NDEF文本数据
     *
     * @param text
     * @return
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }

    /**
     * 写数据
     *
     * @param ndefMessage 创建好的NDEF文本数据
     * @param tag         标签
     * @return
     */
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
            Log.v("fail", "writeTag中写卡失败" + e.getMessage());
            return false;
        }
    }

    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

}
