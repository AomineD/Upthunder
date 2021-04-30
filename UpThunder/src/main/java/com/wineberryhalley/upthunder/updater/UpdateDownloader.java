package com.wineberryhalley.upthunder.updater;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.aurora.gplayapi.data.models.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class UpdateDownloader {

    public interface DownloadListener{
        void DonwloadComplete(ArrayList<java.io.File> files);
        void OnProgressChanged(int ind, int progress, double velocity);
        void OnError(String error);
    }

    private ArrayList<File> f = new ArrayList<>();
    private ArrayList<java.io.File> filesExist = new ArrayList<>();
    private DownloadListener ls;
    public UpdateDownloader(List<File> files, DownloadListener listener){
f = (ArrayList<File>) files;
ls = listener;
    }


    private int downloadedSize;

    private int index = 0;
    public void downloadApk(){
        index = 0;
        downloadNow();
    }

    private void downloadNow(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                URL url = null;
                Long startTime = System.currentTimeMillis();
                try {
                    java.io.File fold = Cont.context.getCacheDir();
                    String folderpat = fold + "/"+Cont.context.getPackageName()+"/apk";
                    java.io.File folder = new java.io.File(folderpat);
                    if(!folder.exists()){
                        folder.mkdirs();
                    }

                    String urlFile = f.get(index).getUrl();
                    String nam = f.get(index).getName();

                    String pathto = folder.getAbsolutePath() +"/"+nam;
                    java.io.File path = new java.io.File(pathto);


             //       Log.e("MAIN", "run: descargando: "+urlFile+" en "+path.getAbsolutePath() );
                    url = new URL(urlFile);
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(4000);
                    int filesize = connection.getContentLength();
                    InputStream inputStream = connection.getInputStream();
                    OutputStream outputStream = new FileOutputStream(path);
                    byte[] buffer = new byte[16384];
             //         Log.e(TAG, "run: "+path );
                    int length = 0;
                    downloadedSize = 0;
                    double speedInKBps = 0;
                    while ((length = inputStream.read(buffer)) > 0) {

                        outputStream.write(buffer, 0, length);
                        downloadedSize += length;
                        //  Log.i("downloaded Size", "" + downloadedSize);
                        try {
                            long timeInSecs = (System.currentTimeMillis() - startTime) / 1000; //converting millis to seconds as 1000m in 1 second
                            //    Log.e(TAG, "mDownloaded: "+mDownloaded+" timeInSecs: "+timeInSecs );
                            speedInKBps = (downloadedSize / timeInSecs) / 1024D;

                        } catch (ArithmeticException ae) {
                            //   Log.e(TAG, "onHandleWork: "+ae.getMessage() );
                        }


                        ls.OnProgressChanged(index,(downloadedSize * 100) / filesize, speedInKBps);

                      //  Log.e("MAIN", "run: "+downloadedSize+" "+filesize );
                        if (downloadedSize == filesize) {
                            filesExist.add(path);
                           index++;
                           if(index < f.size()){
                               downloadNow();
                           }else{

                               ls.DonwloadComplete(filesExist);
                               break;
                           }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ls.OnError(e.getMessage());
                }

            }
        });

        t.start();
    }
}
