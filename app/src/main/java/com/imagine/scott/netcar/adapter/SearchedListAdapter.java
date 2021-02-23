package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.SearchedListActivity;

import java.util.ArrayList;

/**
 * Created by Scott on 15/12/5.
 */
public class SearchedListAdapter extends RecyclerView.Adapter<SearchedListAdapter.ViewHolder> {

    private ArrayList<PoiItem> poiItems;
    private SearchedListActivity searchedListActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listItem;

        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    public SearchedListAdapter(SearchedListActivity searchedListActivity) {
        this.searchedListActivity = searchedListActivity;
        this.poiItems = SearchedListActivity.poiItems;
    }

    @Override
    public SearchedListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_searched_result, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView itemSearchedName = (TextView) holder.listItem.findViewById(R.id.item_searched_name);
/*        TextView itemSearchedDistance = (TextView) holder.listItem.findViewById(R.id.item_searched_distance);
        TextView itemSearchedDirection = (TextView) holder.listItem.findViewById(R.id.item_searched_direction);*/
        itemSearchedName.setText(poiItems.get(position).getTitle());

        //itemSearchedDistance.setText(poiItems.get(position).getDistance());
        //itemSearchedDirection.setText(poiItems.get(position).getDirection());

        holder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedListActivity.selected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return SearchedListActivity.poiItems.size();
    }
}