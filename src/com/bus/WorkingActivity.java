package com.bus;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.model.Station;
import com.tools.PlayHandle;
import com.tools.ProgramData;
import com.tools.XMLHandle;

public class WorkingActivity extends Activity implements OnClickListener,
		AMapLocationListener, OnMapClickListener {
	private List<Station> StationList = new ArrayList<Station>();
	private int i = 0;// Code of StationList
	private int j = 0;
	private Button btnNext, btnApproach, btnTurn, btnOffer, btnCrowd,
			btnAntiTheft;
	private TextView txtNext, txtApproach, txtStation;
	private String StationName;
	private String path;
	PlayHandle player = new PlayHandle();
	XMLHandle XMLer = new XMLHandle();
	ProgramData data = new ProgramData();

	// ����Χ��
	private MapView mMapView;// ��ͼ�ؼ�
	private AMap mAMap;
	private LocationManagerProxy mLocationManagerProxy;// ��λʵ��
	private Marker mGPSMarker;// ��λλ����ʾ
	private PendingIntent mPendingIntent;
	private Circle mCircle;

	public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
	private int flag = 1;// �Ѿ���վΪ0���Ѿ���վΪ1

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.working);
		txtNext = (TextView) findViewById(R.id.txtNext);
		txtApproach = (TextView) findViewById(R.id.txtApproach);
		txtStation = (TextView) findViewById(R.id.txtStation);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnApproach = (Button) findViewById(R.id.btnApproach);
		btnTurn = (Button) findViewById(R.id.btnTurn);
		btnOffer = (Button) findViewById(R.id.btnOffer);
		btnCrowd = (Button) findViewById(R.id.btnCrowd);
		btnAntiTheft = (Button) findViewById(R.id.btnAntiTheft);
		btnNext.setOnClickListener(this);
		btnApproach.setOnClickListener(this);
		btnTurn.setOnClickListener(this);
		btnOffer.setOnClickListener(this);
		btnCrowd.setOnClickListener(this);
		btnAntiTheft.setOnClickListener(this);

		Bundle bundle = this.getIntent().getExtras();
		path = bundle.getString("path");
		StationList = XMLer.LoadXML(path);

		// ����Χ��
		// ��ͼ
		init(savedInstanceState);
	}

	//
	private void init(Bundle savedInstanceState) {
		mMapView = (MapView) findViewById(R.id.main_mapView);
		mMapView.onCreate(savedInstanceState);
		mAMap = mMapView.getMap();

		mAMap.setOnMapClickListener(this);
		IntentFilter fliter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		fliter.addAction(GEOFENCE_BROADCAST_ACTION);
		registerReceiver(mGeoFenceReceiver, fliter);

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		Intent intent = new Intent(GEOFENCE_BROADCAST_ACTION);
		mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
				intent, 0);
		// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
		// ע�����ú��ʵĶ�λʱ��ļ������С���֧��Ϊ2000ms���������ں���ʱ�����removeUpdates()������ȡ����λ����
		// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
		// ����������ʱ��Ϊ-1����λֻ��һ��
		// �ڵ��ζ�λ����£���λ���۳ɹ���񣬶��������removeUpdates()�����Ƴ����󣬶�λsdk�ڲ����Ƴ�
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 2000, 15, this);

		MarkerOptions markOptions = new MarkerOptions();
		mGPSMarker = mAMap.addMarker(markOptions);
		mAMap.setOnMapClickListener(this);

		mLocationManagerProxy.addGeoFenceAlert(
				Float.parseFloat(StationList.get(0).getLatitude()),
				Float.parseFloat(StationList.get(0).getLongitude()), 100,
				1000 * 60 * 30, mPendingIntent);
	}

	private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			// ���ܹ㲥
			if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
				Bundle bundle = intent.getExtras();
				// ���ݹ㲥��status��ȷ�����������ڻ�����������
				int status = bundle.getInt("status");
				// �Ѿ���վΪ0���Ѿ���վΪ1;
				if (status == 0 && flag == 1) {
					Next();
					flag = 0;
				}
				if (status == 1 && flag == 0 && j != StationList.size() - 1) {
					Approach();
					flag = 1;
				}

			}
		}
	};

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location.getAMapException().getErrorCode() == 0) {
			updateLocation(location.getLatitude(), location.getLongitude());

		}
	}

	/*
	 * �����µľ�γ�ȸ���GPSλ�ú����õ�ͼ����
	 */
	private void updateLocation(double latitude, double longtitude) {
		if (mGPSMarker != null) {
			mGPSMarker.setPosition(new LatLng(latitude, longtitude));
		}

	}

	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	protected void onPause() {
		super.onPause();
		// ���ٶ�λ
		mLocationManagerProxy.removeGeoFenceAlert(mPendingIntent);
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
		unregisterReceiver(mGeoFenceReceiver);
		mMapView.onPause();

	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();

		mMapView.onDestroy();

	}

	@Override
	public void onMapClick(LatLng latLng) {
		mLocationManagerProxy.removeGeoFenceAlert(mPendingIntent);
		if (mCircle != null) {
			mCircle.remove();
		}
	}

	// //////////////////////////////////////////
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnNext) {
			Next();
		}
		if (v == btnApproach) {
			Approach();
		}
		if (v == btnTurn) {
			player.PlayInvariable("Turn");
		}
		if (v == btnOffer) {
			player.PlayInvariable("Offer");
		}
		if (v == btnCrowd) {
			player.PlayInvariable("Crowd");
		}
		if (v == btnAntiTheft) {
			player.PlayInvariable("AntiTheft");
		}
	}

	private void getNextStation() {
		if (i < StationList.size()) {
			i++;
			j++;
			StationName = StationList.get(i).getName();
		}
	}

	private void Next() {
		// ��վ��
		if (j == 0) {
			getNextStation();
			player.PlayFirst(StationList.get(0).getName(),
					StationList.get(StationList.size() - 1).getName(),
					StationName);
			j++;
			// ��ȡ�¾�γ��
			mLocationManagerProxy.addGeoFenceAlert(
					Float.parseFloat(StationList.get(i).getLatitude()),
					Float.parseFloat(StationList.get(i).getLongitude()), 100,
					1000 * 60 * 30, mPendingIntent);
		}
		// ����վ����һվ���յ�վ
		else if (j == StationList.size() - 1) {
			getNextStation();
			player.PlayNextLast(StationName);
			this.btnNext.setEnabled(false);
			mLocationManagerProxy.addGeoFenceAlert(
					Float.parseFloat(StationList.get(i).getLatitude()),
					Float.parseFloat(StationList.get(i).getLongitude()), 100,
					1000 * 60 * 30, mPendingIntent);
		}
		// �м�վ��
		else {
			getNextStation();
			player.PlayNext(StationName);
			// ��ȡ�¾�γ��
			mLocationManagerProxy.addGeoFenceAlert(
					Float.parseFloat(StationList.get(i).getLatitude()),
					Float.parseFloat(StationList.get(i).getLongitude()), 100,
					1000 * 60 * 30, mPendingIntent);
		}

		txtStation.setText(StationName);
		txtNext.setVisibility(View.VISIBLE);
		txtApproach.setVisibility(View.INVISIBLE);
	}

	private void Approach() {
		if (i == StationList.size() - 1) {
			player.PlayApproachLast(StationName);
		} else {
			player.PlayApproach(StationName);
		}
		txtNext.setVisibility(View.INVISIBLE);
		txtApproach.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * add()�������ĸ������������ǣ� 1��������������Ļ���дMenu.NONE,
		 * 2��Id���������Ҫ��Android�������Id��ȷ����ͬ�Ĳ˵� 3��˳���Ǹ��˵�������ǰ������������Ĵ�С����
		 * 4���ı����˵�����ʾ�ı�
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "������");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "����");
		return true;
	}

	// �˵��ѡ���¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			callPhone("10010");
			break;
		case Menu.FIRST + 2:
			callPhone("10011");
			break;
		}
		return false;
	}

	private void callPhone(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		startActivity(intent);
	}

}
