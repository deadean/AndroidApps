package com.klampsit.barleybreak.game.field;

import java.util.ArrayList;

import com.klampsit.barleybreak.utils.Dir;

public final class Field implements Comparable<Field> {

    private ArrayList<Dir> mPath;
    private int[][] mField;
    private int[][] mSolved;
    private Coord<Integer> mFreeCoord;
    private OnExchangeListener mOnExchange;

    /**
     * Default constructor
     * */
    private Field() {
        mPath = new ArrayList<Dir>();
        mField = null;
        mSolved = null;
        mFreeCoord = null;
        mOnExchange = null;
    }

    /**
     * Requires size pair through
     * 
     * @param aSizePair {@link Coord} object with x - column count, and y
     * - row count
     */
    public Field(Coord<Integer> aSizePair) {
        this(aSizePair.x, aSizePair.y);
    }

    /**
     * Fixed size constructor
     * 
     * @param aXSize Column count
     * @param aYSize Rows count
     */
    public Field(int aXSize, int aYSize) {
        this();        
        mField = getSolved(aXSize, aYSize);
        init();
    }

    /**
     * Copy constructor
     */
    public Field(Field fld) {
        this();
        int[][] fieldCopy = new int[fld.mField.length][fld.mField[0].length];

        for (int i = 0; i < fld.mField.length; ++i) {
            System.arraycopy(fld.mField[i], 0, fieldCopy[i], 0, fld.mField[0].length);
        }

        mField = fieldCopy;

        for (Dir e : fld.mPath) {
            mPath.add(e);
        }

        init();
    }

    /**
     * Initializations which need to be done after constructor
     */
    private void init() {
        mSolved = getSolved(mField.length, mField[0].length);
        mFreeCoord = new Coord<Integer>(mField.length - 1, mField[0].length - 1);
    }

    /**
     * Gives the row/col of cells that are moved from argument cell to zero cell
     * @param aCell from which
     * @return List of coordinates of moving cells
     */    
    public ArrayList<Coord<Integer>> getLineFrom(Coord<Integer> aCell) {
        ArrayList<Coord<Integer>> crds = new ArrayList<Coord<Integer>>();

        switch (mFreeCoord.getSameWith(aCell)) {
        case Coord.SAME_X:
            int n = aCell.y - mFreeCoord.y;

            for (int i = 0; i < Math.abs(n); ++i) {
                crds.add(new Coord<Integer>(aCell.y + ((n < 0) ? i : -i), aCell.x));
            }

            break;
        case Coord.SAME_Y:
            n = aCell.x - mFreeCoord.x;

            for (int i = 0; i < Math.abs(n); ++i) {
                crds.add(new Coord<Integer>(aCell.y, aCell.x + ((n < 0) ? i : -i)));
            }

            break;
        }

        return crds;
    }

    
    /**
     * Gives movable cells     
     * @return ArrayList of movable cells coordinates 
     */
    public ArrayList<Coord<Integer>> getMovableCells() {
        ArrayList<Coord<Integer>> crds = new ArrayList<Coord<Integer>>();
        Coord<Integer> tempCell = new Coord<Integer>(0, mFreeCoord.x);

        for (tempCell.y = 0; tempCell.y < mField.length; ++tempCell.y) {
            if (tempCell.y != mFreeCoord.y) {
                crds.add(new Coord<Integer>(tempCell));
            }
        }

        for (tempCell.x = 0, tempCell.y = mFreeCoord.y; tempCell.x < mField[0].length; ++tempCell.x) {
            if (tempCell.x != mFreeCoord.x) {
                crds.add(new Coord<Integer>(tempCell));
            }
        }

        return crds;
    }

    /**
     * Doing moves specified by path argument
     * @param path Array of moves, which must be done
     * @param fixed To fix or not this move in this Field path-story
     * */    
    public void doMoves(Dir[] path, boolean fixed) {
        if (path != null) {
            for (Dir e : path) {
                move(e, fixed);
            }
        }
    }

    /**
     * Gives coordinate location of specified number value
     * @param num For what number locate
     * @return Location coordinate
     */
    public Coord<Integer> getCoordForNum(int num) {
        if (num == 0 && mFreeCoord != null) {
            return mFreeCoord;
        }

        for (int i = 0; i < mField.length; i++) {
            for (int j = 0; j < mField[0].length; j++) {
                if (mField[i][j] == num) {
                    return new Coord<Integer>(i, j);
                }
            }
        }

        return null;
    }

    /**
     * Gives number from field for specified location
     * @param xy Cell coordinate
     * @return 
     */
    public int getNumAt(Coord<Integer> xy) {
        return xy == null ? 0 : mField[xy.y][xy.x];
    }

    /**
     * Gives possible directions for current zero element position
     * @return List of directions
     */
    public ArrayList<Dir> getPossibleDirs() {
        ArrayList<Dir> res = new ArrayList<Dir>();

        for (Dir direct : Dir.values()) {
            switch (direct) {
            case LEFT:
                if (mFreeCoord.x > 0) {
                    res.add(direct);
                }

                break;
            case RIGHT:
                if (mFreeCoord.x < mField[0].length - 1) {
                    res.add(direct);
                }

                break;
            case UP:
                if (mFreeCoord.y > 0) {
                    res.add(direct);
                }

                break;
            case DOWN:
                if (mFreeCoord.y < mField.length - 1) {
                    res.add(direct);
                }

                break;
            }
        }

        return res;
    }

    /**
     * 
     * @param direct Move direction
     * @param fixed Fixed or not in this field path-story
     * @return number to which move was done 
     */
    public int move(Dir direct, boolean fixed) {
        Coord<Integer> c = new Coord<Integer>(mFreeCoord);

        switch (direct) {
        case LEFT:
            c.x--;
            break;
        case RIGHT:
            c.x++;
            break;
        case UP:
            c.y--;
            break;
        case DOWN:
            c.y++;
            break;
        }

        if (fixed) {
            mPath.add(direct);
        }

        return exchangeFreeWith(c);
    }

    public int exchangeFreeWith(Coord<Integer> aCoord) {
        int num = mField[mFreeCoord.y][mFreeCoord.x] = mField[aCoord.y][aCoord.x];
        mField[aCoord.y][aCoord.x] = 0;
        mFreeCoord = aCoord;
        return num;
    }

    public void moveManyFrom(Coord<Integer> aCell, boolean aFixed) {
        ArrayList<Integer> cells = new ArrayList<Integer>();

        switch (mFreeCoord.getSameWith(aCell)) {
        case Coord.SAME_X:
            int n = aCell.y - mFreeCoord.y;

            for (int i = 0; i < Math.abs(n); ++i) {
                cells.add(move(n > 0 ? Dir.DOWN : Dir.UP, aFixed));
            }

            break;
        case Coord.SAME_Y:
            n = aCell.x - mFreeCoord.x;

            for (int i = 0; i < Math.abs(n); ++i) {
                cells.add(move(n > 0 ? Dir.RIGHT : Dir.LEFT, aFixed));
            }

            break;
        }

        if (mOnExchange != null) {
            mOnExchange.applyMove(cells);
        }
    }

    public int notAtPlaceCount() {
        int s = 0;

        for (int i = 0; i < mField.length; ++i) {
            for (int j = 0; j < mField[0].length; ++j) {
                if (mField[i][j] != mSolved[i][j]) {
                    ++s;
                }
            }
        }

        return s;
    }

    @Override
    public int compareTo(Field another) {
        return another.notAtPlaceCount() - this.notAtPlaceCount();
    }

    public boolean isSame(Field fld) {
        for (int i = 0; i < mField.length; i++) {
            for (int j = 0; j < mField[0].length; j++) {
                if (mField[i][j] != fld.mField[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isSolved() {
        return notAtPlaceCount() == 0;
    }

    public void setField(int[][] fld) {
        mField = fld;
        mFreeCoord = getCoordForNum(0);
    }

    public int[][] getField() {
        int[][] res = new int[mField.length][mField[0].length];

        for (int i = 0; i < mField.length; i++) {
            System.arraycopy(mField[i], 0, res[i], 0, mField[0].length);
        }

        return res;
    }

    public Dir removeLast() {
        return (!mPath.isEmpty()) ? mPath.remove(mPath.size() - 1) : null;
    }

    public boolean hasEmptyPath() {
        return mPath.isEmpty();
    }

    public Dir[] getPath() {
        return mPath.toArray(new Dir[mPath.size()]);
    }

    public Dir getLast() {
        return mPath.get(mPath.size() - 1);
    }

    public void setOnExchange(OnExchangeListener onExchange) {
        this.mOnExchange = onExchange;
    }

    public Coord<Integer> getSizePair() {
        return new Coord<Integer>(mField[0].length, mField.length);
    }

    public int getElementCount() {
        return mField.length * mField[0].length;
    }

    public static int[][] getSolved(int aXSize, int aYSize) {
        int[][] res = new int[aYSize][aXSize];
        int k = 1;

        for (int i = 0; i < aYSize; i++) {
            for (int j = 0; j < aXSize; j++) {
                res[i][j] = k++;
            }
        }

        res[aYSize - 1][aXSize - 1] = 0;
        return res;
    }

    public Dir getDirection(Coord<Integer> from) {
        return getDirection(from, mFreeCoord);
    }

    public static Dir getDirection(Coord<Integer> from, Coord<Integer> to) {
        switch (from.getSameWith(to)) {
        case Coord.SAME_X:
            return from.y < to.y ? Dir.DOWN : Dir.UP;
        case Coord.SAME_Y:
            return from.x < to.x ? Dir.RIGHT : Dir.LEFT;
        }

        return null;
    }
}
