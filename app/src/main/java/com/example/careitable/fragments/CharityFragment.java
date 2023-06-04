package com.example.careitable.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.careitable.R;
import com.example.careitable.databinding.FragmentAccountBinding;
import com.example.careitable.databinding.FragmentCharityBinding;

public class CharityFragment extends Fragment {

    private FragmentCharityBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentCharityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        return view;
    }
}