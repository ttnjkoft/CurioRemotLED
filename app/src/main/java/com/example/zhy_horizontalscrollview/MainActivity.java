package com.example.zhy_horizontalscrollview;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zhy_horizontalscrollview.MyHorizontalScrollView.CurrentImageChangeListener;
import com.example.zhy_horizontalscrollview.MyHorizontalScrollView.OnItemClickListener;

public class MainActivity extends Activity
{
	private final static String TAG =MainActivity.class.getName();
	private MyHorizontalScrollView mHorizontalScrollView;
	private HorizontalScrollViewAdapter mAdapter;
	private boolean mScanning;
	private static final long SCAN_PERIOD = 10000;
	private static final int REQUEST_ENABLE_BT = 1;
	private ImageView mImg;
	private int rssiVal;
	private int curioIndex;
	private boolean mConnected = false;
	private boolean ledStatus=false;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothLeService mBluetoothLeService;
	private BluetoothGattCharacteristic characteristic;
	private CircularSeekBar myseekbar;
	private AlertDialog.Builder builder;
	private long startTime;
	private ProgressDialog pDialog;
	private int[] imagesnot={R.mipmap.hypetablenot,R.mipmap.hypeclampnot,R.mipmap.hypefloornot,R.mipmap.hypesuspendednot,
			R.mipmap.structotablenot,R.mipmap.structofloornot,R.mipmap.superlighttablenot,R.mipmap.structotablenot};
	public static final int[] images={R.mipmap.hypetable,R.mipmap.hypeclamp,R.mipmap.hypefloor,R.mipmap.hypesuspended,
			R.mipmap.structotable,R.mipmap.structofloor,R.mipmap.superlighttable,R.mipmap.structotable};
	private String[] devName={"Hype Table","Hype Clamp","Hype Floor","Hype Suspended",
			"Structo Table","Structo Floor","Superlight Table","Structo Table"};
	private ArrayList<Item> dataItem=new ArrayList<Item>();

	private Handler mHandler,handler ;
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private int index=0;
	private static final int SENSOR_SHAKE = 10;
	private static final int SENSOR_TIME_MIN_GAP = 1500;//ms
	private static final int curiomaf=0x7dcc;
	public static  int txPowerLevel = Integer.MIN_VALUE;
	private static final int DATA_TYPE_FLAGS = 0x01;
	private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
	private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
	private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
	private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
	private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
	private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
	private static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
	private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
	private static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
	private static final int DATA_TYPE_SERVICE_DATA = 0x16;
	private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;
	private static final int[] imagesvaules={1,2,3,4,5,6,8,10};
	private ItemDAO mSQL;
	private String mDeviceName;
	private String mDeviceAddress;
	private	int mDeviceShake;
	private	int mDeviceImage;
	private int mDeviceImageNot;
	private Boolean firstcall=true;
	private boolean connTimeout=false;
	private TextView selectDeviceName;
	private TextView seekbarValue;
	private TimerTask task;
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}

		}

		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		findViews();
		checkBle();
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	private void checkBle()
	{
		// 檢查手機硬體是否為BLE裝置
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}
		//初始藍牙Adapter
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 檢查手機使否開啟藍芽裝置
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	private void findViews()
	{
		mHandler=new Handler();
		mImg = (ImageView) findViewById(R.id.id_content);
		mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
		mAdapter = new HorizontalScrollViewAdapter(this,dataItem);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		startTime=System.currentTimeMillis();
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mSQL=new ItemDAO(this);
		myseekbar=(CircularSeekBar)findViewById(R.id.view);
		selectDeviceName=(TextView)findViewById(R.id.textView);
		seekbarValue=(TextView)findViewById(R.id.textView2);
		pDialog=new ProgressDialog(this);
		handler=new Handler();

		final Runnable runnable=new Runnable() {
			@Override
			public void run() {
				scanLeDevice(true);
			}
		};

		task=new TimerTask() {
			@Override
			public void run() {
				if(dataItem.isEmpty())
				handler.postDelayed(runnable,10000);
			}
		};
		mImg.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(v.getContext(), SettngDeviceActivity.class);
				intent.putExtra(SettngDeviceActivity.EXTRAS_DEVICE_ADDRESS, dataItem.get(index).getDevice().getAddress());
				intent.putExtra(SettngDeviceActivity.EXTRAS_DEVICE_NAME, dataItem.get(index).getDevicename());
				intent.putExtra(SettngDeviceActivity.EXTRAS_DEVICE_IMAGE, dataItem.get(index).getImage());
				intent.putExtra(SettngDeviceActivity.EXTRAS_SHAKE_FLAG, dataItem.get(index).getShake());
				startActivityForResult(intent, 77);
				return true;
			}
		});



		//添加滚动回调
		mHorizontalScrollView
				.setCurrentImageChangeListener(new CurrentImageChangeListener() {
					@Override
					public void onCurrentImgChanged(int position,
													View viewIndicator) {

					}
				});


		//添加点击回调
		mHorizontalScrollView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onClick(View view, int position) {
				if (position != index) {
					if(mBluetoothLeService.getGatt1()!=null)
					mBluetoothLeService.disconnect();
					else mBluetoothLeService.connect(dataItem.get(position).getMac());
					index = position;
					showpDialog();
					connTimeout=false;
					new Thread() {
						@Override
						public void run() {
							try {

								Thread.sleep(10000);
								if (pDialog.isShowing()) {
									closepDialog();
									connTimeout=true;
								}


							} catch (Exception e) {
								if (pDialog.isShowing()) {
									closepDialog();
									connTimeout=true;}
							}
						}
					}.start();
				} else selectItemdata(position);

			}
		});

		myseekbar.setSeekBarChangeListener(new CircularSeekBar.OnSeekChangeListener() {
			@Override
			public void onProgressChange(CircularSeekBar view, int newProgress) {

//				if (newProgress <= 10) newProgress = 10;
				seekbarValue.setText(newProgress+"%");
				byte[] value = new byte[10];
				value[0] = (byte) 0x00;
				StringBuilder sb = new StringBuilder();
				sb.append(Integer.toHexString(newProgress));
				if (sb.length() < 2) {
					sb.insert(0, '0'); // pad with leading zero if needed
				}
				if (characteristic != null) {
					String hex = sb.toString();
					characteristic.setValue(hex2byte(hex.getBytes()));
					mBluetoothLeService.writeCharacteristic(characteristic);
					ledStatus = true;

				}
				else{dialog(getString(R.string.dialog_select_device));}

			}
		});

		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
//		dataItem.clear();
//		mLeDevices.clear();
	}

	private void dialog(String message) {
		if (builder == null) {
			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage(message);
			builder.setTitle(getString(R.string.dialog_Title));
			builder.setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
							builder=null;
				}
			});
			builder.show();
		}
	}
	private void showpDialog(){

		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setMessage(getString(R.string.progressDialog_message));
		pDialog.setTitle(getString(R.string.progressDialog_Title));
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
		setDialogFontSize(pDialog, 22);
	}

	private void closepDialog(){
		if(pDialog.isShowing())pDialog.dismiss();
		if(connTimeout)dialog(getString(R.string.error_ble_conn));

	}
	private void cancelpDialog(){
		if(pDialog.isShowing())pDialog.cancel();


	}
	private void setDialogFontSize(Dialog dialog,int size)
	{
		Window window = dialog.getWindow();
		View view = window.getDecorView();
		setViewFontSize(view, size);
	}
	private void setViewFontSize(View view,int size)
	{
		if(view instanceof ViewGroup)
		{
			ViewGroup parent = (ViewGroup)view;
			int count = parent.getChildCount();
			for (int i = 0; i < count; i++)
			{
				setViewFontSize(parent.getChildAt(i),size);
			}
		}
		else if(view instanceof TextView){
			TextView textview = (TextView)view;
			textview.setTextSize(size);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		else scanLeDevice(true);
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());



	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 77){
			switch (resultCode) {
				case Activity.RESULT_OK:
					Item item = dataItem.get(index);
					mDeviceAddress = data.getStringExtra(SettngDeviceActivity.EXTRAS_DEVICE_ADDRESS);
					mDeviceName = data.getStringExtra(SettngDeviceActivity.EXTRAS_DEVICE_NAME);
					mDeviceShake = data.getIntExtra(SettngDeviceActivity.EXTRAS_SHAKE_FLAG, 0);
					item.setDevicename(mDeviceName);
					item.setShake(mDeviceShake);
					item.setDeviceImage(item.getImagenot());
					item.setLedStatus(false);
					selectDeviceName.setText(mDeviceName);
					dataItem.set(index, item);
					mSQL.update(mDeviceAddress, mDeviceShake, mDeviceName);
//					Log.e(TAG, "ActivityResult name=" + mDeviceName + "index=" + index);
					mHorizontalScrollView.initFirstScreenChildren(dataItem.size());

					break;
				default:
					break;
			}
		}
		else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		else if(requestCode ==REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
		{
			scanLeDevice(true);
			return;
		}


//		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		unregisterReceiver(mGattUpdateReceiver);
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}

	}
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
//			Log.i(TAG, "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度" + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
			if(mDeviceShake==1){
				if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {

					if(characteristic!=null)
					{
						long endTime=System.currentTimeMillis();
							if((endTime-startTime)>=SENSOR_TIME_MIN_GAP)
							{
								vibrator.vibrate(100);
								if (ledStatus)turnled(true);
								else turnled(false);
								startTime=endTime;
								dataItem.get(index).setLedStatus(ledStatus);

							}

					}
				}
			}
		}
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

	};

	@Override
	protected void onStop() {
		super.onStop();


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBluetoothLeService.getGatt1()!=null)
		mBluetoothLeService.close();
	}




	private void scanLeDevice(final boolean enable) {

		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}

	}
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
			new BluetoothAdapter.LeScanCallback() {
				@Override
				public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
					runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							//如果找到的是curio的裝置就連接裝置,並取得控制光源的服務
							if (getmufid(device, curiomaf, scanRecord) != null)
							{
								if(dataItem.size()==1)
								mBluetoothLeService.connect(device.getAddress());
							}
						}
					});
				}


			};
	private void selectItemdata(int position){
		Item item=dataItem.get(position);
		BluetoothGatt gat=mBluetoothLeService.getGatt1();
		BluetoothGattCharacteristic temgattchar;
		if(gat!=null)
		{

			temgattchar=getCuriocharacteristic(gat);
			if(temgattchar!=null)
			{
				item.setmGattCharacteristics(temgattchar);
				item.setDeviceImage(item.getImagenot());
				index=position;
				mDeviceShake=item.getShake();
				mHorizontalScrollView.initFirstScreenChildren(dataItem.size());
				mImg.setImageResource(item.getImage());
				selectDeviceName.setText(item.getDevicename());
				ledStatus=item.getLedStatus();
				characteristic=temgattchar;
			}
			else
			{
				mImg.setImageResource(0);
				selectDeviceName.setText("");
				characteristic=null;

			}
		}

	}


	private  byte[] getmufid(final BluetoothDevice device,int manufacturerid,byte[] scanRecord){
		if (scanRecord == null) {
			return null;
		}
		int currentPos = 0;
		int advertiseFlag = -1;
		String localName = null;


		SparseArray<byte[]> manufacturerData = new SparseArray<byte[]>();

		try {
			while (currentPos < scanRecord.length) {
				// length is unsigned int.
				int length = scanRecord[currentPos++] & 0xFF;
				if (length == 0) {
					break;
				}
				// Note the length includes the length of the field type itself.
				int dataLength = length - 1;
				// fieldType is unsigned int.
				int fieldType = scanRecord[currentPos++] & 0xFF;
				switch (fieldType) {
					case DATA_TYPE_FLAGS:
						advertiseFlag = scanRecord[currentPos] & 0xFF;
						break;
					case DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL:
					case DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE:
						break;
					case DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL:
					case DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE:
						break;
					case DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL:
					case DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE:
						break;
					case DATA_TYPE_LOCAL_NAME_SHORT:
					case DATA_TYPE_LOCAL_NAME_COMPLETE:
						localName = new String(
								extractBytes(scanRecord, currentPos, dataLength));
						break;
					case DATA_TYPE_TX_POWER_LEVEL:
						txPowerLevel = scanRecord[currentPos];
						break;
					case DATA_TYPE_SERVICE_DATA:
						break;
					case DATA_TYPE_MANUFACTURER_SPECIFIC_DATA:
						//curio的manufacturerID是0x7dcc
						//byte格式:FF,CC,7D,目前pwm值(low),目前pwm值(hight),BoardType

						int manufacturerId = ((scanRecord[currentPos + 1] & 0xFF) << 8)
								+ (scanRecord[currentPos] & 0xFF);

						byte[] manufacturerDataBytes = extractBytes(scanRecord, currentPos + 2,
								dataLength - 2);
						if(manufacturerId==manufacturerid)
						{

							if(find_item(device)==null)
							{
								//尋找查到的image值位於圖的第幾個index
								curioIndex=find(imagesvaules,scanRecord[currentPos + 4] & 0xFF);
							 if (curioIndex >= 0 & curioIndex <= images.length)
								{
										mDeviceName=devName[curioIndex];
										mDeviceImage=images[curioIndex];
										mDeviceImageNot=imagesnot[curioIndex];
								}
								else
								{
									mDeviceName=getString(R.string.unknow_Curio_Device);
									mDeviceImage=R.mipmap.curioicon;
									mDeviceImageNot=R.mipmap.curioicon;
								}
								mSQL.insert(device.getAddress(), 0, mDeviceName);
								Item mdata=mSQL.select(device.getAddress()).get(0);
								mdata.setDevice(device);
								mdata.setMac(device.getAddress());
								mdata.setImage(mDeviceImage);
								mdata.setImagenot(mDeviceImageNot);
								mdata.setDeviceImage(mDeviceImage);
								mdata.setLedStatus(false);
								dataItem.add(mdata);
								if(firstcall)
								{
									mHorizontalScrollView.initDatas(mAdapter);
									firstcall=false;
								}
								else mHorizontalScrollView.initFirstScreenChildren(dataItem.size());
							}
							manufacturerData.put(manufacturerId, manufacturerDataBytes);
						}
						break;

					default:

						break;
				}
				currentPos += dataLength;
			}
			return manufacturerData.get(manufacturerid);
		} catch (Exception e) {

			return null;
		}

	}

	private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
		byte[] bytes = new byte[length];
		System.arraycopy(scanRecord, start, bytes, 0, length);
		return bytes;
	}

	public int find(int[] array, int value)  //找指定值位於陣列的那個位置
	{
		int temp=-1;
		for(int i=0; i<array.length; i++) {
			if (array[i] == value)
				temp = i;
		}
		return temp;

	}
		//廣播
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;


			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				mBluetoothLeService.close();
				mBluetoothLeService.connect(dataItem.get(index).getMac());

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {

					if(!dataItem.isEmpty())
					{
						closepDialog();
						selectItemdata(index);
					}




			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

			}
		}
	};
	private byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("長度不是偶數");
	}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		b = null;
		return b2;
	}
	private void turnled(boolean onoff){
		byte[] value = new byte[2];
		value[0] = (byte) 0x00;
		value[1] =(byte) 0x5a;

		if (characteristic != null) {
			if(onoff) {
				myseekbar.setProgress(0);
				ledStatus=false;
			}
			else
			{
				myseekbar.setProgress(100);
				ledStatus=true;
			}

			mBluetoothLeService.writeCharacteristic(characteristic);

		}

	}

	private BluetoothGattCharacteristic getCuriocharacteristic(BluetoothGatt gat){
		BluetoothGattService mgat=mBluetoothLeService.getGattserver(gat,
				BluetoothLeService.UUID_CRIO_LIGHT_DEVICE);
		if (mgat!=null)
			return mgat.getCharacteristic(BluetoothLeService.UUID_PWM_brightness_level);
		else
			return null;

	}
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

		return intentFilter;
	}
	private Item find_item(BluetoothDevice device){
		if(!dataItem.isEmpty())
		{
			for(Item item:dataItem)
			{
				if(item.getDevice().equals(device))return item;
			}
		}
		return null;

	}




}
