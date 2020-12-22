package kr.anymobi.floproject.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable {
    public String singer;
    public String album;
    public String title;
    public String duration;
    public String image;
    public String file;
    public String lyrics;

    public MusicData(Parcel in) {
        singer = in.readString();
        album = in.readString();
        title = in.readString();
        duration = in.readString();
        image = in.readString();
        file = in.readString();
        lyrics = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {
        @Override
        public MusicData createFromParcel(Parcel in) {
            return new MusicData(in);
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };

    public String getSinger() {
        return singer;
    }

    public String getAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public String getFile() {
        return file;
    }

    public String getLyrics() {
        return lyrics;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(singer);
        parcel.writeString(album);
        parcel.writeString(title);
        parcel.writeString(duration);
        parcel.writeString(image);
        parcel.writeString(file);
        parcel.writeString(lyrics);
    }
}
