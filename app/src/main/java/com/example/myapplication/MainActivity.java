package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
//import com.kingsense.eegsdk_sversion.sdk.eegsdk;
//import com.kingsense.eegsdk_sversion.sdk.BleListener;

import com.kingsense.eegsdk_fversion.sdk.eegsdk;
import com.kingsense.eegsdk_fversion.sdk.BleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btSearch;
    private Button btConnect;
    private Button btDisconnect;
    private Button btSave;
    private Button btSaveStop;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvReceive;
    private TextView tvCurConState;
    private LinearLayout deviceListView;
    private LinearLayout dataReceive;
    private ListView devices;

    private Spinner spinner;
    private DevicesAdapter devicesAdapter;

    private BluetoothDevice curBluetoothDevice;  //当前选择的设备
    private boolean curConnState = false; //当前设备连接状态,true表示已连接，false表示未连接


    private static final String TAG = "MainActivity";
    private Context mContext;
    private eegsdk eegsdk;

    private LineChart lineChart1,lineChart2,lineChart3,lineChart4;
    private LineChartView lineChartView1,lineChartView2,lineChartView3,lineChartView4;

    private static final float  WIDTH = 1680;//参考设备的宽，单位是dp DPI:160时
    private static float appDensity;//表示屏幕密度
    private static float appScaleDensity; //字体缩放比例，默认appDensity


    private final BleListener bleListener=new BleListener() {
        @Override
        public void onDeviceFound(BluetoothDevice bluetoothDevice) { //搜索到设备回调
            devicesAdapter.addDevice(bluetoothDevice);
        }

        @Override
        public void onDiscoveryOutTime() { //扫描结束回调
            Log.e(TAG,"蓝牙设备扫描结束");
            tvCurConState.setText("搜索结束");
        }

        @Override
        public void onServiceDiscoverySucceed(BluetoothGatt bluetoothGatt, int i) { //发现服务成功
            curConnState = true;
            tvCurConState.setText("连接成功");
        }

        @Override
        public void onConnectFailure() {//连接失败
            curConnState = false;
            tvCurConState.setText("连接失败");

        }

        @Override
        public void onDisConnectSuccess() {//断开连接成功
            curConnState = false;
            tvCurConState.setText("断开成功");

        }

        @Override
        public void onReceiveData(JSONObject jsonObject) {
            try {
                JSONArray channel_1=jsonObject.getJSONArray("channel_1");
                JSONArray channel_2=jsonObject.getJSONArray("channel_2");
                int battery=jsonObject.getInt("power");
                boolean leadFlag=jsonObject.getBoolean("lead");

                runOnUiThread(()->{
                    try {
                        lineChartView1.addEntryJson(new JSONArray[]{channel_1, channel_2});
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onReceiveFrequency(JSONObject jsonObject) {
            try {
                double delta = jsonObject.getDouble("delta");
                double theta=jsonObject.getDouble("theta");
                double alpha=jsonObject.getDouble("alpha");
                double beta=jsonObject.getDouble("beta");
                double gamma=jsonObject.getDouble("gamma");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineChartView2.addEntry(new double[]{delta,theta,alpha,beta,gamma});
                    }
                });

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveFeature(JSONObject jsonObject) {
            try {
                double att = jsonObject.getDouble("attention");
                double relax=jsonObject.getDouble("relax");
                double meditation=jsonObject.getDouble("meditation");
//                double emotion=jsonObject.getDouble("emotion");
                double tired=jsonObject.getDouble("tired");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineChartView3.addEntry(new double[]{att,relax,tired});
                    }
                });

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onReceivePPG(JSONObject jsonObject) {
            try {
                JSONArray red=jsonObject.getJSONArray("red");
                JSONArray ired=jsonObject.getJSONArray("ired");
                if(!jsonObject.isNull("spo2")){
                    int spo2=jsonObject.getInt("spo2");
                    int hr=jsonObject.getInt("hr");
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveMPU(JSONObject jsonObject) {
            try {
                JSONArray acc_x=jsonObject.getJSONArray("acc_x");
                JSONArray acc_y=jsonObject.getJSONArray("acc_y");
                JSONArray acc_z=jsonObject.getJSONArray("acc_z");
                JSONArray gyro_x=jsonObject.getJSONArray("gyro_x");
                JSONArray gyro_y=jsonObject.getJSONArray("gyro_y");
                JSONArray gyro_z=jsonObject.getJSONArray("gyro_z");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveBlink(JSONObject jsonObject) {
            try {
                JSONArray blink=jsonObject.getJSONArray("blink");
                runOnUiThread(()->{
                    try {
                        lineChartView4.addEntryJson(new JSONArray[]{blink});
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onReceiveGnash(JSONObject jsonObject) {
            try {
                JSONArray gnash = jsonObject.getJSONArray("gnash");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onReceiveEmotion(JSONObject jsonObject) {
            try {
                double emotion = jsonObject.getDouble("emotion");
                Log.e(TAG, String.valueOf(emotion));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDensity(getApplication(),this);
        setContentView(R.layout.activity_main);
        mContext=MainActivity.this;

        initView();
        initBle();
    }

    private void setDensity(final Application application, Activity activity){
        //获取当前app的屏幕显示信息
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (appDensity == 0){
            //初始化赋值操作
            appDensity = displayMetrics.density;
            appScaleDensity = displayMetrics.scaledDensity;

            //添加字体变化监听回调
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体发生更改，重新对scaleDensity进行赋值
                    if (newConfig != null && newConfig.fontScale > 0){
                        appScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }
                @Override
                public void onLowMemory() {

                }
            });
        }

        //计算目标值density, scaleDensity, densityDpi
        float targetDensity = displayMetrics.widthPixels / WIDTH; // 1920 / 1920 = 1.0
        float targetScaleDensity = targetDensity * (appScaleDensity / appDensity);
        int targetDensityDpi = (int) (targetDensity * 160);

        //替换Activity的density, scaleDensity, densityDpi
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        dm.density = targetDensity;
        dm.scaledDensity = targetScaleDensity;
        dm.densityDpi = targetDensityDpi;
    }

    /**
     * 初始化视图,并监听点击事件
     */
    private void initView() {
        btSearch = findViewById(R.id.serach);
        btConnect = findViewById(R.id.ble_connect);
        btDisconnect = findViewById(R.id.ble_disconnect);
        btSave=findViewById(R.id.save);
        btSaveStop=findViewById(R.id.saveOver);
        tvName = findViewById(R.id.name);
        tvAddress = findViewById(R.id.adress);
//        tvReceive = findViewById(R.id.receive_result);
        tvCurConState = findViewById(R.id.ble_state);
        dataReceive = findViewById(R.id.data_receive);
        deviceListView = findViewById((R.id.device_list));
        devices = findViewById(R.id.devices);
        spinner=findViewById(R.id.spinner);



        Switch gSwitch = findViewById(R.id.switch7);
        gSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            eegsdk.setIs50Hz(b);
        });
        Switch aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            eegsdk.setFilterEnable(b);
        });
        Switch bSwitch = findViewById(R.id.switch2);
        bSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            eegsdk.setNotchFilterEnable(b);
            Log.e(TAG, String.valueOf(b));
        });
        Switch cSwitch = findViewById(R.id.switch3);
        cSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            eegsdk.setOnlyHardware(b);
        });
        Switch dSwitch = findViewById(R.id.switch4);
        dSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            String str=eegsdk.getVersion();
            Log.e(TAG,str);
        });
        Switch eSwitch = findViewById(R.id.switch5);
        eSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

        });
        Switch fSwitch = findViewById(R.id.switch6);
        fSwitch.setOnCheckedChangeListener((compoundButton, b) -> {

        });




        //监听点击事件
        btSearch.setOnClickListener(this);
        btConnect.setOnClickListener(this);
        btDisconnect.setOnClickListener(this);
        btSave.setOnClickListener(this);
        btSaveStop.setOnClickListener(this);

        //列表适配器
        devicesAdapter = new DevicesAdapter(mContext);
        devices.setAdapter(devicesAdapter);

        //折线图
        lineChart1=findViewById(R.id.lineChart1);
        String[] name1={"af7","af8"};
        lineChartView1=new LineChartView(lineChart1,this,name1,3000,new float[]{0f,1.8f});

        lineChart2=findViewById(R.id.lineChart2);
        String[] name2={"delte","theta","alpha","beta","gamma"};
        lineChartView2=new LineChartView(lineChart2,this,name2,100,new float[]{0,100f});

        lineChart3=findViewById(R.id.lineChart3);
        String[] name3={"focus","relax","tried"};
        lineChartView3=new LineChartView(lineChart3,this,name3,100,new float[]{0,100f});

        lineChart4=findViewById(R.id.lineChart4);
        String[] name4={"gnash"};
        lineChartView4=new LineChartView(lineChart4,this,name4,3000,new float[]{0,2f});

        devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) devicesAdapter.getItem(i);
                if (eegsdk != null) {
                    eegsdk.scanStop();
                }
                tvName.setText(bluetoothDevice.getName());
                tvAddress.setText(bluetoothDevice.getAddress());
                curBluetoothDevice = bluetoothDevice;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    eegsdk.setMagnify(position+2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(mContext, "没有操作", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void initBle(){
        byte[] set= new byte[]{(byte) 0b11100011,(byte) 0b11110000};
        //初始化蓝牙,bleListener回调接口，按自己需求对其实现
        eegsdk=new eegsdk();
        eegsdk.init(mContext,bleListener,set);
        //检查蓝牙是否可用
        if(!eegsdk.checkBLE()) {
            Log.d(TAG, "该设备不支持低功耗蓝牙");
            Toast.makeText(mContext, "该设备不支持低功耗蓝牙", Toast.LENGTH_LONG).show();
        }else{
            if(!eegsdk.isEnable()){//蓝牙未打开，去打开蓝牙
                eegsdk.openBluetooth(false);
            }
        }
        //打开相关权限
        eegsdk.openPermission();

    }


    @SuppressLint({"MissingPermission", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.serach){
            dataReceive.setVisibility(View.GONE);
            deviceListView.setVisibility(View.VISIBLE);
            searchBtDevice();
        }
        if(id==R.id.ble_connect){
            if(!curConnState) {
                dataReceive.setVisibility(View.VISIBLE);
                deviceListView.setVisibility(View.GONE);
                if(!eegsdk.connectDevice(curBluetoothDevice)){
                    Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "当前设备已连接", Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.ble_disconnect){
            if(curConnState) {
                eegsdk.disconnectDevice();
            }else{
                Toast.makeText(this, "当前设备未连接", Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.save){
            if(curConnState){
                btSave.setVisibility(View.GONE);
                btSaveStop.setVisibility(View.VISIBLE);
                eegsdk.store();
//                    mHandler.postDelayed(stop,3000);
            }else{
                Toast.makeText(this, "当前设备未连接", Toast.LENGTH_SHORT).show();
            }
        }
        if(id==R.id.saveOver){
            btSave.setVisibility(View.VISIBLE);
            btSaveStop.setVisibility(View.GONE);
            eegsdk.stopStore();
        }
    }



    @SuppressLint("MissingPermission")
    private void searchBtDevice() {
        if (eegsdk.isDiscovery()) { //当前正在搜索设备...
            eegsdk.scanStop();
        }
        if(devicesAdapter != null){
            devicesAdapter.clear();  //清空列表
        }
        //开始搜索
        tvCurConState.setText("开始搜索");
        eegsdk.scanDevice(30000);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void finish() {
        super.finish();
        eegsdk.disconnectDevice();
    }
}