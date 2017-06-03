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
		// 获取默认的NFC控制器
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			promt.setText("设备不支持NFC！");
			finish();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			promt.setText("请在系统设置中先启用NFC功能！");
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 得到是否检测到ACTION_TECH_DISCOVERED触发
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
			// 处理该intent
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
		// 发送之前检查短信内容是否为空
		smsManager.sendTextMessage(number, null, content + time, null, null);
	}
}
