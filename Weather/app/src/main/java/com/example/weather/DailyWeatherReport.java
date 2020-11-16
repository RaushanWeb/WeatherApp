package com.example.weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyWeatherReport
{
    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";



    private String cityName;
    private String country;

    public String getCityName()
    {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public int getCurrentTemp()
    {
        return currentTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public String getWeather() {
        return weather;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    private int currentTemp;
    private int maxTemp;
    private int minTemp;
    private String weather;
    private String formattedDate;

    public DailyWeatherReport(String cityName, String country, int currentTemp, int maxTemp, int minTemp, String weather, String formattedDate)
    {
        this.cityName = cityName;
        this.country = country;
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weather = weather;
        //this.formattedDate = rawDateToPretty(formattedDate);
        this.formattedDate = formattedDate;
    }

   /* public String rawDateToPretty(String rawDate)
    {
        //convert Raw date into formated date
        //String s = new SimpleDateFormat("EEE,MMM d",Locale.getDefault()).format(new Date());
        //rawDate = s;
        return rawDate;
    }*/
}
