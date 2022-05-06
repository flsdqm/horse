package com.example.horsey;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BgmService extends Service {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying=true;
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if (mediaPlayer==null){
            mediaPlayer=new MediaPlayer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("signal",1)){
            case 1:
                if (isPlaying==false){
                    //重置mediaplayer
                    mediaPlayer.reset();
                    //将需要播放的资源与之绑定
                    mediaPlayer=MediaPlayer.create(this,R.raw.davidhoffman);
                    //是否循环播放
                    mediaPlayer.setLooping(true);
                    //开始播放
                    mediaPlayer.start();
                    System.out.println("已播放");
                    isPlaying=true;
                }else if (!(!isPlaying)&&mediaPlayer.isPlaying()&&mediaPlayer!=null){
                    mediaPlayer.start();
                }
                break;
            case 0:
                //播放器不为空，并且正在播放
                if (mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    isPlaying=false;
                }
                break;
        }
        return START_NOT_STICKY;
    }

}
