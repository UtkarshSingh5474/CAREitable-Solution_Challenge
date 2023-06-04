package com.example.careitable.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.careitable.dao.DonationAdsFetchObject;
import com.example.careitable.R;
import com.example.careitable.databinding.ActivityDonationAdViewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class DonationAdViewActivity extends AppCompatActivity {

    private static final String TAG = "donation-ad-activity";
    private ActivityDonationAdViewBinding binding;
    private long photoCount;
    private ImageView photo, catImageView;
    private TextView tvTitle, tvDesc, catTv,tvUserName,tvPhotoCount,tvLocation,tvPostedDate;
    private CardView markAsDonated, viewerCV;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private String donationAdId;
    private String phoneNumber;
    private String ownerID;
    private String ownerName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonationAdViewBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        mStorage = FirebaseStorage.getInstance().getReference();

        donationAdId = getIntent().getStringExtra("DONATION_AD_UID");


        initialiseComponents();

        binding.photo.setOnClickListener(view -> {

            if (photoCount > 0L) {
                Intent i = new Intent(this, ImageListActivity.class);
                i.putExtra("PROPERTY_UID", donationAdId);
                i.putExtra("PHOTOS_COUNT", photoCount);
                startActivity(i);
            } else {
                Toast.makeText(this, "there are no photos", Toast.LENGTH_SHORT).show();
            }

        });

        binding.callLL.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(intent);

        });

        binding.chatLL.setOnClickListener(view -> {

            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "+91" + phoneNumber + "&text=" + "Hi! I got your number from CAREitable App.");

            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(sendIntent);

        });

        binding.markAsDonated.setOnClickListener(view -> {

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this)
                    .setTitle("Mark As Donated")
                    .setMessage("Marking as donated will remove this Post")
                    .setCancelable(true)
                    .setPositiveButton("Confirm", new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference donationAdDocument = db.collection("DonationAds").document(donationAdId);
                            donationAdDocument.update("isDonated",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(DonationAdViewActivity.this, "Thanks For Donating!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    onBackPressed();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DonationAdViewActivity.this, "An Error Occurred!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            });


                        }
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .build();

            // Show Dialog
            mBottomSheetDialog.show();



        });

        binding.delete.setOnClickListener(view -> {

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Deleting will permanently remove this Post")
                    .setCancelable(true)
                    .setPositiveButton("Delete",R.drawable.ic_delete, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference donationAdDocument = db.collection("DonationAds").document(donationAdId);
                            DocumentReference userAccountDocument = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            db.runTransaction(new Transaction.Function<Void>() {

                                @Nullable
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                    transaction.delete(donationAdDocument);
                                    transaction.update(userAccountDocument,"donationAdIds",FieldValue.arrayRemove(donationAdId));
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    StorageReference folderRef = FirebaseStorage.getInstance().getReference().child("donationAds/" + donationAdId );
                                    folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            for (StorageReference item : listResult.getItems()) {
                                                item.delete();
                                            }
                                            Toast.makeText(DonationAdViewActivity.this, "Post Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                            onBackPressed();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonationAdViewActivity.this, "An Error Occurred!", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(DonationAdViewActivity.this, "An Error Occurred!", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();

                                }
                            });


                        }
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .build();

            // Show Dialog
            mBottomSheetDialog.show();



        });
    }

    private void initialiseComponents() {

        photo = findViewById(R.id.photo);
        catImageView = findViewById(R.id.catImageView);
        catTv = findViewById(R.id.catTv);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
        tvUserName = findViewById(R.id.tvUserName);
        viewerCV = findViewById(R.id.viewerCV);
        tvPhotoCount = findViewById(R.id.tvPhotoCount);
        tvLocation = findViewById(R.id.tvLocation);
        tvPostedDate = findViewById(R.id.tvPostedDate);
        getDetails();
    }

    private void getDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("DonationAds").document(donationAdId);
        CollectionReference userCollectedRef = db.collection("Users");
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot donationAdsSnapshot = transaction.get(docRef);
                if (donationAdsSnapshot.exists()) {
                    DonationAdsFetchObject pfo = donationAdsSnapshot.toObject(DonationAdsFetchObject.class);
                    Log.i(TAG, "apply: "+pfo.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //tvPhotoCount.setText(pfo.toString());
                            photoCount = pfo.getPhotos();
                            tvPhotoCount.setText(""+photoCount);
                            if (photoCount > 0L)
                                updatePhotos();
                            else
                                Toast.makeText(getApplicationContext(), "there are no photos for this property", Toast.LENGTH_SHORT).show();
                            tvLocation.setText(pfo.getLocation());
                            tvPostedDate.setText("Posted: " + pfo.getDatePosted());
                            tvTitle.setText(pfo.getTitle());
                            tvDesc.setText(pfo.getDescription());
                            switch (pfo.getCategory()){
                                case 0:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_clothesdonate));
                                    catTv.setText("Clothes");
                                    break;
                                case 1:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_fooddonate));
                                    catTv.setText("Food");
                                    break;
                                case 2:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_bookdonate));
                                    catTv.setText("Books");
                                    break;
                                case 3:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_toydonate));
                                    catTv.setText("Toys");
                                    break;
                                case 4:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_stationarydonate));
                                    catTv.setText("Stationary");
                                    break;
                                case 5:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_electronicdonate));
                                    catTv.setText("Electronics");
                                    break;
                                case 6:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_medicinedonate));
                                    catTv.setText("Medicines");
                                    break;
                                case 7:
                                    catImageView.setImageDrawable(getDrawable(R.drawable.ic_otherdonate));
                                    catTv.setText("Others");
                                    break;
                            }
                            if (pfo.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                binding.donateDeleteCV.setVisibility(View.VISIBLE);

                            }else {
                                viewerCV.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    ownerID = pfo.getOwner();
                    phoneNumber = pfo.getPhoneNumber();
                } else {
                    Toast.makeText(getApplicationContext(), "Something wrong. Please Login again", Toast.LENGTH_SHORT).show();
                }
                DocumentSnapshot userDocumentSnapshot = transaction.get(userCollectedRef.document(ownerID));
                ownerName = userDocumentSnapshot.getString("name");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvUserName.setText(ownerName);
                    }
                });

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error Code. T11",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePhotos() {
        try {
            StorageReference riversRef = mStorage.child("donationAds/" + donationAdId + "/" + donationAdId + "_" + 0 + ".jpg");
            File localFile = null;
            localFile = File.createTempFile(donationAdId + "_" + 0, "jpg");

            final File finalLocalFile = localFile;
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            photo.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Toast.makeText(AdsDetailedView.this, "error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "unknown error (re005)", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void imageList() {

    }
}