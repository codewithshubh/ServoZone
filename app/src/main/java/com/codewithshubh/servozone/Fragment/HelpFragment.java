package com.codewithshubh.servozone.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codewithshubh.servozone.Activity.MainActivity;
import com.codewithshubh.servozone.Adapter.FaqAdapter;
import com.codewithshubh.servozone.Model.FaqModel;
import com.codewithshubh.servozone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment {

    private TextView toolbar_title;
    private FaqAdapter faqAdapter;
    private List<FaqModel> faqModelList;
    private RecyclerView rv_faq;
    private AlertDialog dialog;

    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        toolbar_title = ((MainActivity)getActivity()).findViewById(R.id.activity_main_toolbar_tv);
        toolbar_title.setText(getResources().getString(R.string.help_fragment_toolbar_title));

        //code goes here
        Init(view);

        GetFaqData();

        return view;
    }

    private void GetFaqData() {
        dialog.show();
        DatabaseReference myRefHelp = FirebaseDatabase.getInstance().getReference("FAQs");
        myRefHelp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    faqModelList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        FaqModel faqModel = child.getValue(FaqModel.class);
                        faqModelList.add(faqModel);
                        //Log.d("Question: ", faqModel.getQuestion());
                    }
                    //Log.d("Count: ", String.valueOf(faqModelList.size()));
                    faqAdapter.notifyDataSetChanged();
                }
                else
                    Toasty.warning(getActivity(), "No data found", Toasty.LENGTH_SHORT, true).show();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toasty.error(getActivity(), "Something went wrong... Try again!", Toasty.LENGTH_SHORT, true).show();
            }
        });
    }

    private void Init(View view) {
        rv_faq = view.findViewById(R.id.fragment_help_rv);
        faqModelList = new ArrayList<>();
        faqAdapter = new FaqAdapter(getActivity(), faqModelList);
        rv_faq.setHasFixedSize(true);
        //Setting Recyclerview
        rv_faq.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_faq.setAdapter(faqAdapter);
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }
}
