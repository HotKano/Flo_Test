package kr.anymobi.floproject.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.anymobi.floproject.R;
import kr.anymobi.floproject.activities.PlayerActivity;
import kr.anymobi.floproject.data.LyricsData;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.ViewHolder> {
    private final ArrayList<LyricsData> lyricsData;
    private boolean lyricsToggle = false;
    private int zoomLevel = 20;
    private final PlayerActivity playerActivity;

    public LyricsAdapter(ArrayList<LyricsData> lyricsData, Activity activity) {
        this.lyricsData = lyricsData;
        this.playerActivity = (PlayerActivity) activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lyricsView;
        LinearLayout lyricsTouchView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lyricsView = itemView.findViewById(R.id.lyricsItemView);
            lyricsTouchView = itemView.findViewById(R.id.lyricsTouchView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_detail_lyrics_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lyricsView.setText(lyricsData.get(position).getLyrics());
        holder.lyricsView.setTextSize(zoomLevel);

        if (lyricsData.get(position).focus)
            holder.lyricsView.setTextColor(Color.RED);
        else
            holder.lyricsView.setTextColor(Color.BLACK);

        holder.lyricsTouchView.setOnClickListener(view -> {
            if (lyricsToggle) {
                // 재생 중 일때만 해당 가사로 이동한다.
                if (playerActivity.getMusicServiceBinder().getPlayerState())
                    playerActivity.getMusicServiceBinder().seekTo(lyricsData.get(position).getTimeZone());
            } else {
                // 가사 터치 비활성화 시 Fragment Managing 을 하고 있는 액티비티를 onBack 처리하여 Fragment 를 닫는다.
                ((Activity) holder.itemView.getContext()).onBackPressed();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lyricsData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setLyricsToggle(boolean lyricsToggle) {
        this.lyricsToggle = lyricsToggle;
    }

    public void setTextSize(int zoomLevel) {
        switch (zoomLevel) {
            case 1:
                this.zoomLevel = 20;
                break;
            case 2:
                this.zoomLevel = 30;
                break;
            case 3:
                this.zoomLevel = 40;
                break;
        }
    }

}
