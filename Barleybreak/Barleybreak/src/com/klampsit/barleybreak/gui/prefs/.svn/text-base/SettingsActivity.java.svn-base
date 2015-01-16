package com.klampsit.barleybreak.gui.prefs;

import java.util.List;

import com.klampsit.barleybreak.R;
import com.klampsit.barleybreak.utils.OnActivityResultListener;
import com.klampsit.barleybreak.utils.SettingsStringsManager;
import com.klampsit.barleybreak.utils.Utils;
import com.klampsit.barleybreak.utils.db.DBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.ImageView;

public class SettingsActivity extends PreferenceActivity {
    private OnActivityResultListener mOnActivityResult;

    public static AlertDialog.Builder getImageDialog(final Activity aSender) {
        return new AlertDialog.Builder(aSender).setIcon(null)
            .setTitle(R.string.pref_ad_image_sel_type_t)
            .setCancelable(true)
            .setPositiveButton(R.string.pref_gal, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    aSender.startActivityForResult(pickPhoto, 1);
                }
            })
            .setNegativeButton(R.string.pref_cam, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    aSender.startActivityForResult(takePicture, 0);
                }
            });
    }

    public static AlertDialog.Builder getPreDialog(final Activity aSender, View aImView) {
        AlertDialog.Builder ab = new AlertDialog.Builder(aSender).setTitle(R.string.pref_g_pict_t)
            .setView(aImView)
            .setPositiveButton(R.string.pref_pict_change, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getImageDialog(aSender).show();
                }
            })
            .setNegativeButton(R.string.pref_pict_cancel, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        return ab;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings, target);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mOnActivityResult != null) {
            mOnActivityResult.doOnResult(requestCode, resultCode, data);
        }
    }

    public static class MainPref extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.mainsettings);
            PreferenceManager pm = getPreferenceManager();
            pm.findPreference("clearScoresTable")
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.warn)
                            .setMessage(R.string.sureScClear)
                            .setPositiveButton(R.string.ad_Yes, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DBAdapter dba = new DBAdapter(getActivity());
                                    dba.open();
                                    dba.removeAllRecords();
                                    dba.close();
                                }
                            })
                            .setNegativeButton(R.string.ad_No, null)
                            .create()
                            .show();
                        return true;
                    }
                });
        }
    }

    public static class GamePref extends PreferenceFragment {
        private ImageView mImageView;
        private PreferenceManager mPrefManager;
        private SettingsStringsManager mStrManager;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mStrManager = new SettingsStringsManager(getResources());
            mPrefManager = getPreferenceManager();
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            addPreferencesFromResource(R.xml.gamesettings2);
            String diff = mStrManager.get(R.string.S_Difficulty);
            final Preference df = mPrefManager.findPreference(diff);
            df.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference pref, Object newValue) {
                    String summ = getResources().getString(R.string.pref_diff_summsub)
                        + " "
                        + getResources().getStringArray(R.array.pref_diffs)[Integer.parseInt(newValue.toString()) - 1];
                    df.setSummary(summ);
                    return true;
                }
            });

            String summ = getResources().getString(R.string.pref_diff_summsub)
                + " "
                + getResources().getStringArray(R.array.pref_diffs)[Integer.parseInt(settings.getString(diff,
                    null)
                    .toString()) - 1];
            df.setSummary(summ);

            final String gameImage_Set = mStrManager.get(R.string.S_GameImage);
            final String impath = settings.getString(gameImage_Set, null);
            Preference image_pref = mPrefManager.findPreference(gameImage_Set);

            if (impath != null) {
                image_pref.setIcon(Utils.getDrawableFromPath(getResources(), impath));
            } else {
                image_pref.setSummary(R.string.pref_summ_imagech);
            }

            image_pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mImageView = new ImageView(getActivity());
                    mImageView.setImageDrawable(Utils.getDrawableFromPath(getResources(),
                        settings.getString(gameImage_Set, null)));
                    getPreDialog(getActivity(), mImageView).show();
                    return true;
                }
            });

            ((SettingsActivity) getActivity()).mOnActivityResult = new OnActivityResultListener() {

                @Override
                public void doOnResult(int arg1, int resultCode, Intent data) {
                    if (resultCode == RESULT_OK) {
                        Uri selectedImage = data.getData();
                        String path = "";

                        if (selectedImage == null) {
                            Bitmap img = (Bitmap) data.getExtras().get("data");

                            if (img == null) {
                                return;
                            }

                            selectedImage = Uri.parse(Media.insertImage(getActivity().getContentResolver(),
                                img,
                                "BarleyImage",
                                "Image for barley-break solving"));
                        }

                        path = Utils.getRealPathFromURI(getActivity().getContentResolver(),
                            selectedImage);
                        settings.edit().putString(gameImage_Set, path).commit();
                        Drawable res = Utils.getDrawableFromPath(getResources(), path);
                        mImageView.setImageDrawable(res);
                        mPrefManager.findPreference(gameImage_Set).setIcon(res);
                    }
                }
            };
        }
    }
}
