package edu.ncsu.signalstrengthcollector;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class MainApplication extends Application {
	
	boolean shouldGetSignalStrength = false;
	//boolean shouldGetSignalStrength2 = false;
	//boolean shouldGetSignalStrength3 = false;

	ArrayList<List<ScanResult>> signalStrengths = new ArrayList<List<ScanResult>>();
	//ArrayList<List<ScanResult>> signalStrengthsFloor2 = new ArrayList<List<ScanResult>>();
	//ArrayList<List<ScanResult>> signalStrengthsFloor3 = new ArrayList<List<ScanResult>>();
	
	boolean shouldGetWifiAndCellPositions = false;
	//boolean shouldGetWifiAndCellPositions2 = false;
	//boolean shouldGetWifiAndCellPositions3 = false;
	
	ArrayList<Location> wifiAndCellPositions = new ArrayList<Location>();
	//ArrayList<Location> wifiAndCellPositionsFloor2 = new ArrayList<Location>();
	//ArrayList<Location> wifiAndCellPositionsFloor3 = new ArrayList<Location>();
	
	boolean shouldGetGpsPositions = false;
	//boolean shouldGetGpsPositions2 = false;
	//boolean shouldGetGpsPositions3 = false;
	
	ArrayList<Location> gpsPositionsFloor = new ArrayList<Location>();
	//ArrayList<Location> gpsPositionsFloor2 = new ArrayList<Location>();
	//ArrayList<Location> gpsPositionsFloor3 = new ArrayList<Location>();
	
	WifiManager wifi;
	LocationManager locationManager;

	@Override
	public void onCreate() {
		super.onCreate();
		//Log.d(APP_NAME, "APPLICATION onCreate");
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled() == false)
        {
            //Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            //wifi.setWifiEnabled(true);
        } 
        
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

	}

	@Override
	public void onTerminate() {
		// This doesn't get called on real Android Devices... See docs
		//Log.d(APP_NAME, "APPLICATION onTerminate");
		super.onTerminate();      
	}
}