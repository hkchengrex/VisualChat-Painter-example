package com.example.rex.visualchatexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;


public class MainActivity extends ActionBarActivity {

    Button LoadPic;
    Button reset;
    Button ddshift;
    Button pick;
    Button erase;
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
            Board.LoadFromDrawable(R.drawable.colortest);
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

}
