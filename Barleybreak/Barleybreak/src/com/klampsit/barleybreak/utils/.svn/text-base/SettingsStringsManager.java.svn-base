package com.klampsit.barleybreak.utils;

import com.klampsit.barleybreak.R;

import android.content.res.Resources;
import android.util.SparseArray;

public class SettingsStringsManager {
    private SparseArray<String> mStrings;
    
    public SettingsStringsManager(Resources aRes) {
        mStrings = new SparseArray<String>();
        put(R.string.S_Difficulty, aRes);
        put(R.string.S_GameImage, aRes);
        put(R.string.S_GameSet, aRes);
        put(R.string.S_MoveSpeed, aRes);
        put(R.string.S_Music, aRes);
        put(R.string.S_Size, aRes);
        put(R.string.S_TimeTrial, aRes);
        put(R.string.S_Username, aRes);
    }
    
    public String get(Integer aId) {
        return mStrings.get(aId);
    }
    
    public void put(Integer aId, Resources aRes) {
        mStrings.append(aId, aRes.getString(aId));
    }
}
