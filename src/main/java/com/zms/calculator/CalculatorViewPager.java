
package com.zms.calculator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class CalculatorViewPager extends ViewPager {
    public CalculatorViewPager(Context context) {
        super(context);
    }

    public CalculatorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * ViewPager inherits ViewGroup's default behavior of delayed clicks
     * on its children, but in order to make the calc buttons more responsive
     * we disable that here.
     */
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
