package cm.aptoidesdk.pt.aptoidedemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.Aptoide;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.Screenshot;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.entities.misc.Group;
import cm.aptoide.pt.aptoidesdk.entities.util.SyncEndlessController;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.List;
import java.util.Locale;

/**
 * Created by neuro on 28-10-2016.
 */

public class MainActivity extends AppCompatActivity {

  private TextView tv;
  private SyncEndlessController<App> appEndlessController;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    tv = (TextView) findViewById(R.id.tv);
  }

  public void adsClick(View view) {

    List<Ad> l = Aptoide.getAds(3);
    if (l == null || l.size() == 0) {
      tv.setText("ad response: empty");
    } else {
      tv.setText(getAdsText(l));
    }
  }

  private CharSequence getAdsText(List<Ad> l) {
    StringBuilder sb = new StringBuilder();
    sb.append("<b>GetAds Response</b>\n\n");
    int i = 0;
    for (Ad ad : l) {
      sb.append(String.format(Locale.ENGLISH, "<b>Ad %d</b>", i++));
      sb.append("\nStore Name: " + ad.getStore() + "\n");
      sb.append("\nApp Name: " + ad.getName() + "\n");
      sb.append("\nId: " + ad.getAppId() + "\n");
      sb.append("\nPackage Name: " + ad.getPackageName() + "\n");
      sb.append("\nVersion Name: " + ad.getVername() + "\n");
      sb.append("\nVersion Code: " + ad.getVercode() + "\n");
      sb.append("\nDescription: " + ad.getDescription() + "\n\n");
      sb.append("\nIcon Path: " + ad.getIconPath() + "\n\n");
      sb.append("\nThumbnail Icon Path: " + ad.getIconThumbnailPath() + "\n\n");
    }
    return AptoideUtils.HtmlU.parse(sb.toString());
  }

  public void searchClick(View view) {

    List<SearchResult> l = Aptoide.searchApps("facebook", "apps");
    if (l == null || l.size() == 0) {
      tv.setText("search response: empty");
    } else {
      StringBuilder sb = new StringBuilder();
      for (SearchResult i : l) {
        sb.append("search: app name: " + i.getName() + "\n");
      }
      tv.setText(sb.toString());
    }
  }

  public void appsClick(View view) {

    App app = Aptoide.getApp("cm.aptoide.pt", "apps");
    if (app == null) {
      tv.setText("app response: empty");
    } else {
      tv.setText(getAppText(app));
    }
  }

  @NonNull private CharSequence getAppText(App app) {
    StringBuilder sb = new StringBuilder();

    sb.append("<b>GetApp Response</b>\n\n");

    sb.append("App name: " + app.getName() + "\n");
    sb.append("\nDeveloper name " + app.getDeveloper().getName() + "\n");
    sb.append("\nApp size: " + app.getFile().getSize() + "\n");

    sb.append("\nScreenshots: " + "\n");
    List<Screenshot> screenshots = app.getMedia().getScreenshots();
    for (int i = 0; i < (screenshots.size() > 3 ? 3 : screenshots.size()); i++) {
      sb.append(screenshots.get(i).getUrl() + "\n");
    }

    sb.append("\nDescription:\n" + app.getMedia().getDescription() + "\n");
    sb.append("\nIcon Path: " + app.getIconPath() + "\n");
    sb.append("\nThumbnail Icon Path: " + app.getIconThumbnailPath() + "\n");

    return AptoideUtils.HtmlU.parse(sb.toString());
  }

  public void listAppsClick(View view) {

    if (appEndlessController == null) {
      appEndlessController = Aptoide.listApps(Group.GAMES);
    }
    List<App> first = appEndlessController.loadMore();
    if (first.size() == 0) {
      tv.setText("ListApps response: empty");
    } else {
      tv.setText("app name: " + first.get(0).getName());
    }
  }
}
