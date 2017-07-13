package com.example.leapfrogassignment.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leapfrogassignment.R;
import com.example.leapfrogassignment.helper.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class RegisterActivity extends AppCompatActivity {
    Button btnImageSelect;
    ImageView ivselectedImage;
    public static int CAMERA_REQUEST = 0;
    public static int RESULT_LOAD_IMAGE = 1;
    AlertDialog b;
    EditText etName, etEmail, etPassword;
    Button btnSignUp;
    private FirebaseAuth auth;
    ProgressDialog progressDialog;

    FirebaseStorage storage;
    StorageReference storageRef;
    TextView link_login;
    Bitmap bmp;
    public String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        //creating reference to firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Config.reference_url);


        initializeViews();
        btnImageSelect.setOnClickListener(btnImgselectlistener);
        btnSignUp.setOnClickListener(signupClickListener);
        link_login.setOnClickListener(link_loginListener);
    }

    private void initializeViews() {
        btnImageSelect = (Button) findViewById(R.id.btnImageSelect);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        ivselectedImage = (ImageView) findViewById(R.id.ivselectedImage);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        link_login = (TextView) findViewById(R.id.link_login);
    }


    View.OnClickListener btnImgselectlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openImageDialog();
        }
    };

    View.OnClickListener link_loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            onBackPressed();
        }
    };

    private void openImageDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.image_option, null);
        dialogBuilder.setView(dialogView);

        final Button btnCamera = (Button) dialogView.findViewById(R.id.btnCamera);
        final Button btnGallery = (Button) dialogView.findViewById(R.id.btnGallery);
        btnCamera.setOnClickListener(camera_clickListener);
        btnGallery.setOnClickListener(gallery_clickListener);

        dialogBuilder.setTitle("Choose");

        b = dialogBuilder.create();
        b.show();


    }


    View.OnClickListener gallery_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery();
        }
    };

    private void openGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);


        //opens gallery + file chooser
       /* Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);*/
    }


    View.OnClickListener camera_clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openCamera();
        }
    };


    View.OnClickListener signupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            final String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getApplicationContext(), getString(R.string.entername), Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.enteremail), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.enterpassword), Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), getString(R.string.passwordtooshort), Toast.LENGTH_SHORT).show();
                return;
            }

            if (bmp == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.selectimage), Toast.LENGTH_SHORT).show();
                return;
            }


            showProgressDialog();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Toast.makeText(RegisterActivity.this, getString(R.string.usercreationstatus) + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                            if (!task.isSuccessful()) {
                                hideProgressDialog();
                                Toast.makeText(RegisterActivity.this, getString(R.string.authenticationfailed) + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {


                                //succesful signup
                                FirebaseUser user = task.getResult().getUser();
                                Log.d(TAG, "onComplete: uid=" + user.getUid());
                                Config.curr_user_id = user.getUid();
                                Config.pro_pic_name = "profileimage" + Config.curr_user_id + ".jpg";

                                //save username in database
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Config.USERS);
                                mDatabase.child(Config.curr_user_id).setValue(name);


                                uploadImageToStorage(user.getUid());
                            }
                        }
                    });


        }
    };

    private void uploadImageToStorage(String uid) {
        // Get the data from an ImageView as bytes
        ivselectedImage.setDrawingCacheEnabled(true);
        ivselectedImage.buildDrawingCache();
        Bitmap bitmap = ivselectedImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference mountainsRef = storageRef.child("profileimage" + uid + ".jpg");
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                hideProgressDialog();
                Toast.makeText(RegisterActivity.this, exception.getLocalizedMessage() + getString(R.string.imagewontupload), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                hideProgressDialog();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                clearInputFields();
                goToFeedPage();
            }
        });
    }

    private void clearInputFields() {
        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        ivselectedImage.setImageBitmap(null);
        ivselectedImage.setImageDrawable(null);
        bmp = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void goToFeedPage() {
        Intent intent = new Intent(getApplicationContext(), FirebasefeedActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, 2);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage(getString(R.string.creatingprofile));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("picture", true);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String temPath = getPath(selectedImage, RegisterActivity.this);

            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            bmp = BitmapFactory.decodeFile(temPath, btmapOptions);
            ivselectedImage.setImageBitmap(bmp);


        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bmp = (Bitmap) data.getExtras().get("data");
            ivselectedImage.setImageBitmap(bmp);
        }
        b.dismiss();


    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}

