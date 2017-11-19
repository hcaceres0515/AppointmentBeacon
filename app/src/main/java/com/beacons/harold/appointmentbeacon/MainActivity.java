package com.beacons.harold.appointmentbeacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.provider.Settings.Secure;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter BTAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d("Devices", deviceHardwareAddress);
            }
        }
    };

    public void searchBeacon(View view) {

//        String bluetoothStatusMessage = "";
//
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (mBluetoothAdapter == null) {
//            // Device does not support Bluetooth
//            Log.d("My search button", "Bluetooth is not supported");
//            bluetoothStatusMessage = "This device not support bluetooth";
//            return;
//        }
//
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            //bluetoothStatusMessage = "Bluetooth is activated";
//        } else {
//            bluetoothStatusMessage = "Bluetooth is activated";
//        }
//
//
//        displayBluetoothStatusMessage(bluetoothStatusMessage);

        BluetoothLeScanner mLEScanner = BTAdapter.getBluetoothLeScanner();


        // start looking for bluetooth devices
        BTAdapter.startDiscovery();

        List<ScanFilter> filters = new ArrayList<ScanFilter>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        mLEScanner.startScan(filters, settings, mScanCallback);
    }

    public void getPhoneDetails(View view) {

        //String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // https://medium.com/@ssaurel/how-to-retrieve-an-unique-id-to-identify-android-devices-6f99fd5369eb

        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String m_deviceId = TelephonyMgr.getDeviceId();

        TextView phoneDetail = (TextView) findViewById(R.id.phone_detail);
//        phoneDetail.setText("UNIQUE ID: "  + m_deviceId);

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();

        Log.d("My getPhoneDetail", "UNIQUE ID: "  + m_deviceId);
        Log.d("My getPhoneDetail", "MAC ADDRESS: "  + address);

        phoneDetail.setText("UNIQUE ID:\n"  + m_deviceId + "\nMACADDRESS:\n"  + address);
    }

    public void displayBluetoothStatusMessage(String message) {

        TextView bluetoothStatus = (TextView) findViewById(R.id.bluetooth_status);
        bluetoothStatus.setText(message);
    }

    public void changeLayout(View view) {
        Intent intent = new Intent(this, SearchBeaconsActivity.class);
        startActivity(intent);
        Log.v("Button event", "change layout to scan beacons");
    }


    private ScanCallback mScanCallback = new ScanCallback() {


        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            BluetoothDevice btDevice = result.getDevice();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };
}
