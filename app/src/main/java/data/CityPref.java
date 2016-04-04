package data;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by ANTHONY on 04/04/2016.
 */
public class CityPref {
    SharedPreferences prefs;

    public CityPref(Activity activity) {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity() {
        return prefs.getString("city", "Lagos,NG");
    }

    public void setCity(String city) {
        prefs.edit().putString("city", city).commit();
    }
}
