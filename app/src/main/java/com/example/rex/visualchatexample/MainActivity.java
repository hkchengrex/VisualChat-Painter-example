package com.example.rex.visualchatexample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Button LoadPic;
    Button reset;
    view_Canvas Board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       init();
    }

    public void init(){
        LoadPic = (Button) findViewById(R.id.button1);
        LoadPic.setOnClickListener(LoadPicture);
        reset = (Button) findViewById(R.id.button2);
        reset.setOnClickListener(Reset);
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

}
