package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.AddMyCarActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.bean.Car;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class VehicleModelListAdapter extends RecyclerView.Adapter<VehicleModelListAdapter.ViewHolder> {

    private AddMyCarActivity addMyCarActivity;
    private List<Car> cars;
    private View lastSelectItem;
    private Integer selectedPosition;

    public VehicleModelListAdapter(AddMyCarActivity addMyCarActivity, List<Car> cars) {
        this.addMyCarActivity = addMyCarActivity;
        this.cars = cars;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;

        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public VehicleModelListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehiclemodel, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (selectedPosition != null && position == selectedPosition) {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
        } else {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
        }
        TextView itemVehicleModelName = (TextView) holder.listItem.findViewById(R.id.item_vehiclemodel_name);
        itemVehicleModelName.setText(cars.get(position).getVehicleModel());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectItem != null) {
                    lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
                }
                selectedPosition = position;
                lastSelectItem = holder.listItem;
                lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
                addMyCarActivity.onModelSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }
}
