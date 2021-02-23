package com.imagine.scott.netcar.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.CityListAdapter;
import com.imagine.scott.netcar.adapter.ProvinceListAdapter;
import com.imagine.scott.netcar.bean.Province;
import com.imagine.scott.netcar.bean.Region;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetRegionActivity extends AppCompatActivity {

    private List<Province> itemProvinces;
    private List<Region> itemCitys;

    private RecyclerView provinceList;
    private RecyclerView cityList;

    private ProvinceListAdapter provinceListAdapter;
    private CityListAdapter cityListAdapter;

    private LinearLayoutManager mProvinceLayoutManager;
    private LinearLayoutManager mCityLayoutManager;

    private GetProvinceListTask mProvinceTask = null;
    private GetCityListTask mCityTask = null;

    private View mProgressView;
    private View mSetRegionFormView;

    private String currentProvinceName;
    private String currentCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_region);
        mProgressView = findViewById(R.id.set_region_progress);
        mSetRegionFormView = findViewById(R.id.set_region_form_view);
        itemProvinces = new ArrayList<>();
        itemCitys = new ArrayList<>();
        provinceList = (RecyclerView) findViewById(R.id.provinceList);
        cityList = (RecyclerView) findViewById(R.id.cityList);
        provinceList.setHasFixedSize(true);
        cityList.setHasFixedSize(true);
        mProvinceLayoutManager = new LinearLayoutManager(SetRegionActivity.this);
        mCityLayoutManager = new LinearLayoutManager(SetRegionActivity.this);
        provinceList.setLayoutManager(mProvinceLayoutManager);
        cityList.setLayoutManager(mCityLayoutManager);

        provinceListAdapter = new ProvinceListAdapter(SetRegionActivity.this, itemProvinces);
        provinceList.setAdapter(provinceListAdapter);

        showProgress(true);
        mProvinceTask = new GetProvinceListTask();
        mProvinceTask.execute();
    }

    public void onProvinceSelected(int position) {
        currentProvinceName = itemProvinces.get(position).getRegionName();
        mCityTask = new GetCityListTask();
        mCityTask.execute(itemProvinces.get(position).getId());
    }

    public void onCitySelected(int position) {
        currentCityName = itemCitys.get(position).getRegionName();
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
                Intent intent = getIntent();
                intent.putExtra("userRegion", currentProvinceName + " " + currentCityName);
                this.setResult(2, intent);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetProvinceListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            Connection connection = new Connection();
            return connection.uploadParams("", "GetProvince", null);
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            mProvinceTask = null;
            try {
                itemProvinces.clear();
                itemProvinces.addAll(ResultJSONOperate.getProvincesJson(result));
                provinceListAdapter.notifyDataSetChanged();
                provinceList.requestLayout();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SetRegionActivity.this, "省份信息获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class GetCityListTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {

            //showProgress(true);
            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();
            map.put("parentid", params[0]);
            return connection.uploadParams("", "GetCity", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mCityTask = null;
            try {
                itemCitys = ResultJSONOperate.getCitysJson(result);
                Log.v("Scott", Integer.toString(itemCitys.size()));
                cityListAdapter = new CityListAdapter(SetRegionActivity.this, itemCitys);
                cityListAdapter.notifyDataSetChanged();
                cityList.setAdapter(cityListAdapter);
                cityList.requestLayout();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SetRegionActivity.this, "城市信息获取失败", Toast.LENGTH_LONG).show();
            }

        }
    }

    //region show progress
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSetRegionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSetRegionFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSetRegionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSetRegionFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //endregion
}
