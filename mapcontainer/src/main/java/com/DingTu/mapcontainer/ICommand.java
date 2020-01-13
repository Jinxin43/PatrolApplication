package com.DingTu.mapcontainer;

/**
 * Created by Dingtu2 on 2017/5/31.
 */

import android.view.MotionEvent;

public interface ICommand {
    void MouseDown(MotionEvent e);
    void MouseMove(MotionEvent e);
    void MouseUp(MotionEvent e);
}
