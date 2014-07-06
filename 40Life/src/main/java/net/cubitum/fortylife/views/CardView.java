package net.cubitum.fortylife.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;

import net.cubitum.fortylife.R;
import net.cubitum.fortylife.util.CardTransformation;

public class CardView extends FrameLayout {
    FrameLayout mFrameLayout;
    CardImageView mCardImageView;
    String mCardImageUrl;
    String mCardName;

    public CardView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card, this);
        if (isInEditMode()) {
            return;
        }
        loadViews();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.card, this);
        if (isInEditMode()) {
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

    public String getCardName() {
        return mCardName;
    }

    public void setCardName(String mCardName) {
        this.mCardName = mCardName;
    }

    public int getCardColor() {
        return mCardImageView.getCardColor();
    }

    public void setCardImage(String url) {
        mCardImageUrl = url;
        try {
            Picasso.with(this.getContext()).load(url).transform(new CardTransformation()).into(mCardImageView);
        } catch (Exception ex) {

        }

    }

    public String getCardImage() {
        return mCardImageUrl;
    }

}
