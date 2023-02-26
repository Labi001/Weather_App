package com.labinot.bajrami.weather_app.activities;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.database.DataBaseHelper;
import com.labinot.bajrami.weather_app.database.LoadDataBase;
import com.labinot.bajrami.weather_app.helper.AsyncHelper;
import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.helper.GPS_Tracker;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;
import com.labinot.bajrami.weather_app.helper.WeatherHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends BaseActivity {

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;
    static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String[] mPermissions;

    private TextView current_date,current_location,desc_today,temp_today,feels_like,
    wind_speed,pressure,humidity,desc_tomorrow,temp_tomorrow,error_code_message;

    private Button view_more_btn;
    private FloatingActionButton autolocate_fab;
    private ImageView imgTemp_today,imgTemp_tomorrow;
    private FrameLayout mFrameLayout;
    private Toolbar searchToolbar;
    private CardView card_today,card_tomorrow;
    private NestedScrollView scrollView,error_scroll;
    private CoordinatorLayout coordinatorLayout;
    private GPS_Tracker gps_tracker;

    private Handler mHandler = new Handler(Looper.myLooper());
    private boolean isGpsSettingsLaunched = false;
    private boolean gpsFounded = false;
    private DataBaseHelper dataBaseHelper;

    private SimpleCursorAdapter suggestionAdapter;
    private Menu search_menu;
    private MenuItem item_search;
    boolean coordinate_search;
    String city,language,unit,longitude,latitude;

    @Override
    public void onBackPressed() {

        if (item_search != null && item_search.isActionViewExpanded())
            item_search.collapseActionView();
        else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_search:

                circleReveal(true, true);

                item_search.expandActionView();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();
        setTheme(Theme);

        setContentView(R.layout.activity_main);

        loadViews();
        setupSwipe();
        setUpToolbar();
        setSearchToolbar();
        Init();
        getWeatherData();
        initBottomBar();
        setupNavigationDrawer();

        autolocate_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (PermissionAllChecker()) {
                    case GRANTED:
                       gpsFounded = false;
                        autoLocate();

                        // scroll_view.setVisibility(View.GONE);
                        break;
                    case DENIED:
                        CheckPermissions();
                        break;
                    case BLOCKED_OR_NEVER_ASKED:
                        permissionBlocked();
                        break;
                    default:
                        break;
                }

            }
        });
    }




    private void autoLocate() {

        if(gps_tracker == null){

            gps_tracker = new GPS_Tracker(this, new GPS_Tracker.LocationInterface() {
                @Override
                public void autoLocationInfo(double latitude, double longitude) {

                    if(!gpsFounded){

                        gpsFounded = true;

                        MySharedPreferences.setBooleanPref(MainActivity.this, Constants.coordinate_search_key,true);
                        MySharedPreferences.setNormalPref(MainActivity.this,Constants.latitude_pref_key, String.valueOf(latitude));
                        MySharedPreferences.setNormalPref(MainActivity.this,Constants.longitude_pref_key, String.valueOf(longitude));

                          getWeatherData();
                        swipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                            }
                        },500);



                    }


                }
            });

        }

        if (gps_tracker.getLocation())

            swipeRefreshLayout.setRefreshing(true);
        else

            showGpsSettings();

    }

    private void showGpsSettings() {

        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);
        alertDialog.setTitle(R.string.alert_title_gps);
        alertDialog.setMessage(R.string.alert_message_gps);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.alert_positive_btn_gps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
                isGpsSettingsLaunched = true;
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snackBarMessage(getString(R.string.snack_bar_gps_turned_off));
                    }
                }, 300);
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isGpsSettingsLaunched){
            isGpsSettingsLaunched = false;
            autoLocate();
        }
    }

    private void setSearchToolbar() {

        mFrameLayout.setBackgroundColor(MaterialColors.getColor(this,R.attr.searchToolbarColor,Color.BLACK));

        searchToolbar = findViewById(R.id.searchToolbar);

        if (searchToolbar != null) {

            searchToolbar.inflateMenu(R.menu.menu_search);
            search_menu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    circleReveal(true, false);
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            item_search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    setVisibilityToolbar(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    circleReveal(true, false);
                    return true;
                }
            });

            initSearchView();

        }

    }

    private void initSearchView() {

        searchToolbar.setBackgroundColor(MaterialColors.getColor(this, R.attr.floatButtonRippleColor, Color.WHITE));
        searchToolbar.setTitleTextColor(MaterialColors.getColor(this, R.attr.searchToolbarText, Color.WHITE));
        searchToolbar.setCollapseIcon(R.drawable.ic_back_search);


        MenuItem search = search_menu.findItem(R.id.action_filter_search);
        search.setIcon(R.drawable.ic_search);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.findViewById(androidx.appcompat.R.id.search_plate)
                .setBackgroundColor(Color.TRANSPARENT);

        EditText textSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        textSearch.setHint(R.string.search);
        textSearch.setHintTextColor(MaterialColors.getColor(this, R.attr.searchToolbarIconColor, Color.WHITE));
        textSearch.setTextColor(MaterialColors.getColor(this, R.attr.searchToolbarText, Color.WHITE));

        //Cursori venojet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            GradientDrawable drawable = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.cursor, null);
            if (drawable != null) {
                drawable.setColor(getColor(R.color.blue_accent));
            }
            textSearch.setTextCursorDrawable(drawable);
        }

        final String[] from = new String[]{DataBaseHelper.DATABASE_CITY, DataBaseHelper.DATABASE_ISO2, DataBaseHelper.DATABASE_ADMIN_NAME};
        final int[] to = new int[]{R.id.suggestion_text, R.id.country_text, R.id.extra_info};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion_row, null, from, to, 0) {

            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };


        suggestionAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @SuppressLint("Range")
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if (view.getId() == R.id.suggestion_text) {

                    TextView city_text = view.findViewById(R.id.suggestion_text);
                    String city_text_value = cursor.getString(columnIndex);
                    city_text.setText(getString(R.string.city_cursor_string, city_text_value));

                    return true;

                } else if (view.getId() == R.id.country_text) {

                    TextView country_text = view.findViewById(R.id.country_text);
                    String country_text_value = cursor.getString(columnIndex);
                    country_text.setText(getString(R.string.country_cursor_string, country_text_value));

                    return true;

                } else if (view.getId() == R.id.extra_info) {

                    TextView extra_info = view.findViewById(R.id.extra_info);
                    extra_info.setText(cursor.getString(columnIndex));

                    String extra_info_text = cursor.getString(columnIndex);
                     String check_Country = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DATABASE_ISO2));

                    // Log.d(LOG_TAG, "Country - " + check_Country);

                    if (check_Country.equals(Constants.UNITED_STATES_EXTRA_INFO)) {
                        extra_info.setText(getString(R.string.extra_cursor_string, extra_info_text));
                    } else {
                        extra_info.setText("");
                    }

                    return true;

                } else {
                    return false;
                }
            }
        });

        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @SuppressLint("Range")
            @Override
            public boolean onSuggestionClick(int position) {

                searchView.clearFocus();
                searchView.setFocusable(false);

                CursorAdapter cursorAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);

                String clicked_city = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DATABASE_CITY));
                String clicked_lat = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DATABASE_LAT));
                String clicked_lng = cursor.getString(cursor.getColumnIndex(DataBaseHelper.DATABASE_LNG));

                searchView.setQuery(clicked_city, false);

                callSearch(clicked_city, clicked_lat, clicked_lng);

                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.setFocusable(false);
                callSearch(query, null, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                Cursor cursor = dataBaseHelper.getSuggestions(newText);

                if (cursor.getCount() > 0)
                    suggestionAdapter.changeCursor(cursor);

               return false;
            }
        });
    }

    private void callSearch(String query, String lat, String lng) {

        item_search.collapseActionView();

        if(lat==null){

            MySharedPreferences.setBooleanPref(MainActivity.this,Constants.coordinate_search_key,false);
            MySharedPreferences.setNormalPref(MainActivity.this,Constants.edit_text_location_key,query);

        }else{

            MySharedPreferences.setBooleanPref(MainActivity.this,Constants.coordinate_search_key,true);
            MySharedPreferences.setNormalPref(MainActivity.this,Constants.latitude_pref_key,lat);
            MySharedPreferences.setNormalPref(MainActivity.this,Constants.longitude_pref_key,lng);

        }

        getWeatherData();

    }

    public void circleReveal(final boolean containsOverflow, final boolean isShow) {

        // make the view visible and start the animation
        final int startAnimFrom = 2;

        searchToolbar.post(new Runnable() {
            @Override
            public void run() {

                int width = searchToolbar.getWidth();

                width -= (startAnimFrom * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);

                if (containsOverflow)
                    width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

                int cx = width;
                int cy = searchToolbar.getHeight() / 2;

                Animator anim;

                if (isShow)
                    anim = ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, 0, (float) width);
                else
                    anim = ViewAnimationUtils.createCircularReveal(searchToolbar, cx, cy, (float) width, 0);

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isShow) {
                            searchToolbar.setVisibility(View.GONE);
                            setVisibilityToolbar(true);
                            super.onAnimationEnd(animation);
                        }
                    }
                });

                anim.setDuration(220);

                if (isShow)
                    searchToolbar.setVisibility(View.VISIBLE);
                // start the animation
                anim.start();

            }
        });

    }

    private void loadViews() {

        current_date = findViewById(R.id.current_date);
        current_location = findViewById(R.id.current_location);
        desc_today = findViewById(R.id.desc);
        temp_today = findViewById(R.id.today_temp);
        feels_like = findViewById(R.id.feels_like);
        wind_speed = findViewById(R.id.wind_speed);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        desc_tomorrow = findViewById(R.id.tomorrow_desc);
        temp_tomorrow = findViewById(R.id.tomorrow_temp);
        view_more_btn = findViewById(R.id.view_more_btn);
        autolocate_fab = findViewById(R.id.auto_locate_fab);
        imgTemp_today = findViewById(R.id.temp_image);
        imgTemp_tomorrow = findViewById(R.id.temp_image_tomorrow);
        card_today = findViewById(R.id.card_view_today);
        card_tomorrow = findViewById(R.id.card_view_tomorrow);
        scrollView = findViewById(R.id.scroll_view);
        error_scroll = findViewById(R.id.error_scroll);
        error_code_message = findViewById(R.id.error_code_message);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        coordinatorLayout = findViewById(R.id.coordinate_layout);
        mFrameLayout = findViewById(R.id.mFrameLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomTabLayout = findViewById(R.id.bottomTab_layout);

        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setVisibility(View.GONE);
        error_scroll.setVisibility(View.GONE);


    }

    private void initBottomBar() {

        setupBottomBar();

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0)
                 MySharedPreferences.setNormalPref(MainActivity.this,Constants.list_unit_code_key,getString(R.string.metric));
                else
                    MySharedPreferences.setNormalPref(MainActivity.this,Constants.list_unit_code_key,getString(R.string.imperial));

                getWeatherData();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab tab_zero = bottomTabLayout.getTabAt(0);
        TabLayout.Tab tab_one = bottomTabLayout.getTabAt(1);

        if(unit.contains(getString(R.string.metric))){

            bottomTabLayout.post(new Runnable() {
                @Override
                public void run() {

                    if(tab_zero != null)
                        tab_zero.select();

                }
            });

        }else{

            bottomTabLayout.post(new Runnable() {
                @Override
                public void run() {

                    if(tab_one != null)
                       tab_one.select();
                }
            });


        }


    }

    private void Init() {

        weatherHelper = new WeatherHelper();

        mPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                swipeRefreshLayout.setEnabled(scrollY == 0);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherData();
            }
        });

     dataBaseHelper = new DataBaseHelper(MainActivity.this);

     if(dataBaseHelper.checkDatabase()){

        openDatabase();

     }else{

         LoadDataBase loadDataBase = new LoadDataBase(MainActivity.this);
         loadDataBase.doInBackground();

     }




    }

    public int PermissionAllChecker() {

        if (MySharedPreferences.isFirstTimeAskingPermission(this)) {

            MySharedPreferences.firstTimeAskingPermission(this, false);

            for (String permission : mPermissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return DENIED;
                }
            }

        } else {
            for (String permission : mPermissions) {

                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                        return DENIED;

                    } else {

                        return BLOCKED_OR_NEVER_ASKED;
                    }
                }
            }
        }

        return GRANTED;
    }

    private void CheckPermissions() {

        int result;

        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : mPermissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        }

    }

    private void permissionBlocked() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        dialog.setTitle(R.string.alert_title_permission_blocked);
        dialog.setMessage(R.string.alert_message_permission_blocked);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                i.setData(uri);
                startActivity(i);
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED && ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED) {
                autoLocate();
            } else {
                snackBarMessage(getString(R.string.permission_denied));
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("ResourceType")
    private void snackBarMessage(String message) {

         Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.blue_accent))
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAnchorView(autolocate_fab)
                .setActionTextColor(Color.WHITE)
                .setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void launchDetails(View view) {

        if(view.getId() == R.id.view_more_btn)
            startActivity(new Intent(this, DailyForecastActivity.class));

    }

    private void getWeatherData() {

        city = MySharedPreferences.getNormalPref(MainActivity.this,Constants.edit_text_location_key,Constants.LOCATION_CITY_DEFAULT);
        latitude = MySharedPreferences.getNormalPref(MainActivity.this,Constants.latitude_pref_key,"0");
        longitude = MySharedPreferences.getNormalPref(MainActivity.this,Constants.longitude_pref_key,"0");
        coordinate_search = MySharedPreferences.getBooleanPref(MainActivity.this,Constants.coordinate_search_key,false);
        unit = MySharedPreferences.getNormalPref(MainActivity.this,Constants.list_unit_code_key,getString(R.string.metric));
        language = "en";


        if(coordinate_search){

            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(latitude,longitude,unit,language),
                    AsyncHelper.checkTomorrowWeather(latitude,longitude,unit,language,"8")
            );

        }else{

            extractDataFromServer(
                    AsyncHelper.checkCurrentWeather(city,unit,language),
                    AsyncHelper.checkTomorrowWeather(city,unit,language,"8")
            );

        }

       swipeRefreshLayout.setRefreshing(true);
    }

    private void extractDataFromServer(String yourCurrentRequest,String yourTomorrowRequest){

        AsyncHttpClient client = new AsyncHttpClient();

        //Weather for Today
        client.get(yourCurrentRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String t_data = new String(responseBody);

                if(!t_data.equals(""))
                    weatherHelper.ParseTodayData(t_data);

                updateTodayData();

                scrollView.setVisibility(View.VISIBLE);
                autolocate_fab.setVisibility(View.VISIBLE);
                error_scroll.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                scrollView.setVisibility(View.GONE);
                autolocate_fab.setVisibility(View.GONE);
                MySharedPreferences.setNormalPref(MainActivity.this,Constants.edit_text_location_key,Constants.LOCATION_CITY_DEFAULT);
                error_scroll.setVisibility(View.VISIBLE);
                error_code_message.setText(error.getMessage());

            }
        });

        //Weather Forecast
        client.get(yourTomorrowRequest, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String data = new String(responseBody);

                if(!data.equals(""))
                    weatherHelper.ParseTomorrowData(data);

                updateTomorrowData();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }

    private void animateTodayData(){

        current_date.setAlpha(0);
        current_date.animate().setDuration(250).alpha(1);

        current_location.setAlpha(0);
        current_location.animate().setDuration(250).alpha(1);

        temp_today.setAlpha(0);
        temp_today.animate().setDuration(250).alpha(1);

        feels_like.setAlpha(0);
        feels_like.animate().setDuration(250).alpha(1);

        desc_today.setAlpha(0);
        desc_today.animate().setDuration(250).alpha(1);

        wind_speed.setAlpha(0);
        wind_speed.animate().setDuration(250).alpha(1);

        humidity.setAlpha(0);
        humidity.animate().setDuration(250).alpha(1);

        pressure.setAlpha(0);
        pressure.animate().setDuration(250).alpha(1);

        imgTemp_today.setImageAlpha(0);
        ValueAnimator imageAnim = ValueAnimator.ofInt(0, 255);
        imageAnim.setDuration(250);
        imageAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imgTemp_today.setImageAlpha((Integer) animation.getAnimatedValue());
            }
        });
        imageAnim.start();

        // Set Color to Card
        int oldColor = MySharedPreferences.getNormalPref_Integer(
                MainActivity.this,
                Constants.today_card_current_color_key,
                0);

        int colorTo = ContextCompat.getColor(this, weatherHelper.getWeatherColor());
        int colorFrom;

//        if (oldColor == 0) {
//            colorFrom = getResources().getColor(android.R.color.transparent);
//        } else {
//            colorFrom = getResources().getColor(oldColor);
//        }


        MySharedPreferences.setNormalPref_Integer(MainActivity.this, Constants.today_card_current_color_key, weatherHelper.getWeatherColor());

//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//        colorAnimation.setDuration(250);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                card_view.setCardBackgroundColor((int) animation.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();


    }
    private void updateTodayData() {

        updatedNavigationDetails();

        MySharedPreferences.setNormalPref(MainActivity.this,Constants.edit_text_location_key,weatherHelper.getCity());
        MySharedPreferences.setNormalPref(MainActivity.this,Constants.latitude_pref_key, String.valueOf(weatherHelper.getLatitude()));
        MySharedPreferences.setNormalPref(MainActivity.this,Constants.longitude_pref_key, String.valueOf(weatherHelper.getLongitude()));

        refreshLastTimeCheck();

        if(unit.contains(getString(R.string.metric))){

            temp_today.setText(getString(R.string.setTempC,weatherHelper.getCurrent_temp()));
            feels_like.setText(getString(R.string.setFeelsLikeC,weatherHelper.getFeels_like()));
            wind_speed.setText(getString(R.string.setKMH,weatherHelper.getSpeed()));

        }else{

            temp_today.setText(getString(R.string.setTempF,weatherHelper.getCurrent_temp()));
            feels_like.setText(getString(R.string.setFeelsLikeF,weatherHelper.getFeels_like()));
            wind_speed.setText(getString(R.string.setMPH,weatherHelper.getSpeed()));

        }


        current_location.setText(getString(R.string.setCity_Country,weatherHelper.getCity(),weatherHelper.getCountry()));
        imgTemp_today.setImageResource(weatherHelper.getWeatherIcon());
        desc_today.setText(weatherHelper.getDescription());
        humidity.setText(getString(R.string.setHUM,weatherHelper.getHumidity()));
        pressure.setText(getString(R.string.setPRE,weatherHelper.getPressure()));

        animateTodayData();

        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

    private void refreshLastTimeCheck() {


        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
         current_date.setText(getString(R.string.setTimeDate,currentDate,currentTime));


    }

    private void updateTomorrowData(){

        if(unit.contains(getString(R.string.metric)))
            temp_tomorrow.setText(getString(R.string.setTempC,weatherHelper.getTomorrow_temp()));
        else
            temp_tomorrow.setText(getString(R.string.setTempF,weatherHelper.getTomorrow_temp()));



        imgTemp_tomorrow.setImageResource(weatherHelper.getWeatherIcon());
        desc_tomorrow.setText(weatherHelper.getDesc_tomorrow());
       animateTomorrowData();

    }

    private void animateTomorrowData(){

        temp_tomorrow.setAlpha(0);
        temp_tomorrow.animate().setDuration(250).alpha(1);

        desc_tomorrow.setAlpha(0);
        desc_tomorrow.animate().setDuration(250).alpha(1);

        imgTemp_tomorrow.setImageAlpha(0);
        ValueAnimator imageAnim = ValueAnimator.ofInt(0, 255);
        imageAnim.setDuration(250);
        imageAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imgTemp_tomorrow.setImageAlpha((Integer) animation.getAnimatedValue());
            }
        });
        imageAnim.start();

        // Set Tomorrow Card Color
        int oldColor = MySharedPreferences.getNormalPref_Integer(
                MainActivity.this,
                Constants.tomorrow_card_current_color_key,
                0);

        int colorTo = ContextCompat.getColor(this, weatherHelper.getWeatherColor());
        int colorFrom;

//        if (oldColor == 0) {
//            colorFrom = getResources().getColor(android.R.color.transparent);
//        } else {
//            colorFrom = getResources().getColor(oldColor);
//        }

        MySharedPreferences.setNormalPref_Integer(MainActivity.this, Constants.tomorrow_card_current_color_key,weatherHelper.getWeatherColor());

//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//        colorAnimation.setDuration(250);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                card_tomorrow.setCardBackgroundColor((int) animation.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();

    }

    public void openDatabase() {

        try {
            dataBaseHelper.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}