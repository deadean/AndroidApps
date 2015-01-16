package com.klampsit.barleybreak.utils.timer;

import java.util.Calendar;

public class GameTimer {
	private long firsttime;
	private boolean isPaused = true;
	private boolean type = false;
	private long starttime;
	private long fixtime;

	public GameTimer(long starttime, boolean type) {
		this.type = type;
		this.firsttime = starttime;
	}

	public void start() {
		isPaused = false;
		resetTime();
	}	
	
	public void resetTime() {
		starttime = Calendar.getInstance().getTimeInMillis();
	}

	public long getTimeMs() {
		long time = notInverse();
		return (!type) ? time : (firsttime - time);
	}

	public void fixTime() {
		fixtime = notInverse();
	}

	public void setPaused(boolean paused) {
		if (paused) {
			fixTime();
		} else {
			if (fixtime != 0) {
				resetTime();
			}
		}

		isPaused = paused;
	}

	public boolean isPaused() {
		return isPaused;
	}
	
	public long notInverse(){
		return (isPaused) ? fixtime : (fixtime
				+ Calendar.getInstance().getTimeInMillis() - starttime);
	}
}
