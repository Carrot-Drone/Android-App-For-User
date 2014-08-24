package com.lchpartners.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lchpartners.shadal.R;

/**
 * TextView styled with Seoul Namsan Font family.
 * Created by Gwangrae Kim on 2014-08-24.
 */
public class NamsanTextView extends TextView {
    private final static String TAG = "NamsanTextView";

    public final static int WEIGHT_LIGHT = 0;
    private static Typeface light = null;
    public final static int WEIGHT_MEDIUM = 1;
    private static Typeface medium = null;
    public final static int WEIGHT_BOLD = 2;
    private static Typeface bold = null;
    public final static int WEIGHT_EXTRA_BOLD = 3;
    private static Typeface extraBold = null;

    public NamsanTextView(Context context) {
        super(context);
    }
    public NamsanTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setNamsanFont(context, attrs);
    }
    public NamsanTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        setNamsanFont(context, attrs);
    }

    public NamsanTextView setNamsanFont (Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NamsanTextView);
        int weight = ta.getInt(R.styleable.NamsanTextView_weight, 0);
        return setNamsanFont(context, weight);
    }

    public NamsanTextView setNamsanFont (Context context, int weight) {
        try {
            Typeface tf = medium;

            if(medium == null) {
                light = Typeface.createFromAsset(context.getAssets(),"fonts/namsan0.ttf");
                medium = Typeface.createFromAsset(context.getAssets(),"fonts/namsan1.ttf");
                bold = Typeface.createFromAsset(context.getAssets(),"fonts/namsan2.ttf");
                extraBold = Typeface.createFromAsset(context.getAssets(),"fonts/namsan3.ttf");
            }

            switch (weight) {
                case WEIGHT_MEDIUM :
                    tf = medium;
                    break;
                case WEIGHT_BOLD :
                    tf = bold;
                    break;
                case WEIGHT_EXTRA_BOLD :
                    tf = extraBold;
                    break;
                case WEIGHT_LIGHT :
                    tf = light;
                    break;
            }

            setTypeface(tf);
        }
        catch(Exception e) {}
        return this;
    }
}
