package com.cheekybastards.ftw.model;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by surge on 6/1/15.
 */

public class PrefCategory extends PreferenceCategory {
    public PrefCategory(Context context) {
        super(context);
    }

    public PrefCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrefCategory(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.parseColor("#0099CC"));
    }
}