package com.example.careitable.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.careitable.R;
import com.example.careitable.databinding.FragmentAdUserInfoBinding;
import com.example.careitable.databinding.FragmentDonationsBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

public class AdUserInfoFragment extends Fragment implements LocationListener {

    private FragmentAdUserInfoBinding binding;
    LocationManager locationManager;
    Spinner stateSpinner, citySpinner;
    public static String cityName = "Select City";
    public static String stateName = "Select State";
    public static EditText edtPhoneNumber;
    private ArrayAdapter<CharSequence> stateAdapter, cityAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdUserInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        edtPhoneNumber = binding.edtPhoneNumber;

        getLocationFromDevice();
        initialiseComponents(view);

        binding.btnGetLocation.setOnClickListener(view1 -> {

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

        return view;
    }



    private void initialiseComponents(View root) {



        stateSpinner = root.findViewById(R.id.stateSpinner);
        citySpinner = root.findViewById(R.id.citySpinner);

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
                citySpinner = binding.citySpinner;
                stateName = stateSpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.stateSpinner) {
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
                cityName = citySpinner.getSelectedItem().toString();
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnGetLocation.setOnClickListener(view -> {

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

    @Override
    public void onLocationChanged(@NonNull Location location) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();

            initialiseComponents(getView());


            Toast.makeText(getContext(), "Location Updated", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

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
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
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