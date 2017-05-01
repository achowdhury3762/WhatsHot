package nyc.c4q.ashiquechowdhury.soundcloudhot.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import io.reactivex.Observable;
import nyc.c4q.ashiquechowdhury.soundcloudhot.BuildConfig;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.UserList;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SoundCloudClient {
    private static final String API_URL = "https://api.soundcloud.com/";

    private static SoundCloudClient instance;

    private final SoundCloudApi api;

    public static SoundCloudClient getInstance() {
        if(instance == null) {
            instance = new SoundCloudClient();
        }

        return instance;
    }

    private SoundCloudClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        api = retrofit.create(SoundCloudApi.class);
    }

    public Observable<UserList> getFollowersFromId(int id) {
        return api.getFollowers(id, BuildConfig.CLIENT_ID);
    }

    public Observable<List<Track>> getTrackListFromId(int id) {
        return api.getFavorites(id, BuildConfig.CLIENT_ID);
    }

    public Observable<List<User>> getUsers(String userNameQuery) {
        return api.getUsers(userNameQuery, BuildConfig.CLIENT_ID);
    }
}
