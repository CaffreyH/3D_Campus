package com.imooc.baidumap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.ImageMarker;
import com.baidu.lbsapi.panoramaview.OnTabMarkListener;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.tools.Point;
import com.baidu.pano.platform.plugin.indooralbum.IndoorAlbumPlugin;

/**
 * 全景Demo主Activity
 */
public class PanoDemoMain extends Activity {
    private PanoramaView mPanoView;
    ImageMarker marker = new ImageMarker();  
    ImageMarker marker2 = new ImageMarker();
    ImageMarker marker3 = new ImageMarker();  
    Bitmap bitmap;
    LinearLayout ll_detail;
    LinearLayout ll_detail2;
    int location=-1;
    Spinner spinner;
    //Handler mhandler;
    public static int flag=0;
    int mylon=0;
    int mylay=0;
    public static String getL;
    public static String getLon;
    public static String getLat;
	private Handler mhandler = new Handler() {  
	    public void handleMessage(android.os.Message msg) {
	    	if (msg.what == 12345678) {  
              
//	    		Toast.makeText(PanoDemoMain.this,""+(String)msg.obj,
//						Toast.LENGTH_SHORT).show();
	    		getL = (String)msg.obj;
	    		getLon=getL.substring(0,9); 
	    		getLat=getL.substring(11,18);
//	    		Toast.makeText(PanoDemoMain.this,""+getLon,
//						Toast.LENGTH_SHORT).show();
//	    		Toast.makeText(PanoDemoMain.this,""+getLat,
//						Toast.LENGTH_SHORT).show();
	    		flag=1;
//	    		Toast.makeText(PanoDemoMain.this,""+flag,
//						Toast.LENGTH_SHORT).show();

    			
            }  
	    }  
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        client();
        // 先初始化BMapManager
        initBMapManager();
        setContentView(R.layout.map3d);
   
     Intent intent1=this.getIntent();
     final String dis=intent1.getStringExtra("dis");
     
     ll_detail=(LinearLayout)findViewById(R.id.show_detials);   
     ll_detail2=(LinearLayout)findViewById(R.id.show_detials2);   
     bitmap=BitmapFactory.decodeResource(this.getResources(),R.drawable.go4);
     mPanoView = (PanoramaView) findViewById(R.id.panorama);
     Toast.makeText(PanoDemoMain.this,  
    		 ""+dis,  
            Toast.LENGTH_SHORT).show();
     
     mPanoView.setPanorama(dis);
     mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
     mPanoView.addMarker(marker);
     mPanoView.addMarker(marker2);
     mPanoView.addMarker(marker3);
     mPanoView.setArrowTextureByBitmap(bitmap);
   //  IndoorAlbumPlugin.getInstance().init();
  //   mPanoView.setPanoramaByUid("2aee5148ca47d720441a3238", PanoramaView.PANOTYPE_INTERIOR);
    
     ll_detail.setOnClickListener(new Button.OnClickListener(){
		 public void onClick(View v){
			ll_detail.setVisibility(View.INVISIBLE);
		 }
	 }
	 );
     ll_detail2.setOnClickListener(new Button.OnClickListener(){
		 public void onClick(View v){
			ll_detail2.setVisibility(View.INVISIBLE);
		 }
	 }
	 );
     spinner = (Spinner)findViewById(R.id.spinner);
     ArrayList<String> data_list = new ArrayList<String>();
     data_list.add("图书馆");
     data_list.add("艺术学院");
     data_list.add("计算机学院");
     data_list.add("建筑与环境学院");
     
     ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(PanoDemoMain.this, android.R.layout.simple_spinner_item, data_list);
     //设置样式
     arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //加载适配器
     spinner.setAdapter(arr_adapter);
     spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
         @Override
         
         public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {     
        	// position=4;
         	 if(position ==0)
         	 {
         		location=0;
         		mPanoView.setPanorama("0501920000140530033809693SL");
         	 }
         	 else if(position ==1)
         	 {
         		location=1;
         		mPanoView.setPanorama("0501920000140530031626197SL");
         	 }
         	 else if(position ==2)
         	 {
         		location=2;
         		mPanoView.setPanorama("0501920000140526061943024SL");
         	 } 
         	 else if(position ==3)
         	 {
         		location=3;
         		mPanoView.setPanorama("0501920000140530033603629SL");
         	 } 
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {
        	 mPanoView.setPanorama(dis); 
         }
     });
        

    }
    private void initBMapManager() {
    	
        PanoDemoApplication app = (PanoDemoApplication) this.getApplication();
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new PanoDemoApplication.MyGeneralListener());
            marker.setMarkerPosition(new Point(104.00717, 30.56224));
            marker.setMarkerHeight(1.57f);
            marker.setMarker(getResources().getDrawable(R.drawable.icon_lib));
            marker.setOnTabMarkListener(new OnTabMarkListener() {
				@Override
				public void onTab() {
					 Toast.makeText(PanoDemoMain.this,
                             "四川大学图书馆", Toast.LENGTH_SHORT).show();
					 ll_detail.setVisibility(1);
				}
            		});
       
            marker2.setMarkerPosition(new Point(104.007227,30.562128));
            marker2.setMarkerHeight(1.57f);
            marker2.setMarker(getResources().getDrawable(R.drawable.icon_build));
            marker2.setOnTabMarkListener(new OnTabMarkListener() {
				@Override
				public void onTab() {
					 Toast.makeText(PanoDemoMain.this,
                             "四川大学第一教学楼", Toast.LENGTH_SHORT).show();
					 ll_detail2.setVisibility(1);
				}
            		});

        									//Double.parseDouble(getLon),Double.parseDouble(getLat)            
            if(flag==1)
            {
    		marker3.setMarkerPosition(new Point(Double.parseDouble(getLon),Double.parseDouble(getLat)));
            marker3.setMarkerHeight(0.1f);
            marker3.setMarker(getResources().getDrawable(R.drawable.man3));
            marker3.setOnTabMarkListener(new OnTabMarkListener() {
				@Override
				public void onTab() {
					 Toast.makeText(PanoDemoMain.this,
                             "一名游客", Toast.LENGTH_SHORT).show();
				}
            		});
            }
    }

    @Override  
    protected void onPause() {  
        super.onPause();  
        mPanoView.onPause();  
    }  
     
    @Override  
    protected void onResume() {  
        super.onResume();  
        mPanoView.onResume();  
    }  
     
    @Override  
    protected void onDestroy() {  
        mPanoView.destroy();  
        super.onDestroy();  
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
					msg.what=12345678;
					msg.obj=line;
			        mhandler.sendMessage(msg);  
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
}