package com.labinot.bajrami.weather_app.helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPS_Tracker extends Service implements LocationListener {

    private Context context;
    public static final String GPS_TRACKER_LOG = "GPSTrackerLog";
    private LocationInterface locationInterface;
    boolean isNetworkEnabled = false;
    protected LocationManager mLocationManager;
    private Location location;
    double latitude,longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;

    public GPS_Tracker(Context context, LocationInterface locationInterface) {
        this.context = context;
        this.locationInterface = locationInterface;
    }

    public boolean getLocation(){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {

            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(GPS_TRACKER_LOG, "isNetworkEnabled - " + isNetworkEnabled);

            if (isNetworkEnabled) {

                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                );

                location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {

                    Log.d(GPS_TRACKER_LOG, "Location - " + location.getLatitude());
                    Log.d(GPS_TRACKER_LOG, "mLocationManager - " + mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude());

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    locationInterface.autoLocationInfo(latitude, longitude);

                }

                return true;

            } else return false;

        }


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        if(latitude != 0 && longitude != 0)
            locationInterface.autoLocationInfo(latitude,longitude);

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

        if(mLocationManager != null)
            mLocationManager.removeUpdates(GPS_Tracker.this);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface LocationInterface{
        void autoLocationInfo(double latitude,double longitude);
    }

}
