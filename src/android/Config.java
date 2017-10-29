package nl.dannisi.backgroundlocation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Bundle;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Config class
 */
public class Config implements Parcelable {

    private String url;
    private String params;
    private String headers;
    private int minDistanceMeters = 0;
    private int minTime = 60;
    private Boolean debug = false;
    private Boolean toasts = false;
    private Boolean restartAlreadyRunning = true;

    public Config () {
    }

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getUrl());
        out.writeString(getParams());
        out.writeString(getHeaders());
        out.writeInt(getMinDistanceMeters());
        out.writeInt(getMinTime());
        out.writeValue(getDebug());
        out.writeValue(getToasts());
        out.writeValue(getRestartAlreadyRunning());
    }

    public static final Parcelable.Creator<Config> CREATOR = new Parcelable.Creator<Config>() {
        public Config createFromParcel(Parcel in) {
            return new Config(in);
        }

        public Config[] newArray(int size) {
            return new Config[size];
        }
    };

    private Config(Parcel in) {
        setUrl(in.readString());
        setParams(in.readString());
        setHeaders(in.readString());
        setMinDistanceMeters(in.readInt());
        setMinTime(in.readInt());
        setDebug((Boolean) in.readValue(null));
        setToasts((Boolean) in.readValue(null));
        setRestartAlreadyRunning((Boolean) in.readValue(null));
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getHeaders() {
        return this.headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public Integer getMinDistanceMeters() {
        return this.minDistanceMeters;
    }

    public void setMinDistanceMeters(Integer minDistanceMeters) {
        this.minDistanceMeters = minDistanceMeters;
    }

    public Integer getMinTime() {
        return this.minTime;
    }

    public void setMinTime(Integer minTime) {
        this.minTime = minTime;
    }

    public Boolean getDebug() {
        return this.debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Boolean getToasts() {
        return this.toasts;
    }

    public void setToasts(Boolean toasts) {
        this.toasts = toasts;
    }

    public Boolean getRestartAlreadyRunning() {
        return this.restartAlreadyRunning;
    }

    public void setRestartAlreadyRunning(Boolean restartAlreadyRunning) {
        this.restartAlreadyRunning = restartAlreadyRunning;
    }

    @Override
    public String toString () {
        return new StringBuffer()
                .append("Config[url=").append(getUrl())
                .append(" params=").append(getParams())
                .append(" headers=").append(getHeaders())
                .append(" minDistanceMeters=").append(getMinDistanceMeters())
                .append(" minTime=").append(getMinTime())
                .append(" debug=").append(getDebug())
                .append(" toasts=").append(getToasts())
                .append(" restartAlreadyRunning=" ).append(getRestartAlreadyRunning())
                .append("]")
                .toString();
    }

    public Parcel toParcel () {
        Parcel parcel = Parcel.obtain();
        this.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static Config fromByteArray (byte[] byteArray) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(byteArray, 0, byteArray.length);
        parcel.setDataPosition(0);
        return Config.CREATOR.createFromParcel(parcel);
    }

    public static Config fromJSONObject (JSONObject jObject) throws JSONException {
        Config config = new Config();
        config.setUrl(jObject.optString("url", config.getUrl()));
        config.setParams(jObject.optString("params", config.getParams()));
        config.setHeaders(jObject.optString("headers", config.getHeaders()));
        config.setMinDistanceMeters(jObject.optInt("minDistanceMeters", config.getMinDistanceMeters()));
        config.setMinTime(jObject.optInt("minTime", config.getMinTime()));
        config.setDebug(jObject.optBoolean("debug", config.getDebug()));
        config.setToasts(jObject.optBoolean("toasts", config.getToasts()));
        config.setRestartAlreadyRunning(jObject.optBoolean("restartAlreadyRunning", config.getRestartAlreadyRunning()));

        return config;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("url", getUrl());
        json.put("params", getParams());
        json.put("headers", getHeaders());
        json.put("minDistanceMeters", getMinDistanceMeters());
        json.put("minTime", getMinTime());
        json.put("debug", getDebug());
        json.put("toasts", getToasts());
        json.put("restartAlreadyRunning", getRestartAlreadyRunning());

        return json;
  	}
}
