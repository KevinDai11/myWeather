package com.example.myweather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class weatherData {
    private String mTemp,mCity,mWeatherType;
    private Bitmap mIcon;
    private int mCondition;

    public static weatherData fromJson(JSONObject jsonObject){
        try{
            weatherData WD = new weatherData();
            WD.mCity = jsonObject.getString("name");
            WD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            WD.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");

            WD.mIcon=updateWeatherIcon(jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
            double temp = (jsonObject.getJSONObject("main").getDouble("temp")-273.15)*9/5+32;
            int rounded = (int)Math.rint(temp);
            WD.mTemp = Integer.toString(rounded);
            return WD;
        }
        catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap updateWeatherIcon(String s) throws MalformedURLException {
        try{

            String url = "http://openweathermap.org/img/wn/"+s+"@4x.png";
            Log.d("works",url);
            URL n = new URL(url);
            Bitmap mI = BitmapFactory.decodeStream(n.openConnection().getInputStream());
            return mI;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getmTemp() {
        return mTemp+"℉";
    }

    public Bitmap getmIcon() {
        return mIcon;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
