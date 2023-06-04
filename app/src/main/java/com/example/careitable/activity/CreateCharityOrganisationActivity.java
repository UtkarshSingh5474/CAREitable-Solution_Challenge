package com.example.careitable.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.careitable.dao.CharityData;
import com.example.careitable.R;
import com.example.careitable.databinding.ActivityCreateCharityOrganisationBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class CreateCharityOrganisationActivity extends AppCompatActivity {

    private ActivityCreateCharityOrganisationBinding binding;

    private ImageView orgLogoImageView;
    private Bitmap orgLogoBitmap;
    ActivityResultLauncher<Intent> organiserLogoImageActivityResultLauncher;

    Spinner stateSpinner, citySpinner;
    public static String cityName = "Select City";
    public static String stateName = "Select State";
    private ArrayAdapter<CharSequence> stateAdapter, cityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCharityOrganisationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        orgLogoImageView = binding.organiserLogoImageView;
        initialiseSpinners(binding.getRoot());

        binding.cancel.setOnClickListener(v -> {
            MaterialDialog mDialog = new MaterialDialog.Builder(CreateCharityOrganisationActivity.this)
                    .setTitle("Discard Donation Post?")
                    .setMessage("All the filled details will be Lost!")
                    .setCancelable(false)
                    .setPositiveButton("Discard", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            // Delete Operation
                            onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancel", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .build();

            // Show Dialog
            mDialog.show();
        });

        // Create an ActivityResultLauncher that will handle the result of the gallery or camera intent
        organiserLogoImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getData() != null) {
                                // Get the image URI from the data
                                Uri selectedImage = data.getData();
                                try {
                                    // Load the image into a bitmap
                                    orgLogoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                    // Resize the bitmap
                                    //orgLogoBitmap = getResizedBitmap(orgLogoBitmap, 500); // Change 500 to the desired size in pixels
                                    // Set the bitmap to the ImageView
                                    orgLogoImageView.setImageBitmap(orgLogoBitmap);
                                    binding.organiserLogoPlaceholderImageView.setVisibility(View.GONE);
                                    orgLogoImageView.setVisibility(View.VISIBLE);
                                    binding.organiserLogoImageViewContainer.setVisibility(View.VISIBLE);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (data.getExtras() != null) {
                                // Get the image from the camera intent
                                orgLogoBitmap = (Bitmap) data.getExtras().get("data");
                                // Resize the bitmap
                                //orgLogoBitmap = getResizedBitmap(orgLogoBitmap, 500); // Change 500 to the desired size in pixels
                                // Set the bitmap to the ImageView
                                orgLogoImageView.setImageBitmap(orgLogoBitmap);
                                binding.organiserLogoPlaceholderImageView.setVisibility(View.GONE);
                                orgLogoImageView.setVisibility(View.VISIBLE);
                                binding.organiserLogoImageViewContainer.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });

        binding.organiserLogoCV.setOnClickListener(view -> {
            //take gallery permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
            chooseFromGallery(1);
        });

        binding.next.setOnClickListener(v -> {
            checkFields();
        });

        binding.btnPost.setOnClickListener(view -> {
            if(binding.agreeCheckBox.isChecked()){
                //Post to database
                postToDatabase();
            }else {
                Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkFields(){

        binding.agreeCardView.setVisibility(View.GONE);

        if (binding.aboutCharity.getText().toString().isEmpty()){
            binding.aboutCharity.setError("Please enter a charity description");
            return;
        }

        if (binding.sectorsTagsEditText.getText().toString().isEmpty()){
            binding.sectorsTagsEditText.setError("Please enter a charity sector");
            return;
        }

        if (binding.demographicsTagsEditText.getText().toString().isEmpty()){
            binding.demographicsTagsEditText.setError("Please enter a charity demographic");
            return;
        }

        if (binding.impactCharity.getText().toString().isEmpty()){
            binding.impactCharity.setError("Please enter a charity impact");
            return;
        }

        if (binding.nameOrganisation.getText().toString().isEmpty()){
            binding.nameOrganisation.setError("Please enter a charity name");
            return;
        }

        if(binding.descriptionOrganisation.getText().toString().isEmpty()){
            binding.descriptionOrganisation.setError("Please enter a charity description");
            return;
        }

        if (binding.organiserLogoImageView.getDrawable() == null) {
            binding.organiserLogoOuterCV.getParent().requestChildFocus(binding.organiserLogoCV, binding.organiserLogoCV);
            Toast.makeText(this, "Add a organisation logo", Toast.LENGTH_SHORT).show();
            return;
        }

        if(cityName=="Select City"){
            Toast.makeText(this, "Select a city", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stateName=="Select State"){
            Toast.makeText(this, "Select a state", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.organisationType.getText().toString().isEmpty()){
            binding.organisationType.setError("Please enter a charity type");
            return;
        }

        if (binding.websiteOrganisation.getText().toString().isEmpty()){
            binding.websiteOrganisation.setError("Please enter a charity website");
            return;
        }

        binding.agreeCardView.setVisibility(View.VISIBLE);

    }

    private void postToDatabase(){
        //Post to database
        binding.agreeCheckBox.setActivated(false);
        binding.agreeCardView.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);

        CharityData charityData = new CharityData();
        charityData.setAboutCharity(binding.aboutCharity.getText().toString());
        charityData.setSectorsCharity(binding.sectorsTagsEditText.getText().toString());
        charityData.setDemographicsCharity(binding.demographicsTagsEditText.getText().toString());
        charityData.setImpactCharity(binding.impactCharity.getText().toString());
        charityData.setNameOrganisation(binding.nameOrganisation.getText().toString());
        charityData.setDescriptionOrganisation(binding.descriptionOrganisation.getText().toString());
        charityData.setStateOrganisation(stateName);
        charityData.setCityOrganisation(cityName);
        charityData.setTypeOrganisation(binding.organisationType.getText().toString());
        charityData.setWebsiteOrganisation(binding.websiteOrganisation.getText().toString());


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Charities")
                .add(charityData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Charity Created Successfully", Toast.LENGTH_SHORT).show();
                    //binding.progressBar.setVisibility(View.GONE);

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    // Generate a unique ID for the images
                    String docId = documentReference.getId();

                    // Create a reference to the folder with the same ID in Cloud Storage
                    StorageReference imagesFolderRef = storageRef.child("charities/" + docId + "/");

                    // Get the bitmaps you want to upload
                    Bitmap bitmap1 = orgLogoBitmap;

                    // Create new filenames for the images
                    String filename1 = docId + "-orgLogoImage.png";

                    // Convert the bitmaps to byte arrays
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
                    byte[] byteArray1 = baos1.toByteArray();


                    // Upload the images to Cloud Storage
                    imagesFolderRef.child(filename1).putBytes(byteArray1);

                    binding.loading.setVisibility(View.GONE);

                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.loading.setVisibility(View.GONE);
                    Toast.makeText(this, "Error Creating Charity"+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to handle when the user clicks the "Choose from Gallery" button
    public void chooseFromGallery(int isOrganiserLogo) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (isOrganiserLogo == 1) {
            organiserLogoImageActivityResultLauncher.launch(intent);
        }
    }

    private void initialiseSpinners(View root) {



        stateSpinner = root.findViewById(R.id.stateSpinner);
        citySpinner = root.findViewById(R.id.citySpinner);

        stateAdapter = ArrayAdapter.createFromResource(this, R.array.States, R.layout.spinneritem);
        stateAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);


        if (cityName == null || stateName == null) {

            stateSpinner.setSelection(stateAdapter.getPosition("Select State"));
            cityAdapter = ArrayAdapter.createFromResource(this, R.array.array_default_districts, R.layout.spinneritem);
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

    }
    private ArrayAdapter<CharSequence> getCityAdapter(String stName) {

        switch (stName) {
            case "Select State":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_default_districts, R.layout.spinneritem);
                break;
            case "Andhra Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_andhra_pradesh_districts, R.layout.spinneritem);
                break;
            case "Arunachal Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_arunachal_pradesh_districts, R.layout.spinneritem);
                break;
            case "Assam":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_assam_districts, R.layout.spinneritem);
                break;
            case "Bihar":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_bihar_districts, R.layout.spinneritem);
                break;
            case "Chhattisgarh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_chhattisgarh_districts, R.layout.spinneritem);
                break;
            case "Goa":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_goa_districts, R.layout.spinneritem);
                break;
            case "Gujarat":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_gujarat_districts, R.layout.spinneritem);
                break;
            case "Haryana":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_haryana_districts, R.layout.spinneritem);
                break;
            case "Himachal Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_himachal_pradesh_districts, R.layout.spinneritem);
                break;
            case "Jharkhand":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_jharkhand_districts, R.layout.spinneritem);
                break;
            case "Karnataka":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_karnataka_districts, R.layout.spinneritem);
                break;
            case "Kerala":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_kerala_districts, R.layout.spinneritem);
                break;
            case "Madhya Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_madhya_pradesh_districts, R.layout.spinneritem);
                break;
            case "Maharashtra":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_maharashtra_districts, R.layout.spinneritem);
                break;
            case "Manipur":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_manipur_districts, R.layout.spinneritem);
                break;
            case "Meghalaya":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_meghalaya_districts, R.layout.spinneritem);
                break;
            case "Mizoram":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_mizoram_districts, R.layout.spinneritem);
                break;
            case "Nagaland":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_nagaland_districts, R.layout.spinneritem);
                break;
            case "Odisha":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_odisha_districts, R.layout.spinneritem);
                break;
            case "Punjab":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_punjab_districts, R.layout.spinneritem);
                break;
            case "Rajasthan":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_rajasthan_districts, R.layout.spinneritem);
                break;
            case "Sikkim":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_sikkim_districts, R.layout.spinneritem);
                break;
            case "Tamil Nadu":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_tamil_nadu_districts, R.layout.spinneritem);
                break;
            case "Telangana":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_telangana_districts, R.layout.spinneritem);
                break;
            case "Tripura":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_tripura_districts, R.layout.spinneritem);
                break;
            case "Uttar Pradesh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_uttar_pradesh_districts, R.layout.spinneritem);
                break;
            case "Uttarakhand":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_uttarakhand_districts, R.layout.spinneritem);
                break;
            case "West Bengal":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_west_bengal_districts, R.layout.spinneritem);
                break;
            case "Andaman and Nicobar Islands":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_andaman_nicobar_districts, R.layout.spinneritem);
                break;
            case "Chandigarh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_chandigarh_districts, R.layout.spinneritem);
                break;
            case "Dadra and Nagar Haveli":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_dadra_nagar_haveli_districts, R.layout.spinneritem);
                break;
            case "Daman and Diu":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_daman_diu_districts, R.layout.spinneritem);
                break;
            case "Delhi":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_delhi_districts, R.layout.spinneritem);
                break;
            case "Jammu and Kashmir":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_jammu_kashmir_districts, R.layout.spinneritem);
                break;
            case "Lakshadweep":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_lakshadweep_districts, R.layout.spinneritem);
                break;
            case "Ladakh":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_ladakh_districts, R.layout.spinneritem);
                break;
            case "Puducherry":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.array_puducherry_districts, R.layout.spinneritem);
                break;
            default:
                break;
        }
        cityAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        return cityAdapter;
    }
}