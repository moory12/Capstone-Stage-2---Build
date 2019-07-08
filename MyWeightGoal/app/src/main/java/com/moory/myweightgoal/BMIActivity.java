package com.moory.myweightgoal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BMIActivity extends AppCompatActivity {


    Button bmi_cal;

    EditText height;
    EditText weight;
    Context context;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        context = this;

        bmi_cal = findViewById(R.id.bmi_calculate);
        height  = findViewById(R.id.height_editText);
        weight  = findViewById(R.id.weight_editText);
        editor = getSharedPreferences(context.getString(R.string.shared_preference_name), MODE_PRIVATE).edit();





        if(mAuth.getCurrentUser()!=null) {
            myRef = database.getReference(context.getString(R.string.users)).child(mAuth.getCurrentUser().getUid());
        }

        bmi_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!height.getText().toString().equals("") && !weight.getText().toString().equals("")) {
                    float bmi =  (float)((Float.parseFloat(weight.getText().toString()))/(Math.pow(Double.parseDouble(height.getText().toString()),2)));

                    if (isNetworkAvailable()) {
                        myRef.child(context.getString(R.string.bmi_s)).setValue(bmi);
                        myRef.child(context.getString(R.string.height_s)).setValue(Float.parseFloat(height.getText().toString()));
                        myRef.child(context.getString(R.string.weight_s)).setValue(Float.parseFloat(weight.getText().toString()));
                        editor.putFloat(context.getString(R.string.bmi), bmi);
                        editor.putFloat(context.getString(R.string.weight),Float.parseFloat(weight.getText().toString()));
                        editor.putFloat(context.getString(R.string.height),Float.parseFloat(height.getText().toString()));
                        editor.apply();
                    } else {
                        editor.putFloat(context.getString(R.string.bmi), bmi);
                        editor.putFloat(context.getString(R.string.weight),Float.parseFloat(weight.getText().toString()));
                        editor.putFloat(context.getString(R.string.height),Float.parseFloat(height.getText().toString()));
                        editor.apply();
                    }
                    finish();

                } else {
                    if(height.getText().length()== 0){
                        height.setError(context.getString(R.string.empty_error));
                    }
                    if (weight.getText().length() == 0){
                        weight.setError(context.getString(R.string.empty_error));
                    }
                }
            }

        });




    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
