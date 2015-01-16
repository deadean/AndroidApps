package com.klampsit.barleybreak.game.player;

import com.klampsit.barleybreak.game.field.Coord;

public interface OnMoveListener {
    public void onMove(Coord<Integer> aCell, boolean fixed);
}
