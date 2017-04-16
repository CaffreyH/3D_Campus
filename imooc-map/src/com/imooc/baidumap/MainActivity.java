package com.imooc.baidumap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.imooc.baidumap.MyOrientationListener.OnOrientationListener;

public class MainActivity extends Activity
{
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private String line;
	private Context context;

	// ��λ���
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongtitude;
	// �Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;
	private LocationMode mLocationMode;

	// ���������
	private BitmapDescriptor mMarker;
	private RelativeLayout mMarkerLy;
	
	private ImageView iv1;
	private Button btn_ball;
	Handler mhandler;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
	//	getWindow().setFlags(WindowManager.LayoutParams.FLAG_NEEDS_MENU_KEY,
	//			WindowManager.LayoutParams.FLAG_NEEDS_MENU_KEY);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		this.context = this;

		initView();
		// ��ʼ����λ
		initLocation();
		initMarker();
	//	client();
		mhandler = new Handler() {  
		    @Override  
		    public void handleMessage(android.os.Message msg) {
		    	if (msg.what == 12345678) {  
	              
		    		Toast.makeText(MainActivity.this,""+(String)msg.obj,
							Toast.LENGTH_SHORT).show();
		    		
	            }  
		    }  
		};
		 addOverlays(Info.infos);
		 btn_ball = (Button)findViewById(R.id.button_ball);
	 	 btn_ball.setOnClickListener(new Button.OnClickListener(){
	 			 public void onClick(View v){
	 				centerToMyLocation();

	 			 }
	 		 }
	 		 );
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker marker)
			{
				Bundle extraInfo = marker.getExtraInfo();
				Info info = (Info) extraInfo.getSerializable("info");
				ImageView iv = (ImageView) mMarkerLy
						.findViewById(R.id.id_info_img);
				TextView distance = (TextView) mMarkerLy
						.findViewById(R.id.id_info_distance);
				TextView name = (TextView) mMarkerLy
						.findViewById(R.id.id_info_name);
				TextView zan = (TextView) mMarkerLy
						.findViewById(R.id.id_info_zan);
				final double lat=info.getLatitude();
				final double lon=info.getLongitude();
				final String di=info.getDistance();
				iv.setImageResource(info.getImgId());
				distance.setText(info.getDistance());
				name.setText(info.getName());
				zan.setText(info.getZan() + "");
				zan.setOnClickListener(new Button.OnClickListener(){
					 public void onClick(View v){
						
						 Intent intent = new Intent();
						 intent.setClass(MainActivity.this , PanoDemoMain.class );
						 intent.putExtra("lat1", lat);
						 intent.putExtra("lon1", lon);
						 intent.putExtra("dis", di);
						 startActivity(intent);

					 }
				 }
				 );
				InfoWindow infoWindow;
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(Color.parseColor("#ffffff"));

				final LatLng latLng = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
				p.y -= 47;
				LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

//				infoWindow = new InfoWindow(tv, ll,
//						new OnInfoWindowClickListener()
//						{
//							@Override
//							public void onInfoWindowClick()
//							{
//								mBaiduMap.hideInfoWindow();
//							}
//						});
//				mBaiduMap.showInfoWindow(infoWindow);
				mMarkerLy.setVisibility(View.VISIBLE);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				mMarkerLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});
	}

	private void initMarker()
	{
		mMarker = BitmapDescriptorFactory.fromResource(R.drawable.p1);
		mMarkerLy = (RelativeLayout) findViewById(R.id.id_maker_ly);
	}

	private void initLocation()
	{

		mLocationMode = LocationMode.NORMAL;
		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		// ��ʼ��ͼ��
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener()
				{
					@Override
					public void onOrientationChanged(float x)
					{
						mCurrentX = x;
					}
				});

	}

	private void initView()
	{
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// ��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onResume();
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		// �������򴫸���
		myOrientationListener.start();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		// ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// ֹͣ���򴫸���
		myOrientationListener.stop();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		// ��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
		mMapView.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;

		case R.id.id_map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;

		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled())
			{
				Intent intent = new Intent();
				 intent.setClass(MainActivity.this , PanoDemoMain.class );
				 startActivity(intent);
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("ʵʱ��ͨ(off)");
			} else
			{
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͨ(on)");
			}
			break;
		case R.id.id_map_location:
			centerToMyLocation();
			break;
		case R.id.id_map_mode_common:
			mLocationMode = LocationMode.NORMAL;
			break;
		case R.id.id_map_mode_following:
			mLocationMode = LocationMode.FOLLOWING;
			break;
		case R.id.id_map_mode_compass:
			mLocationMode = LocationMode.COMPASS;
			break;
		case R.id.id_add_overlay:
			addOverlays(Info.infos);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * ��Ӹ�����
	 * 
	 * @param infos
	 */
	private void addOverlays(List<Info> infos)
	{
		mBaiduMap.clear();
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		for (Info info : infos)
		{
			// ��γ��
			latLng = new LatLng(info.getLatitude(), info.getLongitude());
			// ͼ��
			options = new MarkerOptions().position(latLng).icon(mMarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", info);
			marker.setExtraInfo(arg0);
		}

		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(msu);

	}

	/**
	 * ��λ���ҵ�λ��
	 */
	private void centerToMyLocation()
	{
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{

			MyLocationData data = new MyLocationData.Builder()//
					.direction(mCurrentX)//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			// �����Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);

			// ���¾�γ��
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();

			if (isFirstIn)
			{
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;

				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
				client2();
			}

		}
	}

	
	public void client()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					String ip="121.48.193.30";
					Socket socket=new Socket(ip,12345);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String line = br.readLine();
					Message msg = new Message();  
					msg.obj=line;
					msg.what=12345678;
			        mhandler.sendMessage(msg); 
			        OutputStream os=socket.getOutputStream();
			        String weizhi=""+mLatitude+""+mLongtitude+"\n";
					os.write(weizhi.getBytes("utf-8"));
					os.close();
					br.close();
					socket.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void client2()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					String ip="121.48.193.30";
					Socket socket=new Socket(ip,12345);
					OutputStream os=socket.getOutputStream();
			        String weizhi=""+mLatitude+","+mLongtitude+"\n";
					os.write(weizhi.getBytes("utf-8"));
					//os.close();
					socket.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
}
