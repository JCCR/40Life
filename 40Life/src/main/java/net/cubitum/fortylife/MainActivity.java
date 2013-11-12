package net.cubitum.fortylife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //service objects
    private Vibrator mVibrator;

    //ui states
    private boolean mTwoFingersTapped;
    private boolean mInitialized = false;

    //preferences
    private boolean mVibrate = false;
    private int mStartingLife;

    //objects
    private LifeCounter mainLifeCounter;

    //view objects
    private Button btn_mainlife;



    private void vibrate(){
        vibrate(75);
    }
    private void vibrate(int length){
        if(mVibrate){
            mVibrator.vibrate(length);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //initialize objects
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        btn_mainlife = (Button)findViewById(R.id.btn_mainlife);

        //--

        //preference loading
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean powersave = sharedPrefs.getBoolean("power_save_mode",false);

        //power save theme change
        if(powersave){

            LinearLayout layout_mainlife = (LinearLayout) findViewById(R.id.layout_mainlife);
            layout_mainlife.setBackgroundResource(R.color.lifelayout2_background);
        }else{
            LinearLayout layout_mainlife = (LinearLayout) findViewById(R.id.layout_mainlife);
            layout_mainlife.setBackgroundResource(R.color.lifelayout1_background);
        }

        mVibrate = sharedPrefs.getBoolean("vibrate",false);
        //--

        //initialize app ui
        if(!mInitialized){
            mStartingLife = Integer.parseInt(sharedPrefs.getString("life_total", "40"));
            mainLifeCounter = new LifeCounter(mStartingLife);
            btn_mainlife.setText(String.valueOf(mainLifeCounter.getLife()));
            mInitialized = true;
        }
        //--

        //set object listeners
        btn_mainlife.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                if (!mTwoFingersTapped){
                    Toast.makeText(MainActivity.this, "Long tap", 0).show();
                    vibrate();
                }else {
                    mTwoFingersTapped = false;
                }
                return true;
            }
        });
        btn_mainlife.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Only if the OnTouch listener is not activated (mTwoFingersTapped = FALSE) we show the toast for click.
                // Otherwise if the mTwoFingersTapped flag is TRUE, we have to set it to false, so we can use the click
                // for 1 finger.
                if (!mTwoFingersTapped){
                    btn_mainlife.setText(String.valueOf(mainLifeCounter.increaseOrDecreaseLifeBy(1)));
                    vibrate();
                }else {
                    mTwoFingersTapped = false;
                }
            }
        });

        btn_mainlife.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch(action & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        btn_mainlife.setText(String.valueOf(mainLifeCounter.increaseOrDecreaseLifeBy(2)));
                        vibrate(250);
                        // set the mTwoFingersTapped flag to TRUE when we tap with 2 fingers at once
                        mTwoFingersTapped = true;
                        break;
                }
                return false;
            }


        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        boolean powersave = sharedPrefs.getBoolean("power_save_mode",false);
        if(powersave){
            setTheme(R.style.AppTheme2);
        }else{
            setTheme(R.style.AppTheme1);
        }
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }

    // Backwards compatible recreate().
    @Override
    public void recreate()
    {
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            super.recreate();
        }
        else
        {
            startActivity(getIntent());
            finish();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.contentEquals("power_save_mode")){
            this.recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_reset:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

}
