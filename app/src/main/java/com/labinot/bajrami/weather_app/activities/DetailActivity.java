package com.labinot.bajrami.weather_app.activities;

import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;
import com.labinot.bajrami.weather_app.helper.WeatherHelper;

public class DetailActivity extends BaseActivity {

    private WeatherHelper detail_object;
    private String comingFrom = "";

    private CollapsingToolbarLayout mCollapsingToolbar;
    private CardView detail_card_view;
    private LinearLayout sunrise_layout;
    private LinearLayout sunset_layout;

    //Card Text
    private TextView day_name_text;
    private TextView detail_city_text;
    private TextView detail_description;
    private TextView detail_main_temp;
    private TextView detail_feels_like;
    private TextView subtitle_txt;
    private ImageView detail_main_icon;

    //List Text
    private TextView sunriseText;
    private TextView sunsetText;
    private TextView windText;
    private TextView humidityText;
    private TextView pressureText;

    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setTheme(Theme);
        setContentView(R.layout.activity_detail);

        setUpToolbar("");
        setToolbarBackIcon();

        Bundle bundle = getIntent().getExtras();

        if(bundle !=null){
            detail_object = (WeatherHelper) getIntent().getSerializableExtra(Constants.DETAIL_VIEW_KEY);
            comingFrom = getIntent().getStringExtra(Constants.DETAIL_COMING_ACT_KEY);
        }


        
        loadViews();
        initData();
    }
    private void loadViews() {

        mCollapsingToolbar = findViewById(R.id.mCollapsingToolbar);

        detail_card_view = findViewById(R.id.detail_card_view);
        sunrise_layout = findViewById(R.id.sunrise_layout);
        sunset_layout = findViewById(R.id.sunset_layout);

        day_name_text = findViewById(R.id.day_name_text);
        detail_city_text = findViewById(R.id.detail_city_text);
        detail_description = findViewById(R.id.detail_description);
        detail_main_temp = findViewById(R.id.detail_main_temp);
        detail_feels_like = findViewById(R.id.detail_feels_like);
        detail_main_icon = findViewById(R.id.detail_main_icon);

        sunriseText = findViewById(R.id.sunriseText);
        sunsetText = findViewById(R.id.sunsetText);
        windText = findViewById(R.id.windText);
        humidityText = findViewById(R.id.humidityText);
        pressureText = findViewById(R.id.pressureText);
        subtitle_txt = findViewById(R.id.subtitle_txt);

    }

    private void initData() {

        unit = MySharedPreferences.getNormalPref(DetailActivity.this,Constants.list_unit_code_key,getString(R.string.metric));

        detail_card_view.setCardBackgroundColor(getResources().getColor(detail_object.getWeatherColor()));

        day_name_text.setText(detail_object.convertTime(detail_object.getTime(),"EEEE"));

        if(comingFrom.equals(Constants.DAILY_FORECAST)){

          toolbar.setTitle(detail_object.convertTime(detail_object.getTime(),"dd-MMM-yyyy"));
          sunriseText.setText(detail_object.convertTime(detail_object.getSunrise(),"HH:mm"));
          sunsetText.setText(detail_object.convertTime(detail_object.getSunset(),"HH:mm"));
          subtitle_txt.setVisibility(View.GONE);


        }else if(comingFrom.equals(Constants.HOURLY_FORECAST)){

            String time = detail_object.convertTime(detail_object.getTime(),"HH:mm");
            String date = detail_object.convertTime(detail_object.getTime(),"dd-MMM-yyyy");

            toolbar.setTitle(time);
            subtitle_txt.setVisibility(View.VISIBLE);
            subtitle_txt.setText(date);

            sunrise_layout.setVisibility(View.GONE);
            sunset_layout.setVisibility(View.GONE);

        }

        detail_city_text.setText(MySharedPreferences.getNormalPref(DetailActivity.this,Constants.edit_text_location_key,Constants.LOCATION_CITY_DEFAULT));
        detail_description.setText(detail_object.getDescription());
        detail_main_icon.setImageResource(detail_object.getWeatherIcon());

         if(unit.contains(getString(R.string.metric))){

             detail_main_temp.setText(getString(R.string.setTempC,detail_object.getDaily_temp()));
             detail_feels_like.setText(getString(R.string.setFeelsLikeC,detail_object.getFeels_like()));
             windText.setText(getString(R.string.setKMH,detail_object.getSpeed()));

         }else{
             detail_main_temp.setText(getString(R.string.setTempF,detail_object.getDaily_temp()));
             detail_feels_like.setText(getString(R.string.setFeelsLikeF,detail_object.getFeels_like()));
             windText.setText(getString(R.string.setMPH,detail_object.getSpeed()));

         }

         humidityText.setText(getString(R.string.setHUM,detail_object.getHumidity()));
         pressureText.setText(getString(R.string.setPRE,detail_object.getPressure()));

    }

}