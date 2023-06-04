package com.example.careitable.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.careitable.activity.LoginActivity;
import com.example.careitable.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference db = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());

        binding.nameTV.setText(user.getDisplayName());
        binding.emailTV.setText(user.getEmail());

        db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                binding.nameTV.setText(documentSnapshot.getString("name"));
                binding.emailTV.setText(documentSnapshot.getString("email"));
                binding.phoneTV.setText(documentSnapshot.getString("phone"));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error " + e, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSignOut.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        binding.myAds.setOnClickListener(view1 -> {

            Toast.makeText(getContext(), "This Page is Underconstruction!", Toast.LENGTH_SHORT).show();

        });
        binding.aboutUs.setOnClickListener(view1 -> {

            Toast.makeText(getContext(), "This Page is Underconstruction!", Toast.LENGTH_SHORT).show();

        });
        binding.help.setOnClickListener(view1 -> {

            Toast.makeText(getContext(), "This Page is Underconstruction!", Toast.LENGTH_SHORT).show();

        });
        binding.feedback.setOnClickListener(view1 -> {

            Toast.makeText(getContext(), "This Page is Underconstruction!", Toast.LENGTH_SHORT).show();

        });
        binding.terms.setOnClickListener(view1 -> {

            Toast.makeText(getContext(), "This Page is Underconstruction!", Toast.LENGTH_SHORT).show();

        });

        return view;
    }
}