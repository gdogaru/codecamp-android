/*
 * Copyright (c) 2019 Gabriel Dogaru - gdogaru@gmail.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.gdogaru.codecamp.view.agenda.calendar.component;

import android.view.ScaleGestureDetector;


public class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    float startingSpan;
    float endSpan;
    float startFocusX;
    float startFocusY;
    private ZoomableRelativeLayout mZoomableRelativeLayout;

    public OnPinchListener(ZoomableRelativeLayout mZoomableRelativeLayout) {
        this.mZoomableRelativeLayout = mZoomableRelativeLayout;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        startingSpan = detector.getCurrentSpan();
        startFocusX = detector.getFocusX();
        startFocusY = detector.getFocusY();
        return true;
    }

    public boolean onScale(ScaleGestureDetector detector) {
        mZoomableRelativeLayout.scale(detector.getCurrentSpan() / startingSpan, startFocusX, startFocusY);
        return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
        mZoomableRelativeLayout.restore();
    }
}
