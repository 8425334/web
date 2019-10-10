package com.example.bannerlib;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class LotteryWeb extends AppCompatActivity {

    WebView webView;

    DownloadManager mDownloadManager;
    long mId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_web);

        webView = findViewById(R.id.lottery_web);
        dialog = new ProgressDialog(this);
        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        //WebView设置
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        this.webView.setWebViewClient((WebViewClient)new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView webView, final String s) {
                webView.loadUrl(s);
                return true;
            }
        });
        this.webView.setWebViewClient((WebViewClient)new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
            }

            public boolean shouldOverrideUrlLoading(final WebView webView, final String s) {
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                if (TextUtils.isEmpty((CharSequence)hitTestResult.getExtra()) || hitTestResult.getType() == 0) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("\u91cd\u5b9a\u5411: ");
                    sb.append(hitTestResult.getType());
                    sb.append(" && EXTRA\uff08\uff09");
                    sb.append(hitTestResult.getExtra());
                    sb.append("------");
                    Log.e("\u91cd\u5b9a\u5411", sb.toString());
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("GetURL: ");
                    sb2.append(webView.getUrl());
                    sb2.append("\ngetOriginalUrl()");
                    sb2.append(webView.getOriginalUrl());
                    Log.e("\u91cd\u5b9a\u5411", sb2.toString());
                    final StringBuilder sb3 = new StringBuilder();
                    sb3.append("URL: ");
                    sb3.append(s);
                    Log.d("\u91cd\u5b9a\u5411", sb3.toString());
                }
                if (!s.startsWith("http://") && !s.startsWith("https://")) {
                    try {
                        LotteryWeb.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(s)));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return true;
                }
                webView.loadUrl(s);
                return false;
            }
        });

        webView.setDownloadListener(new MyDownload());
        webView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        boolean canGoBack = intent.getBooleanExtra("canGoBack", false);
        if (!canGoBack){
            finish();
        }
    }

    class MyDownload implements DownloadListener {
        public void onDownloadStart(final String s, final String s2, final String s3, final String s4, final long n) {
            if (s.endsWith(".apk")) {
                mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                // 下载过程和下载完成后通知栏有通知消息。
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDescription("apk正在下载");
                //设置保存目录  /storage/emulated/0/Android/包名/files/Download
                request.setDestinationInExternalFilesDir(LotteryWeb.this,Environment.DIRECTORY_DOWNLOADS,"download.apk");
                mId = mDownloadManager.enqueue(request);

                Log.i("TAG", Environment.DIRECTORY_DOWNLOADS);

                //注册内容观察者，实时显示进度
                MyContentObserver downloadChangeObserver = new MyContentObserver(null);
                getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadChangeObserver);

                dialog.show();

                //广播监听下载完成
                listener(mId);

            }
        }

        private void listener(final long id) {
            //Toast.makeText(MainActivity.this,"XXXX",Toast.LENGTH_SHORT).show();
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long longExtra = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == longExtra){
//                    Uri downloadUri = mDownloadManager.getUriForDownloadedFile(id);
                        Intent install = new Intent(Intent.ACTION_VIEW);

                        File apkFile = getExternalFilesDir("DownLoad/download.apk");
                        Log.i("TAG", apkFile.getAbsolutePath());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uriForFile = FileProvider.getUriForFile(context,  context.getPackageName() + ".fileprovider", apkFile);
                            install.setDataAndType(uriForFile,"application/vnd.android.package-archive");
                        }else {
                            install.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
                        }

                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(install);
                        Toast.makeText(LotteryWeb.this,"APP下载完成", Toast.LENGTH_SHORT).show();
                    }
                }

            };

            registerReceiver(broadcastReceiver,intentFilter);
        }

    }


    class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onChange(boolean selfChange) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            DownloadManager dManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final Cursor cursor = dManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                final int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalSize = cursor.getInt(totalColumn);
                int currentSize = cursor.getInt(currentColumn);
                float percent = (float) currentSize / (float) totalSize;
                float progress = (float) Math.floor(percent * 100);

                dialog.setMessage("下载进度:" + (int)progress + "%");
                dialog.setCancelable(false);
                if (progress == 100){
                    dialog.dismiss();
                }
            }
        }

    }

}
