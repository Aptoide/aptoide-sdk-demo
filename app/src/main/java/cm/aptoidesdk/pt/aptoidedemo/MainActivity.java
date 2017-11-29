package cm.aptoidesdk.pt.aptoidedemo;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.aptoidesdk.Ad;
import cm.aptoide.pt.aptoidesdk.Aptoide;
import cm.aptoide.pt.aptoidesdk.entities.App;
import cm.aptoide.pt.aptoidesdk.entities.AppResume;
import cm.aptoide.pt.aptoidesdk.entities.Screenshot;
import cm.aptoide.pt.aptoidesdk.entities.SearchResult;
import cm.aptoide.pt.aptoidesdk.entities.misc.Group;
import cm.aptoide.pt.aptoidesdk.entities.util.SyncEndlessController;
import cm.aptoide.pt.utils.AptoideUtils;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by neuro on 28-10-2016.
 */

public class MainActivity extends AppCompatActivity {

  private TextView tv;
  private SyncEndlessController<AppResume> appEndlessController;
  private SyncEndlessController<SearchResult> appEndlessControllerSearch;
  private DownloadManager downloadManager;
  private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {

      //check if the broadcast message is for our enqueued download
      long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

      if (true) {

        Toast toast =
            Toast.makeText(MainActivity.this, "Image Download Complete", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(referenceId);
        Cursor cursor = downloadManager.query(query);

        cursor.moveToFirst();
        String filePath = cursor.getString(1);
        cursor.close();

        startInstallIntent(MainActivity.this, new File(filePath));
        unregisterReceiver(downloadReceiver);
      }
    }
  };
  private View app1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    tv = (TextView) findViewById(R.id.tv);
    app1 = findViewById(R.id.app1);

    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    registerReceiver(downloadReceiver, filter);
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

    if (appEndlessControllerSearch == null) {
      appEndlessControllerSearch = Aptoide.searchApps("facebook", "apps");
    }
    List<SearchResult> l = appEndlessControllerSearch.loadMore();

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

  private void setupApp(final View view, final App app) {
    TextView appName = (TextView) view.findViewById(R.id.app_name);
    ImageView appIcon = (ImageView) view.findViewById(R.id.app_icon);

    appName.setText(app.getName());
    Glide.with(this).load(AptoideUtils.IconSizeU.getNewImageUrl(app.getIconPath())).into(appIcon);

    view.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        Toast.makeText(MainActivity.this, "Starting download", Toast.LENGTH_LONG).show();

        System.out.println("clicked on " + app.getName() + " in store " + app.getStore());
        downloadData(Aptoide.getApp(app.getId()).getFile().getPath());
      }
    });

    view.setVisibility(View.VISIBLE);
  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  private long downloadData(String filePath) {

    Uri uri = Uri.parse(filePath);

    long downloadReference;

    // Create request for android download manager
    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    DownloadManager.Request request = new DownloadManager.Request(uri);

    //Setting title of request
    request.setTitle("Data Download");

    //Setting description of request
    request.setDescription("Android Data download using DownloadManager.");

    //Set the local destination for the downloaded file to a path within the application's external files directory
    request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS,
        "AndroidTutorialPoint.mp3");

    //Enqueue download and save into referenceId
    downloadReference = downloadManager.enqueue(request);

    return downloadReference;
  }

  public void appsClick(View view) {

    App app = Aptoide.getApp("cm.aptoide.pt", "apps");
    if (app == null) {
      tv.setText("app response: empty");
    } else {
      tv.setText(getAppText(app));
    }

    setupApp(app1, app);
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
    List<AppResume> first = appEndlessController.loadMore();
    if (first.size() == 0) {
      tv.setText("ListApps response: empty");
    } else {
      tv.setText("app name: " + first.get(0).getName());
    }
  }

  public void getAppsClick(View view) {
    List<App> apps = Aptoide.getApps(createPackageList(), "apps");
    if (apps == null) {
      tv.setText("No apps retrived for given packages");
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      for (App app : apps) {
        stringBuilder.append(
            "App name: " + app.getName() + "\n" + " Package: " + app.getPackageName() + "\n");
      }
      tv.setText(stringBuilder.toString());
    }
  }

  private ArrayList<String> createPackageList() {
    ArrayList<String> packages = new ArrayList<>();
    packages.add("com.facebook.orca");
    packages.add("com.spotify.music");
    packages.add("com.somefalse.package");
    packages.add("com.whatsapp");
    packages.add("com.snapchat.android");
    packages.add("com.supercell.clashofclans");
    return packages;
  }
}
