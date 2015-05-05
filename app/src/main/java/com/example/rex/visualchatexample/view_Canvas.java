package com.example.rex.visualchatexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Rex on 30/4/2015.
 */
public class view_Canvas extends View{

    Context context;
    Bitmap bgBitmap;
    Bitmap pathBitmap;
    Paint p = new Paint();
    ScaleGestureDetector scaleDetector;
    GestureDetector gestureDetector;

    //USE FOR D/D Shift
    final int DRAG = 0;
    final int DRAW = 1;
    int MODE = DRAG;

    //MATRIX FOR ZOOMING AND DRAGING
    Matrix matrix = new Matrix();

    //WHITE SPACE AT THE MARGIN
    final int MARGINAL_BLANK = 700;
    float currentZoom = 10f;
    //Image is for the bgBitmap; total is for the whole canvas
    int image_height, image_width, total_height, total_width;

    Canvas PaintLayer;
    Bitmap PaintOverlay;
    //Stored path
    ArrayList<PaintPath> mPaintpaths = new ArrayList<>();
    //Min. separation to save points
    float TOLERANCE = 1f;
    //Coordinate of last paint
    float px,py;
    //Current Drawing Path
    Path mPath = new Path();
    //Current Drawing Paint, with all path included
    PaintPath mPPath;
    //Current Paint
    Paint currPaint = new Paint();

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

        currPaint.setColor(Color.BLACK);
        currPaint.setStyle(Paint.Style.STROKE);
        currPaint.setStrokeWidth(5f);

        mPPath = new PaintPath(currPaint);
    }

    public void initPaintLayer(){
        PaintOverlay = Bitmap.createBitmap(total_width, total_height, Bitmap.Config.ARGB_8888);
        PaintLayer = new Canvas(PaintOverlay);
        PaintLayer.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        if (bgBitmap !=null) {
            canvas.drawBitmap(bgBitmap, matrix, p);
        }else{
            LoadFromDrawable(R.drawable.colortest);
        }

        canvas.drawBitmap(MakePathBitmap(), matrix, p);

        canvas.restore();
    }

    public void LoadFromDrawable(int id){
        Bitmap Source = BitmapFactory.decodeResource(getResources(),id);
        image_width = Source.getWidth();
        image_height = Source.getHeight();
        total_height =2 * MARGINAL_BLANK + image_height;
        total_width = 2 * MARGINAL_BLANK + image_width;

        bgBitmap = Bitmap.createBitmap(total_width, total_height, Bitmap.Config.ARGB_8888);
        Canvas temp= new Canvas(bgBitmap);
        temp.drawColor(Color.WHITE);
        temp.drawBitmap(Source,MARGINAL_BLANK, MARGINAL_BLANK, new Paint());

        matrix.preTranslate(-MARGINAL_BLANK,-MARGINAL_BLANK);
        initPaintLayer();

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
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mtouch_start(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if(((x-px)*(x-py)+(y-py)*(y-py))>TOLERANCE){
                    mtouch_move(x, y);
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                mtouch_up();
                invalidate();
                break;
        }
    }

    public void mtouch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        px = x;
        py = y;
    }
    public void mtouch_move(float x, float y) {
        mPath.quadTo(px, py, (x + px)/2, (y + py)/2);
        px = x;
        py = y;

    }

    public void mtouch_up() {
        mPath.lineTo(px, py);
        mPath = new Path();
        mPPath.paths.add(mPath);
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
        mPaintpaths.clear();
        mPPath.paths.clear();
        mPath.reset();
        invalidate();
    }

    //Shift Drag <--> Draw Mode, a.k.a. D/D
    public void DDShift(){
        MODE=MODE==DRAG?DRAW:DRAG;
    }

    public void ChangeColor(Color c){
        //TODO:finish this
    }

    //A class with a specified paint type, with a arraylist of path included
    public class PaintPath{

        Paint paint;
        public  ArrayList<Path> paths;

        public PaintPath(Paint p){
            paint = p;
            paths = new ArrayList<>();
        }

    }

    public Bitmap MakePathBitmap(){
        Canvas c = new Canvas(PaintOverlay);

        //Draw the current Path
        c.drawPath((mPath),currPaint);

        //Draw the stored Path WITH CURRENT paint
        for (Path p:mPPath.paths){
            c.drawPath((p),mPPath.paint);
        }

        //Draw stored Path WITH HISTORICAL paint
        for (PaintPath PP: mPaintpaths){
            for (Path p :PP.paths){
                c.drawPath((p),PP.paint);
            }
        }

        return PaintOverlay;
    }

}
