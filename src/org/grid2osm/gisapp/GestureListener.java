package org.grid2osm.gisapp;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class GestureListener extends SimpleOnGestureListener {

	// Attributes for gesture recognition
	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;
	private MainActivity mActivity;

	public GestureListener(MainActivity activity) {
		this.mActivity = activity;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	/**
	 * @author Mirek Rusin
	 * @link http://stackoverflow.com/a/12938787
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float diffY = e2.getY() - e1.getY();
		float diffX = e2.getX() - e1.getX();
		if (Math.abs(diffX) > Math.abs(diffY)) {
			if (Math.abs(diffX) > SWIPE_THRESHOLD
					&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
				if (diffX > 0) {
					this.mActivity.onSwipeRight();
				} else {
					this.mActivity.onSwipeLeft();
				}
			}
		} else {
			if (Math.abs(diffY) > SWIPE_THRESHOLD
					&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
				if (diffY > 0) {
					this.mActivity.onSwipeBottom();
				} else {
					this.mActivity.onSwipeTop();
				}
			}
		}
		return false;
	}

}
