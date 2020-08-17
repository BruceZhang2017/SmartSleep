package com.zhang.xiaofei.smartsleep.UI.music;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Login.BaseAppActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.util.ArrayList;

public class MainMusicActivity extends BaseAppActivity {

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.zhang.xiaofei.smartsleep.PlayNewAudio";
    private ImageButton ibLeft;
    boolean serviceBound = false;
    ArrayList<Audio> audioList = new ArrayList<>();
    private TextView tvTitle;
    private TextView tvDesc;
    RecyclerView_Adapter adapter;
    private MyBroadcastReciver reciver;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_main);

        int title = getIntent().getIntExtra("title", 0);
        int message = getIntent().getIntExtra("message", 0);
        int position = getIntent().getIntExtra("position", 0);
        currentPosition = position;
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int a = (position - 1) * 3 + 1;
        int b = a + 1;
        int c = b + 1;
        String strMusicA = "";
        String strMusicB = "";
        String strMusicC = "";
        if (a == 1) {
            strMusicA = getResources().getString(R.string.music_1_1);
            strMusicB = getResources().getString(R.string.music_1_2);
            strMusicC = getResources().getString(R.string.music_1_3);
        } else if (a == 4) {
            strMusicA = getResources().getString(R.string.music_1_4);
            strMusicB = getResources().getString(R.string.music_1_5);
            strMusicC = getResources().getString(R.string.music_1_6);
        } else {
            strMusicA = getResources().getString(R.string.music_1_7);
            strMusicB = getResources().getString(R.string.music_1_8);
            strMusicC = getResources().getString(R.string.music_1_9);
        }
        audioList.add(new Audio("http://cloud.yamind.cn/appResource/music/type1/music" + a + ".mp3", strMusicA, "", "Yamind"));
        audioList.add(new Audio("http://cloud.yamind.cn/appResource/music/type1/music" + b + ".mp3", strMusicB, "", "Yamind"));
        audioList.add(new Audio("http://cloud.yamind.cn/appResource/music/type1/music" + c + ".mp3", strMusicC, "", "Yamind"));

        loadAudioList();

        tvTitle = (TextView)findViewById(R.id.tv_subtitle);
        tvDesc = (TextView)findViewById(R.id.tv_desc);
        tvTitle.setText(title);
        tvDesc.setText(message);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.abel.action.broadcast");
        reciver = new MyBroadcastReciver();
        this.registerReceiver(reciver, intentFilter);


    }

    private void loadAudioList() {
        initRecyclerView();
    }


    private void initRecyclerView() {
        if (audioList != null && audioList.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            adapter = new RecyclerView_Adapter(audioList, getApplication());
            if (YMApplication.getInstance().player != null && YMApplication.getInstance().player.playing && YMApplication.getInstance().player.currentPosition == currentPosition) {
                adapter.playStatus = 1;
                StorageUtil storage = new StorageUtil(getApplicationContext());
                adapter.currentIndex = storage.loadAudioIndex();
            }
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addOnItemTouchListener(new Audio.CustomTouchListener(this, new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    int status = adapter.playStatus;
                    if (status == 0 || adapter.currentIndex != index) {
                        adapter.currentIndex = index;
                        playAudio(index);
                    } else {
                        if (YMApplication.getInstance().player != null) {
                            YMApplication.getInstance().player.pauseMedia();
                            adapter.playStatus = 0;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            YMApplication.getInstance().player = binder.getService();
            YMApplication.getInstance().player.currentPosition = currentPosition;
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };


    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            System.out.println("走服务");
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            System.out.println("走广播");
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
        unregisterReceiver(reciver);
    }

    private class MyBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("cn.abel.action.broadcast")) {
                int status = intent.getIntExtra("status", 0);
                System.out.println("收到的通知:" + status);
                adapter.playStatus = status;
                adapter.notifyDataSetChanged();
            }
        }

    }
}
