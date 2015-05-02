package com.example.rex.visualchatexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by Rex on 30/4/2015.
 */
public class view_Canvas extends View{

    Context context;
    Bitmap bitmap;
    Paint p;
    ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;
    boolean DragMode = true;

    Matrix matrix = new Matrix();

    float currentZoom = 1f;
    int image_height;
    int image_width;

    public view_Canvas(Context c){
        super(c);
        context = c;
        init();
    }
    public view_Canvas(Context c, AttributeSet attrs){
        super(c, attrs);
        context = c;
        init();
    }
    public view_Canvas(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        context = c;
        init();
    }

    public void init(){
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new ScrollListener());
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        p = new Paint();
        p.setColor(Color.BLACK);
        if (bitmap!=null) {
            canvas.drawBitmap(bitmap, matrix, p);
        }else{
            LoadFromDrawable(R.drawable.colortest);
        }

        canvas.restore();
    }

    public void LoadFromDrawable(int id){
        bitmap = BitmapFactory.decodeResource(getResources(),id);
        image_width = bitmap.getWidth();
        image_height = bitmap.getHeight();
        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if (DragMode) {
            scaleDetector.onTouchEvent(e);
            gestureDetector.onTouchEvent(e);
        }
        return true;
    }

    public class ScrollListener implements android.view.GestureDetector.OnGestureListener{

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e){
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e){
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e){
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e){
        }

        @Override
        public boolean onScroll(MotionEvent downEvent, MotionEvent currentEvent,
                                float distanceX, float distanceY) {
            matrix.postTranslate(-distanceX, -distanceY);
            invalidate();
            return true;
        }
    }

    public class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            currentZoom *= detector.getScaleFactor();

            transformationMatrix.postTranslate(-focusX, -focusY);
            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
            transformationMatrix.postTranslate(focusX, focusY);

            matrix.postConcat(transformationMatrix);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    public void reset(){
        matrix.reset();
        invalidate();
    }

}
