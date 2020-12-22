package kr.anymobi.floproject.network;

import java.util.ArrayList;

import kr.anymobi.floproject.data.MusicData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitService {

    @GET("2020-flo/{user}/songs.json")
    Call<ArrayList<MusicData>> requestMusics(@Path("user") String userName);

    @GET("2020-flo/song.json")
    Call<MusicData> requestMusic();
}
