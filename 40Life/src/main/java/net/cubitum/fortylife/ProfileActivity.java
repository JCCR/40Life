package net.cubitum.fortylife;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends ActionBarActivity {

    private ImageView mImageView;
    private FrameLayout mFrameLayout;

    private int demoCount = 0;
    private static String[] demoUrls = {
            "http://gatherer.wizards.com/Handlers/Image.ashx?type=card&name=Prossh,%20Skyraider%20of%20Kher",
            "http://gatherer.wizards.com/Handlers/Image.ashx?type=card&name=Jeleva,%20Nephalia's%20Scourge",
            "http://gatherer.wizards.com/Handlers/Image.ashx?type=card&name=Derevi,%20Empyrial%20Tactician",
            "http://gatherer.wizards.com/Handlers/Image.ashx?type=card&name=Marath,%20Will%20of%20the%20Wild",
            "http://gatherer.wizards.com/Handlers/Image.ashx?type=card&name=Oloro,%20Ageless%20Ascetic",
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=376472&type=card",
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=376473&type=card",
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=376471&type=card",
            "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=376474&type=card"

    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            //crop the image without the black border
            Bitmap cropped = Bitmap.createBitmap(result, 11, 12, 200, 285);

            cropped = Bitmap.createScaledBitmap(cropped, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cropped.getWidth(), getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cropped.getHeight(), getResources().getDisplayMetrics()), true);

            mImageView.setImageBitmap(cropped);

            int[] pix = new int[182 * 133];
            result.getPixels(pix, 0, 182, 9, 26, 182, 133);
            int count = 0;
            int[] rgb = {0, 0, 0};
            for (int i = 0; i < pix.length; i++) {
                rgb[0] += (int) (((pix[i]) >> 16 & 0xff) * 1.05);
                rgb[1] += (int) (((pix[i]) >> 8 & 0xff) * 1.1);
                rgb[2] += (int) (((pix[i]) & 0xff) * 1.15);
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
            rgb[index] *= 1.18;
            if (rgb[index] > 255) {
                rgb[index] = 255;
            }
            //increase the saturation and brightness using the HSB color model
            float[] hsb = RGBtoHSB(rgb[0], rgb[1], rgb[2]);
            hsb[0] = hsb[0] * 0.95f;
            hsb[1] = (hsb[1] * 1.2f) + 0.1f;
            hsb[2] = (hsb[2] * 1.4f) + 0.1f;
            if (hsb[1] > 1f) {
                hsb[1] = 1f;
            }
            if (hsb[2] > 1f) {
                hsb[2] = 1f;
            }
            rgb = HSBtoRGB(hsb[0], hsb[1], hsb[2]);

            Bitmap ret = Bitmap.createBitmap(1, 1, result.getConfig());
            Canvas canvas = new Canvas(ret);
            canvas.drawRGB(rgb[0], rgb[1], rgb[2]);

            Bitmap filtered = changeBitmapContrastBrightness(ret, 1.5f);
            int[] rgb2 = getColorFromInt(filtered.getPixel(0, 0));
            ret = Bitmap.createBitmap(1, 1, result.getConfig());
            canvas = new Canvas(ret);
            canvas.drawRGB(rgb[0], rgb[1], rgb[2]);
            canvas.drawARGB(150, rgb2[0], rgb2[1], rgb2[2]);

            mFrameLayout.setBackgroundColor(ret.getPixel(0, 0));
        }


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
    }

    private Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImageTask().execute(demoUrls[demoCount]);
                demoCount++;
                if (demoCount >= demoUrls.length) {
                    demoCount = 0;
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
            return rootView;
        }
    }

}
