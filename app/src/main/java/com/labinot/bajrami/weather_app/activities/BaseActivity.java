package com.labinot.bajrami.weather_app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.labinot.bajrami.weather_app.BuildConfig;
import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.about.About;
import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;
import com.labinot.bajrami.weather_app.helper.WeatherHelper;

public class BaseActivity extends AppCompatActivity {

  public Toolbar toolbar;
  private Handler handler = new Handler(Looper.myLooper());
  public DrawerLayout drawerLayout;
  public NavigationView navigationView;
  private MaterialAlertDialogBuilder changeLogDialog;
    public SwipeRefreshLayout swipeRefreshLayout;
    public int Theme;
  private ConstraintLayout navigation_draw_bag;
    public WeatherHelper weatherHelper;
  private ImageView nav_img;
  private TextView city_id;
  public TabLayout bottomTabLayout;

    public void setupTheme() {

        switch (Integer.parseInt(MySharedPreferences.getNormalPref(BaseActivity.this,Constants.list_theme_key,Constants.THEME_DEFAULT_VALUE))) {
            case 1:
                Theme = R.style.GreenTheme;
                break;
            case 2:
                Theme = R.style.RedTheme;
                break;
            case 3:
                Theme = R.style.PurpleTheme;
                break;

            case 5:
                Theme = R.style.DarkTheme;
                break;
        }

    }


  public void setUpToolbar(){

      toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);


  }

    public void setUpToolbar(String message){

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(message);
        setSupportActionBar(toolbar);


    }

    public void setupSwipe(){

        swipeRefreshLayout.setColorSchemeColors(MaterialColors.getColor(this,R.attr.swipeRefreshProgressColor,Color.BLACK));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(MaterialColors.getColor(this,R.attr.swipeRefreshBackgroundColor,Color.BLACK));

    };

    public void setToolbarBackIcon(){

      toolbar.setNavigationIcon(R.drawable.ic_back);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              onBackPressed();
          }

      });


    }

    public void setupBottomBar(){

      bottomTabLayout.addTab(bottomTabLayout.newTab().setText(getString(R.string.metric)).setIcon(R.drawable.ic_celsius));
      bottomTabLayout.addTab(bottomTabLayout.newTab().setText(getString(R.string.imperial)).setIcon(R.drawable.ic_kelvin));

//      bottomTabLayout.setBackgroundColor(getResources().getColor(R.color.blue_primary));
//      bottomTabLayout.setTabRippleColor(ColorStateList.valueOf(Color.WHITE));

      bottomTabLayout.setTabTextColors(MaterialColors.getColor(this,R.attr.bottomBarText,Color.BLACK),Color.WHITE);

      bottomTabLayout.setTabIconTint(new ColorStateList(

              new int[][]{

                      new int[]{android.R.attr.state_selected},
                      new int[]{android.R.attr.state_enabled}

              },

              new int[]{
                   Color.WHITE,
                   MaterialColors.getColor(this,R.attr.bottomBarIcon,Color.BLACK)

              }

      ));

    }

    public void setVisibilityToolbar(boolean isVisible) {
        if (isVisible)
            toolbar.setVisibility(View.VISIBLE);
        else
           toolbar.setVisibility(View.GONE);
    }

    public void setupNavigationDrawer() {

      View header = navigationView.getHeaderView(0);

      if(toolbar != null){

          toolbar.setNavigationIcon(R.drawable.ic_menu);
          toolbar.setNavigationOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  drawerLayout.openDrawer(GravityCompat.START);

              }
          });

          navigation_draw_bag = header.findViewById(R.id.navigation_draw);
          nav_img = header.findViewById(R.id.nav_image);
          city_id = header.findViewById(R.id.city_id);

          navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
              @Override
              public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                  int id = item.getItemId();
                  drawerLayout.closeDrawer(GravityCompat.START);

                  switch (id){

                      case R.id.home:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  finish();
                                  startActivity(new Intent(getApplicationContext(),MainActivity.class));

                              }
                          },250);

                          return true;

                      case R.id.daily_weather:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                 startActivity(new Intent(getApplicationContext(), DailyForecastActivity.class));

                              }
                          },250);

                          return true;

                      case R.id.hourly_weather:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                  startActivity(new Intent(getApplicationContext(),HourlyForecastActivity.class));

                              }
                          },250);

                          return true;

                      case R.id.help:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                  Intent intent = new Intent(Intent.ACTION_VIEW);
                                  Uri data = Uri.parse(getString(R.string.help_uri));
                                  intent.setData(data);
                                  startActivity(intent);

                              }
                          },250);

                          return true;

                      case R.id.settings:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));

                              }
                          },250);

                          return true;

                      case R.id.changelog:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                  showChangeLog();

                              }

                          },250);

                          return true;

                      case R.id.about:

                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {

                                   startActivity(new Intent(getApplicationContext(), About.class));

                              }
                          },250);

                          return true;


                  }


                  return true;

              }
          });


      }

    }

    private void showChangeLog() {

      changeLogDialog = new MaterialAlertDialogBuilder(this);
      changeLogDialog.setTitle(getString(R.string.change_log_title, BuildConfig.VERSION_NAME));
      changeLogDialog.setMessage(Html.fromHtml(getString(R.string.change_log_message)));

      changeLogDialog.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
          }
      });
      changeLogDialog.show();

    }


    public void updatedNavigationDetails() {

      nav_img.setImageResource(weatherHelper.getWeatherIcon());
      city_id.setText(weatherHelper.getCity());

    }

}
