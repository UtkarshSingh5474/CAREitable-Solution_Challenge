package com.example.careitable.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.careitable.R;
import com.example.careitable.activity.ImageListActivity;
import com.example.careitable.dao.ImageListObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.GroceryViewHolder>{
    private List<ImageListObject> horizontalRecommendedList;
    Context context;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    public ImageListAdapter(List<ImageListObject> horizontalRecommendedList, Context context){
        this.horizontalRecommendedList = horizontalRecommendedList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_layout, parent, false);
        GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, final int position) {
        updatePhotos(holder.recImageView, horizontalRecommendedList.get(position).getPid(), horizontalRecommendedList.get(position).getPos());
    }

    @Override
    public int getItemCount() {
        return horizontalRecommendedList.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView recImageView;
        public GroceryViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            recImageView =view.findViewById(R.id.organisationLogoImaveView);

        }

        @Override
        public void onClick(View view) {
            ImageListActivity.zoomableRelativeLayout.setVisibility(View.VISIBLE);
            ImageListActivity.updatePhotos(getAdapterPosition());
        }
    }

    private void updatePhotos(final ImageView photo, String propertyUid, int pos)
    {
        try {
            StorageReference riversRef = mStorage.child("donationAds/" + propertyUid + "/" + propertyUid + "_" + pos + ".jpg");
            File localFile = null;
            localFile = File.createTempFile(propertyUid + "_" + pos, "jpg");

            final File finalLocalFile = localFile;
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            photo.setImageBitmap(getResizedBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), 500));
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

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}