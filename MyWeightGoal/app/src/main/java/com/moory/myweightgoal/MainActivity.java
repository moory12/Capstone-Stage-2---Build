package com.moory.myweightgoal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    ImageView background;
    ImageView profile_settings;
    Button cal_bmi;
    TextView bmi;
    TextView lose_weight;
    TextView drink_water;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    float BMI =0;
    float height=0;
    float weight;
    private StorageReference mStorageRef;
    SharedPreferences prefs;

    TextView menu_name;
    TextView menu_age;
    TextView menu_gender;
    TextView menu_height;
    TextView menu_weight;
    ImageView menu_image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;


        background = findViewById(R.id.main_background);
        cal_bmi = findViewById(R.id.main_cal_bmi);
        bmi = findViewById(R.id.bmi_textview);
        lose_weight = findViewById(R.id.lose_weight_textview);
        drink_water = findViewById(R.id.drink_water_textview);
        profile_settings = findViewById(R.id.profile_settings);


        menu_name = findViewById(R.id.menu_name);
        menu_age = findViewById(R.id.menu_age);
        menu_gender = findViewById(R.id.menu_gender);
        menu_height = findViewById(R.id.menu_height);
        menu_weight = findViewById(R.id.menu_weight);
        menu_image = findViewById(R.id.menu_user_image);


        profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,EditProfileActivity.class));
            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if(isNetworkAvailable()) {
            new ImageAsyncTask().execute();

        }


        cal_bmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,BMIActivity.class));
            }
        });


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        if(isNetworkAvailable()){
            if(mAuth.getCurrentUser()!=null) {
                myRef = database.getReference(context.getString(R.string.users)).child(mAuth.getCurrentUser().getUid());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(context.getString(R.string.bmi_s)).exists()) {
                            BMI = dataSnapshot.child(context.getString(R.string.bmi_s)).getValue(Float.class);
                            bmi.setText(context.getString(R.string.your_bmi_is) +new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(BMI));
                        }
                        if(dataSnapshot.child(context.getString(R.string.height_s)).exists()){
                            height = dataSnapshot.child(context.getString(R.string.height_s)).getValue(Float.class);
                            lose_weight.setText(context.getString(R.string.ideal_weight)+new DecimalFormat(context.getString(R.string.two_digit_decimal)).format((22 * Math.pow(height,2)))+context.getString(R.string.kg));
                        }
                        if(dataSnapshot.child(context.getString(R.string.weight_s)).exists()){
                            weight = dataSnapshot.child(context.getString(R.string.weight_s)).getValue(Float.class);
                            drink_water.setText(context.getString(R.string.you_should_drink)+new DecimalFormat(context.getString(R.string.two_digit_decimal)).format((weight*(0.033)))+context.getString(R.string.water_every_day));
                        }


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                setFromSharedPreference();
            }
        }
        else{
                setFromSharedPreference();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        //setting menu

        if (isNetworkAvailable()) {


            prefs = getSharedPreferences(context.getString(R.string.shared_preference_name), MODE_PRIVATE);

            mStorageRef = FirebaseStorage.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(context.getString(R.string.url_to_image)).exists()) {
                        Glide.with(context).load(dataSnapshot.child(context.getString(R.string.url_to_image)).getValue(String.class)).into(menu_image);
                    }
                    if (dataSnapshot.child(context.getString(R.string.user_name)).exists()) {
                        menu_name.setText(dataSnapshot.child(context.getString(R.string.user_name)).getValue(String.class));
                    }

                    if (dataSnapshot.child(context.getString(R.string.age_s)).exists()) {
                        menu_age.setText(dataSnapshot.child(context.getString(R.string.age_s)).getValue(String.class));
                    }
                    if (dataSnapshot.child(context.getString(R.string.gender)).exists()) {
                        menu_gender.setText(dataSnapshot.child(context.getString(R.string.gender)).getValue(String.class));
                    }
                    if (dataSnapshot.child(context.getString(R.string.height_s)).exists()) {
                        menu_height.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(dataSnapshot.child(context.getString(R.string.height_s)).getValue(Float.class))+context.getString(R.string.m));
                    }
                    if (dataSnapshot.child(context.getString(R.string.weight_s)).exists()) {
                        menu_weight.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(dataSnapshot.child(context.getString(R.string.weight_s)).getValue(Float.class))+context.getString(R.string.kg));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        } else {
            String name = prefs.getString(context.getString(R.string.user_name), null);
            if (name != null) {
                menu_name.setText(name);
            }

            String age = prefs.getString(context.getString(R.string.age), null);
            if (age != null) {
                menu_age.setText(age);
            }


            String gender = prefs.getString(context.getString(R.string.gender), null);
            if (gender != null) {
                menu_gender.setText(gender);
            }

            float weight = prefs.getFloat(context.getString(R.string.weight), 0);
            if (weight != 0) {
                menu_weight.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(weight)+context.getString(R.string.kg));
            }

            float height = prefs.getFloat(context.getString(R.string.height), 0);
            if (height != 0) {
                menu_height.setText(new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(height)+context.getString(R.string.m));
            }


        }
    }

    public void setFromSharedPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences(context.getString(R.string.shared_preference_name),MODE_PRIVATE);
        BMI = sharedPreferences.getFloat(context.getString(R.string.bmi),0);
        weight = sharedPreferences.getFloat(context.getString(R.string.weight),0);
        height = sharedPreferences.getFloat(context.getString(R.string.height),0);
        bmi.setText(context.getString(R.string.your_bmi_is) +new DecimalFormat(context.getString(R.string.two_digit_decimal)).format(BMI));
        lose_weight.setText(context.getString(R.string.ideal_weight)+new DecimalFormat(context.getString(R.string.two_digit_decimal)).format((22 * Math.pow(height,2)))+context.getString(R.string.kg));
        drink_water.setText(context.getString(R.string.you_should_drink)+new DecimalFormat(context.getString(R.string.two_digit_decimal)).format((weight*(0.033)))+context.getString(R.string.water_every_day));


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    class ImageAsyncTask extends AsyncTask<Void,Void,Bitmap>{


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                background.setImageBitmap(bitmap);
            }
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(Uri.parse(context.getString(R.string.random_image_api)).toString());
                urlConnection = (HttpURLConnection) uri.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w(context.getString(R.string.main_activity_asynctask), context.getString(R.string.error_image) + context.getString(R.string.random_image_api));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }


    }
}
