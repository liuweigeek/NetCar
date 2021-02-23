package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.fragment.CarsFragment;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class GasStationListAdapter extends RecyclerView.Adapter<GasStationListAdapter.ViewHolder> {

/*    private final int mBackground;
    private final TypedValue mTypedValue = new TypedValue();*/

    private CarsFragment carsFragment;
    private List<UserCar> userCars;


    public GasStationListAdapter(CarsFragment carsFragment, List<UserCar> userCars) {
        this.carsFragment = carsFragment;
        this.userCars = userCars;
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
    public GasStationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usercar, parent, false);
        //      listItem.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ImageView itemUsercarLogo = (ImageView) holder.listItem.findViewById(R.id.item_usercar_logo);
        TextView itemUsercarLicense = (TextView) holder.listItem.findViewById(R.id.item_usercar_license);
        TextView itemUsercarInfo = (TextView) holder.listItem.findViewById(R.id.item_usercar_info);
        itemUsercarLicense.setText(userCars.get(position).getLicensePlateNumber());

        UserCar userCar = userCars.get(position);
        itemUsercarLicense.setText(userCar.getLicensePlateNumber());
        itemUsercarInfo.setText(userCar.getCar().getVehicleBrandZh() + " " + userCar.getCar().getVehicleModel());
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carsFragment.onCarItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userCars.size();
    }
}
