package com.example.careitable;

import android.graphics.Bitmap;

public class PhotoListObject {

    private Bitmap image;

    public PhotoListObject(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

}