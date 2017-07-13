package com.example.leapfrogassignment.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leapfrogassignment.R;
import com.example.leapfrogassignment.helper.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText etEmailLogin, etPasswordLogin;
    Button btn_login, btn_signup;
    ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        initializeViews();
        btn_login.setOnClickListener(btnLoginListener);
        btn_signup.setOnClickListener(btnSignupListener);


    }

    private void initializeViews() {
        etEmailLogin = (EditText) findViewById(R.id.etEmailLogin);
        etPasswordLogin = (EditText) findViewById(R.id.etPasswordLogin);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
    }


    View.OnClickListener btnLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            String email = etEmailLogin.getText().toString();
            final String password = etPasswordLogin.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.enteremail), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.enterpassword), Toast.LENGTH_SHORT).show();
                return;
            }

            showProgressDialog();
            //authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            hideProgressDialog();
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 6) {
                                    etPasswordLogin.setError(getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                FirebaseUser user = task.getResult().getUser();
                                Log.d(TAG, "onComplete: uid=" + user.getUid());
                                Config.curr_user_id = user.getUid();
                                Config.pro_pic_name = "profileimage" + Config.curr_user_id + ".jpg";
                                Toast.makeText(LoginActivity.this, getString(R.string.loginsuccesful), Toast.LENGTH_SHORT).show();
                                goToFeedPage();
                            }
                        }
                    });


        }
    };

    private void goToFeedPage() {
        Intent intent = new Intent(getApplicationContext(), FirebasefeedActivity.class);
        startActivity(intent);
    }


    View.OnClickListener btnSignupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    };

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loadingprofile));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearInputFields();

    }

    private void clearInputFields() {
        etEmailLogin.setText("");
        etPasswordLogin.setText("");
    }
}

