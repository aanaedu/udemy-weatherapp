package com.wytelyte.theweatherapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import data.CityPref;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;
import util.Utils;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    Weather weather = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (TextView) findViewById(R.id.city_text);
        iconView = (ImageView) findViewById(R.id.thumnail_icon);
        temp = (TextView) findViewById(R.id.temp_text);
        description = (TextView) findViewById(R.id.cloud_text);
        humidity = (TextView) findViewById(R.id.humid_text);
        pressure = (TextView) findViewById(R.id.pressure_text);
        wind = (TextView) findViewById(R.id.wind_text);
        sunrise = (TextView) findViewById(R.id.rise_text);
        sunset = (TextView) findViewById(R.id.set_text);
        updated = (TextView) findViewById(R.id.update_text);

        CityPref cityPref = new CityPref(MainActivity.this);

        renderWeatherData(cityPref.getCity());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.change_city){
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void renderWeatherData(String city) {
        WeatherTask weatherTask = new WeatherTask();

        String API_PARAMS = "&APPID={insert your key here}&units=metric";
        weatherTask.execute(city + API_PARAMS);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        URL url;
        HttpURLConnection urlConnection = null;
        InputStream is;

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
        }

        private Bitmap downloadImage(String iconCode) {
            try {
                url = new URL(Utils.ICON_URL + iconCode + ".png");
//                url = new URL(Utils.ICON_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();

                if(statusCode != HttpsURLConnection.HTTP_OK){
                    Log.e("DownloadImage", "Error" + statusCode + urlConnection.getResponseMessage());
                    return null;
                }

                is = new BufferedInputStream(urlConnection.getInputStream());

                // Decode contents from the InputStream
                return BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        } // end downloadImage

    }
    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {

            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                if (weather != null) {
                    Log.v("Data: ", weather.location.getCity() + " - " + weather.currentCondition.getDescription());
                    weather.iconData = weather.currentCondition.getIcon();
                }
            } catch (NullPointerException e){
              e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            // fetch icon image
            if(weather.iconData != null){
                new DownloadImageTask().execute(weather.iconData);
            }

            DateFormat dateFormat = DateFormat.getTimeInstance();

            String sunriseDate = dateFormat.format(new Date(weather.location.getSunrise()));
            String sunsetDate = dateFormat.format(new Date(weather.location.getSunset()));
            String lastUpdatedDate = dateFormat.format(new Date(weather.location.getLastUpdate()));

            DecimalFormat decimalFormat = new DecimalFormat("0.0");

            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());

            cityName.setText(weather.location.getCity() + "," + weather.location.getCountry());
            temp.setText("" + tempFormat + "°C");
            description.setText("Condition: " + weather.currentCondition.getCondition() + " (" + weather.currentCondition.getDescription() + ")");
            pressure.setText("Pressure: " + weather.currentCondition.getPressure() + "hPa");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            wind.setText("Wind: " + weather.wind.getSpeed() + "mps");
            sunrise.setText("Sunrise: " + sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last Updated: " + lastUpdatedDate);

        }
    }

    private void showInputDialog(){
        AlertDialog.Builder  builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");

        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Lagos,NG");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPref cityPref = new CityPref(MainActivity.this);
                cityPref.setCity(cityInput.getText().toString().trim());

                String newCity = cityPref.getCity();

                renderWeatherData(newCity);
            }
        });
        builder.show();
    }
}
