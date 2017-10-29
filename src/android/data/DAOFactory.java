package nl.dannisi.backgroundlocation.data;

import android.content.Context;
import nl.dannisi.backgroundlocation.data.sqlite.SQLiteConfigurationDAO;

public abstract class DAOFactory {
    public static ConfigurationDAO createConfigurationDAO(Context context) {
        return new SQLiteConfigurationDAO(context);
    }
}
