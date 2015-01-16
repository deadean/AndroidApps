package com.klampsit.barleybreak.game.field;

public class Coord<T> {
    public static final int DIFFERENT = 0;
    public static final int SAME_X = 1;
    public static final int SAME_Y = 2;

    public T y;
    public T x;

    public Coord(T y, T x) {
        this.y = y;
        this.x = x;
    }

    public Coord(Coord<T> copy) {
        this.y = copy.y;
        this.x = copy.x;
    }

    public int getSameWith(Coord<T> aAnother) {
        if (x == aAnother.x) {
            return SAME_X;
        }

        if (y == aAnother.y) {
            return SAME_Y;
        }

        return DIFFERENT;
    }
}