package net.cubitum.fortylife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;

import net.cubitum.fortylife.views.LargeLifeCounterView;
import net.cubitum.fortylife.views.SmallLifeCounterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //service objects
    private Vibrator mVibrator;
    private TextToSpeech mTextToSpeech;

    private static int TTS_DATA_CHECK = 1;

    private void confirmTTSData() {
        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, TTS_DATA_CHECK);
    }

    private boolean mInitialized = false;

    //preferences
    private boolean mVibrate = false;
    private int mStartingLife;
    private int mPlayerCount;
    private String mPlayerName;
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TTS_DATA_CHECK) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //Voice data does not exist
                Intent installIntent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        } else if (requestCode == 1337) {
            if (resultCode == Activity.RESULT_OK) {
                int color = data.getIntExtra("CardColor", -1);
                if (color != -1) {
                    mSelectedGeneralView.setLifeBackground(color);
                }

            }
        } else if (requestCode == 1338) {
            if (resultCode == Activity.RESULT_OK) {
                int color = data.getIntExtra("CardColor", -1);
                if (color != -1) {
                    mLifeCounterMain.setLifeBackground(color);
                }

            }
        }
    }

    PowerManager mPowerManager = null;
    PowerManager.WakeLock mWakeLock = null;

    @Override
    protected void onResume() {
        super.onResume();

        if (mPowerManager == null) {
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "40Life");
        } else {
            if (mWakeLock.isHeld()) {
                return;
            }
        }
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock == null) {
            return;
        } else {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //initialize service objects
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //Speak
                } else {
                    //Handle initialization error here
                }
            }
        });

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
        mPlayerName = sharedPrefs.getString("player_name", "Player");
        //--

        //initialize app ui
        if (!mInitialized) {
            mLifeCounterMain.initialize(mStartingLife, mPowerSaveMode);
            initializeTableLayoutGenerals();
            mInitialized = true;
        }
        //--

        //set object listeners

    }

    private void initializeTableLayoutGenerals() {
        int[] rc = calculateRowsColumns(mPlayerCount - 1);
        mGeneralsViewList = new ArrayList<List<SmallLifeCounterView>>();
        mTableLayoutGenerals.removeAllViews();
        createTableLayoutGenerals(rc[0], rc[1], rc[2], mTableLayoutGenerals);
    }

    //TODO: change from list<list> collection to something more efficient
    List<List<SmallLifeCounterView>> mGeneralsViewList;

    private void createTableLayoutGenerals(int rows, int columns, int empty, TableLayout tableLayout) {

        for (int i = 0; i < rows; i++) {
            List<SmallLifeCounterView> mRowGeneralsViewList = new ArrayList<SmallLifeCounterView>(rows);
            TableRow row = new TableRow(this);
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            row.setLayoutParams(lp);
            for (int i2 = 0; i2 < columns; i2++) {
                SmallLifeCounterView smlcv = new SmallLifeCounterView(this, (i == rows - 1 && empty > 0 && i2 == columns - 1), i2, i) {
                    @Override
                    public void onLifeLongClick() {
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivityForResult(i, 1337);
                        setSelectedGeneralView(this);
                    }
                };
                TableRow.LayoutParams slp = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT);
                smlcv.setLayoutParams(slp);
                mRowGeneralsViewList.add(smlcv);
                row.addView(smlcv);
            }
            mGeneralsViewList.add(mRowGeneralsViewList);
            tableLayout.addView(row);
        }
    }


    SmallLifeCounterView mSelectedGeneralView;

    public void setSelectedGeneralView(SmallLifeCounterView v) {
        mSelectedGeneralView = v;
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
        if (key.contentEquals("player_count")) {
            mPlayerCount = Integer.parseInt(prefs.getString("player_count", "5"));
            initializeTableLayoutGenerals();
        }
        if (key.contentEquals("player_name")) {
            mPlayerName = prefs.getString("player_name", "Player");
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
            mMenuItem.setIcon(R.drawable.ic_action_random2);
            mMenuItem = menu.findItem(R.id.action_profile);
            mMenuItem.setIcon(R.drawable.ic_action_dragon2);
        } else {
            mMenuItem = menu.findItem(R.id.action_random);
            mMenuItem.setIcon(R.drawable.ic_action_random);
            mMenuItem = menu.findItem(R.id.action_profile);
            mMenuItem.setIcon(R.drawable.ic_action_dragon);
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
                mLifeCounterMain.reset();
                for (List<SmallLifeCounterView> vl : mGeneralsViewList) {
                    for (SmallLifeCounterView v : vl) {
                        v.reset();
                    }
                }
                return true;
            case R.id.action_random:
                return true;
            case R.id.action_profile:
                //startActivity(new Intent(this, ProfileActivity.class));
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(i, 1338);
                return true;
            case R.id.action_announce:
                mTextToSpeech.speak(String.valueOf(mPlayerName + " has " + mLifeCounterMain.getLifeCounter().getAmount()) + " life.", TextToSpeech.QUEUE_ADD, null);
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

    @Override
    public void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

}
