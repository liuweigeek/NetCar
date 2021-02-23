package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.BreakRuleResultActivity;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class BreakRuleResultListAdapter extends RecyclerView.Adapter<BreakRuleResultListAdapter.ViewHolder> {

    private BreakRuleResultActivity breakRuleResultActivity;
    private List<WeizhangResponseHistoryJson> mDate;

    public BreakRuleResultListAdapter(BreakRuleResultActivity breakRuleResultActivity, List<WeizhangResponseHistoryJson> mDate) {
        this.breakRuleResultActivity = breakRuleResultActivity;
        this.mDate = mDate;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View listItem;

        public ViewHolder(View v) {
            super(v);
            listItem = v;
        }
    }

    @Override
    public BreakRuleResultListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_break_rule_result, parent, false);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        WeizhangResponseHistoryJson model = mDate.get(position);

        TextView wz_time = (TextView) holder.listItem.findViewById(R.id.item_break_rule_result_time);
        TextView wz_money = (TextView) holder.listItem.findViewById(R.id.item_break_rule_result_money);
        TextView wz_addr = (TextView) holder.listItem.findViewById(R.id.item_break_rule_result_addr);
        TextView wz_info = (TextView) holder.listItem.findViewById(R.id.item_break_rule_result_info);

        wz_time.setText(model.getOccur_date());
        wz_money.setText("计" + model.getFen() + "分, 罚" + model.getMoney() + "元");
        wz_addr.setText(model.getOccur_area());
        wz_info.setText(model.getInfo());
    }

    @Override
    public int getItemCount() {
        return mDate.size();
    }
}
