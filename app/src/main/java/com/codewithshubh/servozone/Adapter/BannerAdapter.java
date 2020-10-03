package com.codewithshubh.servozone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.codewithshubh.servozone.Model.BannerItemModel;
import com.codewithshubh.servozone.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<BannerItemModel> bannerItemModelList;
    private ViewPager2 viewPager2;
    private int count;


    public BannerAdapter(List<BannerItemModel> bannerItemModelList, ViewPager2 viewPager2) {
        this.bannerItemModelList = bannerItemModelList;
        this.count  = bannerItemModelList.size();
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BannerViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.banner_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        try{
            //if image is received then set
            Picasso.get().load(bannerItemModelList.get(position).getImageUrl())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_banner)
                    .into(holder.iv, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(bannerItemModelList.get(position).getImageUrl())
                            .placeholder(R.drawable.default_banner)
                            .into(holder.iv, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            });

        }catch (Exception e){
            //if there is any exception while getting image then set default
            Picasso.get().load(R.drawable.default_image).into(holder.iv);
        }
    }

    @Override
    public int getItemCount() {
        return (bannerItemModelList != null ? bannerItemModelList.size() : 0);
    }

    class BannerViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView iv;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.banner_item_imageview);
        }
    }
}
