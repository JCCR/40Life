package net.cubitum.fortylife.net.cubitum.fortylife.model;

import android.os.CountDownTimer;

import java.util.ArrayList;

/**
 * Created by JuanCarlos on 11/14/13.
 */
public class SimpleCounter {
    private int amount;
    private int startingAmount;

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getStartingAmount(){
        return startingAmount;
    }

    public int increaseOrDecreaseBy(int diff) {
        amount += diff;
        return amount;
    }

    public SimpleCounter(){
        this(0);
    }

    public SimpleCounter(int startingAmount) {
        this.startingAmount = startingAmount;
        this.amount = startingAmount;
    }

}
