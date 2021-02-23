package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.AddMyCarActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.bean.VehicleBrand;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class VehicleBrandListAdapter extends RecyclerView.Adapter<VehicleBrandListAdapter.ViewHolder> {

    private AddMyCarActivity addMyCarActivity;
    private List<VehicleBrand> vehicleBrandData;
    private View lastSelectItem;
    private Integer selectedPosition;

    public VehicleBrandListAdapter(AddMyCarActivity addMyCarActivity, List<VehicleBrand> vehicleBrandData) {
        this.addMyCarActivity = addMyCarActivity;
        this.vehicleBrandData = vehicleBrandData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;

        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public VehicleBrandListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehiclebrand, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        VehicleBrand vehicleBrand = vehicleBrandData.get(position);
        if (selectedPosition != null && position == selectedPosition) {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
        } else {
            holder.listItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
        }
        ImageView itemVehicleBrandLogo = (ImageView) holder.listItem.findViewById(R.id.item_vehiclebrand_logo);
        TextView itemVehicleBrandName = (TextView) holder.listItem.findViewById(R.id.item_vehiclebrand_name);
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectItem != null) {
                    lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.colorWhite));
                }
                selectedPosition = position;
                lastSelectItem = holder.listItem;
                lastSelectItem.setBackgroundColor(MainActivity.mainActivity.getResources().getColor(R.color.itemSelectedColor));
                addMyCarActivity.onBrandSelected(position);
            }
        });

        itemVehicleBrandName.setText(vehicleBrand.getVehicleBrandZhName());
        String url = MainActivity.mainActivity.addrPreferences.getString("IP", null);
        if (!TextUtils.isEmpty(url)) {
            StringBuffer logourl = new StringBuffer("http://" + url + "/NetCar/carlogo/" + vehicleBrand.getVehicleBrandName() + ".png");
            Picasso.with(itemVehicleBrandLogo.getContext()).load(logourl.toString()).into(itemVehicleBrandLogo);
        }
    }

    @Override
    public int getItemCount() {
        return vehicleBrandData.size();
    }
}
