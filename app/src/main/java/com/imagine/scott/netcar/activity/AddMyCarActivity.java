package com.imagine.scott.netcar.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.VehicleBrandListAdapter;
import com.imagine.scott.netcar.adapter.VehicleModelListAdapter;
import com.imagine.scott.netcar.bean.Car;
import com.imagine.scott.netcar.bean.VehicleBrand;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMyCarActivity extends AppCompatActivity {

    private List<VehicleBrand> itemVehicleBrands;
    private List<Car> itemCars;

    private RecyclerView vehicleBrandList;
    private RecyclerView vehicleModelList;

    private VehicleBrandListAdapter vehicleBrandListAdapter;
    private VehicleModelListAdapter vehicleModelListAdapter;

    private LinearLayoutManager mBrandLayoutManager;
    private LinearLayoutManager mModelLayoutManager;

    private GetBrandListTask mBrandTask = null;
    private GetModelListTask mModelTask = null;

    private View mProgressView;
    private View mAddMyCarFormView;

    private String currentBrandName = "";
    private String currentUserCarName = "";
    private Integer currentUserCarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_car);

        mProgressView = findViewById(R.id.add_mycar_progress);
        mAddMyCarFormView = findViewById(R.id.add_mycar_form_view);
        itemVehicleBrands = new ArrayList<>();
        itemCars = new ArrayList<>();
        vehicleBrandList = (RecyclerView) findViewById(R.id.vehiclebrandList);
        vehicleModelList = (RecyclerView) findViewById(R.id.vehiclemodelList);
        vehicleBrandList.setHasFixedSize(true);
        vehicleModelList.setHasFixedSize(true);
        mBrandLayoutManager = new LinearLayoutManager(AddMyCarActivity.this);
        mModelLayoutManager = new LinearLayoutManager(AddMyCarActivity.this);
        vehicleBrandList.setLayoutManager(mBrandLayoutManager);
        vehicleModelList.setLayoutManager(mModelLayoutManager);
        vehicleBrandList.setItemAnimator(new DefaultItemAnimator());
        vehicleModelList.setItemAnimator(new DefaultItemAnimator());

        vehicleBrandListAdapter = new VehicleBrandListAdapter(AddMyCarActivity.this, itemVehicleBrands);
        vehicleBrandList.setAdapter(vehicleBrandListAdapter);

        showProgress(true);
        mBrandTask = new GetBrandListTask();
        mBrandTask.execute();
    }

    public void onBrandSelected(int position) {
        currentBrandName = itemVehicleBrands.get(position).getVehicleBrandName();
        mModelTask = new GetModelListTask();
        mModelTask.execute(itemVehicleBrands.get(position).getVehicleBrandName());
    }

    public void onModelSelected(int position) {
        currentUserCarName = itemCars.get(position).getVehicleBrandZh() + " " + itemCars.get(position).getVehicleModel();
        currentUserCarId = itemCars.get(position).getId();
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
                intent.putExtra("userCar", currentUserCarName);
                intent.putExtra("usercarId", currentUserCarId);
                this.setResult(3, intent);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetBrandListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            Connection connection = new Connection();
            return connection.uploadParams("", "GetVehicleBrand", null);
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            mBrandTask = null;
            try {
                itemVehicleBrands.clear();
                itemVehicleBrands.addAll(ResultJSONOperate.getCarBrandsJson(result));
                vehicleBrandListAdapter.notifyDataSetChanged();
                vehicleBrandList.requestLayout();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddMyCarActivity.this, "汽车品牌获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class GetModelListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            //showProgress(true);
            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();
            map.put("vehiclebrand", params[0]);
            return connection.uploadParams("", "GetVehicleModel", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mModelTask = null;
            try {
                itemCars = ResultJSONOperate.getCarModelsJson(result);
                vehicleModelListAdapter = new VehicleModelListAdapter(AddMyCarActivity.this, itemCars);
                vehicleModelListAdapter.notifyDataSetChanged();
                vehicleModelList.setAdapter(vehicleModelListAdapter);
                vehicleModelList.requestLayout();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddMyCarActivity.this, "车型信息获取失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //region show progress
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAddMyCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddMyCarFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddMyCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddMyCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //endregion
}
