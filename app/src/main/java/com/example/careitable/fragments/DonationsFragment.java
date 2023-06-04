package com.example.careitable.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.careitable.activity.ChatActivity;
import com.example.careitable.databinding.FragmentDonationsBinding;
import com.example.careitable.service.CheckNetwork;
import com.example.careitable.activity.CreateDonationActivity;
import com.example.careitable.dao.DonationAdsFetchObject;
import com.example.careitable.adapter.DonationAdsListAdapter;
import com.example.careitable.dao.DonationAdsListObject;
import com.example.careitable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DonationsFragment extends Fragment implements LocationListener {

    private FragmentDonationsBinding binding;
    public static ArrayList<String> donationAdListPosition;
    LocationManager locationManager;
    Spinner stateSpinner, citySpinner;
    String cityName="";
    String stateName="";
    String spinnerCity;
    String spinnerState;
    Dialog dialog;
    RecyclerView recyclerView;
    static SwipeRefreshLayout pullToRefresh;
    private static List<DonationAdsListObject> donationAdObject = new ArrayList<>();
    private static DonationAdsListAdapter recommendedListAdapter;


    private ArrayAdapter<CharSequence> stateAdapter, cityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentDonationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        binding.locationTv.setText(getLocationFromDevice());

        recyclerView = binding.adListRecyclerView;

        pullToRefresh = binding.pullToRefresh;

        //Default location
        Toast.makeText(getContext(), "Default Location: Ghaziabad, Uttar Pradesh,India\nFor presentation purpose", Toast.LENGTH_SHORT).show();
        cityName = "Ghaziabad";
        stateName = "Uttar Pradesh";
        binding.locationTv.setText(cityName + ", " + stateName);
        getAdsListByLocaiton(cityName,stateName);


        binding.pullToRefresh.setOnRefreshListener(() -> getAdsListByLocaiton(cityName,stateName));

        binding.changeLocation.setOnClickListener(view1 -> {
            locationChangeDialog();
        });

        binding.addFloatingActionButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), CreateDonationActivity.class));
        });

        binding.chatWindowFloatingActionButton.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), ChatActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckNetwork.isInternetAvailable(getContext());
        getAdsListByLocaiton(cityName,stateName);
    }

    private void getAdsListByLocaiton(String city, String state){
        if (!CheckNetwork.isInternetAvailable(getContext())){
            return;
        }

        pullToRefresh.setRefreshing(true);

        //binding.emptyTextView.setVisibility(View.GONE);
        donationAdListPosition = new ArrayList<>();



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DonationAds")
                .whereEqualTo("location", city+", "+state)
                .whereEqualTo("isDonated",false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            donationAdObject = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {


                                if (document.exists()) {

                                    DonationAdsFetchObject pfo = document.toObject(DonationAdsFetchObject.class);
                                    donationAdObject.add(new DonationAdsListObject(pfo.getTitle(), pfo.getCity(), pfo.getCategory(), pfo.getDescription(),pfo.getDatePosted(),document.getId()));
                                    donationAdListPosition.add(document.getId());
                                }
                            }

                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(horizontalLayoutManager);
                            recommendedListAdapter = new DonationAdsListAdapter(donationAdObject, getContext());
                            Log.d("Test", "onComplete: " + donationAdObject.size());
                            recyclerView.setAdapter(recommendedListAdapter);


                            if (donationAdObject.isEmpty()){
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

    private void locationChangeDialog() {

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottom_sheet_location_change);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Button btnGetLocation = (Button) dialog.findViewById(R.id.btnGetLocation);
        Button btnChangeLocation = (Button) dialog.findViewById(R.id.btnChangeLocation);
        stateSpinner = dialog.findViewById(R.id.stateSpinner);
        citySpinner = dialog.findViewById(R.id.citySpinner);


        stateAdapter = ArrayAdapter.createFromResource(getContext(), R.array.States, R.layout.spinneritem);
        stateAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);


        if (cityName == null || stateName == null) {

            stateSpinner.setSelection(stateAdapter.getPosition("Select State"));
            cityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_default_districts, R.layout.spinneritem);
            cityAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
            citySpinner.setAdapter(cityAdapter);
            citySpinner.setSelection(cityAdapter.getPosition("Select City"));


        } else {
            stateSpinner.setSelection(stateAdapter.getPosition(stateName));


            // Specify the layout to use when the list of choices appears

        }

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                citySpinner = dialog.findViewById(R.id.citySpinner);

                spinnerState = stateSpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.stateSpinner) {
                    if(stateName==null){
                        stateName = spinnerState;
                    }
                    else if (!stateName.equals(spinnerState)) {
                        stateName = spinnerState;
                        cityName = null;
                    }
                    switch (stateName) {
                        case "Select State":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_default_districts, R.layout.spinneritem);
                            break;
                        case "Andhra Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_andhra_pradesh_districts, R.layout.spinneritem);
                            break;
                        case "Arunachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_arunachal_pradesh_districts, R.layout.spinneritem);
                            break;
                        case "Assam":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_assam_districts, R.layout.spinneritem);
                            break;
                        case "Bihar":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_bihar_districts, R.layout.spinneritem);
                            break;
                        case "Chhattisgarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_chhattisgarh_districts, R.layout.spinneritem);
                            break;
                        case "Goa":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_goa_districts, R.layout.spinneritem);
                            break;
                        case "Gujarat":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_gujarat_districts, R.layout.spinneritem);
                            break;
                        case "Haryana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_haryana_districts, R.layout.spinneritem);
                            break;
                        case "Himachal Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_himachal_pradesh_districts, R.layout.spinneritem);
                            break;
                        case "Jharkhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_jharkhand_districts, R.layout.spinneritem);
                            break;
                        case "Karnataka":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_karnataka_districts, R.layout.spinneritem);
                            break;
                        case "Kerala":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_kerala_districts, R.layout.spinneritem);
                            break;
                        case "Madhya Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_madhya_pradesh_districts, R.layout.spinneritem);
                            break;
                        case "Maharashtra":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_maharashtra_districts, R.layout.spinneritem);
                            break;
                        case "Manipur":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_manipur_districts, R.layout.spinneritem);
                            break;
                        case "Meghalaya":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_meghalaya_districts, R.layout.spinneritem);
                            break;
                        case "Mizoram":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_mizoram_districts, R.layout.spinneritem);
                            break;
                        case "Nagaland":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_nagaland_districts, R.layout.spinneritem);
                            break;
                        case "Odisha":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_odisha_districts, R.layout.spinneritem);
                            break;
                        case "Punjab":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_punjab_districts, R.layout.spinneritem);
                            break;
                        case "Rajasthan":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_rajasthan_districts, R.layout.spinneritem);
                            break;
                        case "Sikkim":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_sikkim_districts, R.layout.spinneritem);
                            break;
                        case "Tamil Nadu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_tamil_nadu_districts, R.layout.spinneritem);
                            break;
                        case "Telangana":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_telangana_districts, R.layout.spinneritem);
                            break;
                        case "Tripura":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_tripura_districts, R.layout.spinneritem);
                            break;
                        case "Uttar Pradesh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_uttar_pradesh_districts, R.layout.spinneritem);
                            break;
                        case "Uttarakhand":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_uttarakhand_districts, R.layout.spinneritem);
                            break;
                        case "West Bengal":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_west_bengal_districts, R.layout.spinneritem);
                            break;
                        case "Andaman and Nicobar Islands":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_andaman_nicobar_districts, R.layout.spinneritem);
                            break;
                        case "Chandigarh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_chandigarh_districts, R.layout.spinneritem);
                            break;
                        case "Dadra and Nagar Haveli":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_dadra_nagar_haveli_districts, R.layout.spinneritem);
                            break;
                        case "Daman and Diu":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_daman_diu_districts, R.layout.spinneritem);
                            break;
                        case "Delhi":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_delhi_districts, R.layout.spinneritem);
                            break;
                        case "Jammu and Kashmir":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_jammu_kashmir_districts, R.layout.spinneritem);
                            break;
                        case "Lakshadweep":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_lakshadweep_districts, R.layout.spinneritem);
                            break;
                        case "Ladakh":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_ladakh_districts, R.layout.spinneritem);
                            break;
                        case "Puducherry":
                            cityAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                    R.array.array_puducherry_districts, R.layout.spinneritem);
                            break;
                        default:

                            break;
                    }
                    cityAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);    // Specify the layout to use when the list of choices appears
                    citySpinner.setAdapter(cityAdapter);
                }

                if (cityName != null) {
                    ArrayAdapter<CharSequence> cityAdapter = getCityAdapter(stateName);
                    citySpinner.setAdapter(cityAdapter);
                    citySpinner.setSelection(cityAdapter.getPosition(cityName));


                }
                cityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        //To obtain the selected District from the spinner
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerCity = citySpinner.getSelectedItem().toString();
                cityName = spinnerCity;
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnGetLocation.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            } else {

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationEnabled();
                getLocation();

            }

        });

        btnChangeLocation.setOnClickListener(view -> {

            if(stateName.equals("Select State") || cityName.equals("Select City")){
                Toast.makeText(getContext(), "Please Select A City", Toast.LENGTH_SHORT).show();
            }else {
                cityName=spinnerCity;
                stateName=spinnerState;
                binding.locationTv.setText(cityName + ", " + stateName);
                dialog.dismiss();
                saveLocationToDevice(cityName,stateName);
                getAdsListByLocaiton(cityName,stateName);

            }
        });

    }

    private void saveLocationToDevice(String cityName, String stateName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext
        myEdit.putString("cityName", cityName);
        myEdit.putString("stateName", stateName);

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();
    }

    private String getLocationFromDevice() {
        String locationCombined;

        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);

        cityName = sh.getString("cityName", null);
        stateName = sh.getString("stateName", null);
        if (cityName == null && stateName == null) {
            locationCombined = "No Location Set";
        } else {
            locationCombined = cityName + ", " + stateName;
        }

        return locationCombined;
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Places around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses.get(0).getCountryName()!="India"){
                Toast.makeText(getContext(), "Only Indian Regions Allowed at This time\nSetting Default Location", Toast.LENGTH_SHORT).show();
                stateName="Uttar Pradesh";
                cityName="Ghaziabad";

            }else{
                cityName = addresses.get(0).getLocality();
                stateName = addresses.get(0).getAdminArea();
            }

            binding.locationTv.setText(cityName + ", " + stateName);
            getAdsListByLocaiton(cityName,stateName);
            dialog.dismiss();
            Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();
            saveLocationToDevice(cityName, stateName);

        } catch (Exception e) {
        }
    }

    private ArrayAdapter<CharSequence> getCityAdapter(String stName) {

        switch (stName) {
            case "Select State":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_default_districts, R.layout.spinneritem);
                break;
            case "Andhra Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_andhra_pradesh_districts, R.layout.spinneritem);
                break;
            case "Arunachal Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_arunachal_pradesh_districts, R.layout.spinneritem);
                break;
            case "Assam":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_assam_districts, R.layout.spinneritem);
                break;
            case "Bihar":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_bihar_districts, R.layout.spinneritem);
                break;
            case "Chhattisgarh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_chhattisgarh_districts, R.layout.spinneritem);
                break;
            case "Goa":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_goa_districts, R.layout.spinneritem);
                break;
            case "Gujarat":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_gujarat_districts, R.layout.spinneritem);
                break;
            case "Haryana":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_haryana_districts, R.layout.spinneritem);
                break;
            case "Himachal Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_himachal_pradesh_districts, R.layout.spinneritem);
                break;
            case "Jharkhand":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_jharkhand_districts, R.layout.spinneritem);
                break;
            case "Karnataka":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_karnataka_districts, R.layout.spinneritem);
                break;
            case "Kerala":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_kerala_districts, R.layout.spinneritem);
                break;
            case "Madhya Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_madhya_pradesh_districts, R.layout.spinneritem);
                break;
            case "Maharashtra":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_maharashtra_districts, R.layout.spinneritem);
                break;
            case "Manipur":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_manipur_districts, R.layout.spinneritem);
                break;
            case "Meghalaya":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_meghalaya_districts, R.layout.spinneritem);
                break;
            case "Mizoram":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_mizoram_districts, R.layout.spinneritem);
                break;
            case "Nagaland":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_nagaland_districts, R.layout.spinneritem);
                break;
            case "Odisha":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_odisha_districts, R.layout.spinneritem);
                break;
            case "Punjab":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_punjab_districts, R.layout.spinneritem);
                break;
            case "Rajasthan":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_rajasthan_districts, R.layout.spinneritem);
                break;
            case "Sikkim":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_sikkim_districts, R.layout.spinneritem);
                break;
            case "Tamil Nadu":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_tamil_nadu_districts, R.layout.spinneritem);
                break;
            case "Telangana":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_telangana_districts, R.layout.spinneritem);
                break;
            case "Tripura":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_tripura_districts, R.layout.spinneritem);
                break;
            case "Uttar Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_uttar_pradesh_districts, R.layout.spinneritem);
                break;
            case "Uttarakhand":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_uttarakhand_districts, R.layout.spinneritem);
                break;
            case "West Bengal":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_west_bengal_districts, R.layout.spinneritem);
                break;
            case "Andaman and Nicobar Islands":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_andaman_nicobar_districts, R.layout.spinneritem);
                break;
            case "Chandigarh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_chandigarh_districts, R.layout.spinneritem);
                break;
            case "Dadra and Nagar Haveli":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_dadra_nagar_haveli_districts, R.layout.spinneritem);
                break;
            case "Daman and Diu":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_daman_diu_districts, R.layout.spinneritem);
                break;
            case "Delhi":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_delhi_districts, R.layout.spinneritem);
                break;
            case "Jammu and Kashmir":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_jammu_kashmir_districts, R.layout.spinneritem);
                break;
            case "Lakshadweep":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_lakshadweep_districts, R.layout.spinneritem);
                break;
            case "Ladakh":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_ladakh_districts, R.layout.spinneritem);
                break;
            case "Puducherry":
                cityAdapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.array_puducherry_districts, R.layout.spinneritem);
                break;
            default:
                break;
        }
        cityAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        return cityAdapter;
    }



}