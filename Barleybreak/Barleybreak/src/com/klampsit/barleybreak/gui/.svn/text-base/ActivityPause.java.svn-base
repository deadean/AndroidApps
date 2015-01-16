package com.klampsit.barleybreak.gui;

import com.klampsit.barleybreak.R;
import com.klampsit.barleybreak.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityPause extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setOrientation(this, getIntent().getBooleanExtra("orient", false));
        Utils.hideTitleAndActionBar(this);
        setContentView(R.layout.activity_gamepause);
        ((Button) findViewById(R.id.resumeButton)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityPause.this.finish();
            }
        });
    }
}
