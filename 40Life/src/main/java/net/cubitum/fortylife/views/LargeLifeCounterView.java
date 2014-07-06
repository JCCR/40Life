package net.cubitum.fortylife.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.cubitum.fortylife.R;
import net.cubitum.fortylife.model.LifeCounter;
import net.cubitum.fortylife.model.SimpleCounter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LargeLifeCounterView extends LinearLayout implements Animation.AnimationListener {

    private LinearLayout mLifeLayout;
    private FrameLayout mExtraLayout;
    private ImageView mPoisonIcon;
    private ImageView mCombatDmgIcon;
    private TextView mModeText;
    private ToggleButton mPoisonModeButton;
    private Button mLifeButton;
    private Button mLifePlus;
    private Button mLifeMinus;
    private LifeCounter mLifeCounter;
    private SimpleCounter mPoisonCounter;
    private boolean mTwoFingersTapped;
    private boolean mPoisonMode;
    private boolean mShowExtras;

    public LargeLifeCounterView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lifecounter_large, this);
        loadViews();
    }

    public LargeLifeCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lifecounter_large, this);
        loadViews();
    }

    private void loadViews() {
        mLifeLayout = (LinearLayout) findViewById(R.id.layout_lifecounter);
        mExtraLayout = (FrameLayout) findViewById(R.id.layout_extra);
        mPoisonIcon = (ImageView) findViewById(R.id.poison_icon);
        mCombatDmgIcon = (ImageView) findViewById(R.id.combatdmg_icon);
        mModeText = (TextView) findViewById(R.id.txt_mode);
        mPoisonModeButton = (ToggleButton) findViewById(R.id.btn_toggle_poison);
        mLifeButton = (Button) findViewById(R.id.btn_mainlife);
        mLifePlus = (Button) findViewById(R.id.btn_plus);
        mLifeMinus = (Button) findViewById(R.id.btn_minus);
    }

    public LifeCounter getLifeCounter() {
        return mLifeCounter;
    }

    public void setLifeCounter(LifeCounter mLifeCounter) {
        this.mLifeCounter = mLifeCounter;
    }

    public void update() {
        if (!mPoisonMode) {
            mLifeButton.setText(String.valueOf(mLifeCounter.getAmount()));
        } else {
            mLifeButton.setText(String.valueOf(mPoisonCounter.getAmount()));
        }
    }

    public void togglePoisonMode() {
        mPoisonMode = !mPoisonMode;
        update();
        if (mPoisonMode) {
            mModeText.setText(R.string.poison_caps);
        } else {
            mModeText.setText(R.string.life_caps);
        }
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.dim_text_fade_in_out);
        fadeInAnimation.setAnimationListener(this);
        mModeText.startAnimation(fadeInAnimation);
    }

    public boolean isShowExtras() {
        return mShowExtras;
    }

    public void setShowExtras(boolean mShowExtras) {
        this.mShowExtras = mShowExtras;
        mExtraLayout.setVisibility(mShowExtras ? View.VISIBLE : View.GONE);
    }

    public void restore(LifeCounter lifeCounter, boolean powerSaveTheme, boolean mShowExtras) {
        setLifeCounter(lifeCounter);
        update();
        setShowExtras(mShowExtras);
    }

    public void initialize(int startingLife, boolean powerSaveTheme) {

        if (powerSaveTheme) {
            mLifeLayout.setBackgroundResource(R.color.lifelayout2_background);
            mExtraLayout.setBackgroundResource(R.color.extralayout2_background);
            mPoisonIcon.setImageResource(R.drawable.skull_icon2);
            mCombatDmgIcon.setImageResource(R.drawable.sword_icon2);
        } else {
            mLifeLayout.setBackgroundResource(R.color.lifelayout1_background);
            mExtraLayout.setBackgroundResource(R.color.extralayout1_background);
            mPoisonIcon.setImageResource(R.drawable.skull_icon);
            mCombatDmgIcon.setImageResource(R.drawable.sword_icon);
        }
        mLifeCounter = new LifeCounter(startingLife);
        mPoisonCounter = new SimpleCounter();
        mPoisonMode = false;
        mLifeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mTwoFingersTapped) {
                    if (!mPoisonMode) {
                        //regular life mode -->
                        mLifeCounter.lifeLogUpdate();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        // Get the layout inflater
                        LayoutInflater inflater = LayoutInflater.from(getContext());
                        final View dialogView = inflater.inflate(R.layout.dialog_life_detail, null);
                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);
                        final ListView listView = (ListView) dialogView.findViewById(R.id.listView);
                        builder.setView(dialogView)
                                // Add action buttons
                                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        int newLife;
                                        try {
                                            newLife = Integer.parseInt(CalculatorView.evaluate(editText.getText().toString()).replaceAll("−", "-"));
                                        } catch (Exception e) {
                                            newLife = mLifeCounter.getAmount();
                                        }
                                        mLifeCounter.increaseOrDecreaseBy(newLife - mLifeCounter.getAmount());
                                        mLifeCounter.lifeLogUpdate();
                                        update();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                        editText.setText(String.valueOf(mLifeCounter.getAmount()));
                        List<String> lifeLog = new ArrayList<String>();
                        lifeLog.add(String.valueOf(mLifeCounter.getStartingAmount()));
                        int prev = mLifeCounter.getStartingAmount();
                        String line;
                        for (int i : mLifeCounter.lifeLog) {
                            prev = prev + i;
                            line = String.valueOf(prev);
                            if (i > 0) {
                                line += " (+" + String.valueOf(i) + ")";
                            } else {
                                line += " (" + String.valueOf(i) + ")";
                            }
                            lifeLog.add(line);
                        }
                        Collections.reverse(lifeLog);
                        listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lifeLog));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                editText.setText(((TextView) view).getText().toString().split(" \\(")[0]);
                            }
                        });
                        //dialog.show();
                        //vibrate();
                    } else {
                        //poison counter mode -->

                    }
                } else {
                    mTwoFingersTapped = false;
                }
                return true;
            }
        });
        mLifeButton.setOnClickListener(new View.OnClickListener() {
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
        mLifeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        increaseOrDecreaseLifeBy(2);
                        //vibrate(250);
                        // set the mTwoFingersTapped flag to TRUE when we tap with 2 fingers at once
                        mTwoFingersTapped = true;
                        break;
                }
                return false;
            }
        });
        mLifePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseOrDecreaseLifeBy(1);
            }
        });
        mLifePlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                increaseOrDecreaseLifeBy(5);
                return true;
            }
        });
        mLifeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseOrDecreaseLifeBy(-1);
            }
        });
        mLifeMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                increaseOrDecreaseLifeBy(-5);
                return true;
            }
        });

        mPoisonModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePoisonMode();
            }
        });

        update();
    }

    private void increaseOrDecreaseLifeBy(int diff) {
        if (!mPoisonMode) {
            mLifeButton.setText(String.valueOf(mLifeCounter.increaseOrDecreaseBy(diff)));
        } else {
            mLifeButton.setText(String.valueOf(mPoisonCounter.increaseOrDecreaseBy(diff)));
        }
    }

    public void reset() {
        getLifeCounter().reset();
        update();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mModeText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mModeText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void setLifeBackground(int color) {
        mLifeLayout.setBackgroundColor(color);
    }
}
