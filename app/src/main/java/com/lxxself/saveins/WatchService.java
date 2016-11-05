package com.lxxself.saveins;

import android.app.DownloadManager;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WatchService extends Service {
    private static final String TAG = "WatchService";
    private static final String FOLDER_NAME = "SaveIns";

    ClipboardManager clipboardManager;

    public WatchService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFolder();
        initClipBoard();
    }

    private boolean initFolder() {
        File folder = new File(FOLDER_NAME);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    private void initClipBoard() {
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(listener);
    }
    private ClipboardManager.OnPrimaryClipChangedListener listener=new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            CharSequence charSequence = clipboardManager.getPrimaryClip().getItemAt(0).getText();
            String clip = String.valueOf(charSequence);
            Toast.makeText(WatchService.this, clip, Toast.LENGTH_SHORT).show();
            Log.d(TAG, clip);
            if (isInstagramUrl(clip)) {
                downloadImage(clip);
            }
        }
    };

    private void downloadImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "onCreate: ");
                    Document doc = Jsoup.connect(url).get();
                    Elements es = doc.head().select("meta[content*=cdninstagram]");
                    if (!es.isEmpty()) {
                        Element e = es.get(0);
                        String url = e.attr("content");
                        Log.d(TAG, "run: " + url);

                        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
                        String name = df.format(new Date());

                        startDownload(name, url);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void startDownload(String filename,String url) {
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(FOLDER_NAME, filename + ".jpg");
        request.setTitle("picture from Instagram");
        request.setDescription(getString(R.string.picture_downloading));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = downloadManager.enqueue(request);
    }

    private boolean isInstagramUrl(String clip) {
        if (clip.contains("https://www.instagram.com")) {
            Log.d(TAG, "isInstagramUrl: ");
            return true;
        }
        return false;
    }
}
