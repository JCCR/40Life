package net.cubitum.fortylife.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;

import net.cubitum.fortylife.R;
import net.cubitum.fortylife.util.CardTransformation;

/**
 * Created by JuanCarlos on 12/21/13.
 */
public class CardView extends FrameLayout {
    FrameLayout mFrameLayout;
    CardImageView mCardImageView;


    public CardView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card, this);
        if(isInEditMode()){
            return;
        }
        loadViews();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card, this);
        if(isInEditMode()){
            return;
        }
        loadViews();
    }

    private void loadViews() {

        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mCardImageView = (CardImageView) findViewById(R.id.imageView);
        mCardImageView.setOnColorSetListener(new CardImageView.OnColorSetListener() {
            @Override
            public void onColorSet() {
                mFrameLayout.setBackgroundColor(getCardColor());
            }
        });


    }

    public int getCardColor(){
        return mCardImageView.getCardColor();
    }
    public void setCardImage(String url){
        try{
            Picasso.with(this.getContext()).load(url).transform(new CardTransformation()).into(mCardImageView);
        }catch (Exception ex){

        }

    }


}
