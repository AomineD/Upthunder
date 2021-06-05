package com.wineberryhalley.upthunder.updater;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.gplayapi.data.models.App;
import com.wineberryhalley.bclassapp.permission.PermissionBottom;
import com.wineberryhalley.upthunder.api.AuroraApplication;
import com.wineberryhalley.upthunder.updater.intro.IntroThunderUpdater;

public class AuthGPlay {


    public static String packageName;
    public static int versionCode = 42;
    public interface OnResponseAuth{
        void OnSuccess();
        void OnFail(String erno);
    }

    public static void autenticateNow(AppCompatActivity context) {
        packageName = context.getPackageName();

        if(!StrDt.canUpdate()){
            return;
        }
     //   Log.e("MAIN", "autenticateNow: sik" );
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;


        if(StrDt.checkifHavePermissions(context) && !StrDt.canShowIntro())
        nowYes(context);
        else{
           // Log.e("MAIN", "autenticateNow: no permiss first: "+(StrDt.checkifHavePermissions(context))+" sec: "+(!StrDt.canShowIntro()) );
            IntroThunderUpdater introThunderUpdater = new IntroThunderUpdater(context);
introThunderUpdater.showIntro(new PermissionBottom.OnDismissPermission() {
    @Override
    public void OnDissmisResult(boolean okresult) {
        if(okresult){
            nowYes(context);
        }
    }
});

        }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MAIN", "autenticateNow: "+e.getMessage() );
            e.printStackTrace();
        }

    }


    private static void nowYes(AppCompatActivity context){
        Log.e("MAIN", "got: vah" );
        StrDt.getAnother(new AuthGPlay.OnResponseAuth() {
            @Override
            public void OnSuccess() {
got(context);
            }

            @Override
            public void OnFail(String erno) {
                Log.e("MAIN", "OnFail: "+erno );
                got(context);
            }
        });
    }


    private static void got(AppCompatActivity context){

        UpdateNt updateNt = new UpdateNt(context);

        if(StrDt.getType() == TypeUpdate.ASK ) {
            StrDt.addUpdateFrec();
            Log.e("MAIN", "got: "+StrDt.canShowUpdate() );
            if(StrDt.canShowUpdate() || StrDt.existApk()) {
                Log.e("MAIN", "got: bien" );
                updateNt.showUpdater(new UpdateNt.OnUpdateListener() {
                    @Override
                    public void OnUpdate(App app) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BottomNotifyUpdate bottomNotifyUpdate = new BottomNotifyUpdate(app);
                                    bottomNotifyUpdate.showUpdate(context);
                                }
                            });
                    }
                });
            }
        }else if(StrDt.getType() == TypeUpdate.BACKGROUND){
            updateNt.showUpdater(new UpdateNt.OnUpdateListener() {
                @Override
                public void OnUpdate(App app) {
                        updateNt.downloadInBackground();

                }
            });
        }

    }
}
