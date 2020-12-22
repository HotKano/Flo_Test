package kr.anymobi.floproject.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LyricsData implements Parcelable {
    public String timeZone;
    public String lyrics;
    public boolean focus;

    public LyricsData(String timeZone, String lyrics, boolean focus) {
        this.timeZone = timeZone;
        this.lyrics = lyrics;
        this.focus = focus;
    }

    protected LyricsData(Parcel in) {
        timeZone = in.readString();
        lyrics = in.readString();
    }

    public static final Creator<LyricsData> CREATOR = new Creator<LyricsData>() {
        @Override
        public LyricsData createFromParcel(Parcel in) {
            return new LyricsData(in);
        }

        @Override
        public LyricsData[] newArray(int size) {
            return new LyricsData[size];
        }
    };

    public String getTimeZone() {
        return timeZone;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public boolean isFocus() {
        return focus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(timeZone);
        parcel.writeString(lyrics);
    }
}
