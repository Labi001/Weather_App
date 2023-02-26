package com.labinot.bajrami.weather_app.database;

import static com.labinot.bajrami.weather_app.database.DataBaseHelper.DATABASE_HELPER_LOG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.labinot.bajrami.weather_app.activities.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadDataBase {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final AppCompatActivity mAppCompatActivity;

    public LoadDataBase(AppCompatActivity mAppCompatActivity) {
        this.mAppCompatActivity = mAppCompatActivity;
    }


    public void doInBackground() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                DataBaseHelper dataBaseHelper = new DataBaseHelper(mAppCompatActivity);

               dataBaseHelper.createDatabase();

                dataBaseHelper.close();

                Log.d(DATABASE_HELPER_LOG, "doInBackground - finished");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) mAppCompatActivity).openDatabase();
                    }
                });
            }
        });
    }

}
