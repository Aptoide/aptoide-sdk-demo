package cm.aptoidesdk.pt.aptoidedemo;


import android.app.Application;

import cm.aptoide.pt.aptoidesdk.ads.Aptoide;

/**
 * Created by analara on 10/11/16.
 */

public class MyTestingApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Aptoide.integrate(this, "oemid");
    }
}
