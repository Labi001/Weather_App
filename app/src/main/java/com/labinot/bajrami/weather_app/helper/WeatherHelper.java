package com.labinot.bajrami.weather_app.helper;

import android.annotation.SuppressLint;

import com.labinot.bajrami.weather_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherHelper implements Serializable {

    private Integer weatherIcon = R.drawable.ic_clear_sky_day;
    private Integer weatherColor = R.color.sunny_weather;
    private String city,country,description,desc_tomorrow,formatterDate;
    private Integer weather_id,sunrise,sunset,humidity,tomorrow_weather_id;
    private Double current_temp,daily_temp,feels_like,speed,pressure,tomorrow_temp;
    double longitude,latitude;
    private long time;



    public Integer getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String ID) {

        switch (ID){

            case "01d":
                this.weatherIcon = R.drawable.ic_clear_sky_day;
                break;
            case "02d":
                this.weatherIcon = R.drawable.ic_few_clouds_day;
                break;
            case "03d":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "04d":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "09d":
                this.weatherIcon = R.drawable.ic_shower_rain;
                break;
            case "10d":
                this.weatherIcon = R.drawable.ic_rain_day;
                break;
            case "11d":
                this.weatherIcon = R.drawable.ic_thunderstorm;
                break;
            case "13d":
                this.weatherIcon = R.drawable.ic_snowing;
                break;
            case "50d":
                this.weatherIcon = R.drawable.ic_mist;
                break;
            case "01n":
                this.weatherIcon = R.drawable.ic_clear_sky_night;
                break;
            case "02n":
                this.weatherIcon = R.drawable.ic_few_clouds_night;
                break;
            case "03n":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "04n":
                this.weatherIcon = R.drawable.ic_scattered_clouds;
                break;
            case "09n":
                this.weatherIcon = R.drawable.ic_shower_rain;
                break;
            case "10n":
                this.weatherIcon = R.drawable.ic_rain_night;
                break;
            case "11n":
                this.weatherIcon = R.drawable.ic_thunderstorm;
                break;
            case "13n":
                this.weatherIcon = R.drawable.ic_snowing;
                break;
            case "50n":
                this.weatherIcon = R.drawable.ic_mist;
                break;
            default:
                this.weatherIcon = R.drawable.ic_clear_sky_day;
                break;


        }
    }

    public Integer getWeatherColor() {
        return weatherColor;
    }

    public void setWeatherColor(Integer ID) {

        switch (ID){

            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                this.weatherColor = R.color.sunny_weather;
                break;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:

                this.weatherColor = R.color.bad_weather;
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:

                this.weatherColor = R.color.rainy_weather;
                break;
            case 600:
            case 601:
            case 602:
            case 611:
            case 612:
            case 615:
            case 616:
            case 620:
            case 621:
            case 622:

                this.weatherColor = R.color.snow_weather;
                break;
            case 700:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:

                this.weatherColor = R.color.warning_Weather;
                break;
            case 800:

                this.weatherColor = R.color.clear_sky_color;
                break;
            case 801:

                this.weatherColor = R.color.rainy_weather;
                break;
            case 802:
            case 803:
            case 804:

                this.weatherColor = R.color.good_weather;
                break;
            case 900:
            case 901:
            case 902:
            case 903:
            case 904:
            case 905:
            case 906:

                this.weatherColor = R.color.warning_Weather;
                break;
            default:

                this.weatherColor = R.color.sunny_weather;
                break;

        }

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesc_tomorrow() {
        return desc_tomorrow;
    }

    public void setDesc_tomorrow(String desc_tomorrow) {
        this.desc_tomorrow = desc_tomorrow;
    }

    public String getFormatterDate() {
        return formatterDate;
    }

    public void setFormatterDate(String formatterDate) {
        this.formatterDate = formatterDate;
    }

    public Integer getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(Integer weather_id) {
        this.weather_id = weather_id;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }

    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getTomorrow_weather_id() {
        return tomorrow_weather_id;
    }

    public void setTomorrow_weather_id(Integer tomorrow_weather_id) {
        this.tomorrow_weather_id = tomorrow_weather_id;
    }

    public Double getCurrent_temp() {
        return current_temp;
    }

    public void setCurrent_temp(Double current_temp) {
        this.current_temp = current_temp;
    }

    public Double getDaily_temp() {
        return daily_temp;
    }

    public void setDaily_temp(Double daily_temp) {
        this.daily_temp = daily_temp;
    }

    public Double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Double feels_like) {
        this.feels_like = feels_like;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getTomorrow_temp() {
        return tomorrow_temp;
    }

    public void setTomorrow_temp(Double tomorrow_temp) {
        this.tomorrow_temp = tomorrow_temp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void ParseTodayData(String data) {

        try {
            JSONObject reader = new JSONObject(data);

            JSONObject coordinates = reader.getJSONObject("coord");
            setLongitude(coordinates.getDouble("lon"));
            setLatitude(coordinates.getDouble("lat"));

            String city = reader.getString("name");
            setCity(city);

            JSONArray weatherList = reader.getJSONArray("weather");
            JSONObject weatherListObject = weatherList.getJSONObject(0);

            setWeather_id(weatherListObject.getInt("id"));

            setWeatherColor(weatherListObject.getInt("id"));// Here we set card color for today
            setWeatherIcon(weatherListObject.getString("icon"));// Here we set card icon for today

            setDescription(weatherListObject.getString("description"));

            JSONObject sys = reader.getJSONObject("sys");
            setCountry(sys.getString("country"));
            setSunrise(sys.getInt("sunrise"));
            setSunset(sys.getInt("sunset"));

            JSONObject temp = reader.getJSONObject("main");

            setCurrent_temp(temp.getDouble("temp"));
            setFeels_like(temp.getDouble("feels_like"));
            setHumidity(temp.getInt("humidity"));
            setPressure(temp.getDouble("pressure"));

            JSONObject wind = reader.getJSONObject("wind");
            setSpeed(wind.getDouble("speed"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void ParseTomorrowData(String data) {

        try {
            JSONObject reader = new JSONObject(data);
            JSONArray list = reader.getJSONArray("list");

            // Tomorrow Stats
            JSONObject tomorrowJSONList = list.getJSONObject(7);
            JSONObject tomorrowTemp = tomorrowJSONList.getJSONObject("main");
            setTomorrow_temp(tomorrowTemp.getDouble("temp"));

            JSONArray tomorrowWeather = tomorrowJSONList.getJSONArray("weather");
            JSONObject tomorrowJSONWeather = tomorrowWeather.getJSONObject(0);

            setTomorrow_weather_id(tomorrowJSONWeather.getInt("id"));
            setDesc_tomorrow(tomorrowJSONWeather.getString("description"));

            setWeatherColor(tomorrowJSONWeather.getInt("id"));// here we set card color for today
            setWeatherIcon(tomorrowJSONWeather.getString("icon"));// here we set card icon for today


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @SuppressLint("SimpleDateFormat")
    public String convertTime(long yourLongTime, String yourFormat) {
        Date time = new Date(yourLongTime * 1000);
        return new SimpleDateFormat(yourFormat).format(time);
    }
}
