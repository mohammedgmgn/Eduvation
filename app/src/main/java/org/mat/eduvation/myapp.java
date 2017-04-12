package org.mat.eduvation;

import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

/**
 * Created by gmgn on 9/17/2016.
 */
public class myapp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
      //  attachBaseContext(this);
       Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        //MultiDex.install(this);


    }
}