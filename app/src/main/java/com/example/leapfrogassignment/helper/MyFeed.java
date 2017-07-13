package com.example.leapfrogassignment.helper;


public class MyFeed {
    private int feedId;
    private String userId, userName, status, feedImage, userImage;

    public MyFeed() {
    }

    public MyFeed(int feedId, String userId, String userName, String status, String userImage, String feedImage) {
        super();
        this.feedId = feedId;
        this.userId = userId;
        this.userName = userName;
        this.feedImage = feedImage;
        this.status = status;
        this.userImage = userImage;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public void setFeedImage(String feedImage) {
        this.feedImage = feedImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


}
