package com.imooc.baidumap;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.panoramaview.PanoramaView;
//import com.baidu.mapapi.SDKInitializer;

import android.app.Activity;
import android.os.Bundle;

	public class PanoramaDemoActivityMain extends Activity{
		
		private PanoramaView mPanoView;
		
        @Override
        public void onCreate(Bundle savedInstanceState){
        	super.onCreate(savedInstanceState);
    		setContentView(R.layout.activity_main);
    		PanoDemoApplication app = (PanoDemoApplication) this.getApplication();
            
                app.mBMapManager = new BMapManager(app);
         
                app.mBMapManager.init(new PanoDemoApplication.MyGeneralListener());
            
            mPanoView=(PanoramaView)findViewById(R.id.panorama);
            mPanoView.setPanorama("0100220000130817164838355J5");
    
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
}
