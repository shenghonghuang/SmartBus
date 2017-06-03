package com.bus;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tools.CenterXMLHandle;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

public class LoginActivity extends Activity {
	CenterXMLHandle center = new CenterXMLHandle();

	NfcAdapter nfcAdapter;
	TextView promt;
	String number = center.LoadCenterNumber();
	String content = center.LoadText();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		promt = (TextView) findViewById(R.id.promt);
		// ��ȡĬ�ϵ�NFC������
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			promt.setText("�豸��֧��NFC��");
			finish();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			promt.setText("����ϵͳ������������NFC���ܣ�");
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// �õ��Ƿ��⵽ACTION_TECH_DISCOVERED����
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			// �����intent
			SentSMS();
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	private void SentSMS() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String time = df.format(date);
		SmsManager smsManager = SmsManager.getDefault();
		// ����֮ǰ�����������Ƿ�Ϊ��
		smsManager.sendTextMessage(number, null, content + time, null, null);
	}
}
