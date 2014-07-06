package net.cubitum.fortylife.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.PicassoDrawable;

public class CardImageView extends ImageView {

    int mCardColor;

    public int getCardColor() {
        return mCardColor;
    }

    public CardImageView(Context context) {
        super(context);
    }

    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setImageDrawable(Drawable drawable) {

        if (drawable != null) {

            if (drawable instanceof PicassoDrawable) {
                mCardColor = ((PicassoDrawable) drawable)
                        .getImage()
                        .getBitmap()
                        .getPixel(0, 0);
                super.setImageDrawable(drawable);
                mColorSetListener.onColorSet();
            } else {
                super.setImageDrawable(drawable);
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    OnColorSetListener mColorSetListener;

    public void setOnColorSetListener(OnColorSetListener mColorSetListener) {
        this.mColorSetListener = mColorSetListener;
    }

    interface OnColorSetListener {
        public void onColorSet();
    }


}
