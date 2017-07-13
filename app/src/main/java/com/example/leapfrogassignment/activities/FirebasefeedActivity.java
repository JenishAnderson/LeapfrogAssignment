package com.example.leapfrogassignment.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leapfrogassignment.R;
import com.example.leapfrogassignment.adapters.FeedAdapter;
import com.example.leapfrogassignment.helper.Config;
import com.example.leapfrogassignment.helper.MyFeed;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JENISH on 7/9/2017.
 */
public class FirebasefeedActivity extends AppCompatActivity {
    private static final String TAG = FirebasefeedActivity.class.getSimpleName();
    RecyclerView rvfeeds;
    FeedAdapter feedAdapter;
    private List<MyFeed> feedItems;
    private Context _mcontext;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    ImageView ivMyProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        initialize();
        ivMyProfile.setOnClickListener(myprofileListener);
        rvfeeds.setAdapter(feedAdapter);
        new DatabaseOperations().execute();
    }


    private void initialize() {
        _mcontext = getApplicationContext();
        rvfeeds = (RecyclerView) findViewById(R.id.rvfeeds);
        ivMyProfile = (ImageView) findViewById(R.id.ivMyProfile);
        rvfeeds.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvfeeds.setLayoutManager(layoutManager);
        feedItems = new ArrayList<>();
        feedAdapter = new FeedAdapter(feedItems, _mcontext);
    }


    public class DatabaseOperations extends AsyncTask<Void, Void, Void> {
        DatabaseReference mDatabase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDatabase = FirebaseDatabase.getInstance().getReference(Config.FEEDS);


            try {
                JSONObject obj = new JSONObject(loadJSONFromAsset());
                if (obj != null) {
                    JSONArray feeds = obj.getJSONArray(Config.FEEDS);
                    for (int i = 0; i < feeds.length(); i++) {
                        JSONObject eachobj = feeds.getJSONObject(i);

                        int feedId = Integer.parseInt(eachobj.getString(Config.FEEDID));
                        String userId = eachobj.getString(Config.USERID);
                        String userName = eachobj.getString(Config.USERNAME);
                        String status = eachobj.getString(Config.STATUS);
                        String userImage = eachobj.getString(Config.USERIMAGE);
                        String feedImage = eachobj.getString(Config.FEEDIMAGE);

                        MyFeed myFeed = new MyFeed(feedId, userId, userName, status, userImage, feedImage);
                        feedItems.add(myFeed);

                    }
                    //write to firebase database
                    mDatabase.setValue(feedItems);


                    //read firebase database
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<List<MyFeed>> t = new GenericTypeIndicator<List<MyFeed>>() {
                            };
                            List<MyFeed> fetched_feeds = dataSnapshot.getValue(t);

                            Log.d(TAG, "my list of feeds: " + fetched_feeds);
                            feedAdapter.update(fetched_feeds);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // [START_EXCLUDE]

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FirebasefeedActivity.this, "Failed to load post.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    View.OnClickListener myprofileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    @Override
    public void onBackPressed() {
        askForLogout();

    }

    private void askForLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Log out?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        auth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
}
