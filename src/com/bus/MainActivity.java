package com.bus;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.LocationManagerProxy;
import com.tools.ProgramData;
import com.tools.XMLHandle;

public class MainActivity extends Activity implements OnClickListener {
	private Button btnStartLine1, btnStartLine2;
	private TextView txtLine1, txtLine2;
	XMLHandle XMLer = new XMLHandle();
	ProgramData data = new ProgramData();

	// ��λ����
	private LocationManagerProxy mLocationManagerProxy;
	private TextView mLocationlngTextView;// ��λ��γ����Ϣ
	private TextView mLocationLatTextView;// ��λ��γ����Ϣ
	private TextView mLocationAccurancyTextView;// ��λ������Ϣ
	private TextView mLocationMethodTextView;// ��λ��ʽ��Ϣ
	private TextView mLocationTimeTextView;// ��λʱ����Ϣ

	public static final String GPSLOCATION_BROADCAST_ACTION = "com.location.apis.gpslocationdemo.broadcast";

	private PendingIntent mPendingIntent;

	private Handler mHandler = new Handler() {

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		btnStartLine1 = (Button) findViewById(R.id.btnStartLine1);
		btnStartLine2 = (Button) findViewById(R.id.btnStartLine2);

		txtLine1 = (TextView) findViewById(R.id.txtLine1);
		txtLine2 = (TextView) findViewById(R.id.txtLine2);

		btnStartLine1.setOnClickListener(this);
		btnStartLine2.setOnClickListener(this);
		LoadLine();
		// ��λ
		initView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnStartLine1) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, WorkingActivity.class);
			intent.putExtra("path", data.VoiceFilePath1);
			startActivity(intent);
		}
		if (v == btnStartLine2) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, WorkingActivity.class);
			intent.putExtra("path", data.VoiceFilePath2);
			startActivity(intent);
		}
	}

	private void LoadLine() {
		txtLine1.setText("��·1��\n" + XMLer.LoadLine((data.VoiceFilePath1)));
		txtLine2.setText("��·2��\n" + XMLer.LoadLine((data.VoiceFilePath2)));
	}

	// ��λ
	private BroadcastReceiver mGPSLocationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// ���ܹ㲥
			if (intent.getAction().equals(GPSLOCATION_BROADCAST_ACTION)) {

				// ֻ����һ�ζ�λ����λ�ɹ����Ƴ���λ����
				mLocationManagerProxy.removeUpdates(mPendingIntent);

				Bundle bundle = intent.getExtras();

				// ��ȡbundle�������
				Parcelable parcelable = bundle
						.getParcelable(LocationManagerProxy.KEY_LOCATION_CHANGED);

				Location location = (Location) parcelable;
				if (location == null) {
					return;
				}
				// ��λ�ɹ��ص���Ϣ�����������Ϣ
				mLocationlngTextView.setText("��        �ȣ�"
						+ location.getLongitude());
				mLocationLatTextView.setText("γ        �ȣ�"
						+ location.getLatitude());
				mLocationAccurancyTextView.setText("��        ��"
						+ String.valueOf(location.getAccuracy()));
				mLocationMethodTextView
						.setText("��λ��ʽ" + location.getProvider());

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = new Date(location.getTime());

				mLocationTimeTextView.setText("��λʱ��" + df.format(date));

			}

		}
	};

	private void init() {
		IntentFilter fliter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		fliter.addAction(GPSLOCATION_BROADCAST_ACTION);
		registerReceiver(mGPSLocationReceiver, fliter);
		Intent intent = new Intent(GPSLOCATION_BROADCAST_ACTION);
		mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent, 0);
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		// ����peddingIntent��ʽ���жԶ�λ����
		// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
		// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����removeUpdates()������ȡ����λ����
		// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
		// ����������ʱ��Ϊ-1����λֻ��һ��
		// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������removeUpdates()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
		mLocationManagerProxy
				.requestLocationUpdates(LocationManagerProxy.GPS_PROVIDER,
						2 * 1000, 15, mPendingIntent);
		// ���һֱ��λʧ����2min��ֹͣ��λ
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mLocationManagerProxy.removeUpdates(mPendingIntent);
			}
		}, 10 * 60 * 1000);

	}

	private void initView() {
		mLocationlngTextView = (TextView) findViewById(R.id.gps_location_lng_text);
		mLocationLatTextView = (TextView) findViewById(R.id.gps_location_lat_text);
		mLocationAccurancyTextView = (TextView) findViewById(R.id.gps_location_accurancy_text);
		mLocationMethodTextView = (TextView) findViewById(R.id.gps_location_method_text);
		mLocationTimeTextView = (TextView) findViewById(R.id.gps_location_time_text);
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// �Ƴ���λ����
		mLocationManagerProxy.removeUpdates(mPendingIntent);
		unregisterReceiver(mGPSLocationReceiver);
		// ���ٶ�λ
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}
}
