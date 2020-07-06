package com.loonggg.lib.alarmmanager.clock;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerUtil {
    private MediaPlayer mRingPlayer;
    private static MediaPlayerUtil cUtil;

    public static MediaPlayerUtil getInstance() {
        if (cUtil == null) {
            synchronized (MediaPlayerUtil.class) {
                if (cUtil == null) {
                    cUtil = new MediaPlayerUtil();
                }
            }
        }
        return cUtil;
    }


    /**
     * 播放铃声
     */
    public void startRing(Context context, int postion){
        if (mRingPlayer != null){
            mRingPlayer.stop();
            mRingPlayer.release();
            mRingPlayer = null;
        }
        int sound = 0;
        if (postion == 0) {
            sound = R.raw.sound1;
        } else if (postion == 1) {
            sound = R.raw.sound2;
        } else if (postion == 2) {
            sound = R.raw.sound3;
        } else {
            sound = R.raw.sound4;
        }
        mRingPlayer = MediaPlayer.create(context, sound);
        mRingPlayer.setLooping(false);
        mRingPlayer.start();

    }

    /**
     * 停止铃声
     */
    public void stopRing(){
        if (mRingPlayer != null){
            mRingPlayer.stop();
            mRingPlayer.release();
            mRingPlayer = null;
        }
    }
}
