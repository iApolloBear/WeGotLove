package com.example.glass;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private MapActivity.ReadInput mReadThread = null;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng Current;

    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;

    final static String on = "92";//on
    final static String off = "79";//off

    public MarkerOptions One;
    public MarkerOptions Two;
    public MarkerOptions Three;
    public MarkerOptions Four;
    public MarkerOptions Five;
    public MarkerOptions Six;

    public Marker Uno;
    public Marker Dos;
    public Marker Tres;
    public Marker Cuatro;
    public Marker Cinco;
    public Marker User;

    Polyline CurrentPolyLine;

    private ProgressDialog progressDialog;
    GoogleMap mapApi;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapApi = googleMap;
        mapApi.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Current = new LatLng(location.getLatitude(), location.getLongitude());
                Six = MarkV(Current, BitmapDescriptorFactory.HUE_BLUE);
                User = mapApi.addMarker(Six);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        LatLng Dorado = new LatLng(19.058751, -98.126840);
        LatLng Fuck = new LatLng(19.071399, -98.173273);
        LatLng Fuck1 = new LatLng(19.030992, -98.233845);
        LatLng Fuck2 = new LatLng(19.045136, -98.190505);
        LatLng Fuck3 = new LatLng(19.027700, -98.203780);

        One = MarkV(Dorado, BitmapDescriptorFactory.HUE_GREEN);
        Two = MarkV(Fuck, BitmapDescriptorFactory.HUE_GREEN);
        Three = MarkV(Fuck1, BitmapDescriptorFactory.HUE_GREEN);
        Four = MarkV(Fuck2, BitmapDescriptorFactory.HUE_GREEN).title("Disponible").snippet("Hay 5 Cajones Disponibles");
        Five = MarkV(Fuck3, BitmapDescriptorFactory.HUE_GREEN).title("Disponible").snippet("Hay 3 cajones Disponibles");

        Uno = mapApi.addMarker(One);
        Dos = mapApi.addMarker(Two);
        Tres = mapApi.addMarker(Three);
        Cuatro = mapApi.addMarker(Four);
        Cinco = mapApi.addMarker(Five);
        Uno.setTag(Dorado);
        mapApi.moveCamera(CameraUpdateFactory.newLatLng(Dorado));
        AskForPermissions();
    }

    private void AskForPermissions() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext() ,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = getLastKnownLocation();
                Current = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Six = MarkV(Current, BitmapDescriptorFactory.HUE_BLUE).title("Estás Aquí").snippet("Ubicación Actual");
                User = mapApi.addMarker(Six);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        String provider = providers.get(0);
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        return bestLocation;
    }

    private MarkerOptions MarkV(LatLng Star, float Covenant){
        return new MarkerOptions().position(Star).icon(BitmapDescriptorFactory.defaultMarker(Covenant));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.getPosition().equals(Uno.getPosition())){
            Uno.showInfoWindow();
            return true;
        }else if (marker.getPosition().equals(Dos.getPosition())){
            Dos.showInfoWindow();
            return true;
        } else if(marker.getPosition().equals(Tres.getPosition())){
            Tres.showInfoWindow();
            return true;
        } else if(marker.getPosition().equals(Cuatro.getPosition())){
            Cuatro.showInfoWindow();
            return true;
        } else if(marker.getPosition().equals(Cinco.getPosition())){
            Cinco.showInfoWindow();
            return true;
        }
        else{
            return false;
        }
    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String SuckItAndSee = new String(buffer, 0, i);
                        Log.d(TAG, SuckItAndSee);
                        /*
                         * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
                         */
                        if(SuckItAndSee.contains("d") && !SuckItAndSee.contains("a") && !SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (SuckItAndSee.contains("d") && SuckItAndSee.contains("a") && !SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (!SuckItAndSee.contains("d") && SuckItAndSee.contains("a") && !SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Uno =  mapApi.addMarker(One);
                                    Dos =  mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (!SuckItAndSee.contains("d") && !SuckItAndSee.contains("a") && SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Uno =  mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (!SuckItAndSee.contains("d") && SuckItAndSee.contains("a") && SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (SuckItAndSee.contains("d") && !SuckItAndSee.contains("a") && SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else if (SuckItAndSee.contains("d") && SuckItAndSee.contains("a") && SuckItAndSee.contains("s")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet("No hay cajones").title("No Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    One.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Two.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Three.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Hay 1 Cajón Disponible").title("Disponible");
                                    Uno = mapApi.addMarker(One);
                                    Dos = mapApi.addMarker(Two);
                                    Tres = mapApi.addMarker(Three);
                                }
                            });
                        }
                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new MapActivity.DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new MapActivity.ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(MapActivity.this, "Espere...", "Conectando");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "No Se Ha Podido Conectar", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Conectado");
                mIsBluetoothConnected = true;
                mReadThread = new MapActivity.ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
