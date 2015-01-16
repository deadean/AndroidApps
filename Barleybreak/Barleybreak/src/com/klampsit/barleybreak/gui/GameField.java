package com.klampsit.barleybreak.gui;

import java.util.ArrayList;
import java.util.Random;

import com.klampsit.barleybreak.R;
import com.klampsit.barleybreak.game.field.Coord;
import com.klampsit.barleybreak.game.field.Field;
import com.klampsit.barleybreak.game.field.OnExchangeListener;
import com.klampsit.barleybreak.utils.Dir;
import com.klampsit.barleybreak.utils.Utils;
import com.klampsit.barleybreak.utils.timer.Ticker;
import com.klampsit.barleybreak.utils.timer.TickerListener;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameField extends RelativeLayout implements OnExchangeListener {
    private Field mField;
    private View[] mChilds;
    private Bitmap[] mOldBmps;
    private Coord<Integer> mTargetSize;
    private int mExchangeSpeed;
    private ArrayList<Animator> mAnimatorQueue;
    private Ticker mAnimator;

    {
        mAnimatorQueue = new ArrayList<Animator>();
        mAnimator = new Ticker(1500);
        mAnimator.addListener(new TickerListener() {

            @Override
            public void onGameTimerSignal() {
                synchronized (mAnimatorQueue) {
                    if (!mAnimatorQueue.isEmpty()) {
                        AnimatorSet animSet = new AnimatorSet();
                        animSet.playSequentially(mAnimatorQueue.toArray(new Animator[0]));
                        animSet.start();
                        mAnimatorQueue.clear();                        
                    }
                }
            }
        });
        mAnimator.startTimer();
    }

    public GameField(Context context) {
        super(context);
    }

    public GameField(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void updateCells() {
        for (View v : mChilds) {
            v.setTag(R.id.dir_tag, null);
        }

        Coord<Integer> size = mField.getSizePair();

        for (int i = 0; i < size.y; ++i) {
            for (int j = 0; j < size.x; ++j) {
                Coord<Integer> place = new Coord<Integer>(i, j);
                getView(place).setTag(place);
            }
        }

        ArrayList<Coord<Integer>> possible = mField.getMovableCells();

        for (Coord<Integer> crd : possible) {
            getView(crd).setTag(R.id.dir_tag, mField.getDirection(crd));
        }
    }

    private void makeFromField() {
        int[][] fld = mField.getField();

        for (int[] row : fld) {
            for (int e : row) {
                TextView textV = new TextView(getContext(), null, R.style.ItemText);
                textV.setBackgroundResource(R.drawable.rectshape);
                setItemTextParameters(textV, e + "");
                addView(textV);
                mChilds[e] = textV;
            }
        }
    }

    public ArrayList<View> getViewsThatMustToMove(Coord<Integer> aCoord) {
        ArrayList<Coord<Integer>> possible = mField.getLineFrom(aCoord);
        ArrayList<View> resultViews = new ArrayList<View>();

        for (Coord<Integer> coord : possible) {
            resultViews.add(getView(coord));
        }

        return resultViews;
    }

    public Coord<Float> getOriginalPos(Coord<Integer> crd) {
        Coord<Float> size = new Coord<Float>((float) getView(crd).getHeight(),
            (float) getView(crd).getWidth());
        return new Coord<Float>(crd.y * size.y, crd.x * size.x);
    }

    public View getView(Coord<Integer> cell) {
        return mChilds[mField.getNumAt(cell)];
    }

    public void changeForDiff(int aDiff) {
        boolean puzzleGameType = mOldBmps != null;

        if (aDiff > 2 && puzzleGameType) {
            for (View v : mChilds) {
                ((TextView) v).setText(null);
            }
        }

        if (aDiff > 3) {
            Coord<Integer> zero = mField.getCoordForNum(0);
            getView(zero).getBackground().setAlpha(255);
        }

        if (aDiff > 4 && puzzleGameType) {
            int contentSize = 30;
            Random rand = new Random();
            Coord<Integer> size = mField.getSizePair();

            for (int i = 0; i < Math.round(mChilds.length * contentSize / 100); i++) {
                View v = getView(new Coord<Integer>(rand.nextInt(size.x - 1),
                    rand.nextInt(size.y - 1)));
                LayerDrawable back = (LayerDrawable) v.getBackground();
                Bitmap bmp = ((BitmapDrawable) back.getDrawable(back.getNumberOfLayers() - 1)).getBitmap();
                bmp = Utils.rotate(bmp, (1 + rand.nextInt(2)) * 90);
                back.setDrawableByLayerId(back.getId(back.getNumberOfLayers() - 1),
                    new BitmapDrawable(getResources(), bmp));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void animateEnding() {
        for (int i = 0; i < mChilds.length; i++) {
            ((TextView) mChilds[i]).setText(null);
            LayerDrawable ld = (LayerDrawable) mChilds[i].getBackground();
            ld.setAlpha(255);
            TransitionDrawable tDraw = new TransitionDrawable(new Drawable[] {
                ld.getDrawable(ld.getNumberOfLayers() - 1), ld });
            mChilds[i].setBackgroundDrawable(tDraw);
            tDraw.startTransition(100);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void applyMove(final ArrayList<Integer> viewNums) {
        final Dir dir = (Dir) mChilds[viewNums.get(0)].getTag(R.id.dir_tag);
        final Dir antiDir = Utils.another(dir);
        ArrayList<Animator> anims = new ArrayList<Animator>();

        for (int v : viewNums) {
            View view = mChilds[v];
            anims.add(getAnimatorFor(view, dir, 1));
        }

        final View vTo = mChilds[0];
        anims.add(getAnimatorFor(vTo, antiDir, viewNums.size()));
        AnimatorSet animSet = new AnimatorSet();
        /* AnimatorListener al = new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (int v : viewNums) {
                    updatePositionsFor(v, dir, 1);
                }

                updatePositionsFor(0, antiDir, viewNums.size());                
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        };

        animSet.addListener(al);*/
        animSet.setDuration(mExchangeSpeed);
        animSet.playTogether(anims);
        mAnimatorQueue.add(animSet);
        updateCells();
    }

    public void updatePositionsFor(int vNum, Dir dir, int multiplier) {
        View v = mChilds[vNum];
        Coord<Integer> viewSize = getViewsSize();
        viewSize.x *= multiplier;
        viewSize.y *= multiplier;
        @SuppressWarnings("unchecked")
        Coord<Float> orPos = getOriginalPos((Coord<Integer>) v.getTag(R.id.coord_tag));

        switch (dir) {
        case UP:
            v.setY(orPos.y - viewSize.y);
            break;
        case DOWN:
            v.setY(orPos.y + viewSize.y);
            break;
        case LEFT:
            v.setX(orPos.x - viewSize.x);
            break;
        case RIGHT:
            v.setX(orPos.x + viewSize.x);
            break;
        }
    }

    public Animator getAnimatorFor(View v, Dir dir, int multiplier) {
        Coord<Integer> viewSize = getViewsSize();
        viewSize.x *= multiplier;
        viewSize.y *= multiplier;

        switch (dir) {
        case DOWN:
            return ObjectAnimator.ofFloat(v, "y", v.getY() + viewSize.y);
        case UP:
            return ObjectAnimator.ofFloat(v, "y", v.getY() - viewSize.y);
        case LEFT:
            return ObjectAnimator.ofFloat(v, "x", v.getX() - viewSize.x);
        case RIGHT:
            return ObjectAnimator.ofFloat(v, "x", v.getX() + viewSize.x);
        default:
            return null;
        }
    }

    public void connectWithField(final Field fld) {
        mField = fld;
        mChilds = new View[mField.getElementCount()];
        makeFromField();
        updateCells();
        fld.setOnExchange(this);
    }

    public void imagingTable(int aWidth, int aHeight, int aL, String aImPath) {
        Coord<Integer> size = mField.getSizePair();
        mOldBmps = null;

        boolean puzzleGameType = aImPath != null;
        if (puzzleGameType) {
            Bitmap image = ((BitmapDrawable) Utils.getDrawableFromPath(getResources(), aImPath)).getBitmap();
            float shadow = getResources().getDimension(R.dimen.shadow);
            image = Utils.scaleBitmap(image, aWidth - (size.x * shadow), aHeight
                - (size.y * shadow));
            mOldBmps = Utils.divideToParts(image, size.x, size.y);
        }

        int h = Math.round((float) aHeight / size.y);
        int w = Math.round((float) aWidth / size.x);
        mTargetSize = new Coord<Integer>(h, w);
        int k = 1;

        for (int i = 0; i < size.y; ++i) {
            for (int j = 0; j < size.x; ++j) {
                View v = mChilds[k];

                if (puzzleGameType) {
                    BitmapDrawable image = new BitmapDrawable(getResources(), mOldBmps[k]);
                    LayerDrawable draw = (LayerDrawable) v.getBackground();
                    draw.setDrawableByLayerId(draw.getId(draw.getNumberOfLayers() - 1), image);

                    if (k == 0) {
                        draw.setAlpha(0);
                    }
                }

                if (++k == size.x * size.y) {
                    k = 0;
                }

                v.setMinimumHeight(h);
                v.setMinimumWidth(w);
                v.setY(i * h);
                v.setX(j * w);
            }
        }
    }

    public void setItemTextParameters(TextView aTextV, String aText) {
        aTextV.setGravity(Gravity.CENTER);
        aTextV.setText(aText);
        aTextV.setShadowLayer(15f, 1, 1, Color.WHITE);
        aTextV.setTextAppearance(getContext(), R.style.ItemText);
    }

    public void makePositions() {
        Coord<Integer> size = mField.getSizePair();

        for (int i = 0; i < size.y; ++i) {
            for (int j = 0; j < size.x; ++j) {
                View cur = getView(new Coord<Integer>(i, j));
                cur.setX(j * cur.getWidth());
                cur.setY(i * cur.getHeight());
            }
        }
    }

    public void controlActivate(OnTouchListener otl) {
        for (View v : mChilds) {
            v.setOnTouchListener(otl);
        }
    }

    public void controlDeactivate() {
        for (View v : mChilds) {
            v.setOnTouchListener(null);
        }
    }

    public void setExchangeSpeed(int aMSeconds) {
        mExchangeSpeed = aMSeconds;
    }

    public int getExchangeSpeed() {
        return mExchangeSpeed;
    }

    private Coord<Integer> getViewsSize() {
        return mTargetSize;
    }

    @Override
    protected void finalize() throws Throwable {
        mAnimator.killTimer();
        mAnimator = null;
        super.finalize();
    }
}
