package com.wshuang.mynfc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wshuang.mynfc.R;
import com.wshuang.mynfc.base.AESUtils3;
import com.wshuang.mynfc.base.BaseNfcActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author:Created by WuShuang 2018/10/10.
 * Email:SHUANG7822@qq.com
 * Description:
 */
public class ReadTextActivity extends BaseNfcActivity {
    private TextView TextViewFlt;
    private TextView mNfcText;
    private TextView TextViewNo;
    private TextView editFltDate;
    private TextView Tv_nfcmsg;

    private String mTagText;
    private String mText = " ";
    List<String> CardID = new ArrayList<>();
    private String cardID;
    int i = 0;
    private String  FltData;
    private String FltNr;
    private String Psw="!@c#$G%^s&*";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        TextViewNo = findViewById(R.id.textViewNo);
        TextViewFlt = findViewById(R.id.textViewFlt);
        editFltDate = findViewById(R.id.editFltDate);
        Tv_nfcmsg= findViewById(R.id.tv_nfcmsg);
        Intent intent = getIntent();
        FltData = intent.getStringExtra("FltData");
        FltNr = intent.getStringExtra("FltNr");
        // mText=mText.substring(0,4)+"年"+mText.substring(4,6)+"月"+mText.substring(6,8 )+"日  "+mText.substring(9,mText.length());
        editFltDate.setText("日   期：" + FltData);
        TextViewFlt.setText("航班号：" + FltNr);
        Log.v("ok", "加载成功加载成功加载成功加载成功加载成功加载成功加载成功");


    }

    @Override
    public void onNewIntent(Intent intent) {
        byte[] cardid = null;
        //1.获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        cardid = detectedTag.getId();
        mTagText = "UID:" + bytesToHexString(cardid) + "\n";//获取卡的UID
        Log.v("ok", mTagText);

        cardID = bytesToHexString(cardid);
        Log.v("ok", cardID);
        //2.获取Ndef的实例
        Ndef ndef = Ndef.get(detectedTag);

        if (CardID.contains(cardID)) {
            Toast.makeText(this, "这张卡已经收取过", Toast.LENGTH_SHORT).show();
            Tv_nfcmsg.setText("重收");
            Tv_nfcmsg.setTextColor(Color.parseColor("#22ff33"));

            // mTagText = mTagText.toUpperCase()+ndef.getType() + "\n最大容量:" + ndef.getMaxSize() + "bytes\n\n";


        } else {

            readNfcTag(intent);

            Log.v("ok", "读取成功");
            //Toast.makeText(this, "读取成功", Toast.LENGTH_SHORT).show();
            // mTagText = mTagText.toUpperCase()+ndef.getType() + " 最大容量:" + ndef.getMaxSize() + "bytes\n\n";

           // mNfcText.setText(mTagText);
        }

      //  mNfcText.setText(mTagText);


    }

    /**
     * 读取NFC标签文本数据
     */
    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            } else {

                Toast.makeText(this, "这是张空卡", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg.setText("空卡");
                Tv_nfcmsg.setTextColor(Color.parseColor("#ff2222"));

                //声明一个振动器对象
                Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
                long pattern[] = {0, 300, 100, 300};
                vibrator.vibrate(pattern, -1);
                //vibrator.vibrate(1000);


            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    Log.v("ok", "密码对比");

                    Log.v("ok", "原始"+textRecord);
                    mTagText += textRecord + "\n字符长度：" + contentSize + " bytes";
                    mText=FltData+FltNr;
                    Log.v("ok", "文本"+mText);

                    mText=AESUtils3.encrypt2(mText,Psw);
                    Log.v("ok", "对比"+mText);
                    if (textRecord.equals(mText)) {
                        i = i + 1;
                        TextViewNo.setText(String.format("%d", i));
                        Tv_nfcmsg.setText("成功");
                        Tv_nfcmsg.setTextColor(Color.parseColor("#22ff33"));

                        CardID.add(cardID);
                    } else {
                        Toast.makeText(this, "不是本航班的卡", Toast.LENGTH_SHORT).show();
                        Tv_nfcmsg.setText("非本航卡");
                        Tv_nfcmsg.setTextColor(Color.parseColor("#ff2222"));

                        //声明一个振动器对象
                        Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
                        long pattern[] = {0, 300, 100, 300};
                        vibrator.vibrate(pattern, -1);
                        //vibrator.vibrate(1000);

                    }
                } else {
                    Toast.makeText(this, "消息为空", Toast.LENGTH_SHORT).show();
                    Tv_nfcmsg.setText("空卡");
                    Tv_nfcmsg.setTextColor(Color.parseColor("#ff2222"));

                    //声明一个振动器对象
                    Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
                    long pattern[] = {0, 300, 100, 300};
                    vibrator.vibrate(pattern, -1);
                    //vibrator.vibrate(1000);

                }
            } catch (Exception e) {
                Toast.makeText(this, "读取失败，请重试", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg.setText("重试");
                Tv_nfcmsg.setTextColor(Color.parseColor("#ff2222"));


            }
        }
    }

    /**
     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
     *
     * @param ndefRecord
     * @return
     */
    public static String parseTextRecord(NdefRecord ndefRecord) {
        /**
         * 判断数据是否为NDEF格式
         */
        //判断TNF
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        //判断可变的长度的类型
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            //获得字节数组，然后进行分析
            byte[] payload = ndefRecord.getPayload();
            //下面开始NDEF文本数据第一个字节，状态字节
            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
            //其他位都是0，所以进行"位与"运算后就会保留最高位
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
            int languageCodeLength = payload[0] & 0x3f;
            //下面开始NDEF文本数据第二个字节，语言编码
            //获得语言编码
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            //下面开始NDEF文本数据后面的字节，解析出文本
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
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
