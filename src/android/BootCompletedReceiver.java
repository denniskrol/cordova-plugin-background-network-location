package nl.dannisi.backgroundlocation;

import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;

import nl.dannisi.backgroundlocation.Config;
import nl.dannisi.backgroundlocation.LoggingService;
import nl.dannisi.backgroundlocation.data.DAOFactory;
import nl.dannisi.backgroundlocation.data.ConfigurationDAO;
/**
 * BootCompletedReceiver class
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = "BackgroundNetworkLocationBootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received boot completed");
        ConfigurationDAO dao = DAOFactory.createConfigurationDAO(context);
        Config config = null;

        try {
            config = dao.retrieveConfiguration();
        } catch (JSONException e) {
            //noop
        }

        if (config == null) { return; }

        Log.d(TAG, "Boot completed " + config.toString());

        Log.i(TAG, "Starting service after boot");
        Intent locationServiceIntent = new Intent(context, LoggingService.class);
        locationServiceIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        locationServiceIntent.putExtra("config", config);

        context.startService(locationServiceIntent);
    }
}
