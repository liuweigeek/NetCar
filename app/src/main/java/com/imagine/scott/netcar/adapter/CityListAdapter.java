package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.activity.SetRegionActivity;
import com.imagine.scott.netcar.bean.Region;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {

    private SetRegionActivity setRegionActivity;
    private List<Region> cityData;
    private View lastSelectItem;
    private Integer selectedPosition;

    public CityListAdapter(SetRegionActivity setRegionActivity, List<Region> cityData) {
        this.setRegionActivity = setRegionActivity;
        this.cityData = cityData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;

        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public CityListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (selectedPosition != null && position == selectedPosition) {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
        } else {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
        }
        TextView itemCityName = (TextView) holder.listItem.findViewById(R.id.item_city_name);
        itemCityName.setText(cityData.get(position).getRegionName());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectItem != null) {
                    lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
                }
                selectedPosition = position;
                lastSelectItem = holder.listItem;
                lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
                setRegionActivity.onCitySelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityData.size();
    }
}
