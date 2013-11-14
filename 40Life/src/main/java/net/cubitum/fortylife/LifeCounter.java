package net.cubitum.fortylife;

import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuanCarlos on 11/12/13.
 */
public class LifeCounter {
    private int life;
    private int startingLife;
    private int tempLifeDiff;
    private CountDownTimer lifeLogTimer;
    public List<Integer> lifeLog;

    public void setLife(int newlife) {
        life = newlife;
    }

    public int getLife() {
        return life;
    }

    public int getStartingLife(){
        return startingLife;
    }

    public int increaseOrDecreaseLifeBy(int diff) {
        lifeLogTimer.cancel();
        lifeLogTimer.start();
        tempLifeDiff += diff;
        life += diff;
        return life;
    }

    public LifeCounter(int startingLife) {
        lifeLog = new ArrayList<Integer>();
        this.startingLife = startingLife;
        this.life = startingLife;
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
