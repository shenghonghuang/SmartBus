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

	// 定位数据
	private LocationManagerProxy mLocationManagerProxy;
	private TextView mLocationlngTextView;// 定位经纬度信息
	private TextView mLocationLatTextView;// 定位经纬度信息
	private TextView mLocationAccurancyTextView;// 定位精度信息
	private TextView mLocationMethodTextView;// 定位方式信息
	private TextView mLocationTimeTextView;// 定位时间信息

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
		// 定位
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
		txtLine1.setText("线路1：\n" + XMLer.LoadLine((data.VoiceFilePath1)));
		txtLine2.setText("线路2：\n" + XMLer.LoadLine((data.VoiceFilePath2)));
	}

	// 定位
	private BroadcastReceiver mGPSLocationReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 接受广播
			if (intent.getAction().equals(GPSLOCATION_BROADCAST_ACTION)) {

				// 只进行一次定位，定位成功后移除定位请求
				mLocationManagerProxy.removeUpdates(mPendingIntent);

				Bundle bundle = intent.getExtras();

				// 获取bundle里的数据
				Parcelable parcelable = bundle
						.getParcelable(LocationManagerProxy.KEY_LOCATION_CHANGED);

				Location location = (Location) parcelable;
				if (location == null) {
					return;
				}
				// 定位成功回调信息，设置相关消息
				mLocationlngTextView.setText("经        度："
						+ location.getLongitude());
				mLocationLatTextView.setText("纬        度："
						+ location.getLatitude());
				mLocationAccurancyTextView.setText("精        度"
						+ String.valueOf(location.getAccuracy()));
				mLocationMethodTextView
						.setText("定位方式" + location.getProvider());

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = new Date(location.getTime());

				mLocationTimeTextView.setText("定位时间" + df.format(date));

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
		// 采用peddingIntent方式进行对定位调用
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy
				.requestLocationUpdates(LocationManagerProxy.GPS_PROVIDER,
						2 * 1000, 15, mPendingIntent);
		// 如果一直定位失败则2min后停止定位
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
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(mPendingIntent);
		unregisterReceiver(mGPSLocationReceiver);
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}
}
