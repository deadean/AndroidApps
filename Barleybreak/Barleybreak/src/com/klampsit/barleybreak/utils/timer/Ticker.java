package com.klampsit.barleybreak.utils.timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class Ticker {
    private long mPeriod;
	private final Timer mClockTimer;
	private final Handler mTimerHandler;
	private List<TickerListener> mClockListener;
	
	private class Task extends TimerTask {
		public void run() {
			mTimerHandler.sendEmptyMessage(0);
		}
	}
	
	@SuppressLint("HandlerLeak")
    public Ticker(long t) {
		mTimerHandler = new Handler() {
			public void handleMessage(Message msg) {
				timerSignal();
			}
		};
		
		mClockListener = new ArrayList<TickerListener>();
		mClockTimer = new Timer();
		mPeriod = t;
	}

	public void startTimer() {
	    mClockTimer.schedule(new Task(), mPeriod, mPeriod);	    
	}
	
	public void killTimer() {
		mClockTimer.cancel();
	}

	public void addListener(TickerListener listener) {
		mClockListener.add(listener);
	}
	
	private synchronized void timerSignal() {
        for (TickerListener listener : mClockListener) {
            listener.onGameTimerSignal();
        }
    }
}
