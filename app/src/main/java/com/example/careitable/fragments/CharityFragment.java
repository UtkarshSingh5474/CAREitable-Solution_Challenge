package com.example.careitable.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.careitable.activity.ChatActivity;
import com.example.careitable.dao.CharityData;
import com.example.careitable.adapter.CharityListAdapter;
import com.example.careitable.dao.CharityListObject;
import com.example.careitable.databinding.FragmentCharityBinding;
import com.example.careitable.service.CheckNetwork;
import com.example.careitable.activity.CreateCharityOrganisationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CharityFragment extends Fragment {

    private FragmentCharityBinding binding;
    RecyclerView recyclerView;
    static SwipeRefreshLayout pullToRefresh;
    private static List<CharityListObject> charityObject = new ArrayList<>();
    public static ArrayList<String> charityListPosition;
    private static CharityListAdapter recommendedListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCharityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        recyclerView = binding.charityListRecyclerView;

        pullToRefresh = binding.pullToRefresh;

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCharityList();
            }
        });


        binding.addFloatingActionButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), CreateCharityOrganisationActivity.class));
        });
        binding.chatWindowFloatingActionButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), ChatActivity.class));
        });

        getCharityList();

        return view;
    }

    private void getCharityList(){
        if (!CheckNetwork.isInternetAvailable(getContext())){
            return;
        }

        pullToRefresh.setRefreshing(true);

        //binding.emptyTextView.setVisibility(View.GONE);
        charityListPosition = new ArrayList<>();



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Charities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            charityObject = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                if (document.exists()) {

                                    CharityData pfo = document.toObject(CharityData.class);
                                    charityObject.add(new CharityListObject(pfo.getNameOrganisation(), pfo.getDescriptionOrganisation(), pfo.getStateOrganisation(), pfo.getCityOrganisation(), document.getId()));
                                    charityListPosition.add(document.getId());
                                }
                            }

                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(horizontalLayoutManager);
                            recommendedListAdapter = new CharityListAdapter(charityObject, getContext());
                            Log.d("Test", "onComplete: " + charityObject.size());
                            recyclerView.setAdapter(recommendedListAdapter);


                            if (charityObject.isEmpty()){
                                // binding.emptyTextView.setVisibility(View.VISIBLE);
                                //binding.adListRecyclerView.setVisibility(View.GONE);
                            }


                            pullToRefresh.setRefreshing(false);

                        } else {
                            pullToRefresh.setRefreshing(false);
                            Log.d("rishabhisgreat",task.getException().getMessage());
                            //Toast.makeText(PropertyAds.this, "error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "unknown error (re030)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
