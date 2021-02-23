package com.imagine.scott.netcar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.CityListModel;

import java.util.List;

public class CityModelListAdapter extends BaseAdapter {

    private List<CityListModel> mDate;
    private Context mContext;

    public CityModelListAdapter(Context mContext, List mDate) {
        this.mContext = mContext;
        this.mDate = mDate;
    }


    @Override
    public int getCount() {
        return mDate.size();
    }

    @Override
    public Object getItem(int position) {
        return mDate.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(mContext, R.layout.item_city_model, null);

        CityListModel model = mDate.get(position);
        TextView itemCityModelName = (TextView) view.findViewById(R.id.item_city_model_name);

        itemCityModelName.setText(model.getTextName());
        itemCityModelName.setTag(model.getNameId());

        return view;
    }
}
