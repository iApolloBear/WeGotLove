package com.example.glass;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static com.example.glass.MainActivity.DEVICE_EXTRA;

public class SplashActivity extends Activity {

    BluetoothAdapter adapter = null;
    BluetoothSocket socket = null;
    static final UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int BT_ENABLE_REQUEST = 10;
    private int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "com.example.lightcontrol.SOCKET";
    public static final String DEVICE_UUID = "com.example.lightcontrol.uuid";
    private static final String DEVICE_LIST = "com.example.lightcontrol.devicelist";
    public static final String BUFFER_SIZE = "com.example.lightcontrol.buffersize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice device = adapter.getRemoteDevice("00:19:06:34:EF:34");
                Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                intent.putExtra(DEVICE_EXTRA, device);
                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
                intent.putExtra(BUFFER_SIZE, mBufferSize);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
