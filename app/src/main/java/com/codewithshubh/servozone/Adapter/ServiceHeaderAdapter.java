package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Activity.ServiceActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_NAME;

public class ServiceHeaderAdapter extends RecyclerView.Adapter<ServiceHeaderAdapter.MyViewHolder> {
    private Context context;
    private List<ServiceGroup> serviceGroupList;
    private int px;

    public ServiceHeaderAdapter(Context context, List<ServiceGroup> serviceGroupList) {
        this.context = context;
        this.serviceGroupList = serviceGroupList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_service_header, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServiceHeaderAdapter.MyViewHolder holder, final int position) {

        holder.tv_service_name.setText(serviceGroupList.get(position).getServiceName());
        Log.d("ServiceHeaderAdapter", "onBindViewHolder: "+serviceGroupList.get(position).getImageUrl());
        Picasso.get().load(serviceGroupList.get(position).getImageUrl()).placeholder(R.drawable.default_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.iv_header, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progress.setVisibility(View.VISIBLE);
                        int i = position;
                        Picasso.get().load(serviceGroupList.get(i).getImageUrl()).placeholder(R.drawable.default_image)
                                .into(holder.iv_header, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.progress.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }
                });



        //card click
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServiceActivity.class);
                intent.putExtra(SERVICE_NAME, serviceGroupList.get(position).getServiceName());
                intent.putExtra(SERVICE_ID, serviceGroupList.get(position).getId());
                context.startActivity(intent);
                //Toast.makeText(context, "Button More: " + holder.tv_service_name.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (serviceGroupList != null ? serviceGroupList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_service_name;
        ImageView iv_header;
        CardView cardView;
        AVLoadingIndicatorView progress;
        //Button btn_view_all;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_service_name = itemView.findViewById(R.id.tv_service_header_title);
            cardView = itemView.findViewById(R.id.card_view_home_header);
            iv_header = itemView.findViewById(R.id.iv_service_header);
            progress = itemView.findViewById(R.id.service_header_avi);
        }
    }


}
