package com.ximalaya.ting.android.opensdk.test;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.ximalaya.ting.android.opensdk.test.util.ToolUtil;
import com.ximalaya.ting.android.opensdk.util.NetworkType;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * ClassName:MainFragmentActivity
 *
 * @author jack.qin
 * @Date 2015-5-25 下午5:51:12
 * @see
 * @since Ver 1.1
 */
public class MainFragmentActivity {

    private static final String TAG = "MainFragmentActivity";

    public Activity activity;
    public Handler handler;

    private XmPlayerManager mPlayerManager;

    private boolean mUpdateProgress = true;
    private TrackList mTrackHotList = null;
    private static final int noCrashNotifation = 10000;
    private boolean loadFinished = false;

    public void onCreate() {
        mPlayerManager = XmPlayerManager.getInstance(activity);

        boolean useXmPlayNotification = true;

        if(useXmPlayNotification) {
            Notification mNotification = XmNotificationCreater.getInstanse(activity).initNotification(activity.getApplicationContext(), activity.getClass());
            mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
        } else {
            mPlayerManager.init();
        }

        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);
        mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
            @Override
            public void onConnected() {
                mPlayerManager.removeOnConnectedListerner(this);

                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST);
                //Toast.makeText(activity, "播放器初始化成功", Toast.LENGTH_SHORT).show();
            }
        });

        // 此代码表示播放时会去监测下是否已经下载
        XmPlayerManager.getInstance(activity).setCommonBusinessHandle(XmDownloadManager.getInstance());
        ActivityCompat.requestPermissions(activity ,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE} ,0);

        loadData();
    }

    public void onDestroy() {
        if (mPlayerManager != null) {
            mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        }
        XmPlayerManager.release();
        CommonRequest.release();
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
            Log.i(TAG, "onSoundPrepared");
            //mSeekBar.setEnabled(true);
            //mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            Log.i(TAG, "onSoundSwitch index:" + curModel);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                if (model instanceof Track) {
                    Track info = (Track) model;
                    title = info.getTrackTitle();
                    coverUrl = info.getCoverUrlLarge();
                } else if (model instanceof Schedule) {
                    Schedule program = (Schedule) model;
                    title = program.getRelatedProgram().getProgramName();
                    coverUrl = program.getRelatedProgram().getBackPicUrl();
                } else if (model instanceof Radio) {
                    Radio radio = (Radio) model;
                    title = radio.getRadioName();
                    coverUrl = radio.getCoverUrlLarge();
                }
                //mTextView.setText(title);
                //x.image().bind(mSoundCover ,coverUrl);
                Message message = new Message();
                message.what = 1;
                message.obj = title;
                handler.sendMessage(message);
            }
            updateButtonStatus();
        }

        private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                Message message = new Message(); // 上一首 可点击
                message.what = 2;
                message.arg1 = 1;
                handler.sendMessage(message);
            } else {
                Message message = new Message(); // 上一首 不可点击
                message.what = 2;
                message.arg1 = 0;
                handler.sendMessage(message);
            }
            if (mPlayerManager.hasNextSound()) {
                Message message = new Message(); // 下一首 可点击
                message.what = 3;
                message.arg1 = 1;
                handler.sendMessage(message);
            } else {
                Message message = new Message(); // 下一首 不可点击
                message.what = 3;
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onPlayStop() {
            Log.i(TAG, "onPlayStop");
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 3;
            handler.sendMessage(message);
        }

        @Override
        public void onPlayStart() {
            Log.i(TAG, "onPlayStart");
            Message message = new Message(); // 可暂停
            message.what = 4;
            message.arg1 = 2;
            handler.sendMessage(message);

        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null) {
                if (info instanceof Track) {
                    title = ((Track) info).getTrackTitle();
                } else if (info instanceof Schedule) {
                    title = ((Schedule) info).getRelatedProgram().getProgramName();
                } else if (info instanceof Radio) {
                    title = ((Radio) info).getRadioName();
                }
            }
            System.out.println(title);
            System.out.println("[" + ToolUtil.formatTime(currPos) + "/" + ToolUtil.formatTime(duration) + "]");
            if (mUpdateProgress && duration != 0) {
                //mSeekBar.setProgress((int) (100 * currPos / (float) duration));
            }
            if(info instanceof Track) {
                System.out.println("MainFragmentActivity.onPlayProgress  " + currPos + "   " + duration + "   " + ((Track) info).getDuration());
            }
        }

        @Override
        public void onPlayPause() {
            Log.i(TAG, "onPlayPause");
            //mBtnPlay.setImageResource(R.drawable.widget_play_normal);
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 3;
            handler.sendMessage(message);
        }

        @Override
        public void onSoundPlayComplete() {
            Log.i(TAG, "onSoundPlayComplete");
            //Toast.makeText(MainFragmentActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            //mBtnPlay.setImageResource(R.drawable.widget_play_normal);
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 3;
            handler.sendMessage(message);
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            Log.i(TAG, "XmPlayerException = onError " + exception.getMessage() + "   " + XmPlayerManager.getInstance(activity).isPlaying());

            System.out.println("MainFragmentActivity.onError   "+ exception);
            //mBtnPlay.setImageResource(R.drawable.widget_play_normal);
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 3;
            handler.sendMessage(message);

            if(!NetworkType.isConnectTONetWork(activity)) {
                Toast.makeText(activity, "没有网络导致停止播放", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            //mSeekBar.setSecondaryProgress(position);
            System.out.println("MainFragmentActivity.onBufferProgress   " + position);
        }

        public void onBufferingStart() {
            Log.i(TAG, "onBufferingStart   " + XmPlayerManager.getInstance(activity).isPlaying());
            //mSeekBar.setEnabled(false);
            //mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            Log.i(TAG, "onBufferingStop");
            //mSeekBar.setEnabled(true);
            //mProgress.setVisibility(View.GONE);
        }

    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 2;
            handler.sendMessage(message);
        }

        @Override
        public void onStartGetAdsInfo() {
            Log.i(TAG, "onStartGetAdsInfo");
            //mBtnPlay.setEnabled(false);
            //mSeekBar.setEnabled(false);
            Message message = new Message(); // 不可播放
            message.what = 4;
            message.arg1 = 0;
            handler.sendMessage(message);
        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {

        }

        @Override
        public void onError(int what, int extra) {
            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
            Log.i(TAG, "onCompletePlayAds");
            //mBtnPlay.setEnabled(true);
            //mSeekBar.setEnabled(true);
            Message message = new Message(); // 可播放
            message.what = 4;
            message.arg1 = 1;
            handler.sendMessage(message);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null && model instanceof Track) {
                //x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
            }
        }

        @Override
        public void onAdsStopBuffering() {
            Log.i(TAG, "onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
            Log.i(TAG, "onAdsStartBuffering");
        }
    };

    public Boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }

    public void play() {
        if (loadFinished == false){
            int size = mTrackHotList.getTracks().size();
            Random rand = new Random();
            mPlayerManager.playList(mTrackHotList.getTracks() ,rand.nextInt(size));

            List<Track> playList = XmPlayerManager.getInstance(activity).getPlayList();
            System.out.println("MainFragmentActivity.onOptionsItemSelected   ===   " + playList);
        }
        loadFinished = true;
        mPlayerManager.play();
    }

    public void pause() {
        mPlayerManager.pause();
    }

    public void playPre() {
        mPlayerManager.playPre();
    }

    public void playNext() {
        mPlayerManager.playNext();
    }

    private void loadData() {
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.ALBUM_ID, DTransferConstants.isRelease ? "3475911" : "98768");
        param.put(DTransferConstants.PAGE_SIZE , "10");
        param.put(DTransferConstants.PAGE ,"1");
        CommonRequest.getTracks(param, new IDataCallBack<TrackList>() {

            @Override
            public void onSuccess(TrackList object) {
                Log.e(TAG, "onSuccess " + (object != null));
                if (object != null && object.getTracks() != null && object.getTracks().size() != 0) {
                    if (mTrackHotList == null) {
                        mTrackHotList = object;
                        System.out.println("MainFragmentActivity  ===   " + mTrackHotList.getTracks());
                    }
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "onError " + code + ", " + message);
            }
        });
    }

}
