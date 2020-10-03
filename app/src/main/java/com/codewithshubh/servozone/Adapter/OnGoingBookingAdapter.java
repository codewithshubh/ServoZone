package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithshubh.servozone.API.NotificationClientAPI;
import com.codewithshubh.servozone.Activity.BookingDetailActivity;
import com.codewithshubh.servozone.Fragment.BookingFragment;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Model.BookingDetail;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.Model.User;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Utils.NotificationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Response;
import static com.codewithshubh.servozone.Constant.Constants.BOOKING_ID;
import static com.codewithshubh.servozone.Constant.Constants.CANCELLED;
import static com.codewithshubh.servozone.Constant.Constants.COMPLETED;
import static com.codewithshubh.servozone.Constant.Constants.CONFIRMED;
import static com.codewithshubh.servozone.Constant.Constants.PENDING;
import static com.codewithshubh.servozone.Constant.Constants.SM_ASSIGNED;

public class OnGoingBookingAdapter extends RecyclerView.Adapter<OnGoingBookingAdapter.MyViewHolder> {

    private Context context;
    private List<BookingDetail> bookingDetailList;
    private int px;
    private FirebaseAuth mAuth;
    private String uid, userName, deviceToken;
    private DatabaseReference myRefUser;


    public OnGoingBookingAdapter() {
    }

    public OnGoingBookingAdapter(Context context, List<BookingDetail> bookingDetailList) {
        this.context = context;
        this.bookingDetailList = bookingDetailList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        myRefUser = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userName = user.getName();
                deviceToken = user.getDeviceToken();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ongoing_booking, parent, false);
        return new MyViewHolder(view);
    }
    private String GetCurrentDate() {
        String currentdate;
        Calendar c = Calendar.getInstance();
        int y, m, d;
        y = c.get(Calendar.YEAR);
        m = c.get(Calendar.MONTH)+1;
        d = c.get(Calendar.DAY_OF_MONTH);

        if(m <10){
            if(d <10)
                currentdate = "0"+d+"/"+"0"+m+"/"+y;
            else
                currentdate = d+"/"+"0"+m+"/"+y;
        }
        else{
            if(d<10)
                currentdate = "0"+d+"/"+m+"/"+y;
            else
                currentdate = d+"/"+m+"/"+y;
        }
        return currentdate;
    }

    private String GetCurrentTime() {
        Calendar c = Calendar.getInstance();
        int h, m;
        String currentTime, format;
        h = c.get(Calendar.HOUR_OF_DAY);
        m = c.get(Calendar.MINUTE);

        if (h == 0) {
            h += 12;
            format = "AM";
        } else if (h == 12) {
            format = "PM";
        } else if (h > 12) {
            h -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        if(h<10){
            if(m<10){
                currentTime = "0"+h+":"+"0"+m+" "+format;
            }
            else {
                currentTime = "0"+h+":"+m+" "+format;
            }
        }
        else {
            if(m<10){
                currentTime = h+":"+"0"+m+" "+format;
            }
            else {
                currentTime = h+":"+m+" "+format;
            }
        }
        return currentTime;
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
        String BookingStatus = bookingDetailList.get(position).getBookingStatus();
        if (BookingStatus.equals(PENDING))
            holder.tv_status.setText("Pending");
        else if (BookingStatus.equals(CONFIRMED))
            holder.tv_status.setText("Confirmed");
        else if (BookingStatus.equals(SM_ASSIGNED))
            holder.tv_status.setText("In Progress");
        else if (BookingStatus.equals(COMPLETED))
            holder.tv_status.setText("Completed");
        else if (BookingStatus.equals(CANCELLED))
            holder.tv_status.setText("Cancelled");
        else
            holder.tv_status.setText("NA");
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
        if(bookingDetailList.get(position).getBookingStatus().equals(CONFIRMED)){
            holder.cancel_lin.setVisibility(View.GONE);
            holder.tv_status.setBackgroundResource(R.drawable.btn_confirmed);
        }

        if(bookingDetailList.get(position).getBookingStatus().equals(SM_ASSIGNED)){
            holder.cancel_lin.setVisibility(View.GONE);
            holder.tv_status.setBackgroundResource(R.drawable.btn_confirmed);
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

        holder.cancel_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                sweetAlertDialog.setTitleText("Are you sure?");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.setCanceledOnTouchOutside(false);
                sweetAlertDialog.setContentText("Do you really want to cancel this booking!")
                        .setConfirmText("Yes, Cancel!")
                        .setCancelText("No")
                        .setCancelButtonBackgroundColor(ContextCompat.getColor(context,R.color.dark_gray))
                        .setConfirmButtonBackgroundColor(ContextCompat.getColor(context,R.color.red))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                                DatabaseReference myRefBooking = FirebaseDatabase.getInstance().getReference("Bookings")
                                        .child(bookingDetailList.get(position).getBookingIdParent());
                                myRefBooking.child("bookingFinalDate").setValue(GetCurrentDate()+", "+GetCurrentTime());
                                myRefBooking.child("bookingStatus").setValue(CANCELLED).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        sDialog.dismiss();

                                        //notification payload
                                        String title = "Booking cancelled! :(";
                                        String userArr[] = userName.split(" ");
                                        String message = "Hi "+userArr[0]+"! "+"Your booking with Booking ID "
                                                +bookingDetailList.get(position).getBookingID()+" has been cancelled as per your request";
                                        JsonObject payload = new NotificationUtils(context).buildNotificationPayload(deviceToken, title, message,
                                                bookingDetailList.get(position).getBookingImgUrl(), bookingDetailList.get(position).getUserUID());
                                        // send notification to receiver ID
                                        NotificationClientAPI.getAPIService().sendNotification(payload).enqueue(
                                                new retrofit2.Callback<JsonObject>() {
                                                    @Override
                                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                        if (response.isSuccessful()) {

                                                        }
                                                    }
                                                    @Override public void onFailure(Call<JsonObject> call, Throwable t) { }
                                                });


                                        SweetAlertDialog s = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                                        s.setCancelable(false);
                                        s.setCanceledOnTouchOutside(false);
                                        s.setTitleText("Cancelled!")
                                                .setConfirmButtonBackgroundColor(ContextCompat.getColor(context,R.color.main_green_color))
                                                .setContentText("You booking with booking ID " + bookingDetailList.get(position).getBookingID() + " has been cancelled successfully.")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismissWithAnimation();
                                                        BookingFragment nextFrag= new BookingFragment();
                                                        ((MainActivity)context).getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.container, nextFrag, "findThisFragment")
                                                                .detach(nextFrag).attach(nextFrag)
                                                                .commit();
                                                        ((MainActivity)context).bnv.setSelectedItemId(R.id.bottom_navbar_booking);
                                                    }
                                                })
                                                .show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        sDialog.dismiss();
                                        Toasty.warning(context, "Something went wrong... Try again!", Toasty.LENGTH_SHORT, true).show();
                                    }
                                });
                            }
                        })
                        .show();
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
        TextView tv_bookingid, tv_date, tv_time, tv_service, tv_category, tv_status, tv_cancel, tv_booking_detail;
        ImageView iv_booking_img;
        LinearLayout detail_lin, cancel_lin;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_bookingid = itemView.findViewById(R.id.layout_ongoing_bookingid_tv);
            tv_date = itemView.findViewById(R.id.layout_ongoing_booking_date_tv);
            tv_time = itemView.findViewById(R.id.layout_ongoing_booking_time_tv);
            tv_service = itemView.findViewById(R.id.layout_ongoing_booking_service_tv);
            tv_category = itemView.findViewById(R.id.layout_ongoing_booking_category_tv);
            tv_status = itemView.findViewById(R.id.layout_ongoing_booking_status_tv);
            iv_booking_img = itemView.findViewById(R.id.layout_ongoing_booking_image_iv);
            detail_lin = itemView.findViewById(R.id.layout_ongoing_booking_details_lin);
            cancel_lin = itemView.findViewById(R.id.layout_ongoing_booking_cancel_lin);
            tv_cancel = itemView.findViewById(R.id.layout_ongoing_booking_cancel_tv);
            tv_booking_detail = itemView.findViewById(R.id.layout_ongoing_booking_details_tv);
            cardView = itemView.findViewById(R.id.layout_ongoing_booking_cv);
        }
    }
}
