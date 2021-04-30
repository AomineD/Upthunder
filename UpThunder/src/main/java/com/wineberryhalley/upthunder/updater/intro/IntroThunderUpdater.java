package com.wineberryhalley.upthunder.updater.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.wineberryhalley.bclassapp.BottomBaseShet;
import com.wineberryhalley.bclassapp.permission.PermissionBottom;
import com.wineberryhalley.upthunder.R;
import com.wineberryhalley.upthunder.updater.AuthGPlay;
import com.wineberryhalley.upthunder.updater.StrDt;

import java.util.Timer;
import java.util.TimerTask;

public class IntroThunderUpdater extends BottomBaseShet {

    public IntroThunderUpdater(AppCompatActivity activity){
this.appCompatActivity = activity;
    }

    private String[] stPermiss = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES};



    private StateProgressBar.StateNumber getActual(boolean next){
        if(next)
            p++;

        StateProgressBar.StateNumber am = StateProgressBar.StateNumber.ONE;
        int as =  p+1;
        switch (as){
            case 1:
                am = StateProgressBar.StateNumber.ONE;
                break;
            case 2:
                am = StateProgressBar.StateNumber.TWO;
                break;
            case 3:
                am = StateProgressBar.StateNumber.THREE;
                break;
            case 4:
                am = StateProgressBar.StateNumber.FOUR;
                break;
            case 5:
                am = StateProgressBar.StateNumber.FIVE;
                break;
        }

        return am;
    }

    private AppCompatActivity appCompatActivity;

    private String[] descrs = new String[0];


    @Override
    public int layoutID() {
        return R.layout.bottom_intro_thunder;
    }

    public interface OnDismissPermission{
        void OnDissmisResult(boolean okresult);
    }

    private PermissionBottom.OnDismissPermission dismissPermission;

    private StateProgressBar.StateNumber stateNumber = StateProgressBar.StateNumber.TWO;
    private TextView descr;
    private TextView titl;
    private CardView btn;
    StateProgressBar setpview;

    @Override
    public int heightMax() {
        return 0;
    }

    @Override
    public boolean Modal() {
        return false;
    }

    @Override
    public void OnStart() {
        getBeans();
        setpview = find(R.id.your_state_progress_bar_id);
        titl = find(R.id.title_perm);
        String g = getString(R.string.updater_title_n);
        g = String.format(g, getString(R.string.app_name));
        titl.setText(g);
        descr = find(R.id.permissionNow);
        btn = find(R.id.btn_giveper);
        btn.setOnClickListener(clickTo());
        ((View)find(R.id.btn_deactive)).setOnClickListener(desactive());
        
        
            String gt = descrs[0];
            descr.setText(gt);

        setpview.setStateNumberTextSize(13);

        setpview.setStateDescriptionData(descriptionData);

        setpview.setMaxStateNumber(stateNumber);

        checkPermissionIfSuccess();

        ((View)find(R.id.close_)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNow();
            }
        });
    }

    private View.OnClickListener desactive() {
    return new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            StrDt.noUpdateNever();
            dismissAllowingStateLoss();
        }
    };
    }


    private void closeNow(){
        if(dismissPermission!=null){
            dismissPermission.OnDissmisResult(false);
        }
        StrDt.introShowed();
        dismissAllowingStateLoss();
    }

    public void showIntro(PermissionBottom.OnDismissPermission a){
        this.dismissPermission = a;

        if(StrDt.canShowIntro())
            show(appCompatActivity.getSupportFragmentManager(), "adwghqq");
    }

    private void checkPermissionIfSuccess(){

        if(p == 0) {
            if (ContextCompat.checkSelfPermission(getContext(), stPermiss[p]) == PackageManager.PERMISSION_GRANTED) {
                nextPermission();
            }
        }else{
           if(StrDt.checkifHavePermissions(appCompatActivity)){
               successPermissions();
           }else{
             //  nextPermission();

           }
        }

    }

    private int p;
    private View.OnClickListener clickTo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = setpview.getCurrentStateNumber() - 1;

                if(p == 0) {
                    requestPermissions(new String[]{stPermiss[p]}, 960);
                }else{
                    Log.e("MAIN", "onClick: permiso" );
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", AuthGPlay.packageName))), 960);
                }
            }
        };
    }

    private void nextPermission() {
        Log.e("MAIN", "nextPermission: va "+p );
        StateProgressBar.StateNumber ase = getActual(true);
        if (p < stPermiss.length) {
            setpview.setCurrentStateNumber(ase);
            // Log.e(TAG, "nextPermission: "+descrs[p]);
           if (descrs.length > p && descrs[p] != null && !descrs[p].isEmpty()) {
                String gt = descrs[p];
                descr.setText(gt);
            }

            checkPermissionIfSuccess();
        }else{
                successPermissions();

        }
    }

    private TextView text_ask, text_back;
    private CardView btn_ask, btn_backgro;
    private String[] descriptionData;

    private void successPermissions()
    {
        titl.setText(R.string.success_perm);
        descr.setText(R.string.success_perm_de_2);
        ((View)find(R.id.desc_perm)).setVisibility(View.GONE);
        ((View)find(R.id.relay_btm)).setVisibility(View.GONE);
        setpview.setAllStatesCompleted(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                appCompatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        titl.setText(R.string.select_type);
                        descr.setText(R.string.select_info);
                        setpview.setVisibility(View.GONE);
                        selectType();
                    }
                });


            }
        }, 2300);
    }
    private boolean a = false;
    private void getBeans() {
        a = false;

        descrs = new String[2];
        descrs[0] = getString(R.string.updater_permis_wri);
        descrs[1] = getString(R.string.updater_permis_install);

        descriptionData = new String[2];
        descriptionData[0] = getString(R.string.write_pe_mission);
        descriptionData[1] = getString(R.string.request_pe_mission);
    }


    private void selectType(){

       btn_ask = find(R.id.ask_btn);
       text_ask = find(R.id.ask_text);
       btn_backgro = find(R.id.background_btn);
       text_back = find(R.id.back_text);

        btn_backgro.setOnClickListener(sett(1));
        btn_ask.setOnClickListener(sett(0));
        ((View)find(R.id.types)).setVisibility(View.VISIBLE);
        ((View)find(R.id.relay_btm)).setVisibility(View.VISIBLE);
        TextView t = find(R.id.text_btn);
        btn.setVisibility(View.VISIBLE);
        t.setText(R.string.save_no);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dismissPermission!=null){
                    dismissPermission.OnDissmisResult(true);
                }

                StrDt.introShowed();
                dismissAllowingStateLoss();
            }
        });
                        requestNewSize();

    }

    private View.OnClickListener sett(int i) {
    return new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int blk = ContextCompat.getColor(appCompatActivity, R.color.blk);
            int wh = ContextCompat.getColor(appCompatActivity, R.color.white);
            int acc = ContextCompat.getColor(appCompatActivity, R.color.acctp);

            if(i == 0){

                text_ask.setTextColor(wh);
                btn_ask.setCardBackgroundColor(acc);

                text_back.setTextColor(blk);
                btn_backgro.setCardBackgroundColor(wh);

            }else{
                text_back.setTextColor(wh);
                btn_backgro.setCardBackgroundColor(acc);

                text_ask.setTextColor(blk);
                btn_ask.setCardBackgroundColor(wh);
            }

            StrDt.setTypeUpdate(i);

        }
    };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(p == stPermiss.length - 1){
                successPermissions();
            }else{
                nextPermission();
            }
        }else{
            closeNow();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 960){

            if(StrDt.checkifHavePermissions(getContext())){
                successPermissions();
            }else{
                Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.no_permiss_ins), Toast.LENGTH_SHORT).show();
                closeNow();
            }

        }
    }
}
