package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Model.Address;
import com.codewithshubh.servozone.R;
import java.util.List;
import static com.codewithshubh.servozone.Constant.Constants.ADDRESS_DETAIL;
import static com.codewithshubh.servozone.Constant.Constants.USER_ADDRESS;
import static com.codewithshubh.servozone.Constant.Constants.USER_NAME;
import static com.codewithshubh.servozone.Constant.Constants.USER_PHONE;
import static com.codewithshubh.servozone.Constant.Constants.USER_PIN;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.MyViewHolder> {
    private Context context;
    private List<Address> addressList;
    private int lastSelectedPosition = 0;
    private String name, address, phone;
    private int px;


    public SelectAddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        view = LayoutInflater.from(context).inflate(R.layout.layout_select_address, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ViewGroup.MarginLayoutParams layoutParams;
        layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        /*if(position==addressList.size()-1){
            layoutParams.bottomMargin = px;
            holder.cardView.requestLayout();
        }*/

        holder.tv_name.setText(addressList.get(position).getName());
        holder.tv_contact.setText(addressList.get(position).getContact());
        if(!addressList.get(position).getLandmark().equals("")){
            address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea() + ", (Landmark: "
                    + addressList.get(position).getLandmark() + "), " +
                    addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                    addressList.get(position).getPincode();
            holder.tv_address.setText(address);
        }
        else {
            address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea()  + ", " +
                    addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                    addressList.get(position).getPincode();
            holder.tv_address.setText(address);
        }
        holder.rb.setChecked(position == lastSelectedPosition);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = addressList.get(position).getName();
                phone = addressList.get(position).getContact();
                if(!addressList.get(position).getLandmark().equals("")){
                    address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea() + ", (Landmark: "
                            + addressList.get(position).getLandmark() + "), " +
                            addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                            addressList.get(position).getPincode();
                }
                else {
                    address = addressList.get(position).getHouseNo() + ", " + addressList.get(position).getArea()  + ", " +
                            addressList.get(position).getCity() + ", " + addressList.get(position).getState() + " - " +
                            addressList.get(position).getPincode();
                }

                lastSelectedPosition = position;
                notifyDataSetChanged();

                Intent intent = new Intent(ADDRESS_DETAIL);
                //send data to parent activity
                intent.putExtra(USER_NAME,name);
                intent.putExtra(USER_ADDRESS,address);
                intent.putExtra(USER_PHONE,phone);
                intent.putExtra(USER_PIN, addressList.get(position).getPincode());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }


        });
    }

    @Override
    public int getItemCount() {
        return (addressList != null ? addressList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton rb;
        TextView tv_name, tv_address, tv_contact;
        CardView cardView;
        RelativeLayout rl_layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rb = itemView.findViewById(R.id.layout_selectaddress_rb);
            tv_name = itemView.findViewById(R.id.layout_selectaddress_name);
            tv_address = itemView.findViewById(R.id.layout_selectaddress_address_full);
            tv_contact = itemView.findViewById(R.id.layout_selectaddress_contact);
            cardView = itemView.findViewById(R.id.layout_selectaddress_cv);
            rl_layout = itemView.findViewById(R.id.layout_selectaddress_rl);

        }
    }
}
