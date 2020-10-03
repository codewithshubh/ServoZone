package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Activity.EditAddressActivity;
import com.codewithshubh.servozone.Model.Address;
import com.codewithshubh.servozone.Activity.MyAddressActivity;
import com.codewithshubh.servozone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;
import static com.codewithshubh.servozone.Constant.Constants.ADDRESS_ID;

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.MyViewHolder> {
    private Context context;
    private List<Address> addressList;
    private DatabaseReference myRefRemove;
    private String addressId;

    public MyAddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_myaddress, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String address;
        if(!addressList.get(position).getLandmark().equals("")){
            holder.tv_name.setText(addressList.get(position).getName());
            address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea() + ", (Landmark: "
                    + addressList.get(position).getLandmark() + "), " +
                    addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                    addressList.get(position).getPincode();
            holder.tv_address.setText(address);
            holder.tv_contact.setText(addressList.get(position).getContact());
        }
        else {
            holder.tv_name.setText(addressList.get(position).getName());
            address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea()  + ", " +
                    addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                    addressList.get(position).getPincode();
            holder.tv_address.setText(address);
            holder.tv_contact.setText(addressList.get(position).getContact());
        }

        holder.iv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.iv_option);
                popup.getMenuInflater().inflate(R.menu.edit_address_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.address_edit:
                                addressId = addressList.get(position).getId();
                                Intent i = new Intent(context, EditAddressActivity.class);
                                i.putExtra(ADDRESS_ID, addressId);
                                ((MyAddressActivity)context).startActivityForResult(i,1);
                                break;
                            case R.id.address_remove:
                                myRefRemove = FirebaseDatabase.getInstance().getReference("UserAddress");
                                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setCanceledOnTouchOutside(false);
                                sweetAlertDialog.setCancelable(false);
                                sweetAlertDialog.setTitleText("Are you sure?")
                                        .setContentText("Won't be able to retrieve the address once it is deleted")
                                        .setConfirmText("Yes, Delete!")
                                        .setCancelText("Cancel")
                                        .setCancelButtonBackgroundColor(ContextCompat.getColor(context,R.color.dark_gray))
                                        .setConfirmButtonBackgroundColor(ContextCompat.getColor(context,R.color.red))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                myRefRemove.child(addressList.get(position).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sweetAlertDialog.dismissWithAnimation();
                                                        SweetAlertDialog s = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                                                        s.setCancelable(false);
                                                        s.setCanceledOnTouchOutside(false);
                                                        s.setTitleText("Done!")
                                                                .setContentText("Your address have has been deleted successfully.")
                                                                .setConfirmText("OK")
                                                                .setConfirmButtonBackgroundColor(ContextCompat.getColor(context,R.color.main_green_color))
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        sweetAlertDialog.dismissWithAnimation();
                                                                        ((MyAddressActivity)context).LoadData();
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toasty.error(context, e.getMessage(), Toasty.LENGTH_SHORT, true).show();
                                                    }
                                                });
                                            }
                                        })
                                        .show();

                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return (addressList != null ? addressList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_address, tv_contact;
        ImageView iv_option;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.layout_myaddress_name);
            tv_address = itemView.findViewById(R.id.layout_myaddress_address_full);
            tv_contact = itemView.findViewById(R.id.layout_myaddress_contact);
            iv_option = itemView.findViewById(R.id.layout_myaddress_btn_option);
            cardView = itemView.findViewById(R.id.layout_myaddress_cv);

        }
    }
}
