package kr.anymobi.floproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import kr.anymobi.floproject.R;
import kr.anymobi.floproject.data.LyricsData;
import kr.anymobi.floproject.data.MusicData;
import kr.anymobi.floproject.fragments.LyricsDetailFragment;
import kr.anymobi.floproject.fragments.MusicPlayerFragment;
import kr.anymobi.floproject.network.RetrofitClient;
import kr.anymobi.floproject.network.RetrofitService;
import kr.anymobi.floproject.rxutil.RxEventBusMusic;
import kr.anymobi.floproject.service.Const;
import kr.anymobi.floproject.service.MusicService;
import kr.anymobi.floproject.service.MusicServiceBinder;
import kr.anymobi.floproject.util.CommFunc;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static kr.anymobi.floproject.service.Const.LYRICS_TIME_PATTERN;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_COMPLETION;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_ERROR;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_PREPARED;
import static kr.anymobi.floproject.service.Const.MUSIC_SERVICE_PREPARED_MSG;
import static kr.anymobi.floproject.service.Const.PLAYER_TIME_PATTERN;

public class PlayerActivity extends AppCompatActivity {

    private TestHandler m_objFragmentBaseHandler;
    private MusicServiceBinder musicServiceBinder;
    MusicPlayerFragment musicPlayerFragment;
    LyricsDetailFragment lyricsDetailFragment;

    Timer m_objSeekBarUpdateTimer;

    Disposable disposable;
    MusicData musicData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        m_objFragmentBaseHandler = new TestHandler(getMainLooper());
        musicServiceBinder = new MusicServiceBinder(this);
        m_objSeekBarUpdateTimer = new Timer();

        singleNetwork();
        try {
            eventBusStation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicService(String location) {
        musicServiceBinder = getMusicServiceBinder();
        Intent intent = new Intent(PlayerActivity.this, MusicService.class);
        intent.putExtra("location", location);
        startService(intent);
    }

    public MusicServiceBinder getMusicServiceBinder() {
        if (musicServiceBinder == null)
            musicServiceBinder = new MusicServiceBinder(this);

        return musicServiceBinder;
    }

    @SuppressLint("CheckResult")
    private void eventBusStation() {
        disposable = RxEventBusMusic.getInstance().getObjectObservable().subscribe(status -> {
            switch ((String) status) {
                case MUSIC_SERVICE_PREPARED:
                    m_objSeekBarUpdateTimer = new Timer();
                    m_objSeekBarUpdateTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = Message.obtain();
                            msg.what = MUSIC_SERVICE_PREPARED_MSG;
                            m_objFragmentBaseHandler.sendMessage(msg);
                        }
                    }, 0, 200);
                    break;
                case MUSIC_SERVICE_COMPLETION:
                    boolean loopFlag = musicServiceBinder.getLoopState();
                    // 재생 완료 처리
                    if (musicPlayerFragment != null) {
                        if (!musicServiceBinder.getPlayerState() && loopFlag) {
                            musicPlayerFragment.setPlayToggle();
                        } else if (!musicServiceBinder.getPlayerState()) {
                            m_objSeekBarUpdateTimer.cancel();
                            musicPlayerFragment.playBtnToggle(false);
                        }
                    }
                    if (lyricsDetailFragment != null) {
                        if (!musicServiceBinder.getPlayerState() && loopFlag)
                            lyricsDetailFragment.setPlayToggle();
                        else if (!musicServiceBinder.getPlayerState()) {
                            m_objSeekBarUpdateTimer.cancel();
                            lyricsDetailFragment.playBtnToggle(false);
                        }
                    }
                    break;
                case MUSIC_SERVICE_ERROR:
                    Toast.makeText(PlayerActivity.this, "에러 발생!!!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.d("jongwoo", status + " data");
                    break;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }

        if (m_objSeekBarUpdateTimer != null) {
            m_objSeekBarUpdateTimer.cancel();
        }

        super.onDestroy();
        stopService(new Intent(PlayerActivity.this, MusicService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void singleNetwork() {
        Retrofit retrofitClient = RetrofitClient.getInstance(Const.BASE_URL);
        Call<MusicData> call = retrofitClient.create(RetrofitService.class).requestMusic();
        call.enqueue(new Callback<MusicData>() {
            @Override
            public void onResponse(Call<MusicData> call, Response<MusicData> response) {
                int statusCode = response.code();

                if (statusCode != 200)
                    return;

                musicData = response.body();
                if (musicData != null) {
                    initFragment(musicData);
                    startMusicService(musicData.file);
                }
            }

            @Override
            public void onFailure(Call<MusicData> call, Throwable t) {
                Toast.makeText(PlayerActivity.this, "네트워크 통신에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void initFragment(MusicData musicData) {
        musicPlayerFragment = MusicPlayerFragment.newInstance(musicData);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragmentContainer, musicPlayerFragment, "musicPlayerFragment").commit();
    }

    public void openFragment(ArrayList<LyricsData> data, MusicData musicData) {
        lyricsDetailFragment = LyricsDetailFragment.newInstance(data, musicData);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_down, R.anim.exit_to_down, R.anim.enter_from_down, R.anim.exit_to_down);
        transaction.addToBackStack(null);
        transaction.add(R.id.fragmentContainer, lyricsDetailFragment, "lyricsDetailFragment").commit();
    }

    @SuppressLint("HandlerLeak")
    public class TestHandler extends Handler {

        public TestHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MUSIC_SERVICE_PREPARED_MSG) {
                int nMillisecond = musicServiceBinder.getCurrentPosition();
                if (musicPlayerFragment != null && getMusicServiceBinder().getPlayerState()) {
                    musicPlayerFragment.seekBar.setProgress(nMillisecond);
                    musicPlayerFragment.setCurrentTimePosition(CommFunc.getTimeData(nMillisecond, PLAYER_TIME_PATTERN)); // 현재 재생중인 시간 표시.
                    musicPlayerFragment.setLyrics(CommFunc.getTimeData(nMillisecond, LYRICS_TIME_PATTERN)); // 현재 위치를 기반으로 가사를 세팅 함.
                } else if (musicPlayerFragment != null) {
                    musicPlayerFragment.playBtnToggle(false);
                }

                if (lyricsDetailFragment != null && getMusicServiceBinder().getPlayerState()) {

                    if (lyricsDetailFragment.seekBar != null)
                        lyricsDetailFragment.seekBar.setProgress(nMillisecond);

                    lyricsDetailFragment.setCurrentTimePosition(CommFunc.getTimeData(nMillisecond, PLAYER_TIME_PATTERN)); // 현재 재생중인 시간 표시.
                    lyricsDetailFragment.setLyrics(CommFunc.getTimeData(nMillisecond, LYRICS_TIME_PATTERN)); // 현재 위치를 기반으로 가사를 세팅 함.
                } else if (lyricsDetailFragment != null) {
                    lyricsDetailFragment.playBtnToggle(false);
                }
            }
        }
    }
}