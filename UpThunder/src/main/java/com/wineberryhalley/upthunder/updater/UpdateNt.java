package com.wineberryhalley.upthunder.updater;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.gplayapi.data.models.App;
import com.aurora.gplayapi.data.models.AuthData;
import com.aurora.gplayapi.data.models.File;
import com.aurora.gplayapi.helpers.AppDetailsHelper;
import com.aurora.gplayapi.helpers.PurchaseHelper;
import com.aurora.store.data.installer.AppInstaller;
import com.wineberryhalley.bclassapp.notification.NotificationBase;
import com.wineberryhalley.bclassapp.notification.PriorityNotification;
import com.wineberryhalley.upthunder.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateNt extends NotificationBase {
    @Override
    protected boolean autoCancel() {
        return true;
    }

    Context a;
    Activity activity;
    static UpdateNt instance;
    public UpdateNt(AppCompatActivity context){
        a = Cont.context;
        activity = context;
        String desc = String.format(a.getString(R.string.chekinga), a.getString(R.string.app_name));
        init(a.getString(R.string.app_name), desc, R.drawable.ic_notif_updater, PriorityNotification.LOW);
    instance = this;
    }

    public static void hideIf(){
        if(instance != null){
            instance.hide();
        }
    }

    public static void updateApp(){
        if(instance != null){
            instance.downloadInBackground();
        }
    }

    public void showUpdater(OnUpdateListener listener){
    Notification.Builder builder = getBuilder();

        ls = listener;
        showNotificationBuild(id_notif, builder);
    }

    private static int id_notif = 395;

    private OnUpdateListener ls;


    public interface OnUpdateListener{
        void OnUpdate(App app);
    }


    private static boolean hasUpdate = false;
    private static boolean alreadyShow = false;
    @Override
    protected void onShow() {
pack = AuthGPlay.packageName;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {




                try {



if(!alreadyShow) {
    AuthData authData = StrDt.get();

    AppDetailsHelper appDetailsHelper = new AppDetailsHelper(authData);
     Log.e(TAG, "run: va1" );
    App app = appDetailsHelper.getAppByPackageName(pack);
   //  Log.e(TAG, "run: va2" );
    updates = app.getChanges();
    if(StrDt.existApk() && StrDt.versionIsUpper(activity, app.getVersionCode())) {
        completed(StrDt.getActualFiles(), false);
    }
   else if (AuthGPlay.versionCode < app.getVersionCode() && !hasUpdate) {
        //    Log.e(TAG, "run: va3" );





        if (StrDt.getType() == TypeUpdate.ASK) {
            String desc = String.format(a.getString(R.string.new_udp), a.getString(R.string.app_name));
            init(Cont.context.getString(R.string.has_upd), desc, R.drawable.ic_notif_updater, PriorityNotification.DEFAULT);
        } else {
            String desc = String.format(a.getString(R.string.updating_app), a.getString(R.string.app_name));
            init(Cont.context.getString(R.string.has_upd), desc, R.drawable.ic_notif_updater, PriorityNotification.LOW);
        }
        Notification.Builder builder = getBuilder();


        showNotificationBuild(id_notif, builder);
        hasUpdate = true;
        versionToUpdate = app.getVersionCode();
        ls.OnUpdate(app);
    }
}
                } catch (Exception e) {
                    Log.e(TAG, "run: "+e.getMessage() );
               hide();
                }

                // API wrapper instance is ready

            }
        });

        thread.start();
    }
    String pack = "com.reva.app.pelispluschromecast";

    public static int versionToUpdate = 0;
    private String TAG ="MAIN";

    public void downloadInBackground(){
        alreadyShow = true;
        Context a = Cont.context;
        String title = String.format(a.getString(R.string.dow_d), a.getString(R.string.app_name));
        init(title, a.getString(R.string.downloading_), R.drawable.ic_notif_updater, PriorityNotification.LOW);

        Notification.Builder builder = getBuilder();

showNotificationBuild(id_notif, builder);
        //Log.e(TAG, "downloadInBackground: va " );

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                PurchaseHelper purchaseHelper = new PurchaseHelper(StrDt.get());


                try {
                    List<File> files =   purchaseHelper.purchase(pack, versionToUpdate, 1);


                    Log.e(TAG, "run: maxim "+files.size() );
                    max = files.size();
                    if(max > 0) {
                        UpdateDownloader updateDownloader = new UpdateDownloader(files, getListener());

                        updateDownloader.downloadApk();

                    }
                } catch (Exception e) {
                    Log.e(TAG, "downloadInBackground: "+e.getMessage() );
                    for (StackTraceElement ag:
                         e.getStackTrace()) {
                        Log.e(TAG, "run: "+ag.toString() );
                    }
                    hide();
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }


    public static String updates = "";
    private void hide(){
        hideNotification(id_notif, getBuilder());
    }
    private int max = 0;
    private boolean downloadComplete = false;
    private UpdateDownloader.DownloadListener getListener(){
        return new UpdateDownloader.DownloadListener() {
            @Override
            public void DonwloadComplete(ArrayList<java.io.File> filesList) {
            //    Log.e(TAG, "DonwloadComplete: ya" );
                if(!downloadComplete) {

                    StrDt.saveActualFiles(filesList);
                   completed(filesList, true);
                }
                downloadComplete = true;
            }

            @Override
            public void OnProgressChanged(int ind, int progress, double velocity) {
                if(!downloadComplete) {
             //       Log.e(TAG, "OnProgressChanged: " + progress);
                    int maximun = 100;
                    String abueno = String.format(a.getString(R.string.downloading_2), a.getString(R.string.app_name));
                    String progresso = (ind + 1) + "/" + max + " - " + getVelocity(velocity);


                    init(abueno, progresso, R.drawable.ic_notif_updater, PriorityNotification.MIN);
                    Notification.Builder builder = getBuilder();

                    if (progress < 10 && progress > -1) {
                        builder.setProgress(maximun, progress, true);
                    } else {
                        builder.setProgress(maximun, progress, false);
                    }
                    showNotificationBuild(id_notif, builder);
                }
            }

            @Override
            public void OnError(String error) {
                Log.e(TAG, "OnError downloading: "+error );
  hide();
            }
        };
    }


    private String getVelocity(double d){

return round(d)+" KB/s";
    }

    private double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void completed(ArrayList<java.io.File> filesList, boolean bydownload){
        alreadyShow = true;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                hide();
                Notification.Builder builder = null;
                if(bydownload) {
                    String abueno = String.format(a.getString(R.string.desc_completed), a.getString(R.string.app_name));
                    init(a.getString(R.string.title_download_complete), abueno, R.drawable.ic_notif_updater, PriorityNotification.DEFAULT);
                     builder = getBuilderMoreDesc(abueno);

                }else{
                    String abueno = String.format(a.getString(R.string.desc_completed_2), a.getString(R.string.app_name));
                    init(a.getString(R.string.new_version), abueno, R.drawable.ic_notif_updater, PriorityNotification.DEFAULT);
                    builder = getBuilderMoreDesc(abueno);
                }
                builder.setProgress(0, 0, false);


                ArrayList<String> paths = new ArrayList<>();
                for (java.io.File a:
                     filesList) {
                    paths.add(a.getAbsolutePath());
                }

          //      Log.e(TAG, "run: va bien " +paths.size());

                Intent in = new Intent(Cont.context, CheckUActivity.class);
                in.putExtra("well_init", "ada");
                in.putStringArrayListExtra("fil", paths);

                PendingIntent pendingIntent = PendingIntent.getActivity(Cont.context, 42, in,0);
            builder.setContentIntent(pendingIntent);
             //   setOnClickNotificationBase(CheckUActivity.class, ad, builder);
                //  Log.e(TAG, "DonwloadComplete: yes "+abueno );
                if(bydownload)
                showNotificationBuild(id_notif, builder);
                else if(StrDt.canShowInstallNotif()){
                    showNotificationBuild(id_notif, builder);
                    StrDt.addFrecInstall();
                }else if(!StrDt.canShowInstallNotif()){
                    Log.e(TAG, "run: added+" );
                    StrDt.addFrecInstall();
                }

            }
        }, 200);
    }
}
