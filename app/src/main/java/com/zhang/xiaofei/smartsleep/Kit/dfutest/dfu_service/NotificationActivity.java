package com.zhang.xiaofei.smartsleep.Kit.dfutest.dfu_service;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;


import com.zhang.xiaofei.smartsleep.Kit.dfutest.BaseActivity;
import com.zhang.xiaofei.smartsleep.Kit.dfutest.DfuUpdateActivity;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent intent = new Intent(this, DfuUpdateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                intent.putExtras(bundle); // copy all extras
                startActivity(intent);
            }
        }

        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish();
    }
}
