package com.example.careitable.activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.careitable.databinding.ActivityCreateDonationBinding;
import com.example.careitable.service.CheckNetwork;
import com.example.careitable.R;
import com.example.careitable.dao.UserData;
import com.example.careitable.fragments.AdDetailsFragment;
import com.example.careitable.fragments.AdPhotosFragment;
import com.example.careitable.fragments.AdUserInfoFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class CreateDonationActivity extends AppCompatActivity {

    private ActivityCreateDonationBinding binding;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private String donationAdUid;
    private RelativeLayout loading;
    private TextView cancelTextView, nextTextView;
    private CheckBox agreeCheckBox;
    private CardView agreeCardView;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateDonationBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        if (!CheckNetwork.isInternetAvailable(this)){
            super.onBackPressed();
        }
        setupViewPager();
        initialiseComponents();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        String time = System.currentTimeMillis() + "";
        donationAdUid = "D" + time.substring(time.length() - 7);

        loading = findViewById(R.id.loading);
    }

    private void initialiseComponents() {
        cancelTextView = findViewById(R.id.cancel);
        nextTextView = findViewById(R.id.next);
        loading = findViewById(R.id.loading);
        agreeCardView = findViewById(R.id.agreeCardView);
        agreeCheckBox = findViewById(R.id.agreeCheckBox);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    MaterialDialog mDialog = new MaterialDialog.Builder(CreateDonationActivity.this)
                            .setTitle("Discard Donation Post?")
                            .setMessage("All the filled details will be Lost!")
                            .setCancelable(false)
                            .setPositiveButton("Discard", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    // Delete Operation
                                    CreateDonationActivity.super.onBackPressed();
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
                } else if (mViewPager.getCurrentItem() == 1) {
                    mViewPager.setCurrentItem(0);
                } else if (mViewPager.getCurrentItem() == 2) {
                    mViewPager.setCurrentItem(1);
                }
            }
        });

        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 0) {
                    mViewPager.setCurrentItem(1);
                } else if (mViewPager.getCurrentItem() == 1) {
                    mViewPager.setCurrentItem(2);

                } else if (mViewPager.getCurrentItem() == 2) {
                    agreeCardView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupViewPager() {
        SectionsPagerAdapter mSectionsPagerAdapter;
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setPageMargin(0);

        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setOffscreenPageLimit(3);
            //mViewPager.setPageTransformer(true, new DepthPageTransformer());
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @SuppressLint("SetTextI18n")
            public void onPageSelected(int position) {
                if (position == 0) {
                    cancelTextView.setText("Cancel");
                    nextTextView.setText("Continue");
                } else if (position == 1) {
                    cancelTextView.setText("Previous");
                    nextTextView.setText("Continue");
                } else if (position == 2) {
                    cancelTextView.setText("Previous");
                    nextTextView.setText("Post");
                }
            }
        });
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AdPhotosFragment();
                case 1:
                    return new AdDetailsFragment();
                case 2:
                    return new AdUserInfoFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void finalPost(View v) {
        if (agreeCheckBox.isChecked())
            post();
        else
            agreeCheckBox.setError("mandatory");
    }

    private void post() {
        agreeCardView.setVisibility(View.GONE);

        /* ------------------------ Page 0 --------------------------------------*/
        if (AdPhotosFragment.img2.isEmpty()) {
            mViewPager.setCurrentItem(0);
            Toast.makeText(this, "Atleast one organisationLogoImageView must be uploaded", Toast.LENGTH_SHORT).show();

        }
        /* ------------------------ Page 1 --------------------------------------*/
        else if (AdDetailsFragment.edtTitle.getText().toString().isEmpty()) {
            mViewPager.setCurrentItem(1);
            AdDetailsFragment.edtTitle.setError("Required Field");
            AdDetailsFragment.edtTitle.requestFocus();
        } else if (AdDetailsFragment.edtDesc.getText().toString().isEmpty()) {
            mViewPager.setCurrentItem(1);
            AdDetailsFragment.edtDesc.setError("Required Field");
            AdDetailsFragment.edtDesc.requestFocus();
        } else if (AdDetailsFragment.selectedCardInt == -1) {
            mViewPager.setCurrentItem(1);
            Toast.makeText(this, "Please Select A Category", Toast.LENGTH_SHORT).show();
        }
        /* ------------------------ Page 2 --------------------------------------*/
        else if (AdUserInfoFragment.edtPhoneNumber.getText().toString().isEmpty()) {
            mViewPager.setCurrentItem(2);
            AdUserInfoFragment.edtPhoneNumber.setError("Required Field");
            AdUserInfoFragment.edtPhoneNumber.requestFocus();
        } else if (!isValid(AdUserInfoFragment.edtPhoneNumber.getText().toString())) {
            mViewPager.setCurrentItem(2);
            AdUserInfoFragment.edtPhoneNumber.setError("Invalid Format");
            AdUserInfoFragment.edtPhoneNumber.requestFocus();
        } else if (AdUserInfoFragment.cityName.equals("Select City") || AdUserInfoFragment.stateName.equals("Select State")) {
            mViewPager.setCurrentItem(3);
            Toast.makeText(this, "Please Select A Location", Toast.LENGTH_SHORT).show();
        } else {
            uploadPhotos();
        }
    }

    private void uploadPhotos() {
        for (int i = 0; i < AdPhotosFragment.img2.size(); i++) {
            uploadToStorage(i);
        }
    }

    private void uploadToStorage(final int position) {
        loading.setVisibility(View.VISIBLE);
        Uri file = Uri.fromFile(AdPhotosFragment.img2.get(position));
        StorageReference riversRef = mStorage.child("donationAds/" + donationAdUid + "/" + donationAdUid + "_" + position + ".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (position == (AdPhotosFragment.img2.size() - 1)) {
                            createDonationAdDatabase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        loading.setVisibility(View.GONE);
                        //Toast.makeText(PostAdMain.this, "error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(CreateDonationActivity.this, "unknown error (re023)", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createDonationAdDatabase() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference donationAdDocument = db.collection("DonationAds").document(donationAdUid);
        final DocumentReference userAccountDocument = db.collection("Users").document(mAuth.getCurrentUser().getUid());

        final Map<String, Object> value = new HashMap<>();

        /* ------------------------ Page 0 -------------------------*/
        value.put("owner", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        value.put("photos", AdPhotosFragment.img2.size());

        /* ------------------------ Page 1 -------------------------*/
        value.put("organisationName", AdDetailsFragment.edtTitle.getText().toString());
        value.put("description", AdDetailsFragment.edtDesc.getText().toString());
        value.put("category", AdDetailsFragment.selectedCardInt);
        if (AdDetailsFragment.isNewCheckbox.isChecked()) {
            value.put("isNew", true);
        } else {
            value.put("isNew", false);
        }

        /* ------------------------ Page 2 -------------------------*/
        value.put("phoneNumber", AdUserInfoFragment.edtPhoneNumber.getText().toString());
        value.put("state", AdUserInfoFragment.stateName);
        value.put("city", AdUserInfoFragment.cityName);
        value.put("location",AdUserInfoFragment.cityName+", "+AdUserInfoFragment.stateName);
        value.put("datePosted", getDate());
        value.put("timestamp", new Date());
        value.put("isDonated",false);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot userSnapshot = transaction.get(userAccountDocument);
                UserData ufo = userSnapshot.toObject(UserData.class);

                assert ufo != null;
                List<String> currentAds = ufo.getDonationAdIds();
                if (currentAds != null) {
                    currentAds.add(donationAdUid);
                    transaction.update(userAccountDocument, "donationAdIds", currentAds);
                } else {
                    currentAds = new ArrayList<>();
                    currentAds.add(donationAdUid);
                    transaction.update(userAccountDocument, "donationAdIds", currentAds);
                }

                transaction.set(donationAdDocument, value);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loading.setVisibility(View.GONE);
                setContentView(R.layout.activity_ad_posted);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading.setVisibility(View.GONE);
                        //Toast.makeText(PostAdMain.this, "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(CreateDonationActivity.this, "unknown error (re024)" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static boolean isValid(String s) {


        Pattern p = Pattern.compile("^[6-9]\\d{9}$");

        Matcher m = p.matcher(s);

        return (m.matches());
    }

    public String getDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public void mainscreen(View v) {
        finish();
        super.onBackPressed();
    }
    @Override
    public void onBackPressed(){

        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Discard Donation Post?")
                .setMessage("All the filled details will be Lost!")
                .setCancelable(false)
                .setPositiveButton("Discard", R.drawable.ic_close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // Delete Operation
                        CreateDonationActivity.super.onBackPressed();
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

    }
}