package com.imagine.scott.netcar.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.GasStationDetailActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.bean.GasStation;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.fragment.CarsFragment;
import com.imagine.scott.netcar.fragment.GasStationFragment;
import com.imagine.scott.netcar.operation.Transform;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class GasListAdapter extends RecyclerView.Adapter<GasListAdapter.ViewHolder> {

/*    private final int mBackground;
    private final TypedValue mTypedValue = new TypedValue();*/

    private GasStationFragment gasStationFragment;
    private List<GasStation> gasStations;


    public GasListAdapter(GasStationFragment gasStationFragment, List<GasStation> gasStations) {
        this.gasStationFragment = gasStationFragment;
        this.gasStations = gasStations;
/*        MainActivity.mainActivity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;*/
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;
        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public GasListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gas_station, parent, false);
  //      listItem.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ImageView itemGasLogo = (ImageView) holder.listItem.findViewById(R.id.item_gas_logo);
        TextView itemGasName = (TextView) holder.listItem.findViewById(R.id.item_gas_name);
        TextView itemGasAddr = (TextView) holder.listItem.findViewById(R.id.item_gas_addr);
        TextView itemGasDistance = (TextView) holder.listItem.findViewById(R.id.item_gas_distance);

        GasStation gasStation = gasStations.get(position);

        itemGasName.setText(gasStation.getName());
        itemGasAddr.setText(gasStation.getAddress());
        itemGasDistance.setText(Transform.meterToKilo(gasStation.getDistance()));

        String brandName = gasStation.getBrandname();
        switch (brandName) {
            case "中石油":
                itemGasLogo.setImageDrawable(gasStationFragment.getResources().getDrawable(R.drawable.logo_cnpc));
                break;
            case "中石化":
                itemGasLogo.setImageDrawable(gasStationFragment.getResources().getDrawable(R.drawable.logo_sinopec));
                break;
            case "壳牌":
                itemGasLogo.setImageDrawable(gasStationFragment.getResources().getDrawable(R.drawable.logo_shell));
                break;
            default:
                itemGasLogo.setImageDrawable(gasStationFragment.getResources().getDrawable(R.drawable.logo_gas_station));
        }
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gasStationFragment.onGasClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gasStations.size();
    }
}
