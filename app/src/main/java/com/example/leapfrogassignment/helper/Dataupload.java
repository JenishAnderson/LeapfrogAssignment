package com.example.leapfrogassignment.helper;

import com.example.leapfrogassignment.callbacks.Callbacks.*;

/**
 * Created by JENISH on 7/9/2017.
 */
public class Dataupload {

    DataStored dataStored;

    public Dataupload(DataStored callback) {
        this.dataStored = callback;
    }


    Thread datawrite = new Thread(new Runnable() {
        @Override
        public void run() {

        }
    });

}
