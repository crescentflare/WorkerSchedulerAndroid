package com.crescentflare.workerschedulerexample.utility;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crescentflare.workerscheduler.Worker;

/**
 * The example logger is an easy way to add log lines to the scroll view
 */
public class ExampleLogger
{
    private LinearLayout layoutContainer = null;
    private ScrollView scrollView = null;

    public ExampleLogger(LinearLayout layoutContainer)
    {
        this.layoutContainer = layoutContainer;
        if (layoutContainer.getParent() != null && layoutContainer.getParent() instanceof ScrollView)
        {
            scrollView = (ScrollView)layoutContainer.getParent();
        }
    }

    public Context getContext()
    {
        return layoutContainer.getContext();
    }

    public void log(String message)
    {
        TextView textView = new TextView(layoutContainer.getContext());
        textView.setText(message);
        this.layoutContainer.addView(textView);
        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
