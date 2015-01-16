package com.klampsit.barleybreak.game;

import com.klampsit.barleybreak.game.field.Field;
import com.klampsit.barleybreak.utils.timer.GameTimer;

public class Statistics {
    private Field mField;
    private int mDifficulty;
    private int mSize;
    private GameTimer mTime;

    public Statistics(int aDiff, int aSize, GameTimer aGt, Field aField) {
        mDifficulty = aDiff;
        mSize = aSize;
        mTime = aGt;
        mField = aField;
    }

    public int getDifficulty() {
        return mDifficulty;
    }

    public int getSize() {
        return mSize;
    }

    public int getSteps() {
        return mField.getPath().length;
    }

    public long getTime() {
        return mTime.notInverse();
    }
}
