package com.example.leapfrogassignment.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leapfrogassignment.R;
import com.example.leapfrogassignment.helper.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by JENISH on 7/9/2017.
 */
public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    ImageView ivProfilePic;
    TextView tvProfileName;
    Button btnLogout;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        thr.start();
        initializeUI();
        btnLogout.setOnClickListener(btnlogoutListener);
    }

    private void initializeUI() {
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        btnLogout = (Button) findViewById(R.id.btnLogout);
    }


    View.OnClickListener btnlogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            signOut();

        }
    };

    public void signOut() {
        auth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    Thread thr = new Thread(new Runnable() {
        @Override
        public void run() {

            fetchUsername();


            StorageReference storageRef = storage.getReferenceFromUrl(Config.reference_url);
            StorageReference islandRef = storageRef.child(Config.pro_pic_name);

            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ivProfilePic.setImageBitmap(decodedByte);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(ProfileActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

    private void fetchUsername() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Config.USERS);
        mDatabase.child(Config.curr_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                tvProfileName.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
