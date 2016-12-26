package com.and2long.mplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by L on 2016/12/19.
 */

public class MarqeeTextView extends TextView {

    public MarqeeTextView(Context context) {
        super(context);
    }

    public MarqeeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqeeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
