package net.cubitum.fortylife.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class CardTransformation implements Transformation {


    protected int[] HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return new int[]{r, g, b};
    }

    protected int getIntFromColor(int[] rgb) {
        return 0xff000000 | (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2] << 0);
    }

    protected int[] getColorFromInt(int rgb) {
        return new int[]{(rgb >> 16 & 0xff), (rgb >> 8 & 0xff), (rgb & 0xff)};
    }

    protected float[] RGBtoHSB(int r, int g, int b) {
        float hue, saturation, brightness;
        float[] hsbvals = new float[3];

        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    /**
     * @param bmp      input bitmap
     * @param contrast 0..10 1 is default
     * @return new bitmap
     */
    protected Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast) {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        ColorMatrix cm = new ColorMatrix(new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    static final int sArtWidth = 50, sArtHeight = 37;

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source, 6, 5, 300, 434);
        Bitmap cropped = Bitmap.createBitmap(result, 14, 42, 273, 200);
        cropped = Bitmap.createScaledBitmap(cropped, sArtWidth, sArtHeight, true);
        int[] pix = new int[sArtWidth * sArtHeight];
        cropped.getPixels(pix, 0, sArtWidth, 0, 0, sArtWidth, sArtHeight);
        int count = 0;
        int[] rgb = {0, 0, 0};
        for (int i = 0; i < pix.length; i++) {
            rgb[0] += (int) (((pix[i]) >> 16 & 0xff) * 1.03);
            rgb[1] += (int) (((pix[i]) >> 8 & 0xff) * 1.02);
            rgb[2] += (int) (((pix[i]) & 0xff) * 1.08);
            ++count;
        }
        //find the average color for each spectrum
        rgb[0] = (int) Math.floor(rgb[0] / count);
        rgb[1] = (int) Math.floor(rgb[1] / count);
        rgb[2] = (int) Math.floor(rgb[2] / count);
        Integer maxVal = null;
        int index = -1;
        for (int i = 0; i < rgb.length; i++) {
            int thisNum = rgb[i];
            if (maxVal == null || thisNum > maxVal.intValue()) {
                maxVal = thisNum;
                index = i;
            }
        }
        //increase the most dominant color by 1.18 times
        rgb[index] *= 1.06;
        if (rgb[index] > 255) {
            rgb[index] = 255;
        }
        //increase the saturation and brightness using the HSB color model
        float[] hsb = RGBtoHSB(rgb[0], rgb[1], rgb[2]);
        hsb[0] = (hsb[0] * 1.08f);
        hsb[1] = (hsb[1] * 1.2f) + 0.2f;
        hsb[2] = (hsb[2] * 1.3f) + 0.4f;
        if (hsb[1] > 1f) {
            hsb[1] = 1f;
        }
        if (hsb[2] > 1f) {
            hsb[2] = 1f;
        }
        if (hsb[0] > 1f) {
            hsb[0] = 1f;
        }

        rgb = HSBtoRGB(hsb[0], hsb[1], hsb[2]);

        Bitmap ret = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        canvas.drawRGB(rgb[0], rgb[1], rgb[2]);

        Bitmap filtered = changeBitmapContrastBrightness(ret, 1.5f);
        int[] rgb2 = getColorFromInt(filtered.getPixel(0, 0));
        ret = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(ret);
        canvas.drawRGB(rgb[0], rgb[1], rgb[2]);
        canvas.drawARGB(100, rgb2[0], rgb2[1], rgb2[2]);
        int finalColor = ret.getPixel(0, 0);
        canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColor(finalColor);
        canvas.drawPoint(0f, 0f, paint);

        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "card()";
    }
}