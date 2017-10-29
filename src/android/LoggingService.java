package nl.dannisi.backgroundlocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import nl.dannisi.backgroundlocation.data.ConfigurationDAO;
import nl.dannisi.backgroundlocation.data.DAOFactory;

public class LoggingService extends Service {
	private static final String TAG = "BackgroundNetworkLocationService";
	private LocationManager lm;
	private LocationListener locationListener;

	private JSONObject params;
    private JSONObject headers;
    
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_TIME = "time";

    private Location lastLocation;

    private Config config;
    private static long minTimeMillis;
    
	private final DecimalFormat sevenSigDigits = new DecimalFormat("0.#######");

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		shutdownLoggerService();
	}
	
	 @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            //service has been probably restarted so we need to load config from db
            ConfigurationDAO dao = DAOFactory.createConfigurationDAO(this);
            try {
                config = dao.retrieveConfiguration();
            } catch (JSONException e) {
                Log.e(TAG, "Config exception: " + e.getMessage());
                config = new Config(); //using default config
            }
        }
        else {
            if (intent.hasExtra("config")) {
                config = intent.getParcelableExtra("config");
            }
            else {
                config = new Config(); //using default config
            }
        }

         minTimeMillis = (config.getMinTime() * 1000);
         try {
             headers = new JSONObject(config.getHeaders());
         }
         catch (JSONException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

		startLoggerService();
		
		return START_REDELIVER_INTENT;
	}
	
	private void startLoggerService() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        JSONArray jsonProviders = new JSONArray();
		List<String> matchingProviders = lm.getAllProviders();
		for (String provider: matchingProviders) {
            jsonProviders.put(provider);
		}

        Log.i(TAG, "Available providers: " + jsonProviders.toString());

        SharedPreferences sharedPreferences = getSharedPreferences("location_providers", 0);
        Editor editor = sharedPreferences.edit();
        editor.putString("providers", jsonProviders.toString());
        editor.commit();

        locationListener = new MyLocationListener();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeMillis, config.getMinDistanceMeters(), locationListener);
        //lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, minTimeMillis, config.getMinDistanceMeters(), locationListener);
        Log.i(TAG, "Started background network location service");
		
		if (config.getToasts()) {
			Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
		}
    }

	private void shutdownLoggerService() {
		lm.removeUpdates(locationListener);
		Log.i(TAG, "Stopped background network location service");
		
		if (config.getToasts()) {
			Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
		}
	}

    public class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d(TAG, "New location: lat:" + location.getLatitude() + ", long:" + location.getLongitude() + ", acc:" + location.getAccuracy() + ", prov:" + location.getProvider() + ", spd:" + location.getSpeed());
                lastLocation = location;
                new LogLocation().execute();

                SharedPreferences sharedPreferences = getSharedPreferences("last_location", 0);
                Editor editor = sharedPreferences.edit();
                editor.putFloat("location.latitude", (float) location.getLatitude());
                editor.putFloat("location.longitude", (float) location.getLongitude());
                editor.putFloat("location.accuracy", (float) location.getAccuracy());
                editor.putLong("location.time", location.getTime());
                editor.putString("location.provider", location.getProvider());
                editor.putFloat("location.speed", location.getSpeed());
                editor.commit();
            }
        }

        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
            if (config.getToasts()) {
                Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider, Toast.LENGTH_SHORT).show();
            }
        }

        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
            if (config.getToasts()) {
                Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider, Toast.LENGTH_SHORT).show();
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
	
	class LogLocation extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... args) {
			Log.i(TAG, "Sending location...");
			HttpURLConnection httpcon;
			String result = null;

            params = new JSONObject();

			try {
				JSONObject location = new JSONObject();
				location.put("latitude", lastLocation.getLatitude());
				location.put("longitude", lastLocation.getLongitude());
				location.put("accuracy", lastLocation.getAccuracy());
				location.put("time", lastLocation.getTime());
				location.put("provider", lastLocation.getProvider());
				location.put("speed", lastLocation.getSpeed());
				params.put("location", location);
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			try {
				//Connect
				httpcon = (HttpURLConnection) ((new URL (config.getUrl()).openConnection()));
				httpcon.setDoOutput(true);
				httpcon.setRequestProperty("Content-Type", "application/json");
				httpcon.setRequestProperty("Accept", "application/json");
                Iterator<String> headkeys = headers.keys();
                while(headkeys.hasNext()){
                    String headkey = headkeys.next();
                    if(headkey != null) {
                        httpcon.setRequestProperty(headkey, (String)headers.getString(headkey));
                    }
                }
				httpcon.setRequestMethod("POST");
				httpcon.connect();

				//Write         
				OutputStream os = httpcon.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(params.toString());
				writer.close();
				os.close();

				//Read      
				BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

				String line = null; 
				StringBuilder sb = new StringBuilder();         

				while ((line = br.readLine()) != null) {  
					sb.append(line); 
				}       

				br.close();  
				result = sb.toString();
				Log.d(TAG, "result: " + result);
				
				JSONObject json = new JSONObject(result);
				
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.i(TAG, "Log successful");
				} 
				else {
					Log.w(TAG, "Log failed");
					return json.getString(TAG_MESSAGE);
				}
			} 
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		protected void onPostExecute(String file_url) {
            if (config.getToasts()) {
				Toast.makeText(
				getBaseContext(),
				"Location stored: \nLat: " + sevenSigDigits.format(lastLocation.getLatitude())
								+ " \nLon: " + sevenSigDigits.format(lastLocation.getLongitude())
								+ " \nAcc: " + sevenSigDigits.format(lastLocation.getAccuracy()) + "m"
								+ " \nAcc: " + lastLocation.getSpeed() + "m/s"
								+ " \nProv: " + lastLocation.getProvider(),
				Toast.LENGTH_SHORT).show();
			}
		}
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}


	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		LoggingService getService() {
			return LoggingService.this;
		}
	}
}
