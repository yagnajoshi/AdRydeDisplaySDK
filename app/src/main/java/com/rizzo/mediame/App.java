package com.rizzo.mediame;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    private Activity mCurrentActivity = new MainActivity();
    private static App mMyApp;
    boolean isAppInBackground = true;


    @Override
    public void onCreate() {
        super.onCreate();
        mMyApp = (App) this.getApplicationContext();
        createNotificationChannels();
        setScreenOrientation();

    }
    public static synchronized App getInstance() {
        return mMyApp;
    }

    /**
     * Metodo che inizializza i due channel: chennel1 utilizzato per le notifiche del servizio in backround
     * channel2 per le notifiche relative al download dei media.
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Service",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Service Channel");
            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Download",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Download Channel");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    private void clearReferences() {
        Activity currActivity = mMyApp.getCurrentActivity();
        if (this.equals(currActivity))
            mMyApp.setCurrentActivity(null);
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setScreenOrientation() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                // new activity created; force its orientation to portrait
                try {
                    activity.setRequestedOrientation(
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activity.setTitle(getResources().getString(R.string.app_name));
                setCurrentActivity(activity);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mMyApp.setCurrentActivity(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mMyApp.setCurrentActivity(activity);
                isAppInBackground = false;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                isAppInBackground = true;
                clearReferences();
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                clearReferences();
            }
        });
    }
}
