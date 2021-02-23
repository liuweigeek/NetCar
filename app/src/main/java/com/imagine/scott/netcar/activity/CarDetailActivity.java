package com.imagine.scott.netcar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;
import com.imagine.scott.netcar.widget.CircleImageView;
import com.squareup.picasso.Picasso;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CarDetailActivity extends AppCompatActivity {

    private UserCar userCar;

    public SharedPreferences userInfoPreferences;
    public SharedPreferences addrPreferences;

    private ScanCarInfoTask mAuthTask;
    private DeleteUserCarTask delTask;

    private CircleImageView carDetailLogo;
    private TextView carDetailModel;
    private TextView carDetailDoorNum;
    private TextView carDetailSeatNum;
    private TextView carDetailLicense;
    private TextView carDetailVin;
    private TextView carDetailEngineNum;
    private TextView carDetailMileAge;
    private TextView carDetailLastMaintainMile;
    private ImageView carDetailLampWell;
    private ImageView carDetailEngineWell;
    private ImageView carDetailTransmissionWell;
    private ProgressBar carDetailOilMass;
    private ImageView carDetailTirepressure;
    private TextView carDetailAvgecon;
    private ImageView carDetailAirSacSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.car_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userCar = (UserCar) bundle.getSerializable("carinfo");
        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        addrPreferences = getSharedPreferences("ServerAddr", MODE_PRIVATE);
        initView();
        showInfo();
    }

    public void initView() {
        carDetailLogo = (CircleImageView) findViewById(R.id.car_detail_logo_img);
        carDetailModel = (TextView) findViewById(R.id.car_detail_model);
        carDetailDoorNum = (TextView) findViewById(R.id.car_detail_doornum);
        carDetailSeatNum = (TextView) findViewById(R.id.car_detail_seatnum);
        carDetailLicense = (TextView) findViewById(R.id.car_detail_license);
        carDetailVin = (TextView) findViewById(R.id.car_detail_vin);
        carDetailEngineNum = (TextView) findViewById(R.id.car_detail_enginenum);
        carDetailMileAge = (TextView) findViewById(R.id.car_detail_mileage);
        carDetailLastMaintainMile = (TextView) findViewById(R.id.car_detail_lastmaintainmile);
        carDetailLampWell = (ImageView) findViewById(R.id.car_detail_lampwell);
        carDetailEngineWell = (ImageView) findViewById(R.id.car_detail_enginewell);
        carDetailTransmissionWell = (ImageView) findViewById(R.id.car_detail_transmissionwell);
        carDetailOilMass = (ProgressBar) findViewById(R.id.car_detail_oilmass);
        carDetailTirepressure = (ImageView) findViewById(R.id.car_detail_tirepressure);
        carDetailAvgecon = (TextView) findViewById(R.id.car_detail_avgecon);
        carDetailAirSacSafe = (ImageView) findViewById(R.id.car_detail_airsacsafe);
    }

    public void showInfo() {
        String carModelInfo = userCar.getCar().getVehicleBrandZh() + userCar.getCar().getVehicleModel();
        carDetailModel.setText(carModelInfo);
        carDetailDoorNum.setText(Integer.toString(userCar.getCar().getDoorNum()) + "门");
        carDetailSeatNum.setText(Integer.toString(userCar.getCar().getSeatNum()) + "座");
        carDetailLicense.setText(userCar.getLicensePlateNumber());
        carDetailVin.setText(userCar.getVin());
        carDetailEngineNum.setText(userCar.getEngineNum());
        carDetailMileAge.setText(Integer.toString(userCar.getMileage()) + "公里");
        carDetailLastMaintainMile.setText((Integer.toString(userCar.getLastMaintainMile())) + "公里");
        Drawable drawableNoProblem = getResources().getDrawable(R.drawable.ic_text_no_problem);
        Drawable drawableHasProblem = getResources().getDrawable(R.drawable.ic_text_has_problem);
        carDetailLampWell.setBackgroundDrawable(userCar.getLampWell() ? drawableNoProblem : drawableHasProblem);
        carDetailEngineWell.setBackgroundDrawable(userCar.getEngineWell() ? drawableNoProblem : drawableHasProblem);
        carDetailTransmissionWell.setBackgroundDrawable(userCar.getTransmissionWell() ? drawableNoProblem : drawableHasProblem);
        carDetailOilMass.setProgress((int) (userCar.getOilMass().floatValue() * 100));
        carDetailTirepressure.setBackgroundDrawable(userCar.getTirePressure() ? drawableNoProblem : drawableHasProblem);
        carDetailAvgecon.setText(Integer.toString(userCar.getAvgEcon()) + "升/百公里");
        carDetailAirSacSafe.setBackgroundDrawable(userCar.getAirSacSafe() ? drawableNoProblem : drawableHasProblem);

        String url = addrPreferences.getString("IP", null);
        if (!TextUtils.isEmpty(url)) {
            StringBuffer logourl = new StringBuffer("http://" + url + "/NetCar/carlogo/" + userCar.getCar().getVehicleBrand() + ".png");
            Picasso.with(carDetailLogo.getContext()).load(logourl.toString()).into(carDetailLogo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
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
            case R.id.action_scan:
                Intent scanIntent = new Intent(CarDetailActivity.this, CaptureActivity.class);
                startActivityForResult(scanIntent, 0);
                break;
            case R.id.action_delete:

                new AlertDialog.Builder(this)
                        .setMessage("确认删除当前车辆?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (delTask == null) {
                                    delTask = new DeleteUserCarTask();
                                    delTask.execute(userInfoPreferences.getString("userphone", ""),
                                            Integer.toString(userCar.getId()));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("result"));
                if (jsonObject.optString("qrtype").equals("usercar")) {
                    int mileage = jsonObject.optInt("mile");
                    int lastMaintainMile = jsonObject.optInt("last");
                    boolean lampWell = jsonObject.optInt("lamp") == 1; //汽车车灯状况
                    boolean engineWell = jsonObject.optInt("engine") == 1; //汽车发动机状况
                    boolean transmissionWell = jsonObject.optInt("tran") == 1; //汽车变速箱状况
                    int oilMass = jsonObject.optInt("oil"); //汽车油量
                    boolean tirePressure = jsonObject.optInt("tire") == 1; //胎压
                    int avgEcon = jsonObject.optInt("avg"); //平均油耗
                    boolean airSacSafe = jsonObject.optInt("air") == 1; //气囊安全状况

                    userCar.setMileage(mileage);
                    userCar.setLastMaintainMile(lastMaintainMile);
                    userCar.setLampWell(lampWell);
                    userCar.setEngineWell(engineWell);
                    userCar.setTransmissionWell(transmissionWell);
                    userCar.setOilMass(oilMass);
                    userCar.setTirePressure(tirePressure);
                    userCar.setAvgEcon(avgEcon);
                    userCar.setAirSacSafe(airSacSafe);

                    if (mAuthTask == null) {
                        mAuthTask = new ScanCarInfoTask();
                        mAuthTask.execute(
                                userInfoPreferences.getString("userphone", ""),
                                userCar.getId().toString(),
                                Integer.toString(mileage),
                                Integer.toString(lastMaintainMile),
                                Boolean.toString(lampWell),
                                Boolean.toString(engineWell),
                                Boolean.toString(transmissionWell),
                                Integer.toString(oilMass),
                                Boolean.toString(tirePressure),
                                Integer.toString(avgEcon),
                                Boolean.toString(airSacSafe));
                    } else {
                        Toast.makeText(CarDetailActivity.this, "后台正在执行，请稍后重试", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(CarDetailActivity.this, "二维码错误", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public class ScanCarInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();

            map.put("phone", params[0]);
            map.put("id", params[1]);
            map.put("mileage", params[2]);
            map.put("lastmaintainmile", params[3]);
            map.put("lampwell", params[4]);
            map.put("enginewell", params[5]);
            map.put("transmissionwell", params[6]);
            map.put("oilmass", params[7]);
            map.put("tirepressure", params[8]);
            map.put("avgecon", params[9]);
            map.put("airsacsafe", params[10]);
            return connection.uploadParams("", "ScanCarInfo", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            try {
                switch (Integer.parseInt(ResultJSONOperate.getRegisterCode(result))) {
                    case Constants.SCAN_USERCAR_SUCCESS:
                        showInfo();
                        break;
                    case Constants.SCAN_USERCAR_FAILED:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CarDetailActivity.this, "车型状况上传失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class DeleteUserCarTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();

            map.put("phone", params[0]);
            map.put("usercarid", params[1]);

            return connection.uploadParams("", "DeleteUserCar", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            delTask = null;
            try {
                switch (Integer.parseInt(ResultJSONOperate.getRegisterCode(result))) {
                    case Constants.DELETE_USERCAR_SUCCESS:
                        finish();
                        break;
                    case Constants.DELETE_USERCAR_FAILED:
                        Toast.makeText(CarDetailActivity.this, "删除车辆失败", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CarDetailActivity.this, "删除车辆异常", Toast.LENGTH_LONG).show();
            }
        }
    }
}
