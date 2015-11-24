package edu.happy.mynfcapp;

import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private TextView info;//���ڳ�����Ϣ�Ŀؼ�
	private Button write;//��ť���������д��ģʽ
	private int WRITE_CODE = 1;//activity֮��ͨѶ������ֵ
	private String mText;//��ʱ�洢д����Ϣ��ȫ�ֱ���
    private AlertDialog mDialog;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		ȥ��������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mian_layout);
		info = (TextView)findViewById(R.id.info_show);
		write = (Button)findViewById(R.id.write);
		write.setOnClickListener(this);
		 // mTagContent = (LinearLayout) findViewById(R.id.list);
//        resolveIntent(getIntent());
       
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        //û��nfcӲ������
        if (mAdapter == null) {
            showMessage(R.string.error, R.string.no_nfc);
            return;
        }
        /*
         * PendingIntent �����:
         * 
         * pendingIntent��һ�������Intent����Ҫ����������Intent��ִ�����̵ģ�
         * ��pendingIntent��ִ�в������̵ġ�pendingIntentִ�еĲ���ʵ�����ǲ�
         * ����������Intent�Ĳ���������ʹ��pendingIntent��Ŀ����������������
         * Intent�Ĳ�����ִ������Ҫ����ĳЩ�����ġ�
         */
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
       
	}
	
	   //  չʾû��nfc�Ĵ���
    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }
	
// ����¼��Ĵ���
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		���д��������д�����
        switch(v.getId()){
        case R.id.write:
        	Intent intent = new Intent(this, WriteActivity.class);
			startActivityForResult(intent, WRITE_CODE);
			break;
		}
	}
	
//	activity֮��ͨѶ���ݵĴ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == WRITE_CODE){
			mText = data.getStringExtra("input_data");
			info.setText(mText);
		}
	}
    //�����ڵĴ���ģʽ��singleTop��singleTaskʱ���ã�����ȡ��onCreate����
    //��NFC��ǩ�����ֻ����������Ӻ����
    @Override
    public void onNewIntent(Intent intent) {
        //���δ����Ҫд����ı������ȡ��ǩ�ϵ��ı�����
    	mText = "�Ҳ���������";
        if (mText == null) {
//            Intent myIntent = new Intent(this, ShowNFCTagContentActivity.class);
            //��intent������һ�����ڣ���ʾ���洰�� 
//            myIntent.putExtras(intent);
            //��Ҫָ�����Action������Intent����ʱ��Action���ᴫ��
//            myIntent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
//            startActivity(myIntent);
        }
        //��ָ�����ı�д��NFC��ǩ
        else {
            //��ȡTag����
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //����NdefMessage�����NdefRecord����
            NdefMessage ndefMessage = new NdefMessage(
                    new NdefRecord[] {createTextRecord(mText)});
 
            //��ʼ���ǩд���ı�
            if (writeTag(ndefMessage, tag)) {
                //����ɹ�д���ı�����mtext��Ϊnull
                mText = null;
                //����������ʾ��Ҫд����ı���գ��ı�ֻ��д��һ��
                //��Ҫ����д�룬��Ҫ�ٴ�ָ���µ��ı�������ֻ���ȡ��ǩ�е��ı�
                info.setText("");
            }
 
        }
 
    }
 
    //����һ����װҪд����ı���NdefRecord����
    public NdefRecord createTextRecord(String text) {
        //�������Ա�����ֽ����飬���ı���
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(
                Charset.forName("US-ASCII"));
        //��Ҫд����ı���UTF_8��ʽ���б���
        Charset utfEncoding = Charset.forName("UTF-8");
        //�����Ѿ�ȷ���ı��ĸ�ʽ����ΪUTF_8������ֱ�ӽ�payload�ĵ�1���ֽڵĵ�7λ��Ϊ0
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = 0;
        //����ͳ�ʼ��״̬�ֽ�
        char status = (char) (utfBit + langBytes.length);
        //�����洢payload���ֽ�����
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //����״̬�ֽ�
        data[0] = (byte) status;
        //�������Ա���
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //����ʵ��Ҫд����ı�
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
                textBytes.length);
        //����ǰ�����õ�payload����NdefRecord����
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }
 
    //��NdefMessage����д���ǩ���ɹ�д�뷵��ture�����򷵻�false
    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
 
        try {
            //��ȡNdef����
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                //����Ա�ǩ����IO����
                ndef.connect();
 
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "NFC Tag��ֻ���ģ�", Toast.LENGTH_LONG)
                            .show();
                    return false;
 
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, "NFC Tag�Ŀռ䲻�㣡", Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
 
                //���ǩд������
                ndef.writeNdefMessage(message);
                Toast.makeText(this, "�ѳɹ�д�����ݣ�", Toast.LENGTH_LONG).show();
                return true;
 
            } else {
                //��ȡ���Ը�ʽ�������ǩд������NdefFormatable����
                NdefFormatable format = NdefFormatable.get(tag);
                //���NDEF��ʽ��δ��ʽ���ı�ǩд��NDEF��ʽ����
                if (format != null) {
                    try {
                        //����Ա�ǩ����IO����
                        format.connect();
                        format.format(message);
                        Toast.makeText(this, "�ѳɹ�д�����ݣ�", Toast.LENGTH_LONG)
                                .show();
                        return true;
 
                    } catch (Exception e) {
                        Toast.makeText(this, "д��NDEF��ʽ����ʧ�ܣ�", Toast.LENGTH_LONG)
                                .show();
                        return false;
                    }
                } else {
                    Toast.makeText(this, "NFC��ǩ��֧��NDEF��ʽ��", Toast.LENGTH_LONG)
                            .show();
                    return false;
 
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
 
    }

}
