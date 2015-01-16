package com.klampsit.barleybreak.game.player;

import java.util.ArrayList;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.klampsit.barleybreak.R;
import com.klampsit.barleybreak.game.field.Coord;
import com.klampsit.barleybreak.gui.GameField;
import com.klampsit.barleybreak.utils.Dir;
import com.klampsit.barleybreak.utils.Utils;

public class Player implements OnTouchListener, OnClickListener {
    private Coord<Float> mStartPos = new Coord<Float>((float) 0, (float) 0);
    private Coord<Float> mStartMove = new Coord<Float>((float) 0, (float) 0);
    private boolean mMoveP = false;
    private double mMoveFactor;

    private Runnable onUndo;
    private Runnable onExit;
    private OnMoveListener onMove;
    private Runnable onPause;
    private Runnable onStart;
    private Runnable onResume;

    public GameField mField;

    public Player(GameField aField, double aSpeed) {
        mField = aField;
        mMoveFactor = aSpeed;
    }

    @SuppressWarnings("unchecked")
    public void action(PlayerActions pAct, Object... comData) {
        switch (pAct) {
        case EXIT:
            if (onExit != null) {
                onExit.run();
            }

            break;
        case MOVE:
            if (onMove != null) {
                onMove.onMove((Coord<Integer>) comData[0], true);
            }

            break;
        case PAUSE:
            if (onPause != null) {
                onPause.run();
            }

            break;
        case RESUME:
            if (onResume != null) {
                onResume.run();
            }

            break;
        case START:
            if (onStart != null) {
                onStart.run();
            }

            break;
        case UNDO:
            if (onUndo != null) {
                onUndo.run();
            }

            break;

        }
    }

    public void setOnStart(Runnable onStart) {
        this.onStart = onStart;
    }

    public void setOnMove(OnMoveListener onMove) {
        this.onMove = onMove;
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    public void setOnPause(Runnable onPause) {
        this.onPause = onPause;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnUndo(Runnable onUndo) {
        this.onUndo = onUndo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        v.bringToFront();

        if (v.getTag(R.id.dir_tag) == null) {
            return false;
        }

        Coord<Integer> coord = (Coord<Integer>) v.getTag(R.id.coord_tag);
        ArrayList<View> mustMoveViews = mField.getViewsThatMustToMove(coord);
        Dir dir = (Dir) v.getTag(R.id.dir_tag);

            switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartPos.x = v.getX();
                mStartPos.y = v.getY();
                mStartMove.x = e.getRawX();
                mStartMove.y = e.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                switch (dir) {
                case DOWN:
                    float div1 = e.getRawY() - mStartMove.y;

                    if (div1 < 0) {
                        v.setY(mStartPos.y);
                        break;
                    } else if (Math.abs(div1) > v.getHeight()) {
                        v.setY(mStartPos.y + v.getHeight());
                        break;
                    }

                    v.setY(mStartPos.y + div1);
                    mMoveP = Math.abs(div1) >= mMoveFactor * v.getHeight();
                    break;
                case RIGHT:
                    div1 = e.getRawX() - mStartMove.x;

                    if (div1 < 0) {
                        v.setX(mStartPos.x);
                        break;
                    } else if (Math.abs(div1) > v.getWidth()) {
                        v.setX(mStartPos.x + v.getWidth());
                        break;
                    }

                    v.setX(mStartPos.x + div1);
                    mMoveP = Math.abs(div1) >= mMoveFactor * v.getWidth();
                    break;
                case LEFT:
                    div1 = e.getRawX() - mStartMove.x;

                    if (div1 > 0) {
                        v.setX(mStartPos.x);
                        break;
                    } else if (Math.abs(div1) > v.getWidth()) {
                        v.setX(mStartPos.x - v.getWidth());
                        break;
                    }

                    v.setX(mStartPos.x + div1);
                    mMoveP = Math.abs(div1) >= mMoveFactor * v.getWidth();
                    break;
                case UP:
                    div1 = e.getRawY() - mStartMove.y;

                    if (div1 > 0) {
                        v.setY(mStartPos.y);
                        break;
                    } else if (Math.abs(div1) > v.getHeight()) {
                        v.setY(mStartPos.y - v.getHeight());
                        break;
                    }

                    v.setY(mStartPos.y + div1);
                    mMoveP = Math.abs(div1) >= mMoveFactor * v.getHeight();
                    break;
                }
                
                for (int i = 0; i < mustMoveViews.size(); ++i) {
                    View oV = mustMoveViews.get(i);
                    oV.setX(v.getX() + v.getWidth() * (i + 1));
                    oV.setY(v.getY() + v.getHeight() * (i + 1));
                }

                break;
            case MotionEvent.ACTION_UP:
                v.setX(mStartPos.x);
                v.setY(mStartPos.y);

                if (mMoveP) {
                    action(PlayerActions.MOVE, coord);
                    mMoveP = false;
                }

                break;
            }

        return true;
    }

    @Override
    public void onClick(View v) {
        v.bringToFront();

        if (v.getTag() == null) {
            return;
        }

        action(PlayerActions.MOVE, Utils.another((Dir) v.getTag()));
    }
}