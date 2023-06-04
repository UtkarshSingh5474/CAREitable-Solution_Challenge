package com.example.careitable.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.careitable.dao.CharityData;
import com.example.careitable.databinding.ActivityCharityViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CharityViewActivity extends AppCompatActivity {

    private ActivityCharityViewBinding binding;
    private String charityId;

    private CharityData charityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCharityViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        charityId = getIntent().getStringExtra("CHARITY_ID");

        getDetails();

        binding.donateLL.setOnClickListener(v -> {
            Toast.makeText(this, "This will start the payment procedure", Toast.LENGTH_SHORT).show();
        });

    }

    private void getDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Charities").document(charityId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot charitySnapshot = transaction.get(docRef);
                if (charitySnapshot.exists()) {
                    CharityData pfo = charitySnapshot.toObject(CharityData.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            binding.aboutCharityTv.setText(pfo.getNameOrganisation());

                            String[] charitySectors = pfo.getSectorsCharity().split(" ");
                            binding.sectorsTag.setTags(charitySectors);

                            String[] charityDemographics = pfo.getDemographicsCharity().split(" ");
                            binding.demographicsTag.setTags(charityDemographics);

                            binding.impactCharityTV.setText(pfo.getImpactCharity());

                            binding.nameOrganisationTV.setText(pfo.getNameOrganisation());
                            binding.descriptionOrganisationTV.setText(pfo.getDescriptionOrganisation());
                            binding.tvLocation.setText(pfo.getCityOrganisation() + ", " + pfo.getStateOrganisation());
                            binding.locationOrganisationTV.setText(pfo.getCityOrganisation() + ", " + pfo.getStateOrganisation());
                            binding.typeOrganisationTV.setText(pfo.getTypeOrganisation());
                            binding.websiteOrganisationTV.setText(pfo.getWebsiteOrganisation());

                            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                            try {
                                mStorage.child("charities/"+ charityId + "/" +charityId+"-orgLogoImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext()).load(uri).into(binding.photo);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Something wrong. Please Try again", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error Code. T11",Toast.LENGTH_SHORT).show();
            }
        });
    }


}