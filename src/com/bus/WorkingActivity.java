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

	// 地理围栏
	private MapView mMapView;// 地图控件
	private AMap mAMap;
	private LocationManagerProxy mLocationManagerProxy;// 定位实例
	private Marker mGPSMarker;// 定位位置显示
	private PendingIntent mPendingIntent;
	private Circle mCircle;

	public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
	private int flag = 1;// 已经出站为0，已经进站为1

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

		// 地理围栏
		// 地图
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
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
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

			// 接受广播
			if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
				Bundle bundle = intent.getExtras();
				// 根据广播的status来确定是在区域内还是在区域外
				int status = bundle.getInt("status");
				// 已经出站为0，已经进站为1;
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
	 * 根据新的经纬度更新GPS位置和设置地图中心
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
		// 销毁定位
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
		// 首站点
		if (j == 0) {
			getNextStation();
			player.PlayFirst(StationList.get(0).getName(),
					StationList.get(StationList.size() - 1).getName(),
					StationName);
			j++;
			// 获取新经纬度
			mLocationManagerProxy.addGeoFenceAlert(
					Float.parseFloat(StationList.get(i).getLatitude()),
					Float.parseFloat(StationList.get(i).getLongitude()), 100,
					1000 * 60 * 30, mPendingIntent);
		}
		// 倒二站，下一站，终点站
		else if (j == StationList.size() - 1) {
			getNextStation();
			player.PlayNextLast(StationName);
			this.btnNext.setEnabled(false);
			mLocationManagerProxy.addGeoFenceAlert(
					Float.parseFloat(StationList.get(i).getLatitude()),
					Float.parseFloat(StationList.get(i).getLongitude()), 100,
					1000 * 60 * 30, mPendingIntent);
		}
		// 中间站点
		else {
			getNextStation();
			player.PlayNext(StationName);
			// 获取新经纬度
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
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本
		 */
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "调度室");
		menu.add(Menu.NONE, Menu.FIRST + 2, 2, "警察");
		return true;
	}

	// 菜单项被选择事件
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
