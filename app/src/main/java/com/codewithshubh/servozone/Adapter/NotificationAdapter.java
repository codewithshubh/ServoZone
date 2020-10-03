package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Model.Notification;
import com.codewithshubh.servozone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.codewithshubh.servozone.Constant.Constants.DAY_MILLIS;
import static com.codewithshubh.servozone.Constant.Constants.HOUR_MILLIS;
import static com.codewithshubh.servozone.Constant.Constants.MINUTE_MILLIS;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mynotification, parent, false);
        return new NotificationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_title.setText(notificationList.get(position).getTitle());
        holder.tv_message.setText(notificationList.get(position).getMessage());
        holder.tv_time.setText(getTimeAgo(notificationList.get(position).getTimeStamp()));
        if (notificationList.get(position).getIconUrl().equals(""))
        {
            Picasso.get().load(R.drawable.icon_splash).into(holder.iv_icon);
        }
        else {
            Picasso.get().load(notificationList.get(position).getIconUrl()).placeholder(R.drawable.default_image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.iv_icon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {
                            int i = position;
                            Picasso.get().load(notificationList.get(i).getIconUrl()).placeholder(R.drawable.default_image)
                                    .into(holder.iv_icon, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(R.drawable.icon_splash).into(holder.iv_icon);
                                        }
                                    });
                        }
                    });
        }
        String imageUrl = notificationList.get(position).getImageUrl();
        if (imageUrl.equals(""))
            holder.iv_image.setVisibility(View.GONE);
        else {
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.iv_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError(Exception e) {
                            int i = position;
                            Picasso.get().load(imageUrl).placeholder(R.drawable.default_image)
                                    .into(holder.iv_image, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(R.drawable.default_image).into(holder.iv_image);
                                        }
                                    });
                        }
                    });
        }

        String action = notificationList.get(position).getAction();
        String destination = notificationList.get(position).getActionDestination();

        if (!action.equals("url")){
            holder.btn_link.setVisibility(View.GONE);
        }

        holder.btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
                context.startActivity(browserIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (notificationList!=null?notificationList.size():0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon, iv_image;
        TextView tv_title, tv_message, tv_time;
        Button btn_link;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.layout_mynotification_icon_iv);
            iv_image = itemView.findViewById(R.id.layout_mynotification_image_iv);
            tv_title = itemView.findViewById(R.id.layout_mynotification_title_tv);
            tv_message = itemView.findViewById(R.id.layout_mynotification_message_tv);
            tv_time = itemView.findViewById(R.id.layout_mynotification_time_tv);
            btn_link = itemView.findViewById(R.id.layout_mynotification_btn_link);
        }
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
