package com.moory.myweightgoal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moory.myweightgoal.modules.User;

public class SignUpActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    TextView name;
    TextView age;
    TextView email;
    TextView password;
    RadioButton male;
    RadioButton female;
    Button create_account;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        context = this;
        name = findViewById(R.id.name);
        age  = findViewById(R.id.age);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        male = findViewById(R.id.Male);
        female = findViewById(R.id.Female);
        create_account = findViewById(R.id.create_account);



        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if ((name.getText().length() != 0 && age.getText().length() != 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && password.getText().length() >= 6) && (male.isChecked() || female.isChecked())) {
                        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(context.getString(R.string.sing_up_activity_tag), context.getString(R.string.create_user_success));
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            myRef = database.getReference().child(context.getString(R.string.users)).child(user.getUid());
                                            String gender;
                                            if (male.isChecked()) {
                                                gender = context.getString(R.string.male);
                                            } else {
                                                gender = context.getString(R.string.female);
                                            }
                                            User thisUser = new User(name.getText().toString(), age.getText().toString(), email.getText().toString(), gender);
                                            myRef.setValue(thisUser);
                                            user.sendEmailVerification();
                                            Intent intent = new Intent(context, MainActivity.class);
                                            context.startActivity(intent);

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(context.getString(R.string.sing_up_activity_tag), context.getString(R.string.create_user_failure), task.getException());
                                            Toast.makeText(context, context.getString(R.string.authentication_failed),
                                                    Toast.LENGTH_SHORT).show();

                                        }

                                        // ...
                                    }
                                });

                    } else {
                        if (password.getText().length() < 6) {
                            password.setError(context.getString(R.string.password_hint));
                        }
                        if (name.getText().length() == 0) {
                            name.setError(context.getString(R.string.empty_error));
                        }
                        if (age.getText().length() == 0) {
                            age.setError(context.getString(R.string.empty_error));
                        }
                        if (email.getText().length() == 0) {
                            email.setError(context.getString(R.string.empty_error));
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                            email.setError(context.getString(R.string.valid_email));
                        }
                        if (!male.isChecked() && !female.isChecked()) {
                            male.setError(context.getString(R.string.empty_error));
                            female.setError(context.getString(R.string.empty_error));
                        }

                    }

                }
                else{
                    Toast.makeText(context, context.getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                male.setError(null);
                female.setError(null);
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                male.setError(null);
                female.setError(null);
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
