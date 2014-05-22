package com.indoelection.component;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by anton on 5/22/14.
 */
public class BounvenoTextView extends TextView {
    private Drawable[] drawables;

    public BounvenoTextView(Context context, AttributeSet as) {
        super(context, as);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        try {
            Typeface helveticaTF = Typeface.createFromAsset(
                    getContext().getAssets(), "fonts/Existence-Light.otf");
            super.setTypeface(helveticaTF);
        } catch (Exception e) {
            super.setTypeface(tf);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        drawables = getCompoundDrawables();
    }

    @Override
    public void setError(CharSequence error) {
        // TODO Auto-generated method stub
        super.setError(error);

        if (error == null) {
            restoreDrawables();
        }
    }

    /**
     * Restore drawable images.
     * Needed to prevent overriden drawable after setError called (JellyBean issue).
     */
    private void restoreDrawables() {
        if (drawables != null) {
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

}
