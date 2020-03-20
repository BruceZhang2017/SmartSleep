package com.sunofbeaches.himalaya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sunofbeaches.himalaya.interfaces.IPlayerCallback;
import com.sunofbeaches.himalaya.interfaces.IRecommendViewCallback;
import com.sunofbeaches.himalaya.presenters.PlayerPresenter;
import com.sunofbeaches.himalaya.presenters.RecommendPresenter;
import com.sunofbeaches.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.RecommendTrack;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public class PlayHelper implements IPlayerCallback, IRecommendViewCallback {

    public PlayerPresenter mPlayerPresenter;
    public PlayHelperCallback playHelperCallback;
    private Context context;
    private RecommendPresenter mRecommendPresenter;

    public boolean initPresenter(Context context) {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        this.context = context;

        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if(currentRecommend == null || currentRecommend.size() == 0) {
            mRecommendPresenter = RecommendPresenter.getInstance();
            mRecommendPresenter.registerViewCallback(this);
            mRecommendPresenter.getRecommendList();
            return true;
        }
        return false;
    }

    public void deinitPresenter() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    public boolean isPlaying() {
        if (mPlayerPresenter != null) {
            return mPlayerPresenter.isPlaying();
        }
        return false;
    }

    public String playTitle() {
        if (mPlayerPresenter != null) {
            if (mPlayerPresenter.mCurrentTrack != null) {
                return mPlayerPresenter.mCurrentTrack.getTrackTitle();
            }
        }
        return "";
    }

    public void play() {
        if(mPlayerPresenter != null) {
            boolean hasPlayList = mPlayerPresenter.hasPlayList();
            if(!hasPlayList) {
                //没有设置过播放列表，我们就播放默认的第一个推荐专辑
                //第一个推荐专辑，每天都会变的。
                playFirstRecommend();
            } else {
                if(mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    mPlayerPresenter.play();
                }
            }
        }
    }

    public void playNext() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.playNext();
        }
    }

    public void playPre() {
        //播放前一个节目
        if (mPlayerPresenter != null) {
            mPlayerPresenter.playPre();
        }
    }

    public static void stop() {
        if (PlayerPresenter.getPlayerPresenter().isPlaying()) {
            PlayerPresenter.getPlayerPresenter().pause();
        }
    }

    /**
     * 播放第一个推荐的内容.
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommend();
        if(currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        } else {
            //获取到逻辑层的对象
            mRecommendPresenter = RecommendPresenter.getInstance();
            //先要设置通知接口的注册
            mRecommendPresenter.registerViewCallback(this);
            //获取推荐列表
            mRecommendPresenter.getRecommendList();
        }
        //System.out.println("当前播放的Recommend数量为：" + currentRecommend.size());
    }


    @Override
    public void onPlayStart() {
        if (playHelperCallback != null) {
            playHelperCallback.refreshPlayStatus(true);
        }
    }

    @Override
    public void onPlayPause() {
        if (playHelperCallback != null) {
            playHelperCallback.refreshPlayStatus(false);
        }
    }

    @Override
    public void onPlayStop() {
        if (playHelperCallback != null) {
            playHelperCallback.refreshPlayStatus(false);
        }
    }

    @Override
    public void onPlayError() {
        if (playHelperCallback != null) {
            playHelperCallback.playFail();
        }
    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if(track != null) {
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            if (playHelperCallback != null) {
                playHelperCallback.refreshPlayTitle(trackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        System.out.println("下载中");
        if (playHelperCallback != null) {
            playHelperCallback.callbackHideHUD(false);
        }
    }

    @Override
    public void onNetworkError() {
        System.out.println("网络故障");
        if (playHelperCallback != null) {
            playHelperCallback.callbackHideHUD(true);
        }
    }

    @Override
    public void onEmpty() {
        if (playHelperCallback != null) {
            playHelperCallback.callbackHideHUD(false);
        }
    }

    @Override
    public void onLoading() {

    }
}

