package com.wshuang.mynfc.activity;

import android.content.Intent;
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
import com.wshuang.mynfc.base.BaseNfcActivity;
import java.util.Arrays;

/**
 * Author:Created by WuShuang 2018/10/10.
 * Email:SHUANG7822@qq.com
 * Description:
 */
public class ReadTextActivity extends BaseNfcActivity {
    private TextView TextViewFlt;
    private TextView mNfcText;
    private TextView TextViewNo;
    private String mTagText;
    private String mText = " ";
    int i=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        mNfcText = (TextView) findViewById(R.id.tv_nfctext);
        TextViewNo = (TextView) findViewById(R.id.textViewNo);
        TextViewFlt= (TextView) findViewById(R.id.textViewFlt);
        Intent intent=getIntent();
        mText=intent.getStringExtra("FltNr");
        TextViewFlt.setText("操作航班为："+mText);

    }

    @Override
    public void onNewIntent(Intent intent) {
            //1.获取Tag对象
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            byte[] aa = detectedTag.getId();
            mTagText="UID:"+ bytesToHexString(aa)+"\n";//获取卡的UID
            //2.获取Ndef的实例
            Ndef ndef = Ndef.get(detectedTag);
            mTagText = mTagText.toUpperCase()+ndef.getType() + " 最大容量:" + ndef.getMaxSize() + "bytes\n\n";
            readNfcTag(intent);
            Log.v("ok","disnable2");
            mNfcText.setText(mTagText);

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
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    mTagText += textRecord + "\n\n字符长度：" + contentSize + " bytes";
                    if (textRecord.equals(mText))
                    {
                        i=i+1;
                        TextViewNo.setText(String.format("%d", i));
                    }else
                    {
                        Toast.makeText(this, "不是本航班的卡", Toast.LENGTH_SHORT).show();
                        //声明一个振动器对象
                        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);

                    }
                }
            } catch (Exception e) {
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
