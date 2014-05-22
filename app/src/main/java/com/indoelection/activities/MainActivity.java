package com.indoelection.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.indoelection.R;
import com.indoelection.util.ActivitySplitAnimationUtil;


public class MainActivity extends FragmentActivity {

    private AsyncTask<Void, Void, Bitmap> mBlurTask;
    private RenderScript sRS;
    private ImageView mImageTop;
    private ImageView mImageDown;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageTop = (ImageView)findViewById(R.id.blur_top);
        mImageDown = (ImageView)findViewById(R.id.blur_down);
        mButton = (Button)findViewById(R.id.button_1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySplitAnimationUtil.startActivity(MainActivity.this, new Intent(MainActivity.this, DetailCalonActivity.class));
            }
        });

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.bowo_hatta);
        blur(bitmap1, getApplicationContext(), 15f, new BitmapBlurCallback() {
            @Override
            public void bitmapBlurComplete(Bitmap bitmap) {
                mImageTop.setImageBitmap(bitmap);
            }
        });

        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.jokowi_kalla);
        blur(bitmap2, getApplicationContext(), 15f, new BitmapBlurCallback() {
            @Override
            public void bitmapBlurComplete(Bitmap bitmap) {
                mImageDown.setImageBitmap(bitmap);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    public void blur(final Bitmap bitmap, final Context context, final float radius, final BitmapBlurCallback callback) {
        mBlurTask = new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                if (sRS == null) {
                    sRS = RenderScript.create(context);
                }
                Allocation srcAlloc = Allocation.createFromBitmap(sRS, bitmap);
                Allocation dstAlloc = Allocation.createTyped(sRS, srcAlloc.getType());
                ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(sRS, Element.U8_4(sRS));
                script.setRadius(radius);
                script.setInput(srcAlloc);
                script.forEach(dstAlloc);

                Bitmap dst = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                dstAlloc.copyTo(dst);
                return dst;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                callback.bitmapBlurComplete(result);
                mBlurTask = null;
            }
        };
        mBlurTask.execute();
    }

    public interface BitmapBlurCallback {
        public void bitmapBlurComplete(Bitmap bitmap);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }
}
