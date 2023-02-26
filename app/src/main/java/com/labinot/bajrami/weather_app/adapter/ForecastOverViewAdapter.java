package com.labinot.bajrami.weather_app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.activities.DailyForecastActivity;
import com.labinot.bajrami.weather_app.activities.DetailActivity;
import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;
import com.labinot.bajrami.weather_app.helper.WeatherHelper;

import java.util.ArrayList;


public class ForecastOverViewAdapter extends RecyclerView.Adapter<ForecastOverViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<WeatherHelper> days;
    private WeatherHelper weather_day;

    public ForecastOverViewAdapter(Context context, ArrayList<WeatherHelper> days) {
        this.context = context;
        this.days = days;
    }

    @NonNull
    @Override
    public ForecastOverViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View itemView;

        if(context instanceof DailyForecastActivity)

            itemView = LayoutInflater.from(context).inflate(R.layout.daily_card,parent,false);

        else
            itemView = LayoutInflater.from(context).inflate(R.layout.hourly_card,parent,false);


        return new ViewHolder(itemView,context);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ForecastOverViewAdapter.ViewHolder holder, int position) {

        weather_day = days.get(position);

        long timesTemp = weather_day.getTime();

        if(context instanceof DailyForecastActivity){

            String date = weather_day.convertTime(timesTemp,"dd-MMM");
            String dateDay = weather_day.convertTime(timesTemp,"EEEE");

            holder.forecast_TimeTextView.setText(context.getString(R.string.setForecastTime,date,dateDay));
            holder.forecast_sunrise.setText(context.getString(R.string.Sunrise,weather_day.convertTime(weather_day.getSunrise(),"HH:mm")));
            holder.forecast_sunset.setText(context.getString(R.string.Sunset,weather_day.convertTime(weather_day.getSunset(),"HH:mm")));

        }else{

            String hour = weather_day.convertTime(timesTemp, "HH:mm");
            String completeDate = weather_day.convertTime(timesTemp, "EEE, MMM dd");
            holder.forecast_TimeTextView.setText(hour);
            holder.forecast_date.setText(completeDate);

        }


        holder.forecast_desc_TextView.setText(weather_day.getDescription());
        holder.forecast_ImageView.setImageResource(weather_day.getWeatherIcon());
        holder.forecast_cardView.setCardBackgroundColor(context.getResources().getColor(weather_day.getWeatherColor()));

        boolean isMetric = MySharedPreferences.getNormalPref(context, Constants.list_unit_code_key,context.getString(R.string.metric)).contains(context.getString(R.string.metric));

        if(isMetric){

            holder.forecast_temp_TextView.setText(context.getString(R.string.setTempC,weather_day.getDaily_temp()));
            holder.forecast_feels_like.setText(context.getString(R.string.setFeelsLikeC,weather_day.getFeels_like()));


        }else{

            holder.forecast_temp_TextView.setText(context.getString(R.string.setTempF,weather_day.getDaily_temp()));
            holder.forecast_feels_like.setText(context.getString(R.string.setFeelsLikeF,weather_day.getFeels_like()));

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView transition_icon = v.findViewById(R.id.detail_main_icon);
                TextView transition_temp = v.findViewById(R.id.detail_main_temp);
                TextView transition_feels_like = v.findViewById(R.id.detail_feels_like);

                Pair<View, String> pair1 = Pair.create((View) transition_icon, context.getString(R.string.transition_card_icon));
                Pair<View, String> pair2 = Pair.create((View) transition_temp, context.getString(R.string.transition_temp));
                Pair<View, String> pair3 = Pair.create((View) transition_feels_like, context.getString(R.string.transition_feels_like));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context,
                        pair1,
                        pair2,
                        pair3);

                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra(Constants.DETAIL_VIEW_KEY,days.get(position));

                if(context instanceof DailyForecastActivity)
                    i.putExtra(Constants.DETAIL_COMING_ACT_KEY,Constants.DAILY_FORECAST);
                else
                    i.putExtra(Constants.DETAIL_COMING_ACT_KEY,Constants.HOURLY_FORECAST);

                context.startActivity(i,options.toBundle());

            }
        });



    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView forecast_TimeTextView;
        public TextView forecast_date;
        public TextView forecast_desc_TextView;
        public TextView forecast_temp_TextView;
        public TextView forecast_feels_like;
        public TextView forecast_sunrise;
        public TextView forecast_sunset;
        public ImageView forecast_ImageView;
        public CardView forecast_cardView;

        public ViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.forecast_cardView = itemView.findViewById(R.id.forecast_cardView);
            this.forecast_ImageView = itemView.findViewById(R.id.detail_main_icon);
            this.forecast_temp_TextView = itemView.findViewById(R.id.detail_main_temp);
            this.forecast_feels_like = itemView.findViewById(R.id.detail_feels_like);
            this.forecast_desc_TextView = itemView.findViewById(R.id.detail_description);
            this.forecast_TimeTextView = itemView.findViewById(R.id.detail_city_text);


            if(context instanceof DailyForecastActivity){

                this.forecast_sunrise = itemView.findViewById(R.id.forecast_sunrise);
                this.forecast_sunset = itemView.findViewById(R.id.forecast_sunset);

            }else{

                this.forecast_date = itemView.findViewById(R.id.forecast_date);
            }

        }
    }
}
