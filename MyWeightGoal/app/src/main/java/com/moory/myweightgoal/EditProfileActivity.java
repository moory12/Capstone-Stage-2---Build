package com.moory.myweightgoal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DecimalFormat;

public class EditProfileActivity extends AppCompatActivity {

    ImageView user_image;
    EditText edit_name;
    EditText edit_height;
    EditText edit_age;
    EditText edit_weight;
    EditText edit_gender;
    Button save;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    Context context;
    SharedPreferences prefs;
    private StorageReference mStorageRef;
    private static int RESULT_LOAD_IMG = 1;
    String url_to_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        user_image = findViewById(R.id.edit_user_image);
        edit_age = findViewById(R.id.edit_age);
        edit_height = findViewById(R.id.edit_height);
        edit_weight = findViewById(R.id.edit_weight);
        edit_name = findViewById(R.id.edit_name);
        edit_gender = findViewById(R.id.edit_gender);
        save = findViewById(R.id.save);
        context = this;
        prefs = getSharedPreferences(context.getString(R.string.shared_preference_name), MODE_PRIVATE);


        if (isNetworkAvailable()) {

            myRef = database.getReference(context.getString(R.string.users)).child(mAuth.getCurrentUser().getUid());
            mStorageRef = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(context.getString(R.string.url_to_image)).exists()) {
                        Glide.with(context).load(dataSnapshot.child(context.getString(R.string.url_to_image)).getValue(String.class)).into(user_image);
                    }
                    if (dataSnapshot.child(context.getString(R.string.user_name)).exists()) {
                        edit_name.setText(dataSnapshot.child(context.getString(R.string.user_name)).getValue(String.class));
                    }

                    if (dataSnapshot.child(context.getString(R.string.age_s)).exists()) {
                        edit_age.setText(dataSnapshot.child(context.getString(R.string.age_s)).getValue(String.class));
                    }
                    if (dataSnapshot.child(context.getString(R.string.gender)).exists()) {
                        edit_gender.setText(dataSnapshot.child(context.getString(R.string.gender)).getValue(String.class));
                    }
                    if (dataSnapshot.child(context.getString(R.string.height_s)).exists()) {
                        edit_height.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(dataSnapshot.child(context.getString(R.string.height_s)).getValue(Float.class))+context.getString(R.string.m));
                    }
                    if (dataSnapshot.child(context.getString(R.string.weight_s)).exists()) {
                        edit_weight.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(dataSnapshot.child(context.getString(R.string.weight_s)).getValue(Float.class))+context.getString(R.string.kg));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            String name = prefs.getString(context.getString(R.string.user_name), null);
            if (name != null) {
                edit_name.setText(name);
            }

            String age = prefs.getString(context.getString(R.string.age), null);
            if (age != null) {
                edit_age.setText(age);
            }


            String gender = prefs.getString(context.getString(R.string.gender), null);
            if (gender != null) {
                edit_gender.setText(gender);
            }

            float weight = prefs.getFloat(context.getString(R.string.weight), 0);
            if (weight != 0) {
                edit_weight.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(weight)+context.getString(R.string.kg));
            }

            float height = prefs.getFloat(context.getString(R.string.height), 0);
            if (height != 0) {
                edit_height.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(height)+context.getString(R.string.m));
            }


        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    String name = edit_name.getText().toString();
                    myRef.child(context.getString(R.string.name)).setValue(name);
                    String gender = edit_gender.getText().toString();
                    myRef.child(context.getString(R.string.gender)).setValue(gender);
                    String age = edit_age.getText().toString();
                    myRef.child(context.getString(R.string.age)).setValue(age);
                    float weight = Float.parseFloat(edit_weight.getText().toString());
                    myRef.child(context.getString(R.string.weight_s)).setValue(weight);
                    float height = Float.parseFloat(edit_height.getText().toString());
                    myRef.child(context.getString(R.string.height_s)).setValue(height);
                    if(!url_to_image.equals("")) {
                        myRef.child(context.getString(R.string.url_to_image)).setValue(url_to_image);

                    }
                }
            }
        });


        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                user_image.setImageBitmap(bitmap);
                final StorageReference riversRef = mStorageRef;
                if(selectedImage!=null) {
                    UploadTask uploadTask = riversRef.putFile(selectedImage);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return riversRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                url_to_image = downloadUri.toString();
                            }
                        }
                    });
                }


            } catch (IOException e) {
                Log.i("TAG", "Some exception " + e);
            }

        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

