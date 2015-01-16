package com.klampsit.barleybreak.game;

import com.klampsit.barleybreak.game.field.Coord;

public class GamePreferences {
    private Coord<Integer> mFieldSize;
    private int mDiff;
    private boolean mArtGame = false;
    private boolean mTimeTrial = false;
    private long mTime;
    private Runnable mOnChange;

    public GamePreferences(Coord<Integer> sizePair, int diff, boolean isTimeTrial) {
        mDiff = diff;
        mFieldSize = sizePair;        

        switch (mFieldSize.x * mFieldSize.y) {
            case 2:
                mTime = 20;
                break;
            case 3:
                mTime = 90;
                break;
            case 4:
                mTime = 300; 
                break;
            case 5:
                mTime = 500;
                break;
            case 6:
                mTime = 800;
                break;
            case 7:
                mTime = 1000;
                break;
            case 8:
                mTime = 1200;
                break;
        }

        mTime *= 1000;
        mTimeTrial = isTimeTrial;
    }

    public Coord<Integer> getFieldsize() {
        return mFieldSize;
    }

    public int getDiff() {
        return mDiff;
    }

    public long getTime() {
        return mTime;
    }

    public boolean isArtificalgame() {
        return mArtGame;
    }

    public boolean isTimeTrial() {
        return mTimeTrial;
    }

    public void setOnChange(Runnable onChange) {
        mOnChange = onChange;
    }

    public void setArtificalgame(boolean artificalgame) {
        mArtGame = artificalgame;

        if (mOnChange != null) {
            mOnChange.run();
        }
    }   
}
