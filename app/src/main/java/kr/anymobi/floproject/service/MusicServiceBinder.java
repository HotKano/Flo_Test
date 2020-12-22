package kr.anymobi.floproject.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MusicServiceBinder {
    private ServiceConnection serviceConnection;
    private MusicService musicService;

    public MusicServiceBinder(Context ctx) {
        this.serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                musicService = ((MusicService.MusicServiceBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serviceConnection = null;
                musicService = null;
            }
        };

        Intent objIntent = new Intent(ctx, MusicService.class);
        ctx.bindService(objIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public int getCurrentPosition() {
        if (musicService == null)
            return 0;

        return musicService.getCurrentPosition();
    }

    public boolean getPlayerState() {
        if (musicService == null)
            return false;

        return musicService.getPlayerState();
    }

    public void setPosition(int pos) {
        if (musicService == null)
            return;

        musicService.setPosition(pos);
    }

    // 시작
    public void setStartMedia() {
        if (musicService == null)
            return;

        musicService.setStartMedia();
    }

    // 일시정지
    public void setPauseMedia() {
        if (musicService == null)
            return;

        musicService.setPauseMedia();
    }

    // 반복 유무 설정
    public void setLoopState(boolean flag) {
        if (musicService == null)
            return;

        musicService.setLoopState(flag);
    }

    // 반복 유무 설정 가져오기
    public boolean getLoopState() {
        if (musicService == null)
            return false;

        return musicService.getLoopState();
    }


    // 시간 이동
    public void seekTo(String timeZone) {
        if (musicService == null)
            return;

        musicService.seekTo(timeZone);
    }
}
