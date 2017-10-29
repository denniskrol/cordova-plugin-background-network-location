package nl.dannisi.backgroundlocation.data.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import nl.dannisi.backgroundlocation.data.sqlite.SQLiteConfigurationContract.ConfigurationEntry;

import java.util.ArrayList;

public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {
    private static final String TAG = SQLiteOpenHelper.class.getName();
    public static final String SQLITE_DATABASE_NAME = "cordova_bg_geolocation.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CONFIG_TABLE =
        "CREATE TABLE " + ConfigurationEntry.TABLE_NAME + " (" +
        ConfigurationEntry._ID + " INTEGER PRIMARY KEY," +
        ConfigurationEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_PARAMS + TEXT_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_HEADERS + TEXT_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_MIN_DISTANCE_METERS + INTEGER_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_MIN_TIME + INTEGER_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_DEBUG + INTEGER_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_TOASTS + INTEGER_TYPE + COMMA_SEP +
        ConfigurationEntry.COLUMN_NAME_RESTART_ALREADY_RUNNING + INTEGER_TYPE +
        " )";

    private static final String SQL_DROP_CONFIG_TABLE = "DROP TABLE IF EXISTS " + ConfigurationEntry.TABLE_NAME;

    private static SQLiteOpenHelper instance;

    /**
     * Get SqliteOpenHelper instance (singleton)
     *
     * Use the application context, which will ensure that you
     * don't accidentally leak an Activity's context.
     * See this article for more information: http://bit.ly/6LRzfx
     *
     * @param context
     * @return
     */
    public static synchronized SQLiteOpenHelper getHelper(Context context) {
        if (instance == null)
            instance = new SQLiteOpenHelper(context);

        return instance;
    }

    /**
     * Constructor
     *
     * NOTE: Intended to use only for testing purposes.
     * Use factory method getHelper instead.
     *
     * @param context
     */
    public SQLiteOpenHelper(Context context) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating db: " + this.getDatabaseName());
        execAndLogSql(db, SQL_CREATE_CONFIG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(this.getClass().getName(), "Upgrading database oldVersion: " + oldVersion + " newVersion: " + newVersion);
        Log.d(this.getClass().getName(), "Unsupported for now");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we don't support db downgrade yet, instead we drop table and start over
        execAndLogSql(db, SQL_DROP_CONFIG_TABLE);
        onCreate(db);
    }

    public void execAndLogSql(SQLiteDatabase db, String sql) {
        Log.d(TAG, sql);
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.e(TAG, "Error executing sql: " + e.getMessage());
        }
    }
}
