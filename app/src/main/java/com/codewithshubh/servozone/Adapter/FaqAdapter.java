package com.codewithshubh.servozone.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.codewithshubh.servozone.Model.FaqModel;
import com.codewithshubh.servozone.R;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.MyViewHolder> {

    private Context context;
    private List<FaqModel> faqModelList;
    private int px;
    private int lastSelectedPosition = -1;

    public FaqAdapter() {
    }

    public FaqAdapter(Context context, List<FaqModel> faqModelList) {
        this.context = context;
        this.faqModelList = faqModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Resources r = parent.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_faq, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        ViewGroup.MarginLayoutParams layoutParams;
        layoutParams =
                (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
        /*if(position==0)
            layoutParams.topMargin = px;*/

        FaqModel faqModel = faqModelList.get(position);
        holder.tv_question.setText(faqModel.getQuestion());
        holder.tv_answer.setText(faqModel.getAnswer());

        if(position==lastSelectedPosition){
            holder.rel_ans.setVisibility(View.VISIBLE);
            holder.iv_toggle.setImageResource(R.drawable.expand_less);
            //Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.swipe_down);
            //holder.rel_ans.startAnimation(slideDown);
        }
        else
        {
            holder.rel_ans.setVisibility(View.GONE);
            holder.iv_toggle.setImageResource(R.drawable.expand);
        }

        holder.rel_ques.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (faqModelList.get(position).isOpen()==true)
                    faqModelList.get(position).setOpen(false);
                else
                    faqModelList.get(position).setOpen(true);
                lastSelectedPosition = position;
                notifyDataSetChanged();

            }
        });
        if (!faqModel.isOpen()){
            holder.rel_ans.setVisibility(View.GONE);
            holder.iv_toggle.setImageResource(R.drawable.expand);
        }

    }

    @Override
    public int getItemCount() {
        return (faqModelList!=null?faqModelList.size():0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question, tv_answer;
        RelativeLayout rel_ques, rel_ans;
        ImageView iv_toggle;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_question = itemView.findViewById(R.id.layout_faq_ques_tv);
            tv_answer = itemView.findViewById(R.id.layout_faq_ans_tv);
            rel_ques = itemView.findViewById(R.id.layout_faq_ques_rel);
            rel_ans = itemView.findViewById(R.id.layout_faq_ans_rel);
            iv_toggle = itemView.findViewById(R.id.layout_faq_expand_iv);
            cardView = itemView.findViewById(R.id.layout_faq_cv);
        }
    }
}
