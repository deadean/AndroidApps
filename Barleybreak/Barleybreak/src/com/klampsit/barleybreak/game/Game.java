package com.klampsit.barleybreak.game;

import com.klampsit.barleybreak.game.field.Coord;
import com.klampsit.barleybreak.game.field.Field;
import com.klampsit.barleybreak.game.field.FieldRandomizer;
import com.klampsit.barleybreak.game.player.OnMoveListener;
import com.klampsit.barleybreak.game.player.Player;
import com.klampsit.barleybreak.game.player.PlayerActions;
import com.klampsit.barleybreak.utils.timer.GameTimer;
import com.klampsit.barleybreak.utils.timer.Ticker;
import com.klampsit.barleybreak.utils.timer.TickerListener;

public class Game {
    public static final int NOTEND = 0;
    public static final int FAILED = -1;
    public static final int SUCCESS = 1;
    
    private GameEndListener mOnEnd;
    private FieldRandomizer mRandomizer;
    private Field mField;
    private GamePreferences mGprefs;
    private Player mPlayer;
    private Statistics mStat;
    private Ticker mTicker;
    private GameTimer mGt;
    private boolean mFirst;
    private int mNotAtPlaceBound;

    public Game(final GamePreferences gprefs, Player aPlayer) {
        mGprefs = gprefs;
        mField = new Field(gprefs.getFieldsize());
        mGt = new GameTimer(gprefs.isTimeTrial() ? gprefs.getTime() : 0, gprefs.isTimeTrial());
        mFirst = true;
        setPlayer(aPlayer);
        mTicker = new Ticker(100);
        mTicker.addListener(new TickerListener() {

            @Override
            public void onGameTimerSignal() {
                int gval = gameEnded();

                if (gval != NOTEND && mRandomizer.isStopped()) {
                    mGt.setPaused(true);
                    mTicker.killTimer();
                    mPlayer.setOnResume(null);
                    mPlayer.setOnPause(null);
                    mPlayer.setOnMove(null);

                    if (mOnEnd != null) {
                        mOnEnd.onGameEnd(gval);
                    }
                }
            }
        });

        mStat = new Statistics(gprefs.getDiff(), mField.getElementCount(), mGt, mField);
        updateNotAtPlaceBound();
    }

    public void setPlayer(Player aPlayer) {
        mPlayer = aPlayer;
        mPlayer.setOnStart(new Runnable() {
            @Override
            public void run() {
                mTicker.startTimer();
                mGt.start();
                mPlayer.setOnPause(new Runnable() {

                    @Override
                    public void run() {
                        mGt.setPaused(true);
                    }
                });

                mPlayer.setOnResume(new Runnable() {

                    @Override
                    public void run() {
                        mGt.setPaused(false);
                    }
                });
            }
        });

        mPlayer.setOnMove(new OnMoveListener() {

            @Override
            public void onMove(Coord<Integer> moveTo, boolean fixed) {
                mField.moveManyFrom(moveTo, fixed);

                if (mFirst) {
                    mFirst = false;
                    mPlayer.action(PlayerActions.START);
                }
            }
        });

    }

    public int gameEnded() {
        if ((mGprefs.isTimeTrial()) && (mGt.getTimeMs() <= 0)) {
            return FAILED;
        }

        return win();
    }

    public int win() {
        return (mField.isSolved()) ? SUCCESS : NOTEND;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Statistics getStat() {
        return mStat;
    }

    public GamePreferences getGprefs() {
        return mGprefs;
    }

    public void setOnEnd(GameEndListener onEnd) {
        this.mOnEnd = onEnd;
    }

    public Field getField() {
        return mField;
    }

    public boolean gameIsStarted() {
        return !mFirst;
    }
    
    public Ticker getTicker() {
        return mTicker;
    }

    public GameTimer getGt() {
        return mGt;
    }
    
    public FieldRandomizer getRandomizer() {
        return mRandomizer;
    }
    
    public void setOnRandomizeFinished(Runnable onRandomizeFinish) {
        mRandomizer.setOnStop(onRandomizeFinish);
    }
    
    public void randomize(int stepCount, long aPeriod) {
        mRandomizer = new FieldRandomizer(mField, aPeriod);
        mRandomizer.mix(stepCount, mNotAtPlaceBound);
    }

    private void updateNotAtPlaceBound() {
        switch (Difficulty.values()[mGprefs.getDiff() - 1]) {
        case VERY_EASY:
            mNotAtPlaceBound = 0;
        case EASY:
            mNotAtPlaceBound = mField.getElementCount() / 4;
        case MEDIUM:
            mNotAtPlaceBound = mField.getElementCount() / 3;
        case HARD:
            mNotAtPlaceBound = mField.getElementCount() / 2;
        case IMPOSSIBLE:
            mNotAtPlaceBound = mField.getElementCount() * 3 / 4;
        }
    }
}
