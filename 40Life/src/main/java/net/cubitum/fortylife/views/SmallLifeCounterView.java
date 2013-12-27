package net.cubitum.fortylife.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import net.cubitum.fortylife.R;
import net.cubitum.fortylife.model.SimpleCounter;

/**
 * Created by JuanCarlos on 11/12/13.
 */
public class SmallLifeCounterView extends LinearLayout {

    private LinearLayout mLifeLayout;

    private Button mLifeButton;

    private SimpleCounter mLifeCounter;
    private boolean mTwoFingersTapped;

    public SmallLifeCounterView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lifecounter_small, this);
        loadViews();
    }

    public SmallLifeCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lifecounter_small, this);
        loadViews();
    }

    public SmallLifeCounterView(Context context, boolean dummy){
        this(context);
        if(dummy){
            mLifeButton.setEnabled(false);
            mLifeButton.setText("");
        }
    }

    private void loadViews() {
        mLifeLayout = (LinearLayout) findViewById(R.id.layout_lifecounter);
        mLifeButton = (Button) findViewById(R.id.btn_mainlife);
        initialize(0,false);
    }

    public SimpleCounter getLifeCounter() {
        return mLifeCounter;
    }

    public void update() {
        mLifeButton.setText(String.valueOf(mLifeCounter.getAmount()));
    }

    public void initialize(int startingLife, boolean powerSaveTheme) {

        if (powerSaveTheme) {
            mLifeLayout.setBackgroundResource(R.color.lifelayout2_background);
        } else {
            mLifeLayout.setBackgroundResource(R.color.lifelayout1_background);
        }
        mLifeCounter = new SimpleCounter(startingLife);
        mLifeButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mTwoFingersTapped) {
                      //decrease life?
                } else {
                    mTwoFingersTapped = false;
                }
                return true;
            }
        });
        mLifeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Only if the OnTouch listener is not activated (mTwoFingersTapped = FALSE) we show the toast for click.
                // Otherwise if the mTwoFingersTapped flag is TRUE, we have to set it to false, so we can use the click
                // for 1 finger.
                if (!mTwoFingersTapped) {
                    increaseOrDecreaseLifeBy(1);
                    //vibrate();
                } else {
                    mTwoFingersTapped = false;
                }
            }
        });
        mLifeButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        increaseOrDecreaseLifeBy(-1);
                        //vibrate(250);
                        // set the mTwoFingersTapped flag to TRUE when we tap with 2 fingers at once
                        mTwoFingersTapped = true;
                        break;
                }
                return false;
            }
        });

        update();
    }

    private void increaseOrDecreaseLifeBy(int diff) {

            mLifeButton.setText(String.valueOf(mLifeCounter.increaseOrDecreaseBy(diff)));
    }

}
