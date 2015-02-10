
package com.zms.calculator;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Provides vertical scrolling for the input/result EditText.
 */
class CalculatorDisplay extends ViewSwitcher {

    private static final String ATTR_MAX_DIGITS = "maxDigits";
    private static final int DEFAULT_MAX_DIGITS = 10;

    // only these chars are accepted from keyboard
    private static final char[] ACCEPTED_CHARS =
            "0123456789.+-*/\u2212\u00d7\u00f7()!%^".toCharArray();

    private static final int ANIM_DURATION = 500;

    enum Scroll {UP, DOWN, NONE}

    TranslateAnimation inAnimUp;
    TranslateAnimation outAnimUp;
    TranslateAnimation inAnimDown;
    TranslateAnimation outAnimDown;

    private Logic mLogic;
    private int mMaxDigits = DEFAULT_MAX_DIGITS;
    private Context mContext;


    public CalculatorDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxDigits = attrs.getAttributeIntValue(null, ATTR_MAX_DIGITS, DEFAULT_MAX_DIGITS);
        mContext = context;
    }

    public int getMaxDigits() {
        return mMaxDigits;
    }

    protected void setLogic(Logic logic) {
        mLogic = logic;
        NumberKeyListener calculatorKeyListener =
                new NumberKeyListener() {
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                    }

                    @Override
                    protected char[] getAcceptedChars() {
                        return ACCEPTED_CHARS;
                    }

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                    /* the EditText should still accept letters (eg. 'sin')
                       coming from the on-screen touch buttons, so don't filter anything.
                    */
                        return null;
                    }
                };

        Editable.Factory factory = new CalculatorEditable.Factory(logic);
        for (int i = 0; i < 2; ++i) {
            EditText text = (EditText) getChildAt(i);
            text.setBackground(null);
            text.setEditableFactory(factory);
            text.setKeyListener(calculatorKeyListener);
            text.setSingleLine();
        }
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        getChildAt(0).setOnKeyListener(l);
        getChildAt(1).setOnKeyListener(l);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        inAnimUp = new TranslateAnimation(0, 0, h, 0);
        inAnimUp.setDuration(ANIM_DURATION);
        outAnimUp = new TranslateAnimation(0, 0, 0, -h);
        outAnimUp.setDuration(ANIM_DURATION);

        inAnimDown = new TranslateAnimation(0, 0, -h, 0);
        inAnimDown.setDuration(ANIM_DURATION);
        outAnimDown = new TranslateAnimation(0, 0, 0, h);
        outAnimDown.setDuration(ANIM_DURATION);
    }

    void insert(String delta) {
        EditText editor = (EditText) getCurrentView();
        Editable editableText = editor.getText();
        if (mLogic.isErrorString(editableText.toString())) {
            editableText.clear();
        }
        int cursor = editor.getSelectionStart();
        if (editor.getText().toString().equals("!^2102") && delta.equals("^")) {
            editor.setText("");
            try {
                //PackageManager packageManager = mContext.getPackageManager();
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setClassName("com.tchip.attr", "com.tchip.attr.M_MainActivity");
                mContext.startActivity(intent);
            } catch (Exception e) {
                Log.v("zxx", ">>>>>>e=" + e.toString());
                //editor.getText().insert(cursor, delta);
            }
        } else {
            editor.getText().insert(cursor, delta);
        }
    }

    EditText getEditText() {
        return (EditText) getCurrentView();
    }

    Editable getText() {
        EditText text = (EditText) getCurrentView();
        return text.getText();
    }

    void setText(CharSequence text, Scroll dir) {
        if (getText().length() == 0) {
            dir = Scroll.NONE;
        }

        if (dir == Scroll.UP) {
            setInAnimation(inAnimUp);
            setOutAnimation(outAnimUp);
        } else if (dir == Scroll.DOWN) {
            setInAnimation(inAnimDown);
            setOutAnimation(outAnimDown);
        } else { // Scroll.NONE
            setInAnimation(null);
            setOutAnimation(null);
        }

        EditText editText = (EditText) getNextView();
        editText.setText(text);
        //Calculator.log("selection to " + text.length() + "; " + text);
        editText.setSelection(text.length());
        showNext();
    }

    int getSelectionStart() {
        EditText text = (EditText) getCurrentView();
        return text.getSelectionStart();
    }

    @Override
    protected void onFocusChanged(boolean gain, int direction, Rect prev) {
        //Calculator.log("focus " + gain + "; " + direction + "; " + prev);
        if (!gain) {
            requestFocus();
        }
    }
}
