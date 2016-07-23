package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.activity.QueryPositionActivity;
import com.imagine.scott.netcar.activity.SearchedListActivity;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.fragment.BreakRulesFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class QueryPositionListAdapter extends RecyclerView.Adapter<QueryPositionListAdapter.ViewHolder> {

    private ArrayList<PoiItem> poiItems;
    private QueryPositionActivity queryPositionActivity;

    public QueryPositionListAdapter(QueryPositionActivity queryPositionActivity) {
        this.queryPositionActivity = queryPositionActivity;
        this.poiItems = queryPositionActivity.poiItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;
        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public QueryPositionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_query_position, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TextView itemSearchedName = (TextView) holder.listItem.findViewById(R.id.item_query_position_name);
        itemSearchedName.setText(poiItems.get(position).getTitle());

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryPositionActivity.selected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return queryPositionActivity.poiItems.size();
    }
}
