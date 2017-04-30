package nyc.c4q.ashiquechowdhury.soundcloudhot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.UserList;
import nyc.c4q.ashiquechowdhury.soundcloudhot.network.SoundCloudClient;

public class HotActivity extends AppCompatActivity {
    private SoundCloudClient soundCloudClient;
    private List<Track> trackList;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);

        soundCloudClient = SoundCloudClient.getInstance();
        trackList = new ArrayList<>();
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        RecyclerView userRecyclerList = (RecyclerView) findViewById(R.id.user_recycler);
        userRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userRecyclerList.setAdapter(userAdapter);


        Observable<List<User>> soundCloudUsers = soundCloudClient.getUsers("");
        soundCloudUsers
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<User> soundCloudUsers) {
                        userAdapter.setUserList(soundCloudUsers);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Showing Thirty Users", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        Observable<UserList> soundCloudFollowers = soundCloudClient.getFollowersFromId(1);
        Observable<User> topTwentyFollowers = getTopNUsers(20, soundCloudFollowers);

        getUserIds(topTwentyFollowers)
                .flatMap(new Function<Integer, Observable<List<Track>>>() {
                    @Override
                    public Observable<List<Track>> apply(@NonNull Integer userId) throws Exception {
                        return soundCloudClient.getTrackListFromId(userId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<Track>>empty())
                .subscribe(new Observer<List<Track>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Track> tracks) {
                        trackList.addAll(tracks);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("User Liked Tracks", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Integer> getUserIds(Observable<User> topTwentyFollowers) {
        return topTwentyFollowers
                .map(new Function<User, Integer>() {
                    @Override
                    public Integer apply(@NonNull User user) throws Exception {
                        return user.getId();
                    }
                });
    }

    public Observable<User> getTopNUsers(int value, Observable<UserList> userList) {
        return userList
                .map(new Function<UserList, List<User>>() {
                    @Override
                    public List<User> apply(@NonNull UserList userList) throws Exception {
                        return userList.getCollection();
                    }
                })
                .flatMapIterable(new Function<List<User>, Iterable<? extends User>>() {
                    @Override
                    public Iterable<? extends User> apply(@NonNull List<User> users) throws Exception {
                        return users;
                    }
                })
                .take(value);
    }
}
