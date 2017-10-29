package nl.dannisi.backgroundlocation.data;

import java.util.Date;
import java.util.Collection;

import org.json.JSONException;

import nl.dannisi.backgroundlocation.Config;

public interface ConfigurationDAO {
    public boolean persistConfiguration(Config config) throws NullPointerException;
    public Config retrieveConfiguration() throws JSONException;
}
