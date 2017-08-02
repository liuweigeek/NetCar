package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.Order;
import com.imagine.scott.netcar.fragment.AppointmentFragment;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ViewHolder> {

    private AppointmentFragment appointmentFragment;
    private List<Order> orders;


    public AppointmentListAdapter(AppointmentFragment appointmentFragment, List<Order> orders) {
        this.appointmentFragment = appointmentFragment;
        this.orders = orders;
        /*MainActivity.mainActivity.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
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
    public AppointmentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appo, parent, false);
        //listItem.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TextView itemAppoGasname = (TextView) holder.listItem.findViewById(R.id.item_appo_gasname);
        TextView itemAppoTime = (TextView) holder.listItem.findViewById(R.id.item_appo_time);
        ImageView itemAppoGasLogo = (ImageView) holder.listItem.findViewById(R.id.item_appo_gaslogo);
        switch (orders.get(position).getBrandname()) {
            case "中石油":
                itemAppoGasLogo.setImageDrawable(appointmentFragment.getResources().getDrawable(R.drawable.logo_cnpc));
                break;
            case "中石化":
                itemAppoGasLogo.setImageDrawable(appointmentFragment.getResources().getDrawable(R.drawable.logo_sinopec));
                break;
            case "壳牌":
                itemAppoGasLogo.setImageDrawable(appointmentFragment.getResources().getDrawable(R.drawable.logo_shell));
                break;
            default:
                itemAppoGasLogo.setImageDrawable(appointmentFragment.getResources().getDrawable(R.drawable.logo_gas_station));
        }
        itemAppoGasname.setText(orders.get(position).getGasStation());
        SimpleDateFormat sf=new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        itemAppoTime.setText(sf.format(orders.get(position).getDate()));
        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentFragment.onOrderItemSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
