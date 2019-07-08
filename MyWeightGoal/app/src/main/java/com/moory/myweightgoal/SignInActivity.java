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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {


    Button sign_in;
    EditText email;
    EditText password;
    private FirebaseAuth mAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_in = findViewById(R.id.sign_in_button);
        email = findViewById(R.id.sing_in_email);
        password = findViewById(R.id.sign_in_password);
        mAuth = FirebaseAuth.getInstance();
        context = this;



        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && password.getText().length() >= 6) {
                        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Log.d(context.getString(R.string.sign_in_activity_tag), context.getString(R.string.sign_in_success));
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            Intent intent = new Intent(context, MainActivity.class);
                                            context.startActivity(intent);
                                        } else {
                                            Log.w(context.getString(R.string.sign_in_activity_tag), context.getString(R.string.sign_in_failure), task.getException());
                                            Toast.makeText(context, context.getString(R.string.authentication_failed),
                                                    Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                    } else {
                        if (password.getText().length() < 6) {
                            password.setError(context.getString(R.string.password_hint));
                        }
                        if (email.getText().length() == 0) {
                            email.setError(context.getString(R.string.empty_error));
                        }
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                            email.setError(context.getString(R.string.valid_email));
                        }
                    }

                }
                else{
                    Toast.makeText(context, context.getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();
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
