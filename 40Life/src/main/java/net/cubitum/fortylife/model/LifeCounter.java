package net.cubitum.fortylife.model;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuanCarlos on 11/12/13.
 */
public class LifeCounter extends SimpleCounter {

    private int tempLifeDiff;
    private CountDownTimer lifeLogTimer;
    public List<Integer> lifeLog;

    @Override
    public int increaseOrDecreaseBy(int diff) {
        lifeLogTimer.cancel();
        lifeLogTimer.start();
        tempLifeDiff += diff;
        return super.increaseOrDecreaseBy(diff);
    }

    public LifeCounter(int startingLife) {
        super(startingLife);
        lifeLog = new ArrayList<Integer>();
        tempLifeDiff = 0;
        lifeLogTimer = new CountDownTimer(5000, 5000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                lifeLogUpdate();
            }
        };
    }

    public void lifeLogUpdate(){
        lifeLogTimer.cancel();
        if(tempLifeDiff != 0){
            lifeLog.add(tempLifeDiff);
            tempLifeDiff = 0;
        }
    }


}
