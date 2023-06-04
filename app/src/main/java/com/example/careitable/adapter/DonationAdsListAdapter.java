package com.example.careitable.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.careitable.R;
import com.example.careitable.activity.DonationAdViewActivity;
import com.example.careitable.dao.DonationAdsListObject;
import com.example.careitable.fragments.DonationsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DonationAdsListAdapter extends RecyclerView.Adapter<DonationAdsListAdapter.GroceryViewHolder> {
    private List<DonationAdsListObject> donationAdsList;
    Context context;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public DonationAdsListAdapter(List<DonationAdsListObject> donationAdsList, Context context) {
        this.donationAdsList = donationAdsList;
        this.context = context;
    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_list_layout, parent, false);
        GroceryViewHolder gvh = new GroceryViewHolder(groceryProductView);
        return gvh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final GroceryViewHolder holder, final int position) {
        holder.title.setText(donationAdsList.get(position).getTitle());
        holder.desc.setText(donationAdsList.get(position).getDesc());
        holder.city.setText(donationAdsList.get(position).getCity());
        holder.date.setText(donationAdsList.get(position).getDate());
        switch (donationAdsList.get(position).getCategory()){
            case 0: holder.category.setText("Clothes");
                break;

            case 1: holder.category.setText("Food");
                break;

            case 2: holder.category.setText("Books");
                break;

            case 3: holder.category.setText("Toys");
                break;

            case 4: holder.category.setText("Stationary");
                break;

            case 5: holder.category.setText("Electronics");
                break;

            case 6: holder.category.setText("Medicine");
                break;

            case 7: holder.category.setText("Others");
                break;

            default: break;
        }


        updatePhotos(holder.photo, donationAdsList.get(position).getAdId());

    }

    @Override
    public int getItemCount() {
        return donationAdsList.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView city;
        TextView category;
        TextView date;
        TextView desc;
        ImageView photo;

        public GroceryViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = view.findViewById(R.id.title_list_layout);
            desc = view.findViewById(R.id.desc_list_layout);
            date = view.findViewById(R.id.date_list_layout);
            city = view.findViewById(R.id.city_list_layout);
            category = view.findViewById(R.id.category_list_layout);
            photo = view.findViewById(R.id.image_list_layout);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, DonationAdViewActivity.class);
            i.putExtra("DONATION_AD_UID", DonationsFragment.donationAdListPosition.get(getAdapterPosition()));
            context.startActivity(i);
        }
    }

    private void updatePhotos(final ImageView photo, String donationAdId) {
        try {
            StorageReference riversRef = mStorage.child("donationAds/" + donationAdId + "/" + donationAdId + "_" + 0 + ".jpg");
            File localFile = null;
            localFile = File.createTempFile(donationAdId + "_" + 0, "jpg");

            final File finalLocalFile = localFile;
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            photo.setImageBitmap(getResizedBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()), 150));
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

        float bitmapRatio = (float) width / (float) height;
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
