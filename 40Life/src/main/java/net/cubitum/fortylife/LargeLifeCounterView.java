package net.cubitum.fortylife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by JuanCarlos on 11/12/13.
 */
public class LargeLifeCounterView extends LinearLayout {

    private LinearLayout mLifeLayout;
    private Button mLifeButton;
    private Button mLifePlus;
    private Button mLifeMinus;

    private LifeCounter mLifeCounter;
    private boolean mTwoFingersTapped;

    public LargeLifeCounterView(Context context) {
        super(context);

        loadViews();
    }

    public LargeLifeCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.large_lifecounter, this);

        loadViews();
    }

    private void loadViews() {
        mLifeLayout = (LinearLayout) findViewById(R.id.layout_lifecounter);
        mLifeButton = (Button) findViewById(R.id.btn_mainlife);
        mLifePlus = (Button) findViewById(R.id.btn_plus);
        mLifeMinus = (Button) findViewById(R.id.btn_minus);
    }

    public LifeCounter getLifeCounter() {
        return mLifeCounter;
    }

    public void update() {
        mLifeButton.setText(String.valueOf(mLifeCounter.getLife()));
    }

    public void initialize(int startingLife, boolean powerSaveTheme) {
        if (powerSaveTheme) {
            mLifeLayout.setBackgroundResource(R.color.lifelayout2_background);
        } else {
            mLifeLayout.setBackgroundResource(R.color.lifelayout1_background);
        }
        mLifeCounter = new LifeCounter(startingLife);

        mLifeButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (!mTwoFingersTapped) {
                    AlertDialog dialogShown;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    // Get the layout inflater
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    final View dialogView = inflater.inflate(R.layout.dialog_life_detail, null);
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    final EditText editText = (EditText) dialogView.findViewById(R.id.editText);
                    builder.setView(dialogView)
                            // Add action buttons
                            .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    int newLife;
                                    try {
                                        newLife = Integer.parseInt(CalculatorView.evaluate(editText.getText().toString()).replaceAll("−","-"));
                                    }catch (Exception e){
                                        newLife = mLifeCounter.getLife();
                                    }
                                    mLifeCounter.increaseOrDecreaseLifeBy(newLife - mLifeCounter.getLife());
                                    update();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dialogShown = builder.show();
                    editText.setText(String.valueOf(mLifeCounter.getLife()));
                    //dialog.show();
                    //vibrate();
                } else {
                    mTwoFingersTapped = false;
                }
                return true;
            }
        });
        mLifeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Only if the OnTouch listener is not activated (mTwoFingersTapped = FALSE) we show the toast for click.
                // Otherwise if the mTwoFingersTapped flag is TRUE, we have to set it to false, so we can use the click
                // for 1 finger.
                if (!mTwoFingersTapped) {
                    mLifeButton.setText(String.valueOf(mLifeCounter.increaseOrDecreaseLifeBy(1)));
                    //vibrate();
                } else {
                    mTwoFingersTapped = false;
                }
            }
        });

        mLifeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mLifeButton.setText(String.valueOf(mLifeCounter.increaseOrDecreaseLifeBy(2)));
                        //vibrate(250);
                        // set the mTwoFingersTapped flag to TRUE when we tap with 2 fingers at once
                        mTwoFingersTapped = true;
                        break;
                }
                return false;
            }


        });
    }


}
