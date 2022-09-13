package com.example.careitable.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.careitable.BuildConfig;
import com.example.careitable.FileUtil;
import com.example.careitable.PhotoListObject;
import com.example.careitable.PhotosAdapter;
import com.example.careitable.R;
import com.example.careitable.databinding.FragmentAdPhotosBinding;
import com.example.careitable.databinding.FragmentCharityBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import id.zelory.compressor.Compressor;


public class AdPhotosFragment extends Fragment {

    private FragmentAdPhotosBinding binding;
    private ListView photosListView;
    private FloatingActionButton addFloatingActionButton;
    public static ArrayList<File> img;
    public static ArrayList<File> img2;
    private int positionPhoto = 0;
    private ArrayList<PhotoListObject> objects = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdPhotosBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initialiseComponents(view);
        img = new ArrayList<>();
        img2 = new ArrayList<>();

        return view;


    }

    private void initialiseComponents(View root)
    {
        photosListView = root.findViewById(R.id.photosListView);
        addFloatingActionButton = root.findViewById(R.id.addFloatingActionButton);

        photosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                objects.remove(position);
                img.remove(position);
                PhotosAdapter customAdapter = new PhotosAdapter(getContext(), objects);
                photosListView.setAdapter(customAdapter);
                positionPhoto-=1;
            }
        });

        addFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Option");
                builder.setMessage("From where you want to upload image?");
                builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 1);
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1

                            );
                            return;
                        }

                        uploadFromGallery();

                    }
                });
                builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 1);
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1

                            );
                            return;
                        }

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
        imageUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", img.get(positionPhoto));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getContext(), "Select Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {

                File imageFile = new Compressor(requireContext()).compressToFile(FileUtil.from(getContext(), data.getData()));
                if (imageFile.length() > 1048576) {//greater than 1 MB
                    Toast.makeText(getContext(), "Image size must not exceed 1MB", Toast.LENGTH_SHORT).show();
                }else {
                    img.add(positionPhoto, imageFile);
                    img2.add(positionPhoto, imageFile);
                    objects.add(positionPhoto, new PhotoListObject(BitmapFactory.decodeFile(img.get(positionPhoto).getAbsolutePath())));
                    positionPhoto+=1;
                    PhotosAdapter customAdapter = new PhotosAdapter(getContext(), objects);
                    photosListView.setAdapter(customAdapter);
                }
            } catch (Exception e) {
                //Toast.makeText(getContext(), "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "unknown error (re025)", Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                File imageFile = new Compressor(requireContext()).compressToFile(img.get(positionPhoto));
                if (imageFile.length() > 1048576) {//greater than 1 MB
                    Toast.makeText(getContext(), "Image size must not exceed 1MB", Toast.LENGTH_SHORT).show();
                }else {
                    img2.add(positionPhoto, imageFile);
                    objects.add(positionPhoto, new PhotoListObject(BitmapFactory.decodeFile(img.get(positionPhoto).getAbsolutePath())));
                    positionPhoto+=1;
                    PhotosAdapter customAdapter = new PhotosAdapter(getContext(), objects);
                    photosListView.setAdapter(customAdapter);
                }
            } catch (Exception e) {
                Log.d("test", "onActivityResult: " +e.getLocalizedMessage());
                Toast.makeText(getContext(), "unknown error (re026)", Toast.LENGTH_SHORT).show();
            }
        }
    }
}