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
    Paint p = new Paint();
    ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;

    final int DRAG = 0;
    final int DRAW = 1;
    int MODE = DRAG;

    Matrix matrix = new Matrix();

    final int MARGINAL_BLANK = 700;
    float currentZoom = 1f;
    int image_height;
    int image_width;
    int total_height;
    int total_width;

    Color color;
    Canvas PaintLayer;
    Bitmap b;

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
        b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        PaintLayer = new Canvas(b);
        PaintLayer.drawColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        if (bitmap!=null) {
            canvas.drawBitmap(bitmap, matrix, p);
        }else{
            LoadFromDrawable(R.drawable.colortest);
        }
        canvas.drawBitmap(b,matrix,p);

        canvas.restore();
    }

    public void LoadFromDrawable(int id){
        Bitmap Source = BitmapFactory.decodeResource(getResources(),id);
        image_width = Source.getWidth();
        image_height = Source.getHeight();
        total_height =2 * MARGINAL_BLANK + image_height;
        total_width = 2 * MARGINAL_BLANK + image_width;

        bitmap = Bitmap.createBitmap(total_width, total_height, Bitmap.Config.ARGB_8888);
        Canvas temp= new Canvas(bitmap);
        temp.drawColor(Color.WHITE);
        temp.drawBitmap(Source,MARGINAL_BLANK, MARGINAL_BLANK, new Paint());

        matrix.preTranslate(-MARGINAL_BLANK,-MARGINAL_BLANK);

        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if (MODE==DRAG) {
            scaleDetector.onTouchEvent(e);
            gestureDetector.onTouchEvent(e);
        }else{
            PaintHandler(e);
        }
        return true;
    }

    public void PaintHandler(MotionEvent e){

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

    public void DDShift(){
        MODE = MODE==DRAG?DRAW:DRAG;
    }

    public void ChangeColor(Color c){
        color =c ;
    }

}
