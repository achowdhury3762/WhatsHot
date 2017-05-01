package nyc.c4q.ashiquechowdhury.soundcloudhot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.UserList;
import nyc.c4q.ashiquechowdhury.soundcloudhot.network.SoundCloudClient;

public class HotActivity extends AppCompatActivity implements UserPressedListener {
    private static final long ONE_SECOND = 1000L;
    private SoundCloudClient soundCloudClient;
    private List<Track> trackList;
    private List<User> userList;
    private UserAdapter userAdapter;
    private TrackAdapter trackAdapter;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.user_input)
    EditText userInputName;
    @BindView(R.id.user_recycler)
    RecyclerView userRecyclerList;
    @BindView(R.id.track_recycler)
    RecyclerView trackRecyclerList;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Unbinder unBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);

        unBinder = ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);
        soundCloudClient = SoundCloudClient.getInstance();

        trackList = new ArrayList<>();
        trackAdapter = new TrackAdapter(trackList);
        trackRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trackRecyclerList.setAdapter(trackAdapter);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        userRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userRecyclerList.setAdapter(userAdapter);

        fillUserView();
        Observable<String> textChanges = createTextChangeObservable(userInputName, ONE_SECOND, TimeUnit.MILLISECONDS);
        makeSearchBarNetworkRequests(textChanges);
    }

    private Observable<String> createTextChangeObservable(EditText userInputName, long timeBetween, TimeUnit unitOfTime) {
        return RxTextView.textChanges(userInputName)
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(@NonNull CharSequence charSequence) throws Exception {
                        return charSequence.length() > 3;
                    }
                })
                .debounce(timeBetween, unitOfTime)
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(@NonNull CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                });

    }

    private void makeSearchBarNetworkRequests(Observable<String> textObserver) {
              compositeDisposable.add(textObserver
                        .flatMap(new Function<String, Observable<List<User>>>() {
                            @Override
                            public Observable<List<User>> apply(@NonNull String user) throws Exception {
                                return soundCloudClient.getUsers(user);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<User>>() {
                            @Override
                            public void onNext(@NonNull List<User> users) {
                                userAdapter.setUserList(users);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d("Showing Thirty Users", e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
    }

    private void fillUserView() {
        Observable<List<User>> soundCloudUsers = soundCloudClient.getUsers("");
        compositeDisposable.add(soundCloudUsers
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<User>>() {
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
                }));
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

    @Override
    protected void onDestroy() {
        unBinder.unbind();
        compositeDisposable.dispose();

        super.onDestroy();
    }

    @Override
    public void onUserPressed(User user) {
        loadTracksFromUserId(user.getId());
    }

    private void loadTracksFromUserId(int id) {
        progressBar.setVisibility(View.VISIBLE);
        trackList = new ArrayList<>();
        Observable<UserList> soundCloudFollowers = soundCloudClient.getFollowersFromId(id);
        Observable<User> topTwentyFollowers = getTopNUsers(20, soundCloudFollowers);

        compositeDisposable.add(getUserIds(topTwentyFollowers)
                .flatMap(new Function<Integer, Observable<List<Track>>>() {
                    @Override
                    public Observable<List<Track>> apply(@NonNull Integer userId) throws Exception {
                        return soundCloudClient.getTrackListFromId(userId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<Track>>empty())
                .subscribeWith(new DisposableObserver<List<Track>>() {
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
                        progressBar.setVisibility(View.INVISIBLE);
                        trackAdapter.setTrackList(trackList);
                    }
                }));
    }
}
