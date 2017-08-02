package com.imagine.scott.netcar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.Notification;
import com.imagine.scott.netcar.fragment.NotificationFragment;

import java.util.List;

/**
 * Created by Scott on 15/12/5.
 */
public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private NotificationFragment notificationFragment;
    private List<Notification> notifications;


    public NotificationListAdapter(NotificationFragment notificationFragment, List<Notification> notifications) {
        this.notificationFragment = notificationFragment;
        this.notifications = notifications;
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
    public NotificationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        //listItem.setBackgroundResource(mBackground);
        ViewHolder vh = new ViewHolder(listItem);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {

        TextView itemNotificationTitle = (TextView) holder.listItem.findViewById(R.id.item_notification_title);
        TextView itemNotificationLicense = (TextView) holder.listItem.findViewById(R.id.item_notification_license);
        TextView itemNotificationText = (TextView) holder.listItem.findViewById(R.id.item_notification_text);

        Notification notification = notifications.get(position);

        itemNotificationTitle.setText(notification.getTitle());
        itemNotificationLicense.setText(notification.getUserCar().getLicensePlateNumber());
        itemNotificationText.setText(notification.getText());
        holder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notificationFragment.onNotificationItemLongClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
