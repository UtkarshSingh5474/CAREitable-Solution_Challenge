package com.example.careitable;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageListActivity extends AppCompatActivity {

    private RecyclerView imagesRecyclerView;
    private static String propertyUid;
    static PhotoView zoomablePhotoView;
    static RelativeLayout zoomableRelativeLayout;
    private Long photosCount;
    private static ImageListAdapter recommendedListAdapter;
    private static List<ImageListObject> recommendedObject = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        propertyUid = getIntent().getStringExtra("PROPERTY_UID");
        photosCount = getIntent().getLongExtra("PHOTOS_COUNT", 1L);
        initialiseComponents();
    }

    private void initialiseComponents() {
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        zoomablePhotoView = findViewById(R.id.zoomablePhotoView);
        zoomableRelativeLayout = findViewById(R.id.zoomableRelativeLayout);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(ImageListActivity.this, LinearLayoutManager.VERTICAL, false);
        imagesRecyclerView.setLayoutManager(horizontalLayoutManager);
        recommendedListAdapter = new ImageListAdapter(recommendedObject, ImageListActivity.this);
        imagesRecyclerView.setAdapter(recommendedListAdapter);

        recommendedObject = new ArrayList<>();
        for (int i = 0; i < photosCount; i++)
            recommendedObject.add(new ImageListObject(propertyUid, i));
        recommendedListAdapter = new ImageListAdapter(recommendedObject, ImageListActivity.this);
        imagesRecyclerView.setAdapter(recommendedListAdapter);
        recommendedListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void closeImage(View v)
    {
        zoomableRelativeLayout.setVisibility(View.GONE);
        zoomablePhotoView.setImageBitmap(null);
    }

    static void updatePhotos(int pos)
    {
        try {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = mStorage.child("donationAds/" + propertyUid + "/" + propertyUid + "_" + pos + ".jpg");
            File localFile = null;
            localFile = File.createTempFile(propertyUid + "_" + pos+"_zoom", "jpg");

            final File finalLocalFile = localFile;
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            zoomablePhotoView.setImageBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
