package com.wineberryhalley.upthunder.updater;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.aurora.authenticator.ResultActivity;
import com.aurora.gplayapi.data.models.AuthData;
import com.aurora.gplayapi.data.models.PlayResponse;
import com.aurora.store.data.network.HttpClient;
import com.google.gson.Gson;
import com.wineberryhalley.bclassapp.TinyDB;
import com.wineberryhalley.upthunder.api.AuroraApplication;
import com.wineberryhalley.upthunder.api.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static com.wineberryhalley.upthunder.updater.Cont.tinyDB;

public class StrDt
{
    public static void saveData(Map<String, String> map){
        if(map.get("Email") != null){
            tinyDB.putString(k_e, map.get("Email"));
        }
        if(map.get("Auth") != null){
            tinyDB.putString(k_a, map.get("Auth"));
        }
        if(map.get("Token") != null){
            tinyDB.putString(k_tk, map.get("Token"));
        }

    }

    public static void saveAuthAnom(AuthData authData){
        tinyDB.putObject("anom_adata", authData);
        tinyDB.putBoolean("logg", true);
    }

    public static boolean isSaved(){
        boolean ab = tinyDB.getBoolean("logg", false);
        //Log.e("MAIN", "isSaved: "+ab );
        return ab;
    }

    public static boolean checkifHavePermissions(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context.getPackageManager().canRequestPackageInstalls();
        }else{
            return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static boolean canShowIntro(){
        return !tinyDB.getBoolean("intr_updater", false);
    }

    public static void introShowed(){
        tinyDB.putBoolean("intr_updater", true);
    }


    private static String k_e = "q_le";
    private static String k_a = "q_at";
    private static String k_tk = "q_toka";
    public static String iml(){
        return tinyDB.getString(k_e);
    }

    public static String atq(){
        return tinyDB.getString(k_a);
    }

    public static String tkq(){
        return tinyDB.getString(k_tk);
    }

    public static AuthData get(){
        return tinyDB.getObject("anom_adata", AuthData.class);
    }



    public static Properties getRandom(){
        Properties properties = new Properties();
        int a = new Random().nextInt(propertiesStr.length);
        try {

            String proStr = propertiesStr[a]+".properties";
            properties =      ResultActivity.Companion.loadProperties(proStr);
            //    Log.e("MAIN", "not null: "+(properties != null) );
        } catch (Exception e) {
            Log.e("MAIN", "start: "+e.getMessage() );
        }

      //  Log.e("MAIN", "getRandom: obtuvo = "+a );
        return properties;
    }

    private static String[] propertiesStr = new String[]{
      "poco_f1",
      "op_8_pro",
            "op_7t_pro",
            "sm_s8",
            "op_x",
            "rm_7",
            "sm_s9_plus",
            "hw_mate20",
            "mi_8_se",
            "nk_9",
            "sm_a3",
            "sm_s7",
            "xp_5_dual"
    };

    public static void getAnother(AuthGPlay.OnResponseAuth li){
buildSecureAnonymousAuthData(li);
    }

    public static TypeUpdate getType(){
        int ty = tinyDB.getInt("typeaupd");

        if(ty == 0){
            return TypeUpdate.ASK;
        }else{
            return TypeUpdate.BACKGROUND;
        }
    }

    public static void setTypeUpdate(int type){
         tinyDB.putInt("typeaupd", type);

    }

    public static boolean existApk(){
        ArrayList<File> f = getActualFiles();

        for (File file:
             f) {
            if(file.exists()){
                return true;
            }
        }

        return false;
    }

    public static boolean canShowInstallNotif(){
        int as = tinyDB.getInt("instl_frec");
        if(as >= 2){
            tinyDB.putInt("instl_frec", 0);
        }
        return as >= 2;
    }

    public static void addFrecInstall(){
        int as = tinyDB.getInt("instl_frec");

        as++;
       // Log.e("MAIN", "addFrecInstall: "+as );
        tinyDB.putInt("instl_frec", as);
    }

    public static void saveActualFiles(ArrayList<File> as){
        ArrayList<File> f = tinyDB.getListObject("fl_saveds", File.class);

        f.clear();
        f.addAll(as);

        tinyDB.putListObject("fl_saveds", f);
    }

    public static ArrayList<File> getActualFiles(){
        return tinyDB.getListObject("fl_saveds", File.class);
    }

    public static boolean versionIsUpper(Activity activity, int version){
        PackageManager pm = activity.getPackageManager();

        ArrayList<File> af = getActualFiles();

        if(af.size() > 0){

            PackageInfo info = pm.getPackageArchiveInfo(af.get(0).getAbsolutePath(), 0);

            return info.versionCode >= version && info.versionCode > AuthGPlay.versionCode;
        }else
            return false;

    }

    public static boolean canShowUpdate(){
        int as = tinyDB.getInt("update_frec");
        if(as >= 3){
  //          Log.e("MAIN", "canShowUpdate: reset" );
            tinyDB.putInt("update_frec", 0);
        }
        Log.e("MAIN", "canShowUpdate: "+as );
        return as >= 3;
    }

    public static void addUpdateFrec(){
        int as = tinyDB.getInt("update_frec");

        as++;
      //   Log.e("MAIN", "addFrecInstall: "+as );
        tinyDB.putInt("update_frec", as);
    }

    public static void noUpdateNever(){
        tinyDB.putBoolean("updnever", true);
    }

    public static boolean canUpdate(){
        return !tinyDB.getBoolean("updnever", false);
    }



    public static void buildSecureAnonymousAuthData(AuthGPlay.OnResponseAuth onResponseAuth){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                Gson gson = new Gson();
                Properties properties = getRandom();
                try {
                    PlayResponse playResponse = HttpClient.INSTANCE.getPreferredClient().postAuth(Constants.URL_DISPENSER, gson.toJson(properties).getBytes());
            if(playResponse.isSuccessful()){
                Log.e("MAIN", "run: ya" );
                AuthData authData = gson.fromJson(new String(playResponse.getResponseBytes()), AuthData.class);
            saveAuthAnom(authData);
            onResponseAuth.OnSuccess();
            }else{
                switch (playResponse.getCode()){
                    case 404: onResponseAuth.OnFail("Server 404");
                    break;
                    case 429: onResponseAuth.OnFail("Oops, You are rate limited");
                    break;
                    default:
                        onResponseAuth.OnFail("General error "+playResponse.getErrorString());
                }
            }
                } catch (IOException e) {
                    e.printStackTrace();
                onResponseAuth.OnFail(e.getMessage());
                }


            }
        });

        thread.start();
    }

}
