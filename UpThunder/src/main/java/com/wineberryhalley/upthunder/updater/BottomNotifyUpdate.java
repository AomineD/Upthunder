package com.wineberryhalley.upthunder.updater;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.aurora.gplayapi.data.models.App;
import com.wineberryhalley.bclassapp.BottomBaseShet;
import com.wineberryhalley.upthunder.R;

public class BottomNotifyUpdate extends BottomBaseShet {

    public BottomNotifyUpdate(App a){
        this.app = a;
    }

    @Override
    public int layoutID() {
        return R.layout.btm_notify;
    }

    private AppCompatActivity appCompatActivity;
    public void showUpdate(AppCompatActivity a){

        this.appCompatActivity = a;
        showNow(a.getSupportFragmentManager(), "updtqmsa");
    }

    private App app;

    private TextView tit, desc;
    private CardView btn;

    @Override
    public void OnStart() {

        tit = find(R.id.tit);
        desc = find(R.id.desc_update);
        String g = getString(R.string.update_titla);
        g = String.format(g, getString(R.string.app_name));
        tit.setText(g);



   //     String ad = Html.fromHtml(app.getChanges()).toString();
        String as = String.format(getString(R.string.to_upd), String.valueOf(AuthGPlay.versionCode)) + " "+app.getVersionCode();
        desc.setText(as);
     //   desc.setVisibility(View.GONE);
        btn = find(R.id.btn_giveper);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        ((View)find(R.id.btn_late)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrDt.addUpdateFrec();
                UpdateNt.hideIf();
dismissAllowingStateLoss();
            }
        });
    }

    private void update() {
UpdateNt.updateApp();
dismissAllowingStateLoss();
    }
}
