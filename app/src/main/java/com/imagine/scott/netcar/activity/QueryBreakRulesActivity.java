package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.InputConfigJson;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.UserCar;

public class QueryBreakRulesActivity extends AppCompatActivity {

    private View queryBreakRulesRegionGoto;
    private TextView queryBreakRulesRegion;
    private Button queryBreakRulesSelectProvince;
    private EditText queryBreakRulesLicense;
    private EditText queryBreakRulesVin;
    private EditText queryBreakRulesEngineNum;
    private Button queryBreakRulesQuery;

    private ArrayAdapter<String> shortProvinceAdapter;

    private UserCar userCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_break_rules);
        Toolbar toolbar = (Toolbar) findViewById(R.id.query_break_rules_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("违章查询");
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        userCar = (UserCar) bundle.getSerializable("usercar");
        shortProvinceAdapter = new ArrayAdapter<>(this, R.layout.item_short_province_name, getData());
        initView();
        setListener();
    }

    public void initView() {
        queryBreakRulesRegionGoto = findViewById(R.id.query_break_rules_region_goto);
        queryBreakRulesRegion = (TextView) findViewById(R.id.query_break_rules_region);
        queryBreakRulesSelectProvince = (Button) findViewById(R.id.query_break_rules_select_province);
        queryBreakRulesLicense = (EditText) findViewById(R.id.query_break_rules_license);
        queryBreakRulesVin = (EditText) findViewById(R.id.query_break_rules_vin);
        queryBreakRulesEngineNum = (EditText) findViewById(R.id.query_break_rules_enginenum);
        queryBreakRulesQuery = (Button) findViewById(R.id.query_break_rules_query);
        if (userCar != null) {
            queryBreakRulesSelectProvince.setText(Character.toString(userCar.getLicensePlateNumber().charAt(0)));
            queryBreakRulesLicense.setText(userCar.getLicensePlateNumber().substring(1, 7));
            queryBreakRulesVin.setText(userCar.getVin());
            queryBreakRulesEngineNum.setText(userCar.getEngineNum());
        }
    }

    private String[] getData() {
        return new String[]{"京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
                "吉", "翼", "晋", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏",
                "皖", "湘", "新", "渝", "豫", "粤", "云", "藏", "浙"};
    }

    public void setListener() {
        queryBreakRulesSelectProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(QueryBreakRulesActivity.this);
                dialog.setContentView(R.layout.content_short_province_name);

                GridView shortProvinceNameGridView = (GridView) dialog.findViewById(R.id.short_province_name_gridview);

                shortProvinceNameGridView.setAdapter(shortProvinceAdapter);
                shortProvinceNameGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String shortName = shortProvinceAdapter.getItem(position);
                        if (shortName.length() > 0) {
                            queryBreakRulesSelectProvince.setText(shortName);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        queryBreakRulesRegionGoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(QueryBreakRulesActivity.this, ProvinceListActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        queryBreakRulesQuery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                CarInfo car = new CarInfo();
                String quertCityStr = null;
                String quertCityIdStr = null;

                final String shortProvinceName = queryBreakRulesSelectProvince.getText().toString()
                        .trim();
                final String licenseNumStr = queryBreakRulesLicense.getText()
                        .toString().trim();
                if (queryBreakRulesRegion.getText() != null
                        && !queryBreakRulesRegion.getText().equals("")) {
                    quertCityStr = queryBreakRulesRegion.getText().toString().trim();

                }

                if (queryBreakRulesRegion.getTag() != null
                        && !queryBreakRulesRegion.getTag().equals("")) {
                    quertCityIdStr = queryBreakRulesRegion.getTag().toString().trim();
                    car.setCity_id(Integer.parseInt(quertCityIdStr));
                }
                final String vinNumStr = queryBreakRulesVin.getText()
                        .toString().trim();
                final String engineNumStr = queryBreakRulesEngineNum.getText()
                        .toString().trim();

                Intent intent = new Intent();

                car.setChejia_no(vinNumStr);
                car.setChepai_no(shortProvinceName + licenseNumStr);

                car.setEngine_no(engineNumStr);

                Bundle bundle = new Bundle();
                bundle.putSerializable("carInfo", car);
                intent.putExtras(bundle);

                boolean result = checkQueryItem(car);

                if (result) {
                    intent.setClass(QueryBreakRulesActivity.this, BreakRuleResultActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        switch (resultCode) {
            case 0:
                Bundle bundle = data.getExtras();
                String ShortName = bundle.getString("short_name");
                queryBreakRulesSelectProvince.setText(ShortName);
                break;
            case 1:
                Bundle bundle1 = data.getExtras();
                String cityName = bundle1.getString("city_name");
                String cityId = bundle1.getString("city_id");
                queryBreakRulesRegion.setText(cityName);
                queryBreakRulesRegion.setTag(cityId);
                InputConfigJson inputConfig =
                        WeizhangClient.getInputConfig(Integer.parseInt(cityId));
                System.out.println(inputConfig.toJson());
                setQueryItem(Integer.parseInt(cityId));
                break;
        }
    }

    private void setQueryItem(int cityId) {

        InputConfigJson cityConfig = WeizhangClient.getInputConfig(cityId);

        if (cityConfig != null) {

            Log.v("Scott city id is ", Integer.toString(cityId));

            CityInfoJson city = WeizhangClient.getCity(cityId);

            queryBreakRulesRegion.setText(city.getCity_name());
            queryBreakRulesRegion.setTag(cityId);

            int len_vin = cityConfig.getClassno();
            int len_engine = cityConfig.getEngineno();

            String str = Integer.toString(len_vin) + " " + Integer.toString(len_engine);
            Log.v("Scott vin and engine is", str);

            View row_vin = findViewById(R.id.query_break_rules_vin_row);
            View row_engine = findViewById(R.id.query_break_rules_enginenum_row);

            if (len_vin == 0) {
                Log.v("Scott", " gone vin");
                row_vin.setVisibility(View.GONE);
            } else {
                row_vin.setVisibility(View.VISIBLE);
                setMaxlength(queryBreakRulesVin, len_vin);
                if (len_vin == -1) {
                    queryBreakRulesVin.setHint("请输入完整车架号");
                } else if (len_vin > 0) {
                    queryBreakRulesVin.setHint("请输入车架号后" + len_vin + "位");
                }
            }

            if (len_engine == 0) {
                row_engine.setVisibility(View.GONE);
            } else {
                row_engine.setVisibility(View.VISIBLE);
                setMaxlength(queryBreakRulesEngineNum, len_engine);
                if (len_engine == -1) {
                    queryBreakRulesEngineNum.setHint("请输入完整车发动机号");
                } else if (len_engine > 0) {
                    queryBreakRulesEngineNum.setHint("请输入发动机后" + len_engine + "位");
                }
            }
        }
    }

    private boolean checkQueryItem(CarInfo car) {
        if (car.getCity_id() == 0) {
            queryBreakRulesRegion.setError("请选择查询地");
            return false;
        }

        if (car.getChepai_no().length() != 7) {
            queryBreakRulesLicense.setError("车牌号有误");
            return false;
        }

        if (car.getCity_id() > 0) {
            InputConfigJson inputConfig = WeizhangClient.getInputConfig(car
                    .getCity_id());
            int engineno = inputConfig.getEngineno();
            int registno = inputConfig.getRegistno();
            int classno = inputConfig.getClassno();

            if (classno > 0) {
                if (car.getChejia_no().equals("")) {
                    queryBreakRulesVin.setError("不可为空");
                    return false;
                }

                if (car.getChejia_no().length() != classno) {
                    queryBreakRulesVin.setError("请输入车架号后六位");
                    return false;
                }
            } else if (classno < 0) {
                if (car.getChejia_no().length() == 0) {
                    queryBreakRulesVin.setError("输入全部车架号");
                    return false;
                }
            }

            if (engineno > 0) {
                if (car.getEngine_no().equals("")) {
                    queryBreakRulesEngineNum.setError("不可为空");
                    return false;
                }

                if (car.getEngine_no().length() != engineno) {
                    queryBreakRulesEngineNum.setError("发动机号有误");
                    return false;
                }
            } else if (engineno < 0) {
                if (car.getEngine_no().length() == 0) {
                    queryBreakRulesEngineNum.setError("输入全部发动机号");
                    return false;
                }
            }

            if (registno > 0) {
                if (car.getRegister_no().equals("")) {
                    Toast.makeText(QueryBreakRulesActivity.this, "输入证书编号不为空", Toast.LENGTH_LONG).show();
                    return false;
                }

                if (car.getRegister_no().length() != registno) {
                    Toast.makeText(QueryBreakRulesActivity.this, "输入证书编号后" + registno + "位", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else if (registno < 0) {
                if (car.getRegister_no().length() == 0) {
                    Toast.makeText(QueryBreakRulesActivity.this, "输入全部证书编号", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return true;
        }
        return false;

    }

    //设置/取消最大长度限制
    private void setMaxlength(EditText et, int maxLength) {
        if (maxLength > 0) {
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                    maxLength)});
        } else { //不限制
            et.setFilters(new InputFilter[]{});
        }
    }

}
