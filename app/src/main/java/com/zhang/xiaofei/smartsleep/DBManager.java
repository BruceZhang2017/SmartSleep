package com.zhang.xiaofei.smartsleep;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Message;
import android.util.Log;

import android.os.Handler;

public class DBManager {


    private static final String TAG = "AudioRecord";
    private static final int SAMPLE_RATE_IN_HZ = 8000;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord mAudioRecord;
    private boolean isGetVoiceRun;
    private Object mLock;

    private int currentDB;

    public int getCurrentDB() {
        return currentDB;
    }

    private static DBManager instance;

    private DBManager() {
        mLock = new Object();
    }

    public Handler handler;

    public static DBManager getInstance() {

        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public void stopRecord() {
        currentDB = 0;
        isGetVoiceRun = false;
    }

    public void startRecord() {
        if (isGetVoiceRun) {
            return;
        }
        if (mAudioRecord == null) {
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        }
        isGetVoiceRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startRecording();
            }
        }).start();

    }

    private void startRecording() {
        try {
            mAudioRecord.startRecording();
            short[] buffer = new short[BUFFER_SIZE];
            while (isGetVoiceRun) {
                //r是实际读取的数据长度，一般而言r会小于buffersize
                int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                long v = 0;
                // 将 buffer 内容取出，进行平方和运算
                for (int i = 0; i < buffer.length; i++) {
                    v += buffer[i] * buffer[i];
                }
                // 平方和除以数据总长度，得到音量大小。
                double mean = v / (double) r;
                double volume = 10 * Math.log10(mean);
                Log.e(TAG, "分贝值:" + volume);
                Message message = Message.obtain();
                message.obj = (int)volume;
                handler.sendMessage(message);
                currentDB = (int) volume;
                // 大概一秒二次
                synchronized (mLock) {
                    try {
                        mLock.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}