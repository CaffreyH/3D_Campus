package com.imooc.baidumap;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;


public class PanoDemoApplication extends Application {

    private static PanoDemoApplication mInstance = null;
    public BMapManager mBMapManager = new BMapManager(this);;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initEngineManager(this);
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(PanoDemoApplication.getInstance().getApplicationContext(), "BMapManager  ��ʼ������!",
                    Toast.LENGTH_LONG).show();
        }
        Log.d("ljx", "initEngineManager");
    }

    public static PanoDemoApplication getInstance() {
        return mInstance;
    }

    // �����¼���������������ͨ�������������Ȩ��֤�����
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // ����ֵ��ʾkey��֤δͨ��
//            if (iError != 0) {
//                // ��ȨKey����
//                Toast.makeText(PanoDemoApplication.getInstance().getApplicationContext(),
//                        "����AndoridManifest.xml��������ȷ����ȨKey,������������������Ƿ�������error: " + iError, Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(PanoDemoApplication.getInstance().getApplicationContext(), "key��֤�ɹ�", Toast.LENGTH_LONG)
//                        .show();
//            }
        }
    }
}