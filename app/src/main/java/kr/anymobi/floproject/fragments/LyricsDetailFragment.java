package kr.anymobi.floproject.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.anymobi.floproject.R;
import kr.anymobi.floproject.activities.PlayerActivity;
import kr.anymobi.floproject.adapter.LyricsAdapter;
import kr.anymobi.floproject.data.LyricsData;
import kr.anymobi.floproject.data.MusicData;
import kr.anymobi.floproject.service.MusicServiceBinder;
import kr.anymobi.floproject.util.CommFunc;

import static kr.anymobi.floproject.service.Const.PLAYER_TIME_PATTERN;

public class LyricsDetailFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<LyricsData> lyricsData;
    private LyricsAdapter lyricsAdapter;
    private MusicData musicData;

    // music service 관련
    private MusicServiceBinder musicServiceBinder;

    // 가사 제공
    private RecyclerView lyricsRecycler;
    private TextView titleView, artistView, currentTime, durationView;
    private ImageButton playToggle, playLoopBtn, lyricsToggle, zoom;
    public SeekBar seekBar;

    private boolean loopFlag = false;

    // toggle Flag (Lyrics Focus 관련)
    // TODO 각종 옵션 관련 쉐어드프리퍼런스 이용 예정
    private boolean lyricsToggleFlag = false;
    private int zoomLevel = 1;

    public static LyricsDetailFragment newInstance(ArrayList<LyricsData> listData, MusicData musicData) {
        LyricsDetailFragment fragment = new LyricsDetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, listData);
        args.putParcelable(ARG_PARAM2, musicData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (lyricsData == null)
                lyricsData = getArguments().getParcelableArrayList(ARG_PARAM1);

            musicData = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics_detail, container, false);
        connectedWidget(view);

        if (getActivity() != null)
            musicServiceBinder = ((PlayerActivity) getActivity()).getMusicServiceBinder();

        recyclerSetter(view);
        initData();

        return view;
    }


    private void connectedWidget(View view) {
        // recycler view
        lyricsRecycler = view.findViewById(R.id.lyricsRecycler);
        titleView = view.findViewById(R.id.songTitleLyrics);
        artistView = view.findViewById(R.id.songArtistLyrics);

        ImageButton exit = view.findViewById(R.id.exitBtn);
        zoom = view.findViewById(R.id.zoomBtn);
        lyricsToggle = view.findViewById(R.id.lyricsToggle);

        seekBar = view.findViewById(R.id.playerSeekBar);
        currentTime = view.findViewById(R.id.position);
        durationView = view.findViewById(R.id.duration);

        playToggle = view.findViewById(R.id.playToggle);
        playLoopBtn = view.findViewById(R.id.loopToggle);

        // 리스너 연결
        exit.setOnClickListener(this);
        zoom.setOnClickListener(this);
        lyricsToggle.setOnClickListener(this);
        playToggle.setOnClickListener(this);
        playLoopBtn.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (getActivity() != null)
            switch (view.getId()) {
                case R.id.exitBtn:
                    getActivity().onBackPressed();
                    break;
                case R.id.zoomBtn:
                    setZoomLevel();
                    break;
                case R.id.lyricsToggle:
                    lyricsToggleFlag = !lyricsToggleFlag;
                    lyricsAdapter.setLyricsToggle(lyricsToggleFlag);
                    if (getContext() != null)
                        if (lyricsToggleFlag)
                            lyricsToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_aim_on));
                        else
                            lyricsToggle.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_aim_off));
                    break;
                case R.id.playToggle:
                    setPlayToggle();
                    break;
                case R.id.loopToggle:
                    loopFlag = !loopFlag;

                    if (getContext() != null)
                        if (loopFlag)
                            playLoopBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_on));
                        else
                            playLoopBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_off));

                    if (musicServiceBinder != null)
                        musicServiceBinder.setLoopState(loopFlag);
                    break;
            }
    }

    private void initData() {
        // 가수 제목 setting
        titleView.setText(musicData.title);
        artistView.setText(musicData.singer);

        durationView.setText(CommFunc.getTimeData(Integer.parseInt(musicData.duration) * 1000, PLAYER_TIME_PATTERN));
        currentTime.setText(CommFunc.getTimeData(musicServiceBinder.getCurrentPosition(), PLAYER_TIME_PATTERN));

        playBtnToggle(musicServiceBinder.getPlayerState());

        if (getContext() != null)
            if (musicServiceBinder.getLoopState())
                playLoopBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_on));
            else
                playLoopBtn.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_player_loop_btn_off));

        setSeekBar();
    }

    private void recyclerSetter(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());

        // adapter setting
        lyricsRecycler.setLayoutManager(linearLayoutManager);
        lyricsAdapter = new LyricsAdapter(lyricsData, getActivity());
        // 아이디 별 추적하여 업데이트를 해주는 부분이긴 하나 Adapter 자체가 꼬여 버림 (순서)
        //lyricsAdapter.setHasStableIds(true);
        lyricsRecycler.setAdapter(lyricsAdapter);
    }

    private void setSeekBar() {
        seekBar.setMax(Integer.parseInt(musicData.duration) * 1000);
        seekBar.setProgress(musicServiceBinder.getCurrentPosition());
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

    private void setZoomLevel() {
        zoomLevel += 1;
        // 줌 레벨은 3단계까지 존재함.
        int ZOOM_MAX = 3;
        if (zoomLevel > ZOOM_MAX)
            zoomLevel = 1;

        if (getContext() != null)
            switch (zoomLevel) {
                case 1:
                    zoom.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_zoom_0));
                    break;
                case 2:
                    zoom.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_zoom_1));
                    break;
                case 3:
                    zoom.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_zoom_2));
                    break;
            }

        lyricsAdapter.setTextSize(zoomLevel);
        lyricsAdapter.notifyDataSetChanged();
    }

    public void setLyrics(String timeData) {
        if (getActivity() == null)
            return;

        for (int i = 0; i < lyricsData.size(); i++) {
            String timeZone = lyricsData.get(i).getTimeZone();
            String timeZonePost = "";

            if (i + 1 < lyricsData.size())
                timeZonePost = lyricsData.get(i + 1).getTimeZone();

            lyricsData.get(i).focus = CommFunc.compareTimeZone(timeData, timeZone, timeZonePost);
            lyricsAdapter.notifyDataSetChanged();

            /*if (CommFunc.compareTimeZone(timeData, timeZone, timeZonePost)) {
                //TODO 가사 추적 토글을 활성화 했을때 자동으로 스크롤이 되게하는 기능. -> 적용 고려. -> 작동은 하나 불편해..
                if (lyricsToggleFlag)
                    lyricsRecycler.scrollToPosition(i);
            }*/
        }
    }

    // 음악 재생 현재 시간 표시
    public void setCurrentTimePosition(String timeData) {
        if (currentTime != null)
            currentTime.setText(timeData);
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

}