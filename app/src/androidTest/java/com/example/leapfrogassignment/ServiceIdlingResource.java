package com.example.leapfrogassignment;

import android.app.ActivityManager;
import android.content.Context;
import android.support.test.espresso.IdlingResource;

import com.example.leapfrogassignment.activities.LoginActivity;

/**
 * Created by JENISH on 7/12/2017.
 */
public class ServiceIdlingResource implements IdlingResource {
    private final Context mContext;
    private IdlingResource.ResourceCallback mCallback;

    public ServiceIdlingResource(Context context) {
        this.mContext = context;
    }

    @Override
    public String getName() {
        return ServiceIdlingResource.class.getSimpleName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !isIntentServiceRunning();
        if (idle && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.mCallback = callback;
    }

    private boolean isIntentServiceRunning() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LoginActivity.class.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
