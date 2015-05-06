package com.example.rex.visualchatexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    Button LoadPic;
    Button reset;
    Button ddshift;
    Button pick;
    Button erase;
    Button save;
    view_Canvas Board;
    Context context;
    int currColor = Color.BLACK;
    int currWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       init();
    }

    public void init(){
        context=this;

        LoadPic = (Button) findViewById(R.id.button1);
        LoadPic.setOnClickListener(LoadPicture);
        reset = (Button) findViewById(R.id.button2);
        reset.setOnClickListener(Reset);
        ddshift = (Button) findViewById(R.id.button3);
        ddshift.setOnClickListener(DDShift);
        pick = (Button) findViewById(R.id.button4);
        pick.setOnClickListener(Pick);
        erase = (Button) findViewById(R.id.button5);
        erase.setOnClickListener(EraseSE);
        save = (Button) findViewById(R.id.button6);
        save.setOnClickListener(SaveBitmap);
        Board =(view_Canvas) findViewById(R.id.canvasboard);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener LoadPicture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            performFileSearch();
        }
    };

    View.OnClickListener Reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Board.reset();
        }
    };

    View.OnClickListener DDShift = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Board.DDShift();
        }
    };

    View.OnClickListener Pick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Pick: ");
            View view = getLayoutInflater().inflate(R.layout.dialog_colorpicker, null);
            final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
            final SeekBar WidthSeek = (SeekBar) view.findViewById(R.id.seekBar);
            final SVBar svBar = (SVBar) view.findViewById(R.id.svbar);
            picker.addSVBar(svBar);

            WidthSeek.setProgress(currWidth);
            picker.setColor(currColor);

            picker.setShowOldCenterColor(false);
            builder.setView(view);

            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    currWidth = WidthSeek.getProgress()*3;
                    currColor = picker.getColor();

                    Paint p = new Paint();
                    p.setStyle(Paint.Style.STROKE);
                    p.setColor(currColor);
                    p.setStrokeWidth(currWidth);

                    Board.ChangePaint(p);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    };

    View.OnClickListener EraseSE = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Board.EDShift();
        }
    };

    View.OnClickListener SaveBitmap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bitmap b = Board.bgBitmap;
            Canvas c = new Canvas(b);
            c.drawBitmap(Board.PaintOverlay,0,0,Board.p);
            new SaveBitMapTask().execute(b);
        }
    };

    final int READ_REQUEST_CODE = 25;
    @SuppressLint("NewApi")
    public void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                new LoadBitMapTask().execute(uri);
            }
        }
    }

    private class LoadBitMapTask extends AsyncTask<Uri,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(Uri... params) {
            Bitmap image;
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(params[0], "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                image = DissloveBitmap(fileDescriptor);
                parcelFileDescriptor.close();
            }catch (IOException e){
                return null;
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Board.LoadFromDrawable(bitmap);
        }
    }

    private class SaveBitMapTask extends AsyncTask<Bitmap,Void,Void>{

        @Override
        protected Void doInBackground(Bitmap... params) {
            FileOutputStream out = null;
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/VisualChat";
            File dir = new File(file_path);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            File file = new File(dir, "sketch" + dateFormat.format(cal.getTime()) + ".png");
            try {
                out = new FileOutputStream(file);
                params[0].compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public Bitmap DissloveBitmap(FileDescriptor f){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(f,null,options);
        int size = options.outHeight*options.outWidth;
        int reqsize = Board.getWidth()*Board.getHeight()*5;
        int SampleSize =1;
        System.out.println(size);
        while (size>reqsize){
            size = size/2;
            SampleSize *= 2;
        }
        System.out.println(size);
        options.inJustDecodeBounds = false;
        options.inSampleSize = SampleSize;
        return BitmapFactory.decodeFileDescriptor(f,null,options);
    }

}
