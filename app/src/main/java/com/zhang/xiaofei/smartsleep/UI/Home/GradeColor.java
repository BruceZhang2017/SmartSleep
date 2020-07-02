package com.zhang.xiaofei.smartsleep.UI.Home;

import com.zhang.xiaofei.smartsleep.R;

class GradeColor {
    static int convertGradeToColor(int value) {
        if (value >= 85) {
            return R.color.color_6EE1CA;
        } else if (value >= 70) {
            return R.color.color_499BE5;
        } else if (value >= 50) {
            return R.color.color_626AEA;
        } else if (value > 0) {
            return R.color.color_F3D032;
        } else {
            return R.color.color_E92C2C;
        }
    }
}
