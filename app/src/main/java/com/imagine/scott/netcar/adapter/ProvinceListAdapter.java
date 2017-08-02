package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.activity.SetRegionActivity;
import com.imagine.scott.netcar.bean.Province;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class ProvinceListAdapter extends RecyclerView.Adapter<ProvinceListAdapter.ViewHolder> {

    private SetRegionActivity setRegionActivity;
    private List<Province> provincesData;
    private View lastSelectItem;
    private Integer selectedPosition;

    public ProvinceListAdapter(SetRegionActivity setRegionActivity, List<Province> provincesData) {
        this.setRegionActivity = setRegionActivity;
        this.provincesData = provincesData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;
        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public ProvinceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_province, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (selectedPosition != null && position == selectedPosition) {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
        } else {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
        }
        TextView itemVehicleBrandName = (TextView)holder.listItem.findViewById(R.id.item_province_name);
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectItem != null) {
                    lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
                }
                selectedPosition = position;
                lastSelectItem = holder.listItem;
                lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
                setRegionActivity.onProvinceSelected(position);
            }
        });
        itemVehicleBrandName.setText(provincesData.get(position).getRegionName());
    }

    @Override
    public int getItemCount() {
        return provincesData.size();
    }
}
