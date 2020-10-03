package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Model.ServiceCategory;
import com.codewithshubh.servozone.Model.ServiceGroup;
import com.codewithshubh.servozone.R;
import com.codewithshubh.servozone.Activity.ServiceActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_ID;
import static com.codewithshubh.servozone.Constant.Constants.SERVICE_NAME;

public class ServiceGroupAdapter extends RecyclerView.Adapter<ServiceGroupAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceGroup> serviceGroupList;
    private int px;

    private List<ServiceCategory> serviceCategoryList;
    private ServiceCategoryAdapter serviceCategoryAdapter;

    public ServiceGroupAdapter(Context context, List<ServiceGroup> serviceGroupList) {
        this.context = context;
        this.serviceGroupList = serviceGroupList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 5,r.getDisplayMetrics()));
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_service_group, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if(position==0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.topMargin = px;
            holder.cardView.requestLayout();
        }

        holder.tv_service_name.setText(serviceGroupList.get(position).getServiceName());
        holder.tv_service_desc.setText(serviceGroupList.get(position).getServiceDetail());

        String serviceID = serviceGroupList.get(position).getId();
        serviceCategoryList = new ArrayList<>();
        serviceCategoryAdapter = new ServiceCategoryAdapter(context, serviceCategoryList);
        DatabaseReference mCategory = FirebaseDatabase.getInstance().getReference("ServiceCategory");

        holder.rv_service_category.setHasFixedSize(true);
        holder.rv_service_category.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv_service_category.setAdapter(serviceCategoryAdapter);
        holder.rv_service_category.setNestedScrollingEnabled(false);

        //button view all
        holder.btn_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(context, ServiceActivity.class));
                intent.putExtra(SERVICE_ID, serviceGroupList.get(position).getId());
                intent.putExtra(SERVICE_NAME, serviceGroupList.get(position).getServiceName());
                context.startActivity(intent);
                //Toast.makeText(context, "Button More: "+ holder.tv_service_name.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        mCategory.orderByChild("serviceId").equalTo(serviceID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceCategoryList.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    ServiceCategory serviceCategory = child.getValue(ServiceCategory.class);
                    if(serviceCategory.isActiveStatus()){
                        serviceCategoryList.add(serviceCategory);
                    }
                }
                serviceCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (serviceGroupList != null ? serviceGroupList.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_service_name, tv_service_desc, btn_view_all;
        RecyclerView rv_service_category;
        CardView cardView;
        //Button btn_view_all;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_service_name = itemView.findViewById(R.id.tv_service_group_title);
            tv_service_desc = itemView.findViewById(R.id.tv_service_group_detail);
            btn_view_all = itemView.findViewById(R.id.btn_service_group_view_all);
            rv_service_category = itemView.findViewById(R.id.rv_service_category);
            cardView = itemView.findViewById(R.id.service_group_card_view);
        }
    }
}
