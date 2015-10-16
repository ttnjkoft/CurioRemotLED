package com.example.zhy_horizontalscrollview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import java.util.HashMap;

/**
 * Created by NONO on 2015/9/2.
 */


public class SettngDeviceActivity extends Activity{

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_IMAGE = "DEVICE_IMAGE";
    public static final String EXTRAS_SHAKE_FLAG = "SHAKE_FLAG";
    public static final String EXTRAS_RSSI_FLAG = "RSSI_FLAG";
    private String mDeviceAddress;
    private int mShake,rssiFlag;
    private Boolean fshake,fRssiFlag;
    private int mDeviceImage;
    private String mDeviceName;
    private ImageView mImg;
    private EditText eText;
    private Switch shakeSwitch,rssiSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device_setting);
        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceImage=intent.getIntExtra(EXTRAS_DEVICE_IMAGE, 0);
        mShake=intent.getIntExtra(EXTRAS_SHAKE_FLAG, 0);
        rssiFlag=intent.getIntExtra(EXTRAS_RSSI_FLAG, 0);
        mImg=(ImageView)findViewById(R.id.imageView);
        eText=(EditText)findViewById(R.id.editText);
        shakeSwitch=(Switch)findViewById(R.id.shakeSwitch);
        rssiSwitch=(Switch)findViewById(R.id.rssiSwitch);
        mImg.setImageResource(mDeviceImage);
        fshake=(mShake==1)? true:false;
        fRssiFlag=(rssiFlag==1)? true:false;
        shakeSwitch.setChecked(fshake);
        rssiSwitch.setChecked(fRssiFlag);
        eText.setText(mDeviceName);

    }
    @Override
    public void onBackPressed() {
        Intent intent=getIntent();
        int shake=shakeSwitch.isChecked()?1 : 0;
        int rssiflag=rssiSwitch.isChecked()?1 : 0;
        String deviceName=eText.getText().toString();
        intent.putExtra(SettngDeviceActivity.EXTRAS_DEVICE_ADDRESS,mDeviceAddress);
        intent.putExtra(SettngDeviceActivity.EXTRAS_DEVICE_NAME,deviceName);
        intent.putExtra(SettngDeviceActivity.EXTRAS_SHAKE_FLAG, shake);
        intent.putExtra(SettngDeviceActivity.EXTRAS_RSSI_FLAG,rssiflag);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();

    }
}
