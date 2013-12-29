package net.cubitum.fortylife;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.cubitum.fortylife.search.Card;
import net.cubitum.fortylife.search.CardManaSymbol;
import net.cubitum.fortylife.search.CardSearch;
import net.cubitum.fortylife.search.ICardSearchCache;
import net.cubitum.fortylife.util.CardArrayAdapter;
import net.cubitum.fortylife.util.SimpleDiskCache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity {

    /*
    ManaSelected index to colour map
        WHITE = 0
        BLUE = 1
        BLACK = 2
        RED = 3
        GREEN = 4
    */
    boolean[] mManaSelected = new boolean[5];
    static int sBacklogThreshold =15;
    private CountDownTimer mSearchTimer;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private List<String> mResultBacklog;
    private boolean mUseBacklog;
    private int mBacklogCount;
    static SimpleDiskCache sDiskCache;
    private static final int MAX_DISK_CACHE_SIZE = 10 * 1024 * 1024; //10MB
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        if(sDiskCache ==null){
            try {
                sDiskCache = SimpleDiskCache.open(createDefaultCacheDir(this),1,MAX_DISK_CACHE_SIZE);
            } catch (IOException e) {
                Log.w("Cache","Cache creation failed: "+e.getMessage());
            }
        }
        mSearchTimer = new CountDownTimer(750, 750) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                cardSearch();
            }
        };
        mCardSearch = new CardSearch(new ICardSearchCache() {
            @Override
            public String get(String key) {
                try {
                    SimpleDiskCache.StringEntry entry = sDiskCache.getString(key);
                    if(entry==null){
                        return null;
                    }
                    return entry.getString();
                } catch (IOException e) {
                    Log.w("Cache","Cache get failed ("+key+"): "+e.getMessage());
                    return null;
                }
            }

            @Override
            public void put(String key, String string) {
                try {
                    sDiskCache.put(key,string);
                } catch (IOException e) {
                    Log.w("Cache", "Cache put failed (" + key + "): " + e.getMessage());
                }
            }
        });
        mCardSearch.setOnResultsLoadedListener(new CardSearch.OnResultsLoadedListener() {
            @Override
            public void onResultsLoaded() {
                final List<String> resultList = new ArrayList<String>();
                for (Card c : mCardSearch.results()) {
                    resultList.add(c.imageUrl);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setOnScrollListener(new EndlessScrollListener());
                        mProgressBar.setVisibility(View.INVISIBLE);
                        if(mUseBacklog){
                            mResultBacklog.addAll(resultList);
                            mCardArrayAdapter.addAll(mResultBacklog.subList(0, sBacklogThreshold));
                        }else{
                            mCardArrayAdapter.addAll(resultList);

                        }
                        mCardArrayAdapter.notifyDataSetChanged();

                    }
                });

            }
        });
    }

    private void cardSearch() {
        mListView.setOnScrollListener(sDummyScrollListener);
        mProgressBar.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        mCardArrayAdapter.clear();
        mCardArrayAdapter.notifyDataSetChanged();
        final List<CardManaSymbol> selectedMana = new ArrayList<CardManaSymbol>();
        for (int i = 0; i < 5; i++) {
            if (mManaSelected[i]) {
                switch (i) {
                    case 0:
                        selectedMana.add(CardManaSymbol.WHITE);
                        break;
                    case 1:
                        selectedMana.add(CardManaSymbol.BLUE);
                        break;
                    case 2:
                        selectedMana.add(CardManaSymbol.BLACK);
                        break;
                    case 3:
                        selectedMana.add(CardManaSymbol.RED);
                        break;
                    case 4:
                        selectedMana.add(CardManaSymbol.GREEN);
                        break;
                }
            }
        }
        new Thread() {
            public void run() {
                mUseBacklog=false;
                mBacklogCount=0;
                int result = mCardSearch.filter(selectedMana.toArray(new CardManaSymbol[selectedMana.size()])).search();
                if(result>sBacklogThreshold){
                    mResultBacklog = new ArrayList<String>(result);
                    mUseBacklog =true;
                    mBacklogCount =sBacklogThreshold;
                }
                if (result == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mTextView.setText(R.string.no_results);
                            mTextView.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (result == -1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mTextView.setText(R.string.error_result);
                            mTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }.start();

    }

    CardSearch mCardSearch;
    CardArrayAdapter mCardArrayAdapter;
    ListView mListView;
    static DummyScrollListener sDummyScrollListener = new DummyScrollListener();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.textView);
        mListView = (ListView) findViewById(R.id.listView);
        mCardArrayAdapter = new CardArrayAdapter(this, new ArrayList<String>());
        mListView.setAdapter(mCardArrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seach, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if ((item.getItemId() >= R.id.action_mana_white) && (item.getItemId() <= R.id.action_mana_green)) {
            mSearchTimer.cancel();
            mSearchTimer.start();
        }
        switch (item.getItemId()) {
            case R.id.action_search_byname:
                return true;
            case R.id.action_mana_white:
                mManaSelected[0] = !mManaSelected[0];
                if (mManaSelected[0]) {
                    item.setIcon(R.drawable.ic_action_mana_white2);
                } else {
                    item.setIcon(R.drawable.ic_action_mana_white);
                }
                return true;
            case R.id.action_mana_blue:
                mManaSelected[1] = !mManaSelected[1];
                if (mManaSelected[1]) {
                    item.setIcon(R.drawable.ic_action_mana_blue2);
                } else {
                    item.setIcon(R.drawable.ic_action_mana_blue);
                }
                return true;
            case R.id.action_mana_black:
                mManaSelected[2] = !mManaSelected[2];
                if (mManaSelected[2]) {
                    item.setIcon(R.drawable.ic_action_mana_black2);
                } else {
                    item.setIcon(R.drawable.ic_action_mana_black);
                }
                return true;
            case R.id.action_mana_red:
                mManaSelected[3] = !mManaSelected[3];
                if (mManaSelected[3]) {
                    item.setIcon(R.drawable.ic_action_mana_red2);
                } else {
                    item.setIcon(R.drawable.ic_action_mana_red);
                }
                return true;
            case R.id.action_mana_green:
                mManaSelected[4] = !mManaSelected[4];
                if (mManaSelected[4]) {
                    item.setIcon(R.drawable.ic_action_mana_green2);
                } else {
                    item.setIcon(R.drawable.ic_action_mana_green);
                }
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
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);
            return rootView;
        }
    }

    public class EndlessScrollListener implements AbsListView.OnScrollListener {

        private int visibleThreshold = 3;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                if(mUseBacklog){
                    int end = mBacklogCount+sBacklogThreshold;
                    if(end>mResultBacklog.size()){
                        end = mResultBacklog.size();
                        mUseBacklog =false;
                    }
                    mCardArrayAdapter.addAll(mResultBacklog.subList(mBacklogCount,end));
                    mBacklogCount+=sBacklogThreshold;
                    if(mBacklogCount>=mResultBacklog.size()){
                        mUseBacklog=false;
                    }
                }else{
                    mListView.setOnScrollListener(sDummyScrollListener);
                    new Thread() {
                        public void run() {
                            int result = mCardSearch.next();
                        }
                    }.start();
                }

                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
    public static class DummyScrollListener implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), "card-cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }
}
