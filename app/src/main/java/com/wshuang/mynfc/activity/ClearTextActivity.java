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
import android.os.Parcelable;
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

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ClearTextActivity extends BaseNfcActivity {
    ArrayList<String> CardID = new ArrayList<>();
    ArrayList<String> CardID2 = new ArrayList<>();
    int i = 0;
    private TextView TextViewNo1;
    private TextView TextViewFlt1;
    private TextView editFltDate1;
    private TextView Tv_nfcmsg1;
    private String mTagText;
    private String mText = " ";
    private String cardID;
    private String FltData;
    private String FltNr;
    private String Psw = "!@c#$G%^s&*";
    private SoundUtils mysound;


    public static int getCapacity(ArrayList arrayList) {
        try {
            Field elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
            return ((Object[]) elementDataField.get(arrayList)).length;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_text);
        TextViewNo1 = findViewById(R.id.textViewNo);
        TextViewFlt1 = findViewById(R.id.textViewFlt);
        editFltDate1 = findViewById(R.id.editFltDate);
        Tv_nfcmsg1 = findViewById(R.id.tv_nfcmsg);
        Intent intent = getIntent();
        FltData = intent.getStringExtra("FltData");
        FltNr = intent.getStringExtra("FltNr");
        // mText=mText.substring(0,4)+"年"+mText.substring(4,6)+"月"+mText.substring(6,8 )+"日  "+mText.substring(9,mText.length());
        editFltDate1.setText("日期：" + FltData);
        TextViewFlt1.setText("航班：" + FltNr + "    ");
        Log.v("ok", "核销页面--加载成功2");


        String mysql = "select CardID from Cards where FltNr='" + FltNr + "' AND  Date='" + FltData + "' AND operate='F'";
        Log.v("OK", mysql);

        Cursor cardss = DataSupport.findBySQL(mysql);
        Log.v("OK", "库中数据航班已发卡量" + String.valueOf(cardss.getCount()));
        if (cardss != null && cardss.moveToFirst()) {

            do {
                Log.v("ok", "CARDS ID：" + cardss.getString(0));
                CardID.add(cardss.getString(0));

            } while (cardss.moveToNext());
        }

        mText = FltData + FltNr;
        Log.v("ok", "文本" + mText);
        mText = AESUtils3.encrypt2(mText, Psw);
        mysound = new SoundUtils(this, 3);
        mysound.putSound(0, R.raw.errorpassenger);
        mysound.putSound(1, R.raw.chongfa);
        mysound.putSound(2, R.raw.chongshou);
        mysound.putSound(3, R.raw.chongshi);
        mysound.putSound(4, R.raw.cuowuka);
        mysound.putSound(5, R.raw.kongfuhe);
        mysound.putSound(6, R.raw.shibai);
        mysound.putSound(7, R.raw.feibenhb);


    }

    @Override
    public void onNewIntent(Intent intent) {
        byte[] cardid = null;
        //1.获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.v("ok", "核销-OK");

        cardid = detectedTag.getId();
        mTagText = "UID:" + bytesToHexString(cardid) + "\n";//获取卡的UID
        Log.v("ok", mTagText);

        cardID = bytesToHexString(cardid);
        Log.v("ok", cardID);
        //2.获取Ndef的实例
        Ndef ndef = Ndef.get(detectedTag);
        if (CardID2.contains(cardID)) {
            Log.v("ok", "已经核销： " + CardID.toString());

            Log.v("ok", "已经核销");
            Toast.makeText(this, "此卡已核销", Toast.LENGTH_SHORT).show();
            Tv_nfcmsg1.setText("此卡已核销");
            Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
            return;
        }
        // Optional<String> f = list.stream().filter(p -> key.contains(p)).findFirst()
        //f.get()
        if (CardID.contains(cardID)) {

            Log.v("ok", "核销-已经找到这张卡");
            Log.v("ok", "核销前： " + CardID.toString());

            //修改数据库
            NdefMessage ndefMessage = new NdefMessage(
                    new NdefRecord[]{createTextRecord("")});
            boolean result = writeTag(ndefMessage, detectedTag);
            if (result) {
                Cards cards = new Cards();
                cards.setOperationtime(Calendar.getInstance().getTime());
                cards.setUserID("707091");
                cards.setOperate("C");
                //int m=cardID.indexOf(cardID);
                // CardID.remove(m);
                CardID2.add(cardID);
                cards.updateAll("cardID=? and Date=? and FltNr=? ", cardID, FltData, FltNr);
                //   cards.save();
                i = i + 1;
                TextViewNo1.setText(String.format("%d", i));
                Toast.makeText(this, "成功核销", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg1.setText("核销成功");
                Tv_nfcmsg1.setTextColor(Color.parseColor("#22ff33"));

            } else {
                Toast.makeText(this, "复核牌核销失败", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg1.setText("核销失败");
                Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
                mysound.playSound(6, 0);


                Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                long pattern[] = {0, 300, 100, 300};
                vibrator.vibrate(pattern, -1);
                //vibrator.vibrate(1000);
            }
        } else {
            Toast.makeText(this, "不是本航班的卡", Toast.LENGTH_SHORT).show();
            Tv_nfcmsg1.setText("非本航班卡");
            Log.v("ok", "核销没找  " + cardID);
            Log.v("ok", "核销后： " + CardID.toString());
            mysound.playSound(7, 0);

            Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
            Log.v("ok", "核销没找到结束");
        }
    }

    /**
     * 读取NFC标签文本数据
     */
    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            Log.v("ok", "消息长度：" + rawMsgs.toString());
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            } else {

                Toast.makeText(this, "这是张空复核牌", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg1.setText("空卡");
                Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
                mysound.playSound(5, 0);


                //声明一个振动器对象
                Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                long pattern[] = {0, 300, 100, 300};
                vibrator.vibrate(pattern, -1);
                //vibrator.vibrate(1000);
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    try {
                        String fltnr = AESUtils3.decrypt2(textRecord, Psw);
                        String fltnr2 = fltnr.substring(0, 8) + "  " + fltnr.substring(8);

                        Toast.makeText(this, "不是本航班的复核牌\n" + fltnr2, Toast.LENGTH_LONG).show();
                        Tv_nfcmsg1.setText("非本航牌");
                        Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
                        mysound.playSound(7, 0);

                    } catch (Exception e) {
                        Toast.makeText(this, "不是正确的卡", Toast.LENGTH_SHORT).show();
                        Tv_nfcmsg1.setText("错牌");
                        Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
                        mysound.playSound(4, 0);

                    }

                    //声明一个振动器对象
                    Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                    long pattern[] = {0, 300, 100, 300};
                    vibrator.vibrate(pattern, -1);
                    //vibrator.vibrate(1000);

                }
            } catch (Exception e) {
                Toast.makeText(this, "读失败，请重试", Toast.LENGTH_SHORT).show();
                Tv_nfcmsg1.setText("重试");
                Tv_nfcmsg1.setTextColor(Color.parseColor("#ff2222"));
                mysound.playSound(3, 0);

            }
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
