package nl.dannisi.backgroundlocation.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import nl.dannisi.backgroundlocation.Config;
import nl.dannisi.backgroundlocation.data.ConfigurationDAO;
import nl.dannisi.backgroundlocation.data.sqlite.SQLiteConfigurationContract.ConfigurationEntry;

public class SQLiteConfigurationDAO implements ConfigurationDAO {
    private static final String TAG = SQLiteConfigurationDAO.class.getName();

    private SQLiteDatabase db;

    public SQLiteConfigurationDAO(Context context) {
    SQLiteOpenHelper helper = SQLiteOpenHelper.getHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public SQLiteConfigurationDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public Config retrieveConfiguration() throws JSONException {
        Cursor cursor = null;

        String[] columns = {
            ConfigurationEntry._ID,
            ConfigurationEntry.COLUMN_NAME_URL,
            ConfigurationEntry.COLUMN_NAME_PARAMS,
            ConfigurationEntry.COLUMN_NAME_HEADERS,
            ConfigurationEntry.COLUMN_NAME_MIN_DISTANCE_METERS,
            ConfigurationEntry.COLUMN_NAME_MIN_TIME,
            ConfigurationEntry.COLUMN_NAME_DEBUG,
            ConfigurationEntry.COLUMN_NAME_TOASTS,
            ConfigurationEntry.COLUMN_NAME_RESTART_ALREADY_RUNNING
        };

        String whereClause = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = null;

        Config config = null;
        try {
            cursor = db.query(
                ConfigurationEntry.TABLE_NAME,  // The table to query
                columns,                   // The columns to return
                whereClause,               // The columns for the WHERE clause
                whereArgs,                 // The values for the WHERE clause
                groupBy,                   // don't group the rows
                having,                    // don't filter by row groups
                orderBy                    // The sort order
            );
            if (cursor.moveToFirst()) {
                config = hydrate(cursor);
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return config;
    }

    public boolean persistConfiguration(Config config) throws NullPointerException {
        long rowId = db.replace(ConfigurationEntry.TABLE_NAME, ConfigurationEntry.COLUMN_NAME_NULLABLE, getContentValues(config));
        Log.d(TAG, "Configuration persisted with rowId = " + rowId);
        if (rowId > -1) {
            return true;
        } else {
            return false;
        }
    }

    private Config hydrate(Cursor c) throws JSONException {
        Config config = new Config();
        config.setUrl(c.getString(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_URL)));
        config.setParams(c.getString(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_PARAMS)));
        config.setHeaders(c.getString(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_HEADERS)));
        config.setMinDistanceMeters(c.getInt(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_MIN_DISTANCE_METERS)));
        config.setMinTime(c.getInt(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_MIN_TIME)));
        config.setDebug((c.getInt(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_DEBUG)) == 1) ? true : false);
        config.setToasts((c.getInt(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_TOASTS)) == 1) ? true : false);
        config.setRestartAlreadyRunning((c.getInt(c.getColumnIndex(ConfigurationEntry.COLUMN_NAME_RESTART_ALREADY_RUNNING)) == 1) ? true : false);

        return config;
    }

    private ContentValues getContentValues(Config config) throws NullPointerException {
        ContentValues values = new ContentValues();
        values.put(ConfigurationEntry._ID, 1);
        values.put(ConfigurationEntry.COLUMN_NAME_URL, config.getUrl());
        values.put(ConfigurationEntry.COLUMN_NAME_PARAMS, config.getParams());
        values.put(ConfigurationEntry.COLUMN_NAME_HEADERS, config.getHeaders());
        values.put(ConfigurationEntry.COLUMN_NAME_MIN_DISTANCE_METERS, config.getMinDistanceMeters());
        values.put(ConfigurationEntry.COLUMN_NAME_MIN_TIME, config.getMinTime());
        values.put(ConfigurationEntry.COLUMN_NAME_DEBUG, (config.getDebug() == true) ? 1 : 0);
        values.put(ConfigurationEntry.COLUMN_NAME_TOASTS, (config.getToasts() == true) ? 1 : 0);
        values.put(ConfigurationEntry.COLUMN_NAME_RESTART_ALREADY_RUNNING, (config.getRestartAlreadyRunning() == true) ? 1 : 0);

        return values;
    }
}