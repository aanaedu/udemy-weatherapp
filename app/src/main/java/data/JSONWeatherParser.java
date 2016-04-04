package data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Location;
import model.Weather;
import util.Utils;

/**
 * Created by ANTHONY on 29/03/2016.
 */
public class JSONWeatherParser {
    public static Weather getWeather(String data){
        Weather weather = new Weather();
        Location location;
        // Create JsonObject from data
        try {
            JSONObject jsonObject = new JSONObject(data);

            location = new Location();

            JSONObject coorObj = Utils.getObject("coord", jsonObject);

            location.setLat(Utils.getFloat("lat", coorObj));
            location.setLon(Utils.getFloat("lon", coorObj));

            JSONObject mainObj = Utils.getObject("main", jsonObject);

            // Set the weather info
            JSONArray jsonWeatherArray = jsonObject.getJSONArray("weather");
            JSONObject jsonWeather = jsonWeatherArray.getJSONObject(0);

            // Set CurrentCondition
            weather.currentCondition.setWeatherId(Utils.getInt("id", jsonWeather));
            weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
            weather.currentCondition.setDescription(Utils.getString("description", jsonWeather));
            weather.currentCondition.setIcon(Utils.getString("icon", jsonWeather));
            weather.currentCondition.setPressure(Utils.getFloat("pressure", mainObj));
            weather.currentCondition.setHumidity(Utils.getFloat("humidity", mainObj));
            weather.currentCondition.setMinTemp(Utils.getFloat("temp_min", mainObj));
            weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainObj));
            weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));

            JSONObject windObj = Utils.getObject("wind", jsonObject);
            weather.wind.setSpeed(Utils.getFloat("speed", windObj));
            weather.wind.setDeg(Utils.getFloat("deg", windObj));

            JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
            weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));

            location.setLastUpdate(Utils.getInt("dt", jsonObject));

            JSONObject sysObj = Utils.getObject("sys", jsonObject);


            location.setCountry(Utils.getString("country", sysObj));
            location.setSunrise(Utils.getInt("sunrise", sysObj));
            location.setSunrise(Utils.getInt("sunset", sysObj));
            weather.location = location;


            location.setCity(Utils.getString("name", jsonObject));

            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("Data: ", weather.location.getCity() + " - " + weather.currentCondition.getDescription());
        return null;
    }
}
