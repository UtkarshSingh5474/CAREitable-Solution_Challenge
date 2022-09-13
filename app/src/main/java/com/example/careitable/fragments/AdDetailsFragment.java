package com.example.careitable.fragments;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.careitable.R;
import com.example.careitable.databinding.FragmentAdDetailsBinding;
import com.example.careitable.databinding.FragmentAdPhotosBinding;
import com.example.careitable.databinding.FragmentDonationsBinding;

public class AdDetailsFragment extends Fragment {

    private FragmentAdDetailsBinding binding;
    public static EditText edtTitle,edtDesc;
    public static CheckBox isNewCheckbox;
    public static int selectedCardInt = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //binding.clothesCV.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        initialiseComponents(view);

        return view;
    }
    private void initialiseComponents(View root){

        edtTitle = root.findViewById(R.id.edtTitle);
        edtDesc = root.findViewById(R.id.edtDesc);
        isNewCheckbox = root.findViewById(R.id.isNewCheckbox);

        binding.clothesCV.setOnClickListener(view -> {
            cardController(0);
        });
        binding.foodCV.setOnClickListener(view -> {
            cardController(1);
        });
        binding.bookCV.setOnClickListener(view -> {
            cardController(2);
        });
        binding.toyCV.setOnClickListener(view -> {
            cardController(3);
        });
        binding.stationaryCV.setOnClickListener(view -> {
            cardController(4);
        });
        binding.electonicsCV.setOnClickListener(view -> {
            cardController(5);
        });
        binding.medicineCV.setOnClickListener(view -> {
            cardController(6);
        });
        binding.othersCV.setOnClickListener(view -> {
            cardController(7);
        });

    }

    private void cardController(int CVIndex){
        if(selectedCardInt==-1){
            selectCard(CVIndex);
            selectedCardInt=CVIndex;
        }else if(selectedCardInt==CVIndex){
            deselectCard(CVIndex);
            selectedCardInt=-1;
        }else{
            selectCard(CVIndex);
            deselectCard(selectedCardInt);
            selectedCardInt=CVIndex;
        }
    }

    private void selectCard(int position){
        switch (position){
            case 0:
                selectCardUiChange(binding.clothesCV);
                break;
            case 1:
                selectCardUiChange(binding.foodCV);
                break;
            case 2:
                selectCardUiChange(binding.bookCV);
                break;
            case 3:
                selectCardUiChange(binding.toyCV);
                break;
            case 4:
                selectCardUiChange(binding.stationaryCV);
                break;
            case 5:
                selectCardUiChange(binding.electonicsCV);
                break;
            case 6:
                selectCardUiChange(binding.medicineCV);
                break;
            case 7:
                selectCardUiChange(binding.othersCV);
                break;

        }
    }

    private void selectCardUiChange(CardView view){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("cardElevation", getResources().getDimension(R.dimen.cardSelectedElevation));
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX);
        animator.start();
        view.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardSelected));
    }

    private void deselectCard(int position){
        switch (position){
            case 0:
                deselectCardUiChange(binding.clothesCV);
                break;
            case 1:
                deselectCardUiChange(binding.foodCV);
                break;
            case 2:
                deselectCardUiChange(binding.bookCV);
                break;
            case 3:
                deselectCardUiChange(binding.toyCV);
                break;
            case 4:
                deselectCardUiChange(binding.stationaryCV);
                break;
            case 5:
                deselectCardUiChange(binding.electonicsCV);
                break;
            case 6:
                deselectCardUiChange(binding.medicineCV);
                break;
            case 7:
                deselectCardUiChange(binding.othersCV);
                break;

        }
    }

    private void deselectCardUiChange(CardView view){
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("cardElevation", getResources().getDimension(R.dimen.cardUnselectedElevation));
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX);
        animator.start();
        view.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.cardUnselected));
    }
}