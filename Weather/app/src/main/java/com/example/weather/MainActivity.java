package com.example.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

//import android.location.LocationListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,LocationListener
{
   final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast";
   //final String URL_COORD = "?lat=";//28.6667&lon=77.4333";
   final String URL_UNITS = "&units=metric";
   final String URL_API_KEY = "&appid=3ed0dc7588850cc525a15bbf26fad9bf";

   private GoogleApiClient googleApiClient;
   private final int PERMISSION_LOCATION = 111;
   private ArrayList<DailyWeatherReport> weatherReportArrayList = new ArrayList<DailyWeatherReport>();

   private ImageView weatherIcon;
   private ImageView weatherIconMini;
   private TextView weatherDate;
   private TextView currentTemp;
   private TextView lowTemp;
   private TextView cityCountry;
   private TextView weatherDescription;

   weatherAdapter nAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);
        weatherIconMini = (ImageView)findViewById(R.id.weatherIconMini);
        weatherDate = (TextView)findViewById(R.id.weatherDate);
        currentTemp = (TextView)findViewById(R.id.currentTemp);
        lowTemp = (TextView)findViewById(R.id.lowTemp);
        cityCountry = (TextView)findViewById(R.id.cityCountry);
        weatherDescription = (TextView)findViewById(R.id.weatherDescription);


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_wether_reports);
        nAdapter = new weatherAdapter(weatherReportArrayList);
        recyclerView.setAdapter(nAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        googleApiClient  = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
    public void downloadWeatherData(Location location)
    {
        final String fullcoords  = "?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
        final String url = URL_BASE + fullcoords +URL_UNITS+ URL_API_KEY;

        //Log.v("fun","urlis:" +url);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            JSONObject city = response.getJSONObject("city");
                            String cityName = city.getString("name");
                            String country = city.getString("country");

                            JSONArray list = response.getJSONArray("list");

                            for(int x = 0;x<5;x++)
                            {
                                JSONObject obj = list.getJSONObject(x);
                                JSONObject main = obj.getJSONObject("main");
                                Double currentTemp = main.getDouble("temp");
                                Double maxTemp = main.getDouble("temp_max");
                                Double minTemp = main.getDouble("temp_min");

                                JSONArray weatherArray = obj.getJSONArray("weather");
                                JSONObject weather = weatherArray.getJSONObject(0);
                                String weatherType = weather.getString("main");

                                String rawDate = obj.getString("dt_txt");

                                DailyWeatherReport report = new DailyWeatherReport(cityName,country,currentTemp.intValue(),maxTemp.intValue()
                                        ,minTemp.intValue(),weatherType,rawDate);

                               // Log.v("Jason","Printing from class:- "+report.getWeather());

                                weatherReportArrayList.add(report);
                            }
                            Log.v("Json","Name:- "+cityName+" "+"Country:- "+country);
                        }
                        catch (JSONException e)
                        {
                            Log.v("JSON","Exc: "+e.getLocalizedMessage());
                        }
                        updateUi();
                        nAdapter.notifyDataSetChanged();
                        //updateUi();

                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Fun", "error:" + error.getLocalizedMessage());
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    public void updateUi()
    {
        if (weatherReportArrayList.size() > 0)
        {
            DailyWeatherReport report = weatherReportArrayList.get(0);
            switch (report.getWeather())
            {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rcloudy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rcloudy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    break;
                default:
                    weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                    weatherIconMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            }
            weatherDate.setText("Today" + " "+report.getFormattedDate());
            currentTemp.setText(Integer.toString(report.getCurrentTemp()));
            lowTemp.setText(Integer.toString(report.getMinTemp()));
            cityCountry.setText((report.getCityName() + " , " + report.getCountry()));
            weatherDescription.setText(report.getWeather());

        }
    }
    @Override
    public void onLocationChanged(Location location)
    {
        downloadWeatherData(location);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
        .PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION);
        }
        else
        {
            startLocationOnServices();
        }

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
    public void startLocationOnServices()
    {
        try
        {
            LocationRequest req = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,req,this);
        }
        catch (SecurityException exception) {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode)
        {
            case PERMISSION_LOCATION :
            {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startLocationOnServices();
                }
                else
                {
                    Toast.makeText(this,"Inavlid Location",Toast.LENGTH_LONG).show();
                }
            }
        }

    }
    public class weatherAdapter extends RecyclerView.Adapter<weatherReportViewHolder>
    {
        private ArrayList<DailyWeatherReport> mDailyWaetherReports;

        public weatherAdapter(ArrayList<DailyWeatherReport> mDailyWaetherReports) {
            this.mDailyWaetherReports = mDailyWaetherReports;
        }

        @Override
        public void onBindViewHolder(@NonNull weatherReportViewHolder holder, int position)
        {
            DailyWeatherReport report = mDailyWaetherReports.get(position);
            holder.updateUi(report);
        }

        @Override
        public int getItemCount() {
            return mDailyWaetherReports.size();
        }

        @NonNull
        @Override
        public weatherReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather,parent,false);
            return new weatherReportViewHolder(card);
        }
    }
    public class weatherReportViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView lweatherIcon;
        private TextView lweatherDate;
        private TextView lweatherDescription;
        private TextView ltemphigh;
        private TextView ltemplow;
        public weatherReportViewHolder(@NonNull View itemView)
        {
            super(itemView);
            lweatherIcon = (ImageView)itemView.findViewById(R.id.weathernewIcon);
            lweatherDate = (TextView)itemView.findViewById(R.id.weather_date);
            lweatherDescription = (TextView)itemView.findViewById(R.id.weather_description);
            ltemphigh = (TextView)itemView.findViewById(R.id.temp_high);
            ltemplow = (TextView)itemView.findViewById(R.id.temp_low);

        }
        public void updateUi(DailyWeatherReport report)
        {
            lweatherDate.setText(report.getFormattedDate());
            lweatherDescription.setText(report.getWeather());
            ltemphigh.setText(Integer.toString(report.getMaxTemp()));
            ltemplow.setText(Integer.toString(report.getMinTemp()));
            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rc));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                default:
                    lweatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
            }
        }
    }
}
