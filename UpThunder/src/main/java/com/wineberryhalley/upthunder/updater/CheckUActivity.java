package com.wineberryhalley.upthunder.updater;

import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.aurora.authenticator.ResultActivity;
import com.aurora.gplayapi.data.models.App;
import com.aurora.gplayapi.data.models.AuthData;
import com.aurora.gplayapi.helpers.AppDetailsHelper;
import com.aurora.gplayapi.helpers.AuthHelper;
import com.aurora.gplayapi.helpers.PurchaseHelper;
import com.aurora.store.data.event.InstallerEvent;
import com.aurora.store.data.installer.AppInstaller;
import com.aurora.store.data.installer.IInstaller;
import com.aurora.store.data.model.Installer;
import com.google.protobuf.Any;
import com.wineberryhalley.bclassapp.BaseActivity;
import com.wineberryhalley.upthunder.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class CheckUActivity extends BaseActivity {


    private TextView txt_version_updates, version_tlt, btn_txt;
    private LottieAnimationView loading_lottie;

    private ArrayList<String> paths = new ArrayList<>();
    @Override
    public void Main() {
        if(getIntent() != null){
            String a = getIntent().getStringExtra("well_init");
            if(a != null && !a.isEmpty()){
                start();
            }else{
                finish();
            }

        }else{
            finish();
        }
    }

    @Override
    public void statusChanged(int pixelesSizeBar) {

    }

    @Override
    public int resLayout() {
        return R.layout.activity_check_u;
    }

    @Override
    public ArrayList<String> keysNotification() {
        return null;
    }

    @Override
    public void onReceiveValues(ArrayList<String> values) {

    }

    private void start() {


        txt_version_updates = findViewById(R.id.text_updates);
        version_tlt = findViewById(R.id.txt_version_ttl);
        btn_txt = findViewById(R.id.btn_txt);
        loading_lottie = findViewById(R.id.lottie_loading);
        findViewById(R.id.back_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

   loadData();

        ArrayList<String> abueno = getIntent().getStringArrayListExtra("fil");
        if(abueno.size() > 0) {
            paths.addAll(abueno);
            if (paths.size() < 1) {

version_tlt.setText(R.string.error_);
txt_version_updates.setText(R.string.action_try_later);

            } else {
                findViewById(R.id.install_now).setOnClickListener(clickInstall());
            }


        }else{
            Log.e(TAG, "start: no we" );
        }
    }

    private String TAG ="MAIN";

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(InstallerEvent event){
       // Log.e(TAG, "onEventMainThread: "+event);
        loading_lottie.setVisibility(View.GONE);
        btn_txt.setVisibility(View.VISIBLE);
        if(event instanceof InstallerEvent.Success) {

            version_tlt.setText(R.string.title_installed);
        String form = String.format(getString(R.string.can_go), getString(R.string.app_name));
            txt_version_updates.setText(form);

            btn_txt.setText(R.string.action_close);
            findViewById(R.id.install_now).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }else if(event instanceof InstallerEvent.Cancelled){
            version_tlt.setText(R.string.error_);
            txt_version_updates.setText(R.string.install_canceled);
            btn_txt.setText(R.string.action_close);
            findViewById(R.id.install_now).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }else if(event instanceof InstallerEvent.Failed){
            version_tlt.setText(R.string.error_);
            txt_version_updates.setText(R.string.error_install);
            btn_txt.setText(R.string.action_retry);
            findViewById(R.id.install_now).setOnClickListener(clickInstall());

        }
    }

    private View.OnClickListener clickInstall(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Log.e(TAG, "onClick: click" );
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

v.setOnClickListener(clickClean());
                        // API wrapper instance is ready
                        try {

                            String pack = "com.reva.app.pelispluschromecast";


                            ArrayList<File> files = new ArrayList<>();

                            for (String pth :
                                    paths) {
                                files.add(new File(pth));
                            }


                            AppInstaller appInstaller = new AppInstaller(getApplicationContext());
                            IInstaller iInstaller = appInstaller.getPreferredInstaller();
                            iInstaller.install(pack, files);
                            Log.e(TAG, "run: installing..." );
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //btn_txt.setText(R.string.action_installing);
                                    btn_txt.setVisibility(View.GONE);
                                    version_tlt.setText(R.string.action_wait_moment);
                                    loading_lottie.setVisibility(View.VISIBLE);
                                    txt_version_updates.setText(R.string.installing_we);
                                }
                            });



                        } catch (Exception e) {
                            Log.e("MAIN", "run Exc: " + e.getMessage());
                            e.printStackTrace();
                        }


                    }
                });

                thread.start();
            }
        };
    }

    private View.OnClickListener clickClean(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }


    private void loadData(){
        Thread at = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    if(!UpdateNt.updates.isEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txt_version_updates.setText(Html.fromHtml(UpdateNt.updates, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            txt_version_updates.setText(Html.fromHtml(UpdateNt.updates));
                        }
                    }else{
                        txt_version_updates.setText(R.string.default_update_text);
                    }
                    }
                });

            }
        });

        at.start();
    }
}