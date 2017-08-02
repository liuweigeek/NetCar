package com.imagine.scott.netcar.activity;

import android.app.Activity;
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
import com.cheshouye.api.client.json.CityInfoJson;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.CityModelListAdapter;
import com.imagine.scott.netcar.bean.CityListModel;

import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends AppCompatActivity {
	private ListView cityListView;
	private CityModelListAdapter mAdapter;
	
	private String provinceName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_list);
		Toolbar toolbar = (Toolbar) findViewById(R.id.city_list_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("选择城市");
		Bundle bundle = getIntent().getExtras();
		provinceName = bundle.getString("province_name");
		final String provinceId = bundle.getString("province_id");


		cityListView = (ListView) findViewById(R.id.city_list_listview);

		mAdapter = new CityModelListAdapter(this, getData(provinceId));
		cityListView.setAdapter(mAdapter);

		cityListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {

				TextView txt_name = (TextView) view.findViewById(R.id.item_city_model_name);

				Intent intent = new Intent();
				intent.putExtra("city_name", txt_name.getText());
				intent.putExtra("city_id",
						txt_name.getTag().toString());
				setResult(20, intent);
				finish();
			}
		});
	}

	private List<CityListModel> getData(String provinceId) {
		List<CityListModel> list = new ArrayList<>();

		List<CityInfoJson> cityList = WeizhangClient.getCitys(Integer
				.parseInt(provinceId));

		for (CityInfoJson cityInfoJson : cityList) {
			String cityName = cityInfoJson.getCity_name();
			int cityId = cityInfoJson.getCity_id();

			CityListModel model = new CityListModel();
			model.setNameId(cityId);
			model.setTextName(cityName);
			list.add(model);
		}
		return list;
	}
}
