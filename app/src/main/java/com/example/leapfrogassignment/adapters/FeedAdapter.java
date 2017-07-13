package com.example.leapfrogassignment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.leapfrogassignment.R;
import com.example.leapfrogassignment.helper.AppController;
import com.example.leapfrogassignment.helper.MyFeed;
import com.example.leapfrogassignment.helper.MyFeedimage;
import com.example.leapfrogassignment.helper.MyProfileImage;

import java.util.List;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    //Imageloader to load image
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Context context;

    //List to store all superheroes
    private List<MyFeed> feedItems;

    //Constructor of this class
    public FeedAdapter(List<MyFeed> items, Context context) {
        super();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        this.feedItems = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//Getting the particular item from the list
        MyFeed item = feedItems.get(position);

        holder.populateView(item);
    }


    public void update(List<MyFeed> feeds) {
        feedItems.clear();
        for (MyFeed feed : feeds) {
            feedItems.add(feed);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return feedItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //Views

        TextView name, statusMsg, url;
        MyProfileImage profilePic;
        MyFeedimage feedImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            statusMsg = (TextView) itemView
                    .findViewById(R.id.txtStatusMsg);
            url = (TextView) itemView.findViewById(R.id.txtUrl);
            profilePic = (MyProfileImage) itemView
                    .findViewById(R.id.profilePic);
            feedImageView = (MyFeedimage) itemView
                    .findViewById(R.id.feedImage1);
        }


        public void populateView(MyFeed item) {
            name.setText(item.getUserName());


            // Chcek for empty status message
            if (!TextUtils.isEmpty(item.getStatus())) {
                statusMsg.setText(item.getStatus());
                statusMsg.setVisibility(View.VISIBLE);
            } else {
                // status is empty, remove from view
                statusMsg.setVisibility(View.GONE);
            }


            // user profile pic
            profilePic.setImageUrl(item.getUserImage(), imageLoader);

            // Feed image
            if (item.getFeedImage() != null) {
                feedImageView.setImageUrl(item.getFeedImage(), imageLoader);
                feedImageView.setVisibility(View.VISIBLE);
                feedImageView
                        .setResponseObserver(new MyFeedimage.ResponseObserver() {
                            @Override
                            public void onError() {
                            }

                            @Override
                            public void onSuccess() {
                            }
                        });
            } else {
                feedImageView.setVisibility(View.GONE);
            }
        }
    }


}
