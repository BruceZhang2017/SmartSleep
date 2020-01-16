package com.zhang.xiaofei.smartsleep.UI.Home;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.deadline.statebutton.StateButton;


public class CountDownButton extends StateButton {

    private Handler handler;

    public CountDownButton(Context context) {
        super(context);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                {
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
                break;
            case MotionEvent.ACTION_UP:
                {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                {
                    Message message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


}
