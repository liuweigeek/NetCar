package com.imagine.scott.netcar.activity;

import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.WeizhangIntentService;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.dialog.SetServerAddrDialog;
import com.imagine.scott.netcar.fragment.AppointmentFragment;
import com.imagine.scott.netcar.fragment.BaseFragment;
import com.imagine.scott.netcar.fragment.BreakRulesFragment;
import com.imagine.scott.netcar.fragment.CarsFragment;
import com.imagine.scott.netcar.fragment.GasStationFragment;
import com.imagine.scott.netcar.fragment.MapFragment;
import com.imagine.scott.netcar.fragment.MusicFragment;
import com.imagine.scott.netcar.fragment.NotificationFragment;
import com.imagine.scott.netcar.fragment.SettingFragment;
import com.imagine.scott.netcar.widget.CircleImageView;

import com.igexin.sdk.PushManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public MapFragment mapFragment;
    public GasStationFragment gasStationFragment;
    public AppointmentFragment appointmentFragment;
    public BreakRulesFragment breakRulesFragment;
    public CarsFragment carsFragment;
    public NotificationFragment notificationFragment;
    public MusicFragment musicFragment;
    public SettingFragment settingFragment;
    public BaseFragment currentFragment;
    private HashMap<Integer, BaseFragment> fragments;
    private FragmentTransaction transaction;

    private Toast exitToast;
    private boolean isExitToastShow;

    private NavigationView navigationView;
    private View headerView;
    private CircleImageView navHeadImage;
    private TextView navUsername;

    public SharedPreferences startPreferences;
    public SharedPreferences.Editor startEditor;
    public SharedPreferences addrPreferences;
    public SharedPreferences.Editor addrEditor;
    public SharedPreferences userInfoPreferences;
    public SharedPreferences.Editor userInfoEditor;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPreferences = getSharedPreferences("setup", MODE_PRIVATE);
        startEditor = startPreferences.edit();

        addrPreferences = getSharedPreferences("ServerAddr", MODE_PRIVATE);
        addrEditor = addrPreferences.edit();
        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPreferences.edit();

        if (startPreferences.getBoolean("firstSetUp", true)) {
            addrEditor.putString("IP", "118.190.90.99").commit();
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startEditor.putBoolean("firstSetUp", false).commit();
            startActivity(startIntent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;

        exitToast = Toast.makeText(this, "再次按下返回键退出", Toast.LENGTH_LONG);

        Intent weizhangIntent = new Intent(this, WeizhangIntentService.class);
        weizhangIntent.putExtra("appId", 1849);
        weizhangIntent.putExtra("appKey", "af2248fc3cb4bd76b12335578144b197");
        startService(weizhangIntent);

        PushManager.getInstance().initialize(this.getApplicationContext());

        initFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transactionInit(transaction);
        transactionHide(transaction);
        transaction.show(currentFragment);
        currentFragment.onShowFragment();

        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.nav_username);
        navHeadImage = (CircleImageView) headerView.findViewById(R.id.nav_headimage);

        switchLoginState();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            if (currentFragment != null) {
                if (currentFragment != mapFragment) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transactionHide(transaction);
                    currentFragment.onDismissFragment();
                    currentFragment = mapFragment;
                    transaction.show(currentFragment);
                    currentFragment.onShowFragment();
                    transaction.commit();
                } else if (!isExitToastShow) {
                    showExitToast();
                } else {
                    super.onKeyDown(keyCode, event);
                    finish();
                    System.exit(0);
                }
            }
        }
        return false;
    }

    public void showExitToast() {
        new Thread() {
            public void run() {
                exitToast.show();
                isExitToastShow = true;
                try {
                    Thread.sleep(3500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isExitToastShow = false;
            }
        }.start();
    }

    public void switchLoginState() {
        if (TextUtils.isEmpty(userInfoPreferences.getString("userphone", ""))) {
            navHeadImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_text_nologin_head));
            navUsername.setText("点击头像登录");
            navHeadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            String url = addrPreferences.getString("IP", null);
            if (!TextUtils.isEmpty(url)) {
                String filename = userInfoPreferences.getString("userheadimage", "");
                if (!TextUtils.isEmpty(filename)) {
                    StringBuffer logourl = new StringBuffer("http://" + url + "/NetCar/headimage/" + filename);
                    Picasso.with(navHeadImage.getContext()).load(logourl.toString()).into(navHeadImage);
                }
            } else {
                navHeadImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_text_nologin_head));
            }

            navUsername.setText(userInfoPreferences.getString("username", ""));
            navHeadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                                        MainActivity.this.findViewById(R.id.nav_headimage), getString(R.string.transition_head_img));

                        ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchLoginState();
    }

    public void initFragment() {
        mapFragment = new MapFragment();
        gasStationFragment = new GasStationFragment();
        appointmentFragment = new AppointmentFragment();
        breakRulesFragment = new BreakRulesFragment();
        carsFragment = new CarsFragment();
        notificationFragment = new NotificationFragment();
        musicFragment = new MusicFragment();
        settingFragment = new SettingFragment();

        fragments = new HashMap<>();
        fragments.put(R.id.nav_map, mapFragment);
        fragments.put(R.id.nav_gas_station, gasStationFragment);
        fragments.put(R.id.nav_appointment, appointmentFragment);
        fragments.put(R.id.nav_break_rules, breakRulesFragment);
        fragments.put(R.id.nav_cars, carsFragment);
        fragments.put(R.id.nav_notification, notificationFragment);
        fragments.put(R.id.nav_music, musicFragment);
        fragments.put(R.id.nav_setting, settingFragment);

        currentFragment = mapFragment;
    }
    //切换fab显示状态

    public void showSnackbar(String message) {
        Snackbar.make(navigationView, message, Snackbar.LENGTH_LONG).show();
    }

    //初始化全部Fragment
    public void transactionInit(FragmentTransaction transaction) {
        for (Map.Entry<Integer, BaseFragment> entry : fragments.entrySet()) {
            transaction.add(R.id.container, entry.getValue());
        }
    }
    //隐藏全部Fragment
    public void transactionHide(FragmentTransaction transaction) {
        for (Map.Entry<Integer, BaseFragment> entry : fragments.entrySet()) {
            transaction.hide(entry.getValue());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_set_server_addr:
                SetServerAddrDialog setServerAddrDialog = new SetServerAddrDialog();
                setServerAddrDialog.show(getFragmentManager(), "setServerAddrDialog");
                break;
            case R.id.action_login:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.action_register:
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        setTitle(item.getTitle());
        BaseFragment fragment = fragments.get(item.getItemId());

        if(fragment != currentFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transactionHide(transaction);
            currentFragment.onDismissFragment();
            currentFragment = fragment;
            transaction.show(currentFragment);
            currentFragment.onShowFragment();
            transaction.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
