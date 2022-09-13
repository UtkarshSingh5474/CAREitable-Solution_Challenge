package com.example.careitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.careitable.databinding.ActivityMainBinding;
import com.example.careitable.databinding.ActivityRegisterBinding;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DocumentReference userCollectionRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);



        mAuth = FirebaseAuth.getInstance();

        binding.btnRegister.setOnClickListener(view ->{
            createUser();
        });

        binding.tvLoginHere.setOnClickListener(view ->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void createUser(){
        if (!CheckNetwork.isInternetAvailable(this)){
            return;
        }
        String email = binding.etRegEmail.getText().toString();
        String password = binding.etRegPass.getText().toString();
        String displayName = binding.etRegName.getText().toString();
        String phone = binding.etRegMobileNumber.getText().toString();

        if(TextUtils.isEmpty(displayName)){
            binding.etRegName.setError("Display Name cannot be empty");
            binding.etRegName.requestFocus();
        }
        else if(TextUtils.isEmpty(phone)){
            binding.etRegMobileNumber.setError("Phone Number cannot be empty");
            binding.etRegMobileNumber.requestFocus();
        }
        else if (!isValid(phone)){
            binding.etRegMobileNumber.setError("Invalid Format");
            binding.etRegMobileNumber.requestFocus();
        }
        else if (TextUtils.isEmpty(email)){
            binding.etRegEmail.setError("Email cannot be empty");
            binding.etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            binding.etRegPass.setError("Password cannot be empty");
            binding.etRegPass.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                      UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                              .setDisplayName(displayName).build();
                      user.updateProfile(changeRequest);

                      UserData userData = new UserData();
                      userData.setName(displayName);
                      userData.setEmail(email);
                      userData.setPhone(phone);
                      userCollectionRef = FirebaseFirestore.getInstance().collection("Users").document(user.getUid());
                      userCollectionRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {

                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(RegisterActivity.this, "Data Set Error " + e, Toast.LENGTH_SHORT).show();
                          }
                      });

                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(RegisterActivity.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static boolean isValid(String s)
    {

        Pattern p = Pattern.compile("^[6-9]\\d{9}$");

        Matcher m = p.matcher(s);

        return (m.matches());
    }

}