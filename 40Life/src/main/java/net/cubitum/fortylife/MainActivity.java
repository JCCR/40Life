package net.cubitum.fortylife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //service objects
    private Vibrator mVibrator;

    private boolean mInitialized = false;

    //preferences
    private boolean mVibrate = false;
    private int mStartingLife;
    private int mPlayerCount;
    private boolean mPowerSaveMode = false;

    //objects


    //view objects
    private LargeLifeCounterView mLifeCounterMain;
    private MenuItem mMenuItem;
    private TableLayout mTableLayoutGenerals;

    private void vibrate() {
        vibrate(75);
    }

    private void vibrate(int length) {
        if (mVibrate) {
            mVibrator.vibrate(length);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //initialize objects
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mLifeCounterMain = (LargeLifeCounterView) findViewById(R.id.lifecounter_main);
        mTableLayoutGenerals = (TableLayout) findViewById(R.id.tablelayout_generals);
        //--

        //preference loading
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        mPowerSaveMode = sharedPrefs.getBoolean("power_save_mode", false);
        mPlayerCount = Integer.parseInt(sharedPrefs.getString("player_count", "5"));
        mStartingLife = Integer.parseInt(sharedPrefs.getString("life_total", "40"));
        mVibrate = sharedPrefs.getBoolean("vibrate", false);
        //--

        //initialize app ui
        if (!mInitialized) {
            mLifeCounterMain.initialize(mStartingLife, mPowerSaveMode);
            //mLifeCounterMain = (LargeLifeCounterView) findViewById(R.id.lifecounter_main2);
            mLifeCounterMain.initialize(mStartingLife, mPowerSaveMode);
            int[] rc = calculateRowsColumns(mPlayerCount - 1);
            createTableLayoutGenerals(rc[0], rc[1], rc[2], mTableLayoutGenerals);
            mInitialized = true;
        }
        //--

        //set object listeners

    }

    private void createTableLayoutGenerals(int rows, int columns, int empty, TableLayout tableLayout) {
        for (int i = 0; i < rows; i++) {

            TableRow row = new TableRow(this);
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            row.setLayoutParams(lp);
            for (int i2 = 0; i2 < columns; i2++) {
                SmallLifeCounterView smlcv = new SmallLifeCounterView(this, (i == rows - 1 && empty > 0 && i2 == columns - 1));
                TableRow.LayoutParams slp = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT);
                smlcv.setLayoutParams(slp);
                row.addView(smlcv);
            }
            tableLayout.addView(row);
        }
    }

    private int[] calculateRowsColumns(int n) {
        int[] rc = new int[3];
        if (n > 3) {
            if (isPrime(n)) {
                n++;
                rc[2] = 1;
            }
            for (int c = 3; c > 0; c--) {
                double rd = (double) n / (double) c;
                int r = (int) rd;
                if (rd == r) {
                    rc[0] = r;
                    rc[1] = c;
                    if (c > r) {
                        rc[0] = c;
                        rc[1] = r;
                    }
                    break;
                }
            }
        } else {
            rc[0] = n;
            rc[1] = 1;
        }
        return rc;
    }

    public boolean isPrime(int n) {
        for (int i = 2; i < n; i++) {
            if (n % i == 0) return false;
        }
        return (n > 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        mPowerSaveMode = sharedPrefs.getBoolean("power_save_mode", false);
        if (mPowerSaveMode) {
            setTheme(R.style.AppTheme2);
        } else {
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
    public void recreate() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            super.recreate();
        } else {
            startActivity(getIntent());
            finish();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.contentEquals("power_save_mode")) {
            this.recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        //theme action buttons
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        mPowerSaveMode = sharedPrefs.getBoolean("power_save_mode", false);

        //power save theme change
        if (mPowerSaveMode) {
            mMenuItem = menu.findItem(R.id.action_random);
            mMenuItem.setIcon(R.drawable.action_random_icon2);
            mMenuItem = menu.findItem(R.id.action_profile);
            mMenuItem.setIcon(R.drawable.action_dragon_icon2);
        } else {
            mMenuItem = menu.findItem(R.id.action_random);
            mMenuItem.setIcon(R.drawable.action_random_icon);
            mMenuItem = menu.findItem(R.id.action_profile);
            mMenuItem.setIcon(R.drawable.action_dragon_icon);
        }
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
            case R.id.action_random:
                return true;
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileActivity.class));
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
