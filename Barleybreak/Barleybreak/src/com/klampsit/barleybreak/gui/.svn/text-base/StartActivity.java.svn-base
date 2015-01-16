package com.klampsit.barleybreak.gui;

import com.klampsit.barleybreak.R;
import com.klampsit.barleybreak.gui.prefs.SettingsActivity;
import com.klampsit.barleybreak.utils.SettingsStringsManager;
import com.klampsit.barleybreak.utils.Utils;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class StartActivity extends Activity {
    private SharedPreferences mPreferences;
    private SettingsStringsManager mStrManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.hideTitleAndActionBar(this);
        setContentView(R.layout.activity_start);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mStrManager = new SettingsStringsManager(getResources());
        initMainMenu();
    }

    public Intent getStartGameIntent() {
        return new Intent(StartActivity.this, GameActivity.class);
    }

    public void onStatisticsClicked(View v) {

    }

    public void onSettingsClicked(View v) {
        Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onExitClicked(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onStartClicked(View v) {
        MediaPlayer.create(this, R.raw.clicksnd).start();
        final Intent startGameIntent = getStartGameIntent();
        startGameIntent.putExtra("puzzle", v.getId() == R.id.el1);
        startActivity(startGameIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updatePreferences();

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String path = "";

            if (selectedImage == null) {
                Bitmap img = (Bitmap) data.getExtras().get("data");

                if (img == null) {
                    return;
                }

                selectedImage = Uri.parse(Media.insertImage(getContentResolver(),
                    img,
                    "BarleyImage",
                    "Image for barley-break solving"));

            }

            path = Utils.getRealPathFromURI(getContentResolver(), selectedImage);
            mPreferences.edit().putString(mStrManager.get(R.string.S_GameImage), path).commit();
        }
    }

    private void initMainMenu() {
        
    }

    private void updatePreferences() {
        String name = mPreferences.getString(mStrManager.get(R.string.S_Username), null);
        final TextView tv = (TextView) findViewById(R.id.welcome_inf);
        final String hello = getResources().getString(R.string.hello);

        if (name == null) {
            final EditText et = new EditText(this);
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_input_get)
                .setTitle(R.string.start_dlg_title)
                .setView(et)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        mPreferences.edit()
                            .putString(mStrManager.get(R.string.S_Username),
                                et.getText().toString().isEmpty() ? "Unknown" : et.getText()
                                    .toString())
                            .commit();
                        String welcomeText = String.format("%s, %s !",
                            hello,
                            mPreferences.getString(mStrManager.get(R.string.S_Username), null));
                        tv.setText(welcomeText);
                        Utils.fadeIn(tv, 1300);
                    }
                })
                .show();
        } else {
            String welcomeText = String.format("%s, %s !", hello, name);
            tv.setText(welcomeText);
            Utils.fadeIn(tv, 1300);
        }

        Utils.fadeIn(tv, 1300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
