package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Activity.BookServiceActivity;
import com.codewithshubh.servozone.Interface.IItemClickListener;
import com.codewithshubh.servozone.Activity.LoginActivity;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_CATEGORY_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceCategory> serviceCategoryList;

    public ServiceCategoryAdapter(Context context, List<ServiceCategory> serviceCategoryList) {
        this.context = context;
        this.serviceCategoryList = serviceCategoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_service_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tv_category_name.setText(serviceCategoryList.get(position).getCategoryName());
        Picasso.get().load(serviceCategoryList.get(position).getImageUrl()).placeholder(R.drawable.placeholder)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.iv_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                holder.progress.setVisibility(View.VISIBLE);
                                int i = position;
                                Picasso.get().load(serviceCategoryList.get(i).getImageUrl()).placeholder(R.drawable.placeholder)
                                        .into(holder.iv_image, new Callback() {
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
        holder.setiItemClickListener(new IItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser()==null){
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
                else{
                    Intent i = new Intent(context, BookServiceActivity.class);
                    i.putExtra(SERVICE_ID, serviceCategoryList.get(position).getServiceId());
                    i.putExtra(SERVICE_CATEGORY_ID, serviceCategoryList.get(position).getId());
                    ((MainActivity) context).startActivityForResult(i, 4);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (serviceCategoryList != null ? serviceCategoryList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

       TextView tv_category_name;
       ImageView iv_image;

       AVLoadingIndicatorView progress;

       IItemClickListener iItemClickListener;

        public void setiItemClickListener(IItemClickListener iItemClickListener) {
            this.iItemClickListener = iItemClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_service_category_title);
            iv_image = itemView.findViewById(R.id.iv_service_category);
            progress = itemView.findViewById(R.id.service_category_avi);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iItemClickListener.onItemClickListener(v, getAdapterPosition());
        }
    }
}
