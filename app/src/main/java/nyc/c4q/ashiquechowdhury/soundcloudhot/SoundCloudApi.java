package nyc.c4q.ashiquechowdhury.soundcloudhot;

import java.util.List;

import io.reactivex.Observable;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.UserList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SoundCloudApi {

    @GET("/users/{id}/followers")
    Observable<UserList> getFollowers(@Path("id") int id, @Query("client_id") String clientId);

    @GET("/users/{id}/favorites")
    Observable<List<Track>> getFavorites(@Path("id") int id, @Query("client_id") String clientId);

    @GET("/users")
    Observable<List<User>> getUsers(@Query("q") String userName, @Query("client_id") String clientId);
}
