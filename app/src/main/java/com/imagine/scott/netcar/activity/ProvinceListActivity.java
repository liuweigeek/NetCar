package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.ProvinceInfoJson;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.CityModelListAdapter;
import com.imagine.scott.netcar.bean.CityListModel;

import java.util.ArrayList;
import java.util.List;

public class ProvinceListActivity extends AppCompatActivity {
    private ListView provinceListView;
    private CityModelListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_city_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.city_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("选择省份");
        provinceListView = (ListView) findViewById(R.id.city_list_listview);

        mAdapter = new CityModelListAdapter(this, getData2());
        provinceListView.setAdapter(mAdapter);

        provinceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView itemCityModelName = (TextView) view.findViewById(R.id.item_city_model_name);

                Intent intent = new Intent();
                intent.putExtra("province_name", itemCityModelName.getText());
                intent.putExtra("province_id", itemCityModelName.getTag().toString());

                intent.setClass(ProvinceListActivity.this, CityListActivity.class);
                startActivityForResult(intent, 20);
            }
        });

    }

    private List<CityListModel> getData2() {

        List<CityListModel> list = new ArrayList<CityListModel>();
        List<ProvinceInfoJson> provinceList = WeizhangClient.getAllProvince();

        for (ProvinceInfoJson provinceInfoJson : provinceList) {
            String provinceName = provinceInfoJson.getProvinceName();
            int provinceId = provinceInfoJson.getProvinceId();

            CityListModel model = new CityListModel();
            model.setTextName(provinceName);
            model.setNameId(provinceId);
            list.add(model);
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        Bundle bundle = data.getExtras();
        // ��ȡ����name
        String cityName = bundle.getString("city_name");
        String cityId = bundle.getString("city_id");

        Intent intent = new Intent();
        intent.putExtra("city_name", cityName);
        intent.putExtra("city_id", cityId);
        setResult(1, intent);
        finish();
    }
}
