package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.operation.Transform;
import com.imagine.scott.netcar.util.TTSController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 创建时间：15/12/7 18:11
 * 项目名称：newNaviDemo
 *
 * @author lingxiang.wang
 * @email lingxiang.wang@alibaba-inc.com
 * 类说明：
 */

public class MultipleRoutePlanningActivity extends AppCompatActivity implements AMapNaviListener {

    NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    NaviLatLng startLatlng = new NaviLatLng(39.925041, 116.437901);

    List<NaviLatLng> startList = new ArrayList<>();
    List<NaviLatLng> endList = new ArrayList<>();
    private String eName;
    private String eAddr;
    private double sLat, sLng, eLat, eLng;
    private MapView mapView;
    private AMap amap;
    private AMapNavi aMapNavi;

    private HashMap<Integer, RouteOverLay> routeOverlays = new HashMap<>();
    private int routeIndex;
    private int[] routeIds;
    private TTSController ttsManager;
    private boolean chooseRouteSuccess;
    private boolean calculateSuccess;

    private LinearLayout routeCardViewTab;
    private TextView routeCardViewTitle;
    private TextView routeCardViewDistance;
    private TextView routeCardViewTime;
    private TextView routeCardViewAddr;
    private LinearLayout emulateNavi;
    private LinearLayout gpsNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.route_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("路径规划");
        }

        mapView = (MapView) findViewById(R.id.route_mapview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        routeCardViewTab = (LinearLayout) findViewById(R.id.route_tab);
        routeCardViewTitle = (TextView) findViewById(R.id.route_title);
        routeCardViewDistance = (TextView) findViewById(R.id.route_distance);
        routeCardViewTime = (TextView) findViewById(R.id.route_time);
        routeCardViewAddr = (TextView) findViewById(R.id.route_addr);
        emulateNavi = (LinearLayout) findViewById(R.id.emulate_navi);
        gpsNavi = (LinearLayout) findViewById(R.id.gps_navi);
        amap = mapView.getMap();
        amap.setTrafficEnabled(true);

        aMapNavi = AMapNavi.getInstance(getApplicationContext());
        aMapNavi.addAMapNaviListener(this);

        routeIndex = 0;

        Intent intent = getIntent();
        eName = intent.getStringExtra("eName");
        eAddr = intent.getStringExtra("eAddr");
        sLat = intent.getDoubleExtra("sLat", startLatlng.getLatitude());
        sLng = intent.getDoubleExtra("sLng", startLatlng.getLongitude());
        eLat = intent.getDoubleExtra("eLat", endLatlng.getLatitude());
        eLng = intent.getDoubleExtra("eLng", endLatlng.getLongitude());

        startLatlng = new NaviLatLng(sLat, sLng);
        endLatlng = new NaviLatLng(eLat, eLng);

        ttsManager = TTSController.getInstance(getApplicationContext());
        ttsManager.init();
        ttsManager.startSpeaking();

        amap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });
        calculateRoute();
    }

    public void calculateRoute() {
        startList.add(startLatlng);
        endList.add(endLatlng);

        aMapNavi.calculateDriveRoute(startList, endList, null, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES);
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] routeIds) {

        //当且仅当，使用策略AMapNavi.DrivingMultipleRoutes时回调
        //单路径算路依然回调onCalculateRouteSuccess，不回调这个


        //你会获取路径ID数组
        this.routeIds = routeIds;
        for (int i = 0; i < routeIds.length; i++) {
            //你可以通过对应的路径ID获得一条道路路径AMapNaviPath
            final int index = i;

            AMapNaviPath path = (aMapNavi.getNaviPaths()).get(routeIds[i]);

            //你可以通过这个AMapNaviPath生成一个RouteOverLay用于加在地图上
            RouteOverLay routeOverLay = new RouteOverLay(amap, path, this);
            routeOverLay.setTrafficLine(true);
            routeOverLay.addToMap();
            routeOverlays.put(routeIds[i], routeOverLay);

            Button button = new Button(this);
/*            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int widthPixels= dm.widthPixels;
            button.setWidth(widthPixels * (1 / (routeIds.length - 1)));*/
            button.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            if (i == 0) {
                button.setText("推荐方案");
            } else {
                button.setText("方案" + Integer.toString(i + 1));
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeRoute(index);
                }
            });
            routeCardViewTab.addView(button);
        }
        routeOverlays.get(routeIds[0]).zoomToSpan();
        calculateSuccess = true;
        changeRoute(0);
    }

    //TODO
    @Override
    public void onCalculateRouteSuccess() {

    }

    public void changeRoute(int index) {
        if (!calculateSuccess) {
            Toast.makeText(this, "请先算路", Toast.LENGTH_SHORT).show();
            return;
        }

        if (index >= routeIds.length)
            index = 0;

        //突出选择的那条路
        for (RouteOverLay routeOverLay : routeOverlays.values()) {
            routeOverLay.setTransparency(0.7f);
        }
        routeOverlays.get(routeIds[index]).setTransparency(0);

        //必须告诉AMapNavi 你最后选择的哪条路
        aMapNavi.selectRouteId(routeIds[routeIndex]);
        chooseRouteSuccess = true;

        int allLength = aMapNavi.getNaviPaths().get(routeIds[index]).getAllLength();
        int allTime = aMapNavi.getNaviPaths().get(routeIds[index]).getAllTime();
        routeCardViewTitle.setText(eName);
        routeCardViewDistance.setText("路径全长：" + Transform.meterToKilo(allLength));
        routeCardViewTime.setText("预计用时：" + Transform.secToMin(allTime));
        routeCardViewAddr.setText(eAddr);
        emulateNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEmulateActivity();
            }
        });
        gpsNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGPSActivity();
            }
        });
    }

    public void goToEmulateActivity() {
        if (chooseRouteSuccess && calculateSuccess) {
            //SimpleNaviActivity非常简单，就是startNavi而已（因为导航道路已在这个activity生成好）
            Intent intent = new Intent(this, SimpleNaviActivity.class);
            intent.putExtra("gps", false);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先算路，选路", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToGPSActivity() {
        if (chooseRouteSuccess && calculateSuccess) {
            //SimpleNaviActivity非常简单，就是startNavi而已（因为导航道路已在这个activity生成好）
            Intent intent = new Intent(this, SimpleNaviActivity.class);
            intent.putExtra("gps", true);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先算路，选路", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        aMapNavi.stopNavi();
        ttsManager.destroy();
        aMapNavi.destroy();
    }

    @Override
    public void notifyParallelRoad(int i) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
    }

    @Override
    public void onInitNaviFailure() {
    }

    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onStartNavi(int type) {
    }

    @Override
    public void onTrafficStatusUpdate() {
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
    }

    @Override
    public void onGetNavigationText(int type, String text) {
    }

    @Override
    public void onEndEmulatorNavi() {
    }

    @Override
    public void onArriveDestination() {
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
    }

    @Override
    public void onReCalculateRouteForYaw() {
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo naviInfo) {
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
    }

    @Override
    public void hideCross() {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
    }

    @Override
    public void hideLaneInfo() {
    }
}
