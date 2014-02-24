package edu.ncsu.signalstrengthcollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnMapClickListener {
	
	static final LatLng CENTER = new LatLng(35.769301, -78.676406);
	private GoogleMap map;
	private MainApplication application;
	BroadcastReceiver wifiDataReceiver = null;
	LocationListener wifiAndCellLocationListeners = null;
	LocationListener gpsLocationListeners = null;
	private int count;
	//private String dis_SSID;
	//private int strength;
	private Button btnWriteToFile;
	
	ArrayList<LatLng> pointsList;
	 
    // url to get all existing points list
    private static String url_points = "http://people.engr.ncsu.edu/ywang51/nprg/gen_json_for_android.php";
    
    // JSON Node names
    private static final String TAG_POINTS = "points";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LNG = "lng";
     
    // contacts JSONArray
    JSONArray points = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pointsList = new ArrayList<LatLng>();
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		//map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		
		// Move the camera instantly to the center with a zoom of 20.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 20));
		
		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
		map.setOnMapClickListener(this);	
		
		new PostJSONDataAsyncTask(this, null, url_points, false){
			@Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                Log.d("sigstr", "Updating...");
            }
            
            // Override the onPostExecute to do whatever you want
            @Override
            protected void onPostExecute(String response)
            {
                super.onPostExecute(response);
                Log.d("sigstr", response);
                if (response != null)
                {
                	JSONObject json = null;
                	try{
                		json = new JSONObject(response);
                	} catch (JSONException e){
                		e.printStackTrace();
                        Log.d("sigstr", "Error parsing JSON");
                	}
        			
        			if(json == null){
                    	Log.d("sigstr", "Error parsing server response");
                        return;
                    }
                    
        			pointsList.clear();
        			
                    // If returned object length is 
                    if(json.length() > 0){
            			try {
            	            // Getting Array of existing points
            	            points = json.getJSONArray(TAG_POINTS);
            	             
            	            // looping through All points
            	            for(int i = 0; i < points.length(); i++){
            	                JSONObject c = points.getJSONObject(i);
            	                 
            	                // Storing each json item in variable
            	                double lat = c.getDouble(TAG_LAT);
            	                double lng = c.getDouble(TAG_LNG);
            	                
            	                // adding each coordinate to ArrayList
            	                LatLng temp = new LatLng(lat, lng);
            	                pointsList.add(temp);
            	            }
            	        } catch (JSONException e) {
            	            e.printStackTrace();
            	        }
                    }
                    else {
                        //TODO Do something here if no teams have been made yet
                    }
                    
                    Log.d("sigstr", "Update Success");
                    
                    for(int i = 0; i < pointsList.size(); i++){
            			map.addMarker(new MarkerOptions()
            			.position(pointsList.get(i))
            			.title(""+pointsList.get(i))
            			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            		}
                }
                else
                {
                    // Toast.makeText(context, "Error connecting to server", Toast.LENGTH_LONG).show();
                	Log.d("sigstr", "Error Connecting to Server");
                }

            }
		}.execute();
		
		application = (MainApplication) MainActivity.this.getApplication();
		
		btnWriteToFile = (Button) findViewById(R.id.write_file_button);
		btnWriteToFile.setOnClickListener(new OnClickListener() {

		public void onClick(View v) {

			ArrayList<List<ScanResult>> signalStrengthsList = null;
			ArrayList<LatLng> LocationsList = null;
			//ArrayList<Location> wifiAndCellLocationsList = null;
			//ArrayList<Location> gpsLocationsList = null;

			signalStrengthsList = application.signalStrengths;
			LocationsList = application.TapedPositions;
			//wifiAndCellLocationsList = application.wifiAndCellPositions;
			//gpsLocationsList = application.gpsPositions;


			if(count != signalStrengthsList.size()){
				Toast.makeText(getBaseContext(),
						"Error - number of points != number of measurements",
						Toast.LENGTH_LONG).show();
				// How to recover?
			}

			try {
				File root = Environment.getExternalStorageDirectory();
				File file = new File(root, "measurements.sql");
				if (root.canWrite()) {
		            FileWriter filewriter = new FileWriter(file);
		            BufferedWriter out = new BufferedWriter(filewriter);
		            for (int i = 0; i < LocationsList.size(); i++) {
		            	double lat = LocationsList.get(i).latitude;
		            	double lng = LocationsList.get(i).longitude;
		            	String locationString = "INSERT INTO LOCATIONS (LATITUDE, LONGITUDE) " +
		            			"VALUES ('" + Double.toString(lat) + "', '" + Double.toString(lng) + "');\n";
		                out.write(locationString);
		                Log.d("sigstr", locationString);
		                
		                List<ScanResult> measurements = signalStrengthsList.get(i);
						for(int k = 0; k < measurements.size(); k++){

							ScanResult result = measurements.get(k);
							// TODO N.C. State access points only
							//if(result.SSID == "ncsu" || result.SSID == "ncsu-guest"){
								String measurementString = "INSERT INTO SIGNAL_STRENGTHS (MAC_ADDRRESS, STRENGTH, " +
										"L_ID) SELECT '" + result.BSSID + "', " + Integer.toString(result.level) +
										", LOCATIONS.L_ID FROM LOCATIONS WHERE LOCATIONS.LATITUDE='" + Double.toString(lat) + 
										"' AND LOCATIONS.LONGITUDE='" + Double.toString(lng) + "';\n";

								out.write(measurementString);
								Log.d("sigstr", result.SSID + " " + measurementString);
							//}
						}
		            }
		            out.close();
				}
			} catch (Exception e) {
					Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}
					
			Toast.makeText(getBaseContext(), "Done writing files", Toast.LENGTH_SHORT).show();
			}// onClick
		}); // btnWriteToFile
		
		// Register to get the Signal Strengths
		wifiDataReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent intent){			            	
				if(application.shouldGetSignalStrength){
					// Get signal strengths only right after we touched the map
				    List<ScanResult> results = application.wifi.getScanResults();
				    int size = results.size();
				    //dis_SSID = results.get(size - 1).SSID;
				    //strength = results.get(size - 1).level;
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
		count++;
		if(!application.shouldGetSignalStrength){
			if(application.wifi.startScan() == true){
				application.shouldGetSignalStrength = true;
			}
		}
		//application.locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, looper);
		application.TapedPositions.add(point);
		map.addMarker(new MarkerOptions().position(point));
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
