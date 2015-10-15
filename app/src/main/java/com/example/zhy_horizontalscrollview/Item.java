package com.example.zhy_horizontalscrollview;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

/**
 * Created by NONO on 2015/9/7.
 */
public class Item {
//    public static final String ID = "_id";
    public static final String MAC = "mac";
    public static final String SHAKE = "shake";
    public static final String DEVICE_NAME = "devicename";
    private String mac;
    private String devicename;
    private int shake,rssiflag;
    private int image;
    private int imagenot;
    private int deviceImage;
    private BluetoothDevice device;
    private BluetoothGattCharacteristic mGattCharacteristics;
    private Boolean ledStatus;
//  -------------------------------------------------------------------------------------------------------
    public String getMac(){return mac;}
    public int getShake(){return shake;}
    public int getRssiflag(){return rssiflag;}
    public String getDevicename(){return devicename;}
    public BluetoothDevice getDevice(){return device;}
    public int getImage(){return image;}
    public int getDeviceImage(){return deviceImage;}
    public BluetoothGattCharacteristic getmGattCharacteristics(){return mGattCharacteristics;}
    public int getImagenot(){return imagenot;}
    public Boolean getLedStatus(){return ledStatus;}

//  --------------------------------------------------------------------------------------------------------

    public void setMac(String mac){ this.mac=mac;}
    public void setShake(int shake){this.shake=shake;}
    public void setRssiflag(int rssiflag){this.rssiflag=rssiflag;}
    public void setDevicename(String devicename){this.devicename=devicename;}
    public void setImage(int image){this.image=image;}
    public void setDevice(BluetoothDevice device){this.device=device;}
    public void setImagenot(int imagenot){this.imagenot=imagenot;}
    public void setLedStatus(Boolean ledStatus){this.ledStatus=ledStatus;}
   public void setDeviceImage(int deviceImage){this.deviceImage=deviceImage;}
    public void setmGattCharacteristics(BluetoothGattCharacteristic mGattCharacteristics)
                {this.mGattCharacteristics=mGattCharacteristics;}

}
