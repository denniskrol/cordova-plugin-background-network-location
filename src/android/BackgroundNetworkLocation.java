package nl.dannisi.backgroundlocation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import nl.dannisi.backgroundlocation.Config;
import nl.dannisi.backgroundlocation.data.ConfigurationDAO;
import nl.dannisi.backgroundlocation.data.DAOFactory;

public class BackgroundNetworkLocation extends CordovaPlugin {
    private static final String TAG = "BackgroundNetworkLocation";

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";
    public static final String ACTION_SET_CONFIG = "setConfig";
    public static final String ACTION_GET_LAST_LOCATION = "getLastLocation";
    public static final String ACTION_GET_LOCATION_PROVIDERS = "getLocationProviders";

    private Intent locationServiceIntent;

    private Boolean isEnabled = false;

    private String url;
    private String params;
    private String headers;
    private int minDistanceMeters = 0;
    private int minTime = 60;
    private Boolean debug = false;
    private Boolean toasts = false;
    private Boolean restartAlreadyRunning = true;

    private Config config;
    private CallbackContext callbackContext;

    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        Boolean result = false;
        locationServiceIntent = new Intent(activity, LoggingService.class);

        if (ACTION_START.equalsIgnoreCase(action) && !isEnabled) {
            result = true;
            if (config == null) {
                callbackContext.error("Call configure before calling start");
				return false;
            }

            locationServiceIntent.putExtra("config", config);
            locationServiceIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);

            if (isMyServiceRunning(LoggingService.class)) {
                if (restartAlreadyRunning == true) {
                    Log.i(TAG, "Background service already running, restarting...");
				
                    activity.stopService(locationServiceIntent);
                }
                else {
                    Log.w(TAG, "Background service already running");
                    callbackContext.error("Background service already running");

                    return false;
                }
            }

			activity.startService(locationServiceIntent);
			isEnabled = true;
			callbackContext.success();
        } 
		else if (ACTION_STOP.equalsIgnoreCase(action)) {
            isEnabled = false;
            result = true;
            activity.stopService(locationServiceIntent);
            callbackContext.success();
        }
		else if (ACTION_CONFIGURE.equalsIgnoreCase(action)) {
            this.callbackContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        config = Config.fromJSONObject(data.getJSONObject(0));
                        persistConfiguration(config);
                        // callbackContext.success(); //we cannot do this
                    } catch (JSONException e) {
                        Log.e(TAG, "Config exception: " + e.getMessage());
                        callbackContext.error("Configuration error: " + e.getMessage());
                    } catch (NullPointerException e) {
                        Log.e(TAG, "Config exception: " + e.getMessage());
                        callbackContext.error("Configuration error: " + e.getMessage());
                    }
                }
            });

            result = true;
			callbackContext.success();
        }
		else if (ACTION_GET_LAST_LOCATION.equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(getLastLocation());
                    } 
					catch (JSONException e) {
                        Log.e(TAG, "Getting last location failed");
                        callbackContext.error("Converting locations to JSON failed.");
                    }
                }
            });

            return true;
        }
        else if (ACTION_GET_LOCATION_PROVIDERS.equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(getLocationProviders());
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Getting location providers failed");
                        callbackContext.error("Converting location providerss to JSON failed.");
                    }
                }
            });

            return true;
        }
       
        return result;
    }

    /**
     * Override method in CordovaPlugin.
     * Checks to see if it should turn off
     */
    public void onDestroy() {
        Activity activity = this.cordova.getActivity();

        if (isEnabled) {
            activity.stopService(locationServiceIntent);
        }
    }
	
	public JSONObject getLastLocation() throws JSONException {
		Activity activity = this.cordova.getActivity();
		SharedPreferences settings = activity.getSharedPreferences("last_location", 0);
		
		JSONObject json = new JSONObject();
        json.put("accuracy", settings.getFloat("location.accuracy", 0));
        json.put("latitude", settings.getFloat("location.latitude", 0));
        json.put("longitude", settings.getFloat("location.longitude", 0));
        json.put("time", settings.getLong("location.time", 0));
        json.put("provider", settings.getString("location.provider", ""));
        json.put("speed", settings.getFloat("location.speed", 0));

        return json;
    }

    public JSONArray getLocationProviders() throws JSONException {
        Activity activity = this.cordova.getActivity();
        SharedPreferences settings = activity.getSharedPreferences("location_providers", 0);

        JSONArray json = new JSONArray(settings.getString("providers", ""));

        return json;
    }

    public void persistConfiguration(Config config) throws NullPointerException {
        ConfigurationDAO dao = DAOFactory.createConfigurationDAO(getContext());

        dao.persistConfiguration(config);
    }

    public JSONObject retrieveConfiguration() throws JSONException {
        ConfigurationDAO dao = DAOFactory.createConfigurationDAO(getContext());
        Config config = dao.retrieveConfiguration();
        if (config != null) {
            return config.toJSONObject();
        }
        return null;
    }

    protected Activity getActivity() {
        return this.cordova.getActivity();
    }

    protected Context getContext() {
        return getActivity().getApplicationContext();
    }
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) this.cordova.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
