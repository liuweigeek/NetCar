package com.imagine.scott.netcar.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;
import com.imagine.scott.netcar.widget.CircleImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddInfoActivity extends AppCompatActivity {

    private View mAddInfoFormView;
    private View mProgressView;
    private Spinner mDrivingYearsSpinnerView;
    private Spinner mSexSpinnerView;
    private View mGotoSetRegion;
    private View mGotoSetMyCar;
    private TextView mGotoTextSelectRegion;
    private TextView mGotoTextSelectMyCar;
    private View mUsercarInfoFormView;
    private Button mSetUserCarInfoShortProvince;
    private EditText mSetUserCarInfoLicenseView;
    private EditText mSetUserCarInfoVinView;
    private EditText mSetUserCarInfoEngineView;

    private CircleImageView addInfoHeadImage;

    private ArrayAdapter<String> shortProvinceAdapter;

    private String picPath = null;

    private String phone;

    private String userRegion;
    private Integer userCarId;

    private SaveMoreInfoTask mAuthTask;
    private SaveHeadImageTask mUploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        shortProvinceAdapter = new ArrayAdapter<>(this, R.layout.item_short_province_name, getData());
        mAddInfoFormView = findViewById(R.id.add_info_form_view);
        mProgressView = findViewById(R.id.add_info_progress);
        mDrivingYearsSpinnerView = (Spinner) findViewById(R.id.add_info_spinner_drivingyears);
        mSexSpinnerView = (Spinner) findViewById(R.id.add_info_spinner_sex);
        mGotoSetRegion = findViewById(R.id.add_info_goto_region);
        mGotoSetMyCar = findViewById(R.id.add_info_goto_mycar);
        mGotoTextSelectRegion = (TextView) findViewById(R.id.add_info_text_selectregion);
        mGotoTextSelectMyCar = (TextView) findViewById(R.id.add_info_text_selectcar);

        mUsercarInfoFormView = findViewById(R.id.set_usercar_info_form_view);

        mSetUserCarInfoShortProvince = (Button) findViewById(R.id.set_usercar_info_select_province);
        mSetUserCarInfoLicenseView = (EditText) findViewById(R.id.set_usercar_info_license);
        mSetUserCarInfoVinView = (EditText) findViewById(R.id.set_usercar_info_vin);
        mSetUserCarInfoEngineView = (EditText) findViewById(R.id.set_usercar_info_engine);

        addInfoHeadImage = (CircleImageView) findViewById(R.id.add_info_headimage);

        mGotoSetRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddInfoActivity.this, SetRegionActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        mGotoSetMyCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddInfoActivity.this, AddMyCarActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        mSetUserCarInfoShortProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(AddInfoActivity.this);
                dialog.setContentView(R.layout.content_short_province_name);

                GridView shortProvinceNameGridView = (GridView) dialog.findViewById(R.id.short_province_name_gridview);

                shortProvinceNameGridView.setAdapter(shortProvinceAdapter);
                shortProvinceNameGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String shortName = shortProvinceAdapter.getItem(position);
                        if(shortName.length()>0){
                            mSetUserCarInfoShortProvince.setText(shortName);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        addInfoHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        picPath = null;
                    }
                }).create();
        dialog.show();
    }

    private String[] getData() {
        return new String[] {"京", "津", "沪", "川", "鄂", "甘", "赣", "桂", "贵", "黑",
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
            case R.id.action_save:
                if (checkParams()) {
                    String sex = mSexSpinnerView.getSelectedItem().toString();
                    String drivingYears = mDrivingYearsSpinnerView.getSelectedItem().toString();
                    mAuthTask = new SaveMoreInfoTask();
                    mAuthTask.execute(phone, sex, drivingYears, userRegion, Integer.toString(userCarId),
                            mSetUserCarInfoShortProvince.getText().toString() + mSetUserCarInfoLicenseView.getText().toString(),
                            mSetUserCarInfoVinView.getText().toString(), mSetUserCarInfoEngineView.getText().toString());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkParams() {
        if (TextUtils.isEmpty(mDrivingYearsSpinnerView.getSelectedItem().toString())) {
            Toast.makeText(AddInfoActivity.this, "请选择驾龄", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(mSexSpinnerView.getSelectedItem().toString())) {
            Toast.makeText(AddInfoActivity.this, "请选择性别", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(userRegion)) {
            mGotoTextSelectRegion.setError("请选择所在地区");
            return false;
        }
        if (TextUtils.isEmpty(mGotoTextSelectMyCar.getText().toString())) {
            mGotoTextSelectMyCar.setError("请选择车型");
        }
        if (TextUtils.isEmpty(mSetUserCarInfoShortProvince.getText().toString())) {
            mSetUserCarInfoShortProvince.setError("请选择车牌区域");
            return false;
        }
        if (mSetUserCarInfoLicenseView.getText().toString().length() != 6) {
            mSetUserCarInfoLicenseView.setError("请输入正确的车牌号");
            return false;
        }
        if (mSetUserCarInfoVinView.getText().toString().length() != 6) {
            mSetUserCarInfoVinView.setError("请输入车架号后六位");
            return false;
        }
        if (TextUtils.isEmpty(mGotoTextSelectMyCar.getText().toString())) {
            mSetUserCarInfoLicenseView.setError("请选择车型");
            return false;
        }
        if (mSetUserCarInfoEngineView.getText().toString().length() != 6) {
            mSetUserCarInfoEngineView.setError("请输入发动机号后六位");
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 2 && resultCode == 2) {
            Bundle bundle = intent.getExtras();
            String regionInfo = bundle.getString("userRegion");
            if (!TextUtils.isEmpty(regionInfo)) {
                userRegion = regionInfo;
                mGotoTextSelectRegion.setText(regionInfo);
            } else {
                userRegion = "";
                mGotoTextSelectRegion.setText("");
            }
        } else if (requestCode == 3 && resultCode == 3) {
            Bundle bundle = intent.getExtras();
            String carInfo = bundle.getString("userCar");
            userCarId = bundle.getInt("usercarId");
            if (!TextUtils.isEmpty(carInfo)) {
                mGotoTextSelectMyCar.setText(carInfo);
                mUsercarInfoFormView.setVisibility(View.VISIBLE);
            } else {
                mGotoTextSelectMyCar.setText("");
                mUsercarInfoFormView.setVisibility(View.INVISIBLE);
            }
        } else if (resultCode == Activity.RESULT_OK) {
            /**
             * 当选择的图片不为空的话，在获取到图片的途径
             */
            Uri uri = intent.getData();
            if (uri.toString().endsWith("jpg") || uri.toString().endsWith("png")) {
                if (mUploadImage == null) {
                    try {
                        ContentResolver cr = this.getContentResolver();
                        Bitmap bitmap = BitmapFactory.decodeStream(cr
                                .openInputStream(uri));
                        addInfoHeadImage.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mUploadImage == null) {
                        mUploadImage = new SaveHeadImageTask();
                        mUploadImage.execute(uri.toString().substring(7));
                    }
                }
            }
        }
    }

    public class SaveMoreInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            map.put("sex", params[1]);
            map.put("drivingyears", params[2]);
            map.put("userregion", params[3]);
            map.put("usercarid", params[4]);
            map.put("license", params[5]);
            map.put("vin", params[6]);
            map.put("enginenum", params[7]);

            Connection connection = new Connection();
            return connection.uploadParams("", "AddMoreUserInfo", map);
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            int resCode;
            try {
                resCode = Integer.parseInt(ResultJSONOperate.getRegisterCode(result));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddInfoActivity.this, "未知错误，请重试", Toast.LENGTH_LONG).show();
                return;
            }
            switch (resCode) {
                case Constants.ADD_INFO_SUCCESS:
                    finish();
                    break;
                case Constants.ADD_INFO_FAILED:
                    Toast.makeText(AddInfoActivity.this, "填充信息失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(AddInfoActivity.this, "未知错误，请重试", Toast.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public class SaveHeadImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            File picFile = new File(params[0]);
            Connection connection = new Connection();
            return connection.upLoadFile(phone, "UploadHeadImage", picFile);
        }

        @Override
        protected void onPostExecute(String result) {
            mUploadImage = null;
            int resCode;
            try {
                resCode = Integer.parseInt(ResultJSONOperate.getRegisterCode(result));
                switch (resCode) {
                    case Constants.UPLOAD_HEADIMAGE_SUCCESS:
                        Toast.makeText(AddInfoActivity.this, "上传头像成功", Toast.LENGTH_LONG).show();
                        break;
                    case Constants.UPLOAD_HEADIMAGE_FAILED:
                        Toast.makeText(AddInfoActivity.this, "上传头像失败", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(AddInfoActivity.this, "上传头像异常", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mUploadImage = null;
        }
    }
}
