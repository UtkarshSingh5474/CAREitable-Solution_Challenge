package com.example.careitable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class PhotosAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<PhotoListObject> objects;

    public PhotosAdapter(Context context, ArrayList<PhotoListObject> objects) {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    public int getCount() {
        return objects.size();
    }

    public PhotoListObject getItem(int position) {
        return objects.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.photos_list_layout, null);
            holder.imageview = convertView.findViewById(R.id.image_list_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageview.setImageBitmap(objects.get(position).getImage());
        return convertView;
    }

    private class ViewHolder {
        ImageView imageview;
    }
}
