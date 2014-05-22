package com.indoelection.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

import com.indoelection.R;
import com.indoelection.util.ActivitySplitAnimationUtil;

/**
 * Created by anton on 5/22/14.
 */
public class DetailCalonActivity extends Activity {

    private ImageView mImageTop;
    private AsyncTask<Void, Void, Bitmap> mBlurTask;
    private RenderScript sRS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplitAnimationUtil.prepareAnimation(this);
        setContentView(R.layout.detail_calon_activity);

        mImageTop = (ImageView)findViewById(R.id.blur_top);

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bowo_hatta);
        blur(bitmap1, getApplicationContext(), 15f, new BitmapBlurCallback() {
            @Override
            public void bitmapBlurComplete(Bitmap bitmap) {
                mImageTop.setImageBitmap(bitmap);
            }
        });

        ActivitySplitAnimationUtil.animate(this, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivitySplitAnimationUtil.cancel();
    }

    public interface BitmapBlurCallback {
        public void bitmapBlurComplete(Bitmap bitmap);
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

}
