package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.GasStation;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class GasStationDetailActivity extends AppCompatActivity {

    private MapView mMapView;
    private AMap aMap;  //地图组件

    private double latitude;
    private double longitude;

    private GasStation gasStation;
    private TextView gasStationDetailPrice93;
    private TextView gasStationDetailPrice97;
    private TextView gasStationDetailPrice0;
    private TextView gasStationDetailPrice90;
    private TextView gasStationDetailBrandName;
    private TextView gasStationDetailArea;
    private TextView gasStationDetailType;
    private TextView gasStationDetailDiscount;
    private TextView gasStationDetailExhaust;
    private TextView gasStationDetailFwlsmc;

    private FloatingActionButton brandLogoFab;
    private FabSpeedDial speedDialFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_station_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        gasStation = (GasStation) bundle.getSerializable("gasStation");
        getSupportActionBar().setTitle(gasStation.getName());
        latitude = bundle.getDouble("mLat");
        longitude = bundle.getDouble("mLng");

        mMapView = (MapView) findViewById(R.id.gas_station_detail_mapview);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        showInfo();
        setUpMap();

        LatLng point = new LatLng(gasStation.getLat(), gasStation.getLon());
        //构建MarkerOption，用于在地图上添加Marker
        aMap.addMarker(new MarkerOptions()
                .position(point)
                .title(gasStation.getName())
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_unclick))));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14));
    }

    public void initView() {
        brandLogoFab = (FloatingActionButton) findViewById(R.id.gas_station_detail_fab);
        speedDialFab = (FabSpeedDial) findViewById(R.id.gas_station_detail_fab_speed_dial);

        speedDialFab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.fab_action_appo:
                        Intent appoIntent = new Intent(GasStationDetailActivity.this, AppoActivity.class);
                        Bundle appoBundle = new Bundle();
                        appoBundle.putSerializable("gasStation", gasStation);
                        appoIntent.putExtras(appoBundle);
                        startActivity(appoIntent);
                        break;
                    case R.id.fab_action_go:
                        Intent goIntent = new Intent(MainActivity.mainActivity, MultipleRoutePlanningActivity.class);
                        Bundle goBundle = new Bundle();
                        goBundle.putString("eName", gasStation.getName());
                        goBundle.putString("eAddr", gasStation.getAddress());
                        goBundle.putDouble("sLat", latitude);
                        goBundle.putDouble("sLng", longitude);
                        goBundle.putDouble("eLat", gasStation.getLat());
                        goBundle.putDouble("eLng", gasStation.getLon());
                        goIntent.putExtras(goBundle);
                        startActivity(goIntent);
                        break;
                }
                return super.onMenuItemSelected(menuItem);
            }
        });
        switch (gasStation.getBrandname()) {
            case "中石油":
                brandLogoFab.setImageDrawable(getResources().getDrawable(R.drawable.logo_cnpc));
                break;
            case "中石化":
                brandLogoFab.setImageDrawable(getResources().getDrawable(R.drawable.logo_sinopec));
                break;
            case "壳牌":
                brandLogoFab.setImageDrawable(getResources().getDrawable(R.drawable.logo_shell));
                break;
            default:
                brandLogoFab.setImageDrawable(getResources().getDrawable(R.drawable.logo_gas_station_white));
        }

        gasStationDetailPrice93 = (TextView) findViewById(R.id.gas_station_detail_price_93);
        gasStationDetailPrice97 = (TextView) findViewById(R.id.gas_station_detail_price_97);
        gasStationDetailPrice0 = (TextView) findViewById(R.id.gas_station_detail_price_0);
        gasStationDetailPrice90 = (TextView) findViewById(R.id.gas_station_detail_price_90);
        gasStationDetailBrandName = (TextView) findViewById(R.id.gas_station_detail_brandname);
        gasStationDetailArea = (TextView) findViewById(R.id.gas_station_detail_area);
        gasStationDetailType = (TextView) findViewById(R.id.gas_station_detail_type);
        gasStationDetailDiscount = (TextView) findViewById(R.id.gas_station_detail_discount);
        gasStationDetailExhaust = (TextView) findViewById(R.id.gas_station_detail_exhaust);
        gasStationDetailFwlsmc = (TextView) findViewById(R.id.gas_station_detail_fwlsmc);

        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/DroidSans-Bold.ttf");

        gasStationDetailPrice93.setTypeface(fontFace);
        gasStationDetailPrice97.setTypeface(fontFace);
        gasStationDetailPrice0.setTypeface(fontFace);
        gasStationDetailPrice90.setTypeface(fontFace);

    }
    public void showInfo() {
        gasStationDetailPrice93.setText(Float.toString(gasStation.getGasPrice_93()));
        gasStationDetailPrice97.setText(Float.toString(gasStation.getGasPrice_97()));
        gasStationDetailPrice0.setText(Float.toString(gasStation.getGasPrice_0()));
        gasStationDetailPrice90.setText(Float.toString(gasStation.getGasPrice_90()));
        gasStationDetailBrandName.setText(gasStation.getBrandname());
        gasStationDetailArea.setText(Integer.toString(gasStation.getArea()));
        gasStationDetailType.setText(gasStation.getType());
        gasStationDetailDiscount.setText(gasStation.getDiscount());
        gasStationDetailExhaust.setText(gasStation.getExhaust());
        gasStationDetailFwlsmc.setText(gasStation.getFwlsmc());
    }
    private void setUpMap() {
        aMap = mMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setMyLocationEnabled(false);
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setAllGesturesEnabled(false);
    }
}
