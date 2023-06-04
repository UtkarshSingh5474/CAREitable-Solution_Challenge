package com.example.careitable.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.careitable.R;
import com.example.careitable.activity.CharityViewActivity;
import com.example.careitable.dao.CharityListObject;
import com.example.careitable.fragments.CharityFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CharityListAdapter extends RecyclerView.Adapter<CharityListAdapter.GroceryViewHolder> {
    private List<CharityListObject> charityList;
    Context context;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CharityListAdapter(List<CharityListObject> charityList, Context context) {
        this.charityList = charityList;
        this.context = context;
    }

    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.charities_recyclerview_item, parent, false);
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
        holder.organisationName.setText(charityList.get(position).getOrganisationName());
        holder.desc.setText(charityList.get(position).getOrganisationDescription());
        holder.location.setText(charityList.get(position).getOrganisationCity()+", " + charityList.get(position).getOrganisationState());


        updatePhotos(holder.organisationLogoImageView, charityList.get(position).getCharityID());


    }
    private void updatePhotos(final ImageView orgLogo, String charityID) {
        try {
            mStorage.child("charities/"+ charityID + "/" +charityID+"-orgLogoImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri).into(orgLogo);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return charityList.size();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView organisationName;
        TextView location;
        TextView desc;
        ImageView organisationLogoImageView;

        public GroceryViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            organisationName = view.findViewById(R.id.organisationNameTV);
            desc = view.findViewById(R.id.organisationDescriptionTV);
            location = view.findViewById(R.id.organisationLocationTV);
            organisationLogoImageView = view.findViewById(R.id.organisationLogoImaveView);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, CharityViewActivity.class);
            i.putExtra("CHARITY_ID", CharityFragment.charityListPosition.get(getAdapterPosition()));
            context.startActivity(i);
        }
    }



}
