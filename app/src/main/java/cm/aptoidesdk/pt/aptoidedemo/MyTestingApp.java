package cm.aptoidesdk.pt.aptoidedemo;

import android.app.Application;
import cm.aptoide.pt.aptoidesdk.Aptoide;

/**
 * Created by analara on 10/11/16.
 */

public class MyTestingApp extends Application {

  @Override public void onCreate() {
    super.onCreate();

    Aptoide.integrate(this, "9c63f5e4892289a3e85147dede4dcb75");
  }
}
