package com.imagine.scott.netcar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.Order;
import com.imagine.scott.netcar.fragment.MapFragment;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AppoDetailActivity extends AppCompatActivity {

    private MapView mMapView;
    private AMap aMap;  //地图组件

    public SharedPreferences userInfoPreferences;

    private DeleteOrderTask delTask;

    private Order order;

    private TextView appoDetailGasStationName;
    private TextView appoDetailDate;
    private TextView appoDetailBrandName;
    private TextView appoDetailOilType;
    private TextView appoDetailLitre;
    private TextView appoDetailMoney;
    private ImageView appoDetailQRCode;

    private FloatingActionButton appoDetailLogoFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appo_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appo_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        order = (Order) bundle.getSerializable("orderInfo");

        mMapView = (MapView) findViewById(R.id.appo_detail_mapview);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        initView();
        showInfo();
        setUpMap();

        LatLng point = new LatLng(order.getGasLat(), order.getGasLng());
        //构建MarkerOption，用于在地图上添加Marker
        aMap.addMarker(new MarkerOptions()
                .position(point)
                .title(order.getGasStation())
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_unclick))));
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14));

    }

    public void initView() {
        appoDetailLogoFab = (FloatingActionButton) findViewById(R.id.appo_detail_fab);

        appoDetailLogoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double mLat = MapFragment.amapLocation.getLatitude();
                double mLng = MapFragment.amapLocation.getLongitude();
                if (!TextUtils.isEmpty(Double.toString(mLat)) && !TextUtils.isEmpty(Double.toString(mLng))) {
                    Intent intent = new Intent(AppoDetailActivity.this, MultipleRoutePlanningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("eName", order.getGasStation());
                    bundle.putString("eAddr", order.getGasStation());
                    bundle.putDouble("sLat", mLat);
                    bundle.putDouble("sLng", mLng);
                    bundle.putDouble("eLat", order.getGasLat());
                    bundle.putDouble("eLng", order.getGasLng());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(AppoDetailActivity.this, "获取当前位置失败，请检查权限", Toast.LENGTH_LONG).show();
                }
            }
        });

        appoDetailGasStationName = (TextView) findViewById(R.id.appo_detail_gasstation);
        appoDetailDate = (TextView) findViewById(R.id.appo_detail_date);
        appoDetailBrandName = (TextView) findViewById(R.id.appo_detail_brandname);
        appoDetailOilType = (TextView) findViewById(R.id.appo_detail_oiltype);
        appoDetailLitre = (TextView) findViewById(R.id.appo_detail_litre);
        appoDetailMoney = (TextView) findViewById(R.id.appo_detail_money);
        appoDetailQRCode = (ImageView) findViewById(R.id.appo_detail_qrcode);

        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/DroidSans-Bold.ttf");

        appoDetailLitre.setTypeface(fontFace);
        appoDetailMoney.setTypeface(fontFace);
    }
    public void showInfo() {
        appoDetailGasStationName.setText(order.getGasStation());
        SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        appoDetailDate.setText(sf.format(order.getDate()));
        appoDetailBrandName.setText(order.getBrandname());
        appoDetailOilType.setText(order.getOilType());
        appoDetailLitre.setText(Double.toString(order.getLitre()));
        appoDetailMoney.setText(Double.toString(order.getMoney()));
        JSONObject appoJson = new JSONObject();
        try {
            appoJson.put("qrtype", "order");
            appoJson.put("id", order.getId());
            appoJson.put("gas", order.getGasStation());
            appoJson.put("oiltype", order.getOilType());
            appoJson.put("litre", Double.toString(order.getLitre()));
            appoJson.put("money", Double.toString(order.getMoney()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (appoJson.length() > 0) {
            Bitmap qrcodeBitmap = EncodingUtils.createQRCode(appoJson.toString(),
                    512, 512, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            appoDetailQRCode.setImageBitmap(qrcodeBitmap);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appo_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.home:
                finish();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setMessage("确认删除当前订单?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (delTask == null) {
                                    delTask = new DeleteOrderTask();
                                    delTask.execute(userInfoPreferences.getString("userphone", ""),
                                            Integer.toString(order.getId()));
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class DeleteOrderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();

            map.put("phone", params[0]);
            map.put("orderid", params[1]);

            return connection.uploadParams("", "DeleteOrder", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            delTask = null;
            try {
                switch (Integer.parseInt(ResultJSONOperate.getRegisterCode(result))) {
                    case Constants.DELETE_ORDER_SUCCESS :
                        finish();
                        break;
                    case Constants.DELETE_ORDER_FAILED :
                        Toast.makeText(AppoDetailActivity.this, "删除订单失败", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(AppoDetailActivity.this, "删除订单异常", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
