package com.example.careitable.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import id.zelory.compressor.Compressor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.careitable.BuildConfig;
import com.example.careitable.service.FileUtil;
import com.example.careitable.dao.PhotoListObject;
import com.example.careitable.adapter.PhotosAdapter;
import com.example.careitable.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



import java.io.File;
import java.util.ArrayList;
import java.util.Objects;



public class UploadPhotoActivity extends AppCompatActivity {

    private ListView photosListView;
    private FloatingActionButton addFloatingActionButton;
    static ArrayList<File> img;
    static ArrayList<File> img2;
    private int positionPhoto = 0;
    private ArrayList<PhotoListObject> objects = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        initialiseComponents();
        img = new ArrayList<>();
        img2 = new ArrayList<>();
    }

    private void initialiseComponents()
    {
        photosListView = findViewById(R.id.photosListView);
        addFloatingActionButton = findViewById(R.id.addFloatingActionButton);

        photosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                objects.remove(position);
                img.remove(position);
                PhotosAdapter customAdapter = new PhotosAdapter(getApplicationContext(), objects);
                photosListView.setAdapter(customAdapter);
                positionPhoto-=1;
            }
        });

        addFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Choose Option");
                builder.setMessage("From where you want to upload image?");
                builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uploadFromGallery();

                    }
                });
                builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadFromCamera();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void uploadFromGallery() {
        Intent intt = new Intent(Intent.ACTION_PICK);
        intt.setType("image/*");
        startActivityForResult(intt, 1);
    }

    private void uploadFromCamera() {
        Uri imageUri;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "RIO/CachedImages";
        File outputDir = new File(path);
        outputDir.mkdirs();
        img.add(positionPhoto, new File(path + "/" + "temporary_image_"+positionPhoto+".jpg"));
        //File temp = new File(path + "/" + "temporary_image_"+positionPhoto+".jpg");
        imageUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", img.get(positionPhoto));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 2);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Select Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                File imageFile = new Compressor(Objects.requireNonNull(this)).compressToFile(FileUtil.from(this, data.getData()));
                if (imageFile.length() > 1048576) {//greater than 1 MB
                    Toast.makeText(getApplicationContext(), "Image size must not exceed 1MB", Toast.LENGTH_SHORT).show();
                }else {
                    img.add(positionPhoto, imageFile);
                    img2.add(positionPhoto, imageFile);
                    objects.add(positionPhoto, new PhotoListObject(BitmapFactory.decodeFile(img.get(positionPhoto).getAbsolutePath())));
                    positionPhoto+=1;
                    PhotosAdapter customAdapter = new PhotosAdapter(this, objects);
                    photosListView.setAdapter(customAdapter);
                }
            } catch (Exception e) {
                //Toast.makeText(getContext(), "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "unknown error (re025)", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                File imageFile = new Compressor(this).compressToFile(img.get(positionPhoto));
                if (imageFile.length() > 1048576) {//greater than 1 MB
                    Toast.makeText(this, "Image size must not exceed 1MB", Toast.LENGTH_SHORT).show();
                }else {
                    img2.add(positionPhoto, imageFile);
                    objects.add(positionPhoto, new PhotoListObject(BitmapFactory.decodeFile(img.get(positionPhoto).getAbsolutePath())));
                    positionPhoto+=1;
                    PhotosAdapter customAdapter = new PhotosAdapter(this, objects);
                    photosListView.setAdapter(customAdapter);
                }
            } catch (Exception e) {
                Toast.makeText(this, "unknown error (re026)", Toast.LENGTH_SHORT).show();
            }
        }
    }


}