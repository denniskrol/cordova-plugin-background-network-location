package nl.dannisi.backgroundlocation.data.sqlite;

import android.provider.BaseColumns;

public final class SQLiteConfigurationContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SQLiteConfigurationContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ConfigurationEntry implements BaseColumns {
        public static final String TABLE_NAME = "configuration";
        public static final String COLUMN_NAME_NULLABLE = "NULLHACK";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PARAMS = "params";
        public static final String COLUMN_NAME_HEADERS = "headers";
        public static final String COLUMN_NAME_MIN_DISTANCE_METERS = "min_distance_meters";
        public static final String COLUMN_NAME_MIN_TIME = "min_time";
        public static final String COLUMN_NAME_DEBUG = "debug";
        public static final String COLUMN_NAME_TOASTS = "toasts";
        public static final String COLUMN_NAME_RESTART_ALREADY_RUNNING = "restart_already_running";
    }
}
