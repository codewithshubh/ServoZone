package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Activity.BookServiceActivity;
import com.codewithshubh.servozone.Activity.LoginActivity;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Activity.ServiceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.List;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_CATEGORY_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;

public class ServiceBookAdapter extends RecyclerView.Adapter<ServiceBookAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceCategory> serviceCategoryList;
    private int px;

    public ServiceBookAdapter(Context context, List<ServiceCategory> serviceCategoryList) {
        this.context = context;
        this.serviceCategoryList = serviceCategoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5,r.getDisplayMetrics()));
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_service_booking, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        /*ViewGroup.MarginLayoutParams layoutParams;
        layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        if(position%2==0){
            if(position == 0){

                layoutParams.rightMargin = px;
                layoutParams.topMargin = px;
                holder.cardView.requestLayout();
            }
            else if( position == serviceCategoryList.size()-2){
                layoutParams.rightMargin = px;
                layoutParams.bottomMargin = px;
                holder.cardView.requestLayout();
            }
            else{
                layoutParams.rightMargin = px;
                holder.cardView.requestLayout();
            }
        }
        else{
            if(position == 1){
                layoutParams.rightMargin = px;
                layoutParams.topMargin = px;
                holder.cardView.requestLayout();
            }
            else if(position == serviceCategoryList.size()-1){
                layoutParams.rightMargin = px;
                layoutParams.bottomMargin = px;
                holder.cardView.requestLayout();
            }
            else {
                layoutParams.rightMargin = px;
                holder.cardView.requestLayout();
            }
        }*/

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
        holder.btn_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser()==null){
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
                else{
                    Intent i = new Intent(context, BookServiceActivity.class);
                    i.putExtra(SERVICE_CATEGORY_ID, serviceCategoryList.get(position).getId());
                    i.putExtra(SERVICE_ID, serviceCategoryList.get(position).getServiceId());
                    ((ServiceActivity) context).startActivityForResult(i, 1);
                }

                //Toasty.success(context, serviceCategoryList.get(position).getCategoryName(), Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (serviceCategoryList != null ? serviceCategoryList.size() : 0);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;
        ImageView iv_image;
        Button btn_book;
        CardView cardView;

        AVLoadingIndicatorView progress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.layout_service_booking_category_title);
            iv_image = itemView.findViewById(R.id.layout_service_booking_iv);
            progress = itemView.findViewById(R.id.layout_service_booking_avi);
            btn_book = itemView.findViewById(R.id.layout_service_booking_btn);
            cardView = itemView.findViewById(R.id.layout_service_booking_cardview);
        }
    }

}
