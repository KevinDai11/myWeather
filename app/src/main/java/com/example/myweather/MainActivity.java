package com.example.myweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    final String APP_API_ID = "6812ace775b56edd35bce1b6bab09610";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView cityName, weatherCondition, temp;
    ImageView weatherIcon, reloadIcon;
    RelativeLayout cityFinder;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.CityName);
        weatherCondition = findViewById(R.id.weatherCondition);
        temp = findViewById(R.id.temp);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityFinder = findViewById(R.id.cityFinder);
        reloadIcon = findViewById(R.id.reload);

        cityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cityF = new Intent(MainActivity.this, cityFinderActivity.class);
                startActivity(cityF);
            }
        });
        reloadIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onResume();
            }
        });

        StrictMode.ThreadPolicy policy =new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

/*    @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String city = intent.getStringExtra("City");
        Toast.makeText(MainActivity.this,"loading",Toast.LENGTH_SHORT).show();
        if(city!=null){
            getWeatherForNewLocation(city);
        }
        else{
            getWeatherForCurrentLocation();
        }
    }
    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_API_ID);
                networking(params);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(MainActivity.this, "Location Update Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(MainActivity.this, "Location Update Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }
    private void getWeatherForNewLocation(String city){
        RequestParams params= new RequestParams();
        params.put("q",city.toLowerCase());
        params.put("appid",APP_API_ID);
        networking(params);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void networking(RequestParams p){
        AsyncHttpClient client= new AsyncHttpClient();

        client.get(WEATHER_URL,p,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                weatherData WD = weatherData.fromJson(response);
                updateUI(WD);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, "Failed...Try Again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(weatherData w){

        temp.setText(w.getmTemp());
        cityName.setText(w.getmCity());
        weatherCondition.setText(w.getmWeatherType());
        weatherIcon.setImageBitmap(w.getmIcon());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
