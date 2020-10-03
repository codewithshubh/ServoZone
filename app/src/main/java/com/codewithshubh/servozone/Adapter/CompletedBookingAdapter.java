package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Activity.BookingDetailActivity;
import com.codewithshubh.servozone.Model.BookingDetail;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.List;

import static com.codewithshubh.servozone.Constant.Constants.BOOKING_ID;
import static com.codewithshubh.servozone.Constant.Constants.CANCELLED;
import static com.codewithshubh.servozone.Constant.Constants.COMPLETED;

public class CompletedBookingAdapter extends RecyclerView.Adapter<CompletedBookingAdapter.MyViewHolder> {

    private Context context;
    private List<BookingDetail> bookingDetailList;
    private int px;

    public CompletedBookingAdapter(Context context, List<BookingDetail> bookingDetailList) {
        this.context = context;
        this.bookingDetailList = bookingDetailList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_completed_booking, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ViewGroup.MarginLayoutParams layoutParams;
        layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        if(position==0)
            layoutParams.topMargin = px;
        holder.tv_bookingid.setText(bookingDetailList.get(position).getBookingID());
        holder.tv_date.setText(bookingDetailList.get(position).getDateOfBooking());
        holder.tv_time.setText(bookingDetailList.get(position).getTimeOfBooking());
        holder.tv_status.setText(bookingDetailList.get(position).getBookingFinalDate());
        String ServiceId = bookingDetailList.get(position).getServiceId();
        String ServiceCategoryId = bookingDetailList.get(position).getServiceCategoryId();
        //Getting Service Name
        DatabaseReference myServiceRef = FirebaseDatabase.getInstance().getReference().child("Services");
        myServiceRef.orderByChild("id").equalTo(ServiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        ServiceGroup serviceGroup = child.getValue(ServiceGroup.class);
                        holder.tv_service.setText(serviceGroup.getServiceName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Getting Category Name
        DatabaseReference myCategoryServiceRef = FirebaseDatabase.getInstance().getReference().child("ServiceCategory");
        myCategoryServiceRef.orderByChild("id").equalTo(ServiceCategoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        ServiceCategory serviceCategory = child.getValue(ServiceCategory.class);
                        holder.tv_category.setText(serviceCategory.getCategoryName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if(bookingDetailList.get(position).getBookingStatus().equals(CANCELLED)){
            holder.tv_status_title.setTextColor(Color.RED);
            holder.tv_status_title.setText("Cancelled On: ");
            holder.tv_cancel.setText("CANCELLED");
        }
        if(bookingDetailList.get(position).getBookingStatus().equals(COMPLETED)){
            holder.tv_status_title.setTextColor(context.getResources().getColor(R.color.green));
            holder.tv_status_title.setText("Completed On: ");
            holder.tv_cancel.setText("COMPLETED");
        }



        Picasso.get().load(bookingDetailList.get(position).getBookingImgUrl()).placeholder(R.drawable.default_image)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.iv_booking_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        int i = position;
                        Picasso.get().load(bookingDetailList.get(i).getBookingImgUrl()).placeholder(R.drawable.default_image)
                                .into(holder.iv_booking_img, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                    }
                });

        holder.detail_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookingDetailActivity.class);
                intent.putExtra(BOOKING_ID, bookingDetailList.get(position).getBookingIdParent());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (bookingDetailList!=null?bookingDetailList.size():0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_bookingid, tv_date, tv_time, tv_service, tv_category, tv_status, tv_status_title, tv_cancel, tv_booking_detail;
        ImageView iv_booking_img;
        LinearLayout detail_lin, cancel_lin;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bookingid = itemView.findViewById(R.id.layout_completed_bookingid_tv);
            tv_date = itemView.findViewById(R.id.layout_completed_booking_date_tv);
            tv_time = itemView.findViewById(R.id.layout_completed_booking_time_tv);
            tv_service = itemView.findViewById(R.id.layout_completed_booking_service_tv);
            tv_category = itemView.findViewById(R.id.layout_completed_booking_category_tv);
            tv_status = itemView.findViewById(R.id.layout_completed_booking_status_tv);
            tv_status_title = itemView.findViewById(R.id.layout_completed_booking_status_title_tv);
            iv_booking_img = itemView.findViewById(R.id.layout_completed_booking_image_iv);
            detail_lin = itemView.findViewById(R.id.layout_completed_booking_details_lin);
            tv_cancel = itemView.findViewById(R.id.layout_completed_booking_cancel_tv);
            tv_booking_detail = itemView.findViewById(R.id.layout_completed_booking_details_tv);
            cardView = itemView.findViewById(R.id.layout_completed_booking_cv);
        }
    }
}
