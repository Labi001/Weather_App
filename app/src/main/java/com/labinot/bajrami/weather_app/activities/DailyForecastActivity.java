package com.labinot.bajrami.weather_app.activities;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.Animator;
import android.os.Bundle;
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

public class DailyForecastActivity extends BaseActivity {

    private RecyclerView recyclerViewDays;

    public ArrayList<WeatherHelper> items;
    private NestedScrollView error_layout;
    private String latitude,longitude,unit,language;
    private int days;
    private WeatherHelper mWeatherHelper;
    private ForecastOverViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);

        setupTheme();
        setTheme(Theme);

        setContentView(R.layout.activity_daily_forecast_layout);

        setUpToolbar(getString(R.string.seven_days_weather));
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
        days = Integer.parseInt(MySharedPreferences.getNormalPref(this,Constants.list_days_key,"8"));
        language = "en";

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(AsyncHelper.checkDailyWeather(latitude, longitude, unit, language), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                if(!data.equals(""))
                    parseDailyData(data);

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

    private void parseDailyData(String data) {

        JSONObject reader;

        try {

            reader = new JSONObject(data);

            JSONArray day_list_array = reader.getJSONArray("daily");

          //  Log.d(LOG_TAG, "Day count - " + day_list_array.length());

            for (int i = 1; i < day_list_array.length() && i < days; i++) {

                mWeatherHelper = new WeatherHelper();

                JSONObject day_list_object = day_list_array.getJSONObject(i);

                mWeatherHelper.setTime(day_list_object.getLong("dt"));

                mWeatherHelper.setSunrise(day_list_object.getInt("sunrise"));
                mWeatherHelper.setSunset(day_list_object.getInt("sunset"));

                mWeatherHelper.setPressure(day_list_object.getDouble("pressure"));
                mWeatherHelper.setHumidity(day_list_object.getInt("humidity"));

                mWeatherHelper.setSpeed(day_list_object.getDouble("wind_speed"));

                JSONObject temp = day_list_object.getJSONObject("temp");
                mWeatherHelper.setDaily_temp(temp.getDouble("day"));

                JSONObject feels_like = day_list_object.getJSONObject("feels_like");
                mWeatherHelper.setFeels_like(feels_like.getDouble("day"));

                JSONArray weather = day_list_object.getJSONArray("weather");
                JSONObject JSONWeather = weather.getJSONObject(0);
                mWeatherHelper.setWeather_id(JSONWeather.getInt("id"));
                mWeatherHelper.setDescription(JSONWeather.getString("description"));
                mWeatherHelper.setWeatherColor(JSONWeather.getInt("id"));
                mWeatherHelper.setWeatherIcon(JSONWeather.getString("icon"));

                items.add(mWeatherHelper);
            }

            adapter.notifyDataSetChanged();

            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);

            recyclerViewDays.setAlpha(0);

            recyclerViewDays.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    recyclerViewDays.setVisibility(View.VISIBLE);

                    if(error_layout.getVisibility() == View.VISIBLE)
                        error_layout.setVisibility(View.GONE);
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

        }catch (JSONException e){
            e.printStackTrace();
        }


    }
}