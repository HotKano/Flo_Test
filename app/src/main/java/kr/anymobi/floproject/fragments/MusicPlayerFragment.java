package kr.anymobi.floproject.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.anymobi.floproject.R;
import kr.anymobi.floproject.activities.PlayerActivity;
import kr.anymobi.floproject.data.LyricsData;
import kr.anymobi.floproject.data.MusicData;
import kr.anymobi.floproject.service.MusicServiceBinder;
import kr.anymobi.floproject.util.CommFunc;
import kr.anymobi.floproject.util.ImageProcessor;

import static kr.anymobi.floproject.service.Const.PLAYER_TIME_PATTERN;

public class MusicPlayerFragment extends Fragment implements View.OnClickListener {

    public boolean loopFlag = false;
    ArrayList<LyricsData> lyricsDataArrayList;

    // === View ===
    public TextView title, artist, lyrics, currentTime, durationView;
    public ImageView cover;
    public SeekBar seekBar;
    public ImageButton playToggle, loopToggle;

    private MusicServiceBinder musicServiceBinder;

    private MusicData musicDataPlayer;

    public static MusicPlayerFragment newInstance(MusicData musicData) {
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        args.putParcelable("data", musicData);
        musicPlayerFragment.setArguments(args);
        return musicPlayerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musicDataPlayer = getArguments().getParcelable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        connectedWidget(view);

        if (getActivity() != null)
            musicServiceBinder = ((PlayerActivity) getActivity()).getMusicServiceBinder();

        if (musicDataPlayer != null)
            setPlayerMusic(musicDataPlayer);

        return view;
    }

    private void setPlayerMusic(MusicData musicData) {
        title.setText(musicData.title);
        artist.setText(musicData.singer);
        ImageProcessor imageGetter = new ImageProcessor(cover);
        imageGetter.setImageToView(musicData.image, getContext());

        String[] lyricsData = musicData.lyrics.split("\n");
        if (lyricsData[0].contains("[") && lyricsData[0].contains("]")) {
            lyricsDataArrayList = new ArrayList<>();
            for (String data : lyricsData) {
                lyricsDataArrayList.add(new LyricsData(data.substring(data.indexOf("[") + 1, data.indexOf("]")), data.substring(data.indexOf("]") + 1), false));
            }
        } else {
            lyrics.setText("타임라인에 따른 가사 등록 되지 않은 곡입니다.");
        }

        // seekbar init
        setSeekBar(musicData.duration);
    }


    private void connectedWidget(View view) {
        title = view.findViewById(R.id.playerMusicTitle); // 노래 제목
        artist = view.findViewById(R.id.playerMusicArtist); // 가수
        cover = view.findViewById(R.id.playerMusicCover); // 앨범 커버 이미지
        seekBar = view.findViewById(R.id.playerSeekBar);
        lyrics = view.findViewById(R.id.playerMusicLyrics);
        playToggle = view.findViewById(R.id.playToggle);
        loopToggle = view.findViewById(R.id.loopToggle);
        currentTime = view.findViewById(R.id.position);
        durationView = view.findViewById(R.id.duration);

        // 클릭 리스너
        playToggle.setOnClickListener(this);
        loopToggle.setOnClickListener(this);
        lyrics.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playToggle:
                setPlayToggle();
                break;
            case R.id.loopToggle:
                loopFlag = !loopFlag;

                if (getContext() != null)
                    if (loopFlag)
                        loopToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_on));
                    else
                        loopToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_off));

                if (musicServiceBinder != null)
                    musicServiceBinder.setLoopState(loopFlag);
                break;
            case R.id.playerMusicLyrics:
                if (getActivity() != null)
                    ((PlayerActivity) getActivity()).openFragment(lyricsDataArrayList, musicDataPlayer);
                break;
        }
    }

    public void setPlayToggle() {
        if (musicServiceBinder != null)
            if (musicServiceBinder.getPlayerState()) {
                musicServiceBinder.setPauseMedia();
                playBtnToggle(false);
            } else {
                musicServiceBinder.setStartMedia();
                playBtnToggle(true);
            }
    }

    public void playBtnToggle(boolean playFlag) {
        if (getContext() == null)
            return;

        if (playFlag)
            playToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_pause_btn));
        else
            playToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_play_btn));
    }

    private void setSeekBar(String musicDuration) {
        seekBar.setVisibility(View.VISIBLE);

        // 곡 연주시간 세팅
        seekBar.setMax(Integer.parseInt(musicDuration) * 1000);
        durationView.setText(CommFunc.getTimeData(Integer.parseInt(musicDuration) * 1000, PLAYER_TIME_PATTERN));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int time = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    time = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Service Binder with SeekBar
                musicServiceBinder.setPosition(time);
            }
        });
    }

    // 가사 관련 처리
    public void setLyrics(String timeData) {
        if (getActivity() == null)
            return;

        for (int i = 0; i < lyricsDataArrayList.size(); i++) {
            String timeZone = lyricsDataArrayList.get(i).getTimeZone();
            String timeZonePost = "";

            if (i + 1 < lyricsDataArrayList.size())
                timeZonePost = lyricsDataArrayList.get(i + 1).getTimeZone();

            // 첫번째 가사 전에 전주 중에 가사가 없는 경우에는 가사를 날린다.
            if (CommFunc.compareTimeZoneBeforeFirst(timeData, lyricsDataArrayList.get(0).getTimeZone())) {
                getActivity().runOnUiThread(() -> lyrics.setText(""));
            } else if (CommFunc.compareTimeZone(timeData, timeZone, timeZonePost)) {
                int finalI = i;
                getActivity().runOnUiThread(() -> {
                    String focusLyrics;
                    String nextLyrics;

                    if (finalI + 1 < lyricsDataArrayList.size()) {
                        nextLyrics = lyricsDataArrayList.get(finalI + 1).getLyrics();
                        focusLyrics = String.format("<font color=#3583fb><b>%s</b></font><br/>%s", lyricsDataArrayList.get(finalI).getLyrics(), nextLyrics);
                    } else {
                        focusLyrics = String.format("<font color=#3583fb><b>%s</b></font><br/>", lyricsDataArrayList.get(finalI).getLyrics());
                    }

                    // 재생이 되고 있을때만 TextView 에 가사를 Set 한다.
                    lyrics.setText(Html.fromHtml(focusLyrics));
                });
            }
        }
    }

    // 음악 재생 현재 시간 표시
    public void setCurrentTimePosition(String timeData) {
        currentTime.setText(timeData);
    }


}