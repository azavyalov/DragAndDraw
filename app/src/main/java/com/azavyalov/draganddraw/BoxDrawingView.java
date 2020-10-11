package com.azavyalov.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";
    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    // Use in cases when view is creating in code
    public BoxDrawingView(Context context) {
        super(context);
        setSaveEnabled(true);
    }

    // Use in cases when view is creating by XML
    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Rectangles are drawn in red color
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Background is painting over with a grey color
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // Create new rectangle with base point then adding to array of rectangles
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate(); // For rectangle visibility
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                // Set current rectangle to null while cancelling touch
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                // Set current rectangle to null when there is no touch
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x + ", y=" + current.y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Filling background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.d(TAG, "Saving instance state");
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mBoxes = mBoxen;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        Log.d(TAG, "Restoring instance state");
        mBoxen = savedState.mBoxes;
    }

    private static class SavedState extends BaseSavedState {

        private List<Box> mBoxes;

        public SavedState(Parcelable superState) {
            super(superState);
            Log.d(TAG, "Saving parceble");
        }
    }
}
