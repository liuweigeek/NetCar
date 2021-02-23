package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import java.util.HashMap;
import java.util.Map;

public class AddNewCarActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    public SharedPreferences userInfoPreferences;
    public SharedPreferences.Editor userInfoEditor;

    private View mAddNewCarFormView;
    private View mProgressView;
    private View mGotoSetMyCar;
    private TextView mGotoTextSelectMyCar;
    private Button mSetUserCarInfoShortProvinceView;
    private EditText mSetUserCarInfoLicenseView;
    private EditText mSetUserCarInfoVinView;
    private EditText mSetUserCarInfoEngineView;

    private Integer userCarId;
    private ArrayAdapter<String> shortProvinceAdapter;

    private AddNewCarTask mAuthTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainActivity = MainActivity.mainActivity;
        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userInfoEditor = userInfoPreferences.edit();

        shortProvinceAdapter = new ArrayAdapter<>(this, R.layout.item_short_province_name, getData());
        mAddNewCarFormView = findViewById(R.id.add_new_car_form_view);
        mProgressView = findViewById(R.id.add_new_car_progress);
        mGotoSetMyCar = findViewById(R.id.add_new_car_goto_mycar);
        mGotoTextSelectMyCar = (TextView) findViewById(R.id.add_new_car_text_selectcar);
        mSetUserCarInfoShortProvinceView = (Button) findViewById(R.id.add_new_car_select_province);
        mSetUserCarInfoLicenseView = (EditText) findViewById(R.id.add_new_car_license);
        mSetUserCarInfoVinView = (EditText) findViewById(R.id.add_new_car_vin);
        mSetUserCarInfoEngineView = (EditText) findViewById(R.id.add_new_car_enginenum);

        mGotoSetMyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewCarActivity.this, AddMyCarActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        mSetUserCarInfoShortProvinceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(AddNewCarActivity.this);
                dialog.setContentView(R.layout.content_short_province_name);

                GridView shortProvinceNameGridView = (GridView) dialog.findViewById(R.id.short_province_name_gridview);

                shortProvinceNameGridView.setAdapter(shortProvinceAdapter);
                shortProvinceNameGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String shortName = shortProvinceAdapter.getItem(position);
                        if (shortName.length() > 0) {
                            mSetUserCarInfoShortProvinceView.setText(shortName);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private String[] getData() {
        return new String[]{"京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
                "吉", "翼", "晋", "辽", "鲁", "蒙", "闽", "宁", "青", "琼", "陕", "苏",
                "皖", "湘", "新", "渝", "豫", "粤", "云", "藏", "浙"};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.action_save:
                if (checkParams()) {
                    String phone = mainActivity.userInfoPreferences.getString("userphone", "");
                    if (!TextUtils.isEmpty(phone)) {
                        mAuthTask = new AddNewCarTask();
                        mAuthTask.execute(phone, Integer.toString(userCarId),
                                mSetUserCarInfoShortProvinceView.getText().toString().trim() + mSetUserCarInfoLicenseView.getText().toString(),
                                mSetUserCarInfoVinView.getText().toString(), mSetUserCarInfoEngineView.getText().toString());
                    } else {
                        Snackbar.make(mAddNewCarFormView, "请先登录", Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkParams() {
        if (TextUtils.isEmpty(mGotoTextSelectMyCar.getText().toString().trim())) {
            mSetUserCarInfoLicenseView.setError("请选择车型");
            return false;
        }
        if (TextUtils.isEmpty(mSetUserCarInfoShortProvinceView.getText().toString().trim())) {
            mSetUserCarInfoShortProvinceView.setError("请选择车牌区域");
            return false;
        }
        if (mSetUserCarInfoLicenseView.getText().toString().trim().length() != 6) {
            mSetUserCarInfoLicenseView.setError("请输入正确的车牌号");
            return false;
        }
        if (mSetUserCarInfoVinView.getText().toString().trim().length() != 6) {
            mSetUserCarInfoVinView.setError("请输入车架号后六位");
            return false;
        }
        if (mSetUserCarInfoEngineView.getText().toString().trim().length() != 6) {
            mSetUserCarInfoEngineView.setError("请输入发动机号后六位");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 3 && resultCode == 3) {
            Bundle bundle = intent.getExtras();
            String carInfo = bundle.getString("userCar");
            userCarId = bundle.getInt("usercarId");
            if (!TextUtils.isEmpty(carInfo)) {
                mGotoTextSelectMyCar.setText(carInfo);
            } else {
                mGotoTextSelectMyCar.setText("");
            }
        }
    }

    public class AddNewCarTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            map.put("usercarid", params[1]);
            map.put("license", params[2]);
            map.put("vin", params[3]);
            map.put("enginenum", params[3]);

            Connection connection = new Connection();
            return connection.uploadParams("", "AddNewCar", map);
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            int resCode;
            try {
                resCode = Integer.parseInt(ResultJSONOperate.getRegisterCode(result));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddNewCarActivity.this, "未知错误，请重试", Toast.LENGTH_LONG).show();
                return;
            }
            switch (resCode) {
                case Constants.ADD_USERCAR_SUCCESS:
                    Toast.makeText(MainActivity.mainActivity, "添加汽车成功", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case Constants.ADD_USERCAR_FAILED:
                    Toast.makeText(AddNewCarActivity.this, "添加汽车失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(AddNewCarActivity.this, "未知错误，请重试", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
