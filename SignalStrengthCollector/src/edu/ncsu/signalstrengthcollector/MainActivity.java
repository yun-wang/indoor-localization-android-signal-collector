package edu.ncsu.signalstrengthcollector;

import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnMapClickListener {
	
	static final LatLng CENTER = new LatLng(35.769345, -78.676634);
	private GoogleMap map;
	private MainApplication application;
	BroadcastReceiver wifiDataReceiver = null;
	LocationListener wifiAndCellLocationListeners = null;
	LocationListener gpsLocationListeners = null;
	private String dis_SSID;
	private int strength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		//map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		
		// Move the camera instantly to the center with a zoom of 20.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 20));
		
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
		map.setOnMapClickListener(this);		
		
		application = (MainApplication) MainActivity.this.getApplication();
		
		// Register to get the Signal Strengths
		wifiDataReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent intent){			            	
				if(application.shouldGetSignalStrength){
					// Get signal strengths only right after we touched the map
				    List<ScanResult> results = application.wifi.getScanResults();
				    int size = results.size();
				    dis_SSID = results.get(size - 1).SSID;
				    strength = results.get(size - 1).level;
				    Log.d("sigstr", results.get(size - 1).SSID + " " + results.get(size - 1).level + " " + results.get(size - 1).BSSID);
				                    
				    // Add to the list of signal strengths
				    application.signalStrengths.add(results);
				    application.shouldGetSignalStrength = false;
				            		
				    Toast.makeText(getBaseContext(), "Saved Measurements", Toast.LENGTH_SHORT).show();
				}
			}
		};
		    
		registerReceiver(wifiDataReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		if(!application.shouldGetSignalStrength){
			if(application.wifi.startScan() == true){
				application.shouldGetSignalStrength = true;
			}
		}
		//application.locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, looper);
		map.addMarker(new MarkerOptions().position(point).title(""+point));
	}

	@Override
    public void onStop(){
    	if(wifiDataReceiver != null){
    	    unregisterReceiver(wifiDataReceiver);
    	    wifiDataReceiver = null;
    	}
		
    	super.onStop();
    }
}
