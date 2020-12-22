package kr.anymobi.floproject.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.IOException;

import kr.anymobi.floproject.rxutil.RxEventBusMusic;
import kr.anymobi.floproject.util.CommFunc;

import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_COMPLETION;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_ERROR;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_PREPARED;

//TODO 오디오 포커스 처리
public class MusicService extends Service {
    MediaPlayer mMediaPlayer;
    private final IBinder mBinder = new MusicServiceBinder();
    private boolean loopState = false;
    private boolean stopFlag = false;
    private String url = "";


    public class MusicServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = "";
        if (intent != null) {
            url = intent.getStringExtra("location");
            if (!TextUtils.isEmpty(url)) {
                setPlayerMusic(url);
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void setPlayerMusic(String location) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(location);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                RxEventBusMusic.getInstance().sendData(MUSIC_SERVICE_PREPARED);
                stopFlag = false;
                mMediaPlayer.start();
            });

            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                RxEventBusMusic.getInstance().sendData(MUSIC_SERVICE_COMPLETION);
                if (loopState) {
                    // 반복 재생
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                } else {
                    stopFlag = true;
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mMediaPlayer = null;
                }
            });

            mMediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
                RxEventBusMusic.getInstance().sendData(MUSIC_SERVICE_ERROR);
                mediaPlayer.stop();
                mediaPlayer.release();
                return false;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 재생 위치 파악
    public int getCurrentPosition() {
        if (mMediaPlayer == null)
            return -1;

        return mMediaPlayer.getCurrentPosition();
    }

    // 재생 상태 파악
    public boolean getPlayerState() {
        if (mMediaPlayer != null)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    // 재생 위치 설정
    public void setPosition(int pos) {
        if (mMediaPlayer != null)
            if (mMediaPlayer.isPlaying())
                mMediaPlayer.seekTo(pos);
    }

    // 재생 시작
    public void setStartMedia() {
        if (mMediaPlayer != null && !stopFlag)
            mMediaPlayer.start();
        else if (mMediaPlayer == null) {
            setPlayerMusic(url);
        }
    }

    // 재생 일시정지
    public void setPauseMedia() {
        if (mMediaPlayer != null)
            mMediaPlayer.pause();
    }

    // 재생 반복 설정
    public void setLoopState(boolean flag) {
        loopState = flag;
    }

    // 재생 종료
    public void releaseMedia() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    // 재생 반복 설정 반환
    public boolean getLoopState() {
        return loopState;
    }

    // 재생 구간 이동
    public void seekTo(String timeZone) {
        if (mMediaPlayer != null)
            mMediaPlayer.seekTo(CommFunc.getTimeFromPattern(timeZone));
    }

    private void allStop() {
        // 서비스 종료 시 mediaPlayer 처리
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        stopSelf();
    }

    // 서비스 종료 처리
    @Override
    public void onDestroy() {
        super.onDestroy();
        allStop();
    }
}
