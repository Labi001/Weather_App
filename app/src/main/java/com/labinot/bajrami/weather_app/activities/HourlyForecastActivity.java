package com.labinot.bajrami.weather_app.activities;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.adapter.ForecastOverViewAdapter;
import com.labinot.bajrami.weather_app.helper.AsyncHelper;
import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;
import com.labinot.bajrami.weather_app.helper.WeatherHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HourlyForecastActivity extends BaseActivity {

    public static final String LOG_TAG = "HourlyForecastLog";
    private RecyclerView recyclerViewDays;

    public ArrayList<WeatherHelper> items;
    private NestedScrollView error_layout;
    private String latitude,longitude,unit,language;
    private WeatherHelper mWeatherHelper;
    private ForecastOverViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        setupTheme();
        setTheme(Theme);

        setContentView(R.layout.activity_hourly_forecast);

        setUpToolbar(getString(R.string.hourly_weather));
        setToolbarBackIcon();

        swipeRefreshLayout = findViewById(R.id.mSwipeRefreshLayout);
        setupSwipe();
        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        error_layout = findViewById(R.id.error);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                items.clear();

                recyclerViewDays.setAlpha(1);

                recyclerViewDays.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recyclerViewDays.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).setDuration(250).alpha(0);
                downloadJSON();
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        downloadJSON();

        items = new ArrayList<>();
        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForecastOverViewAdapter(this,items);
        recyclerViewDays.setAdapter(adapter);


    }

    private void downloadJSON() {

        latitude = MySharedPreferences.getNormalPref(this, Constants.latitude_pref_key,"0");
        longitude = MySharedPreferences.getNormalPref(this,Constants.longitude_pref_key,"0");
        unit = MySharedPreferences.getNormalPref(this,Constants.list_unit_code_key,getString(R.string.metric));
        language = "en";

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(AsyncHelper.checkHourlyWeather(latitude, longitude, unit, language), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                if(!data.equals(""))
                    parseHourlyData(data);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

                recyclerViewDays.setVisibility(View.GONE);
                error_layout.setVisibility(View.VISIBLE);

            }
        });

    }

    private void parseHourlyData(String data) {

        JSONObject reader;

        try {

            reader = new JSONObject(data);

            JSONArray hour_list_array = reader.getJSONArray("hourly");

           // Log.d(LOG_TAG, "Hour count - " + hour_list_array.length());

            for (int i = 1; i < hour_list_array.length(); i++) {

                mWeatherHelper = new WeatherHelper();

                JSONObject hour_list_object = hour_list_array.getJSONObject(i);

                mWeatherHelper.setTime(hour_list_object.getLong("dt"));

                mWeatherHelper.setPressure(hour_list_object.getDouble("pressure"));

                mWeatherHelper.setHumidity(hour_list_object.getInt("humidity"));

                mWeatherHelper.setSpeed(hour_list_object.getDouble("wind_speed"));

                mWeatherHelper.setDaily_temp(hour_list_object.getDouble("temp"));

                mWeatherHelper.setFeels_like(hour_list_object.getDouble("feels_like"));

                JSONArray weather = hour_list_object.getJSONArray("weather");
                JSONObject JSONWeather = weather.getJSONObject(0);
                mWeatherHelper.setWeather_id(JSONWeather.getInt("id"));
                mWeatherHelper.setDescription(JSONWeather.getString("description"));
                mWeatherHelper.setWeatherColor(JSONWeather.getInt("id"));
                mWeatherHelper.setWeatherIcon(JSONWeather.getString("icon"));

                items.add(mWeatherHelper);

            }

            adapter.notifyDataSetChanged();

            if (swipeRefreshLayout.isRefreshing())
               swipeRefreshLayout.setRefreshing(false);

                recyclerViewDays.setAlpha(0);
                recyclerViewDays.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        recyclerViewDays.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).setDuration(250).alpha(1);


        } catch (JSONException e) {

            Log.e(LOG_TAG, "JSONException", e);
        }



    }
}