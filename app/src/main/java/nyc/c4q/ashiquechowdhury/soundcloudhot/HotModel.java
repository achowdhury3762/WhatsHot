package nyc.c4q.ashiquechowdhury.soundcloudhot;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.UserList;
import nyc.c4q.ashiquechowdhury.soundcloudhot.network.SoundCloudClient;

public class HotModel {
    private final SoundCloudClient soundCloudClient;
    private List<Track> trackList;

    public HotModel() {
        trackList = new ArrayList<>();
        soundCloudClient = SoundCloudClient.getInstance();
    }

    public Disposable getUserList(Observable<String> textChanges, final ListCallback<User> callback) {
        return textChanges
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
                        callback.onSuccess(users);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Showing Thirty Users", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Disposable getTrackList(int userId, final ListCallback<Track> callback) {
        trackList = new ArrayList<>();
        Observable<UserList> soundCloudFollowers = soundCloudClient.getFollowersFromId(userId);
        Observable<User> topTwentyFollowers = getTopNUsers(20, soundCloudFollowers);
        return getUserIds(topTwentyFollowers)
                .flatMap(new Function<Integer, Observable<List<Track>>>() {
                    @Override
                    public Observable<List<Track>> apply(@NonNull Integer userId) throws Exception {
                        return soundCloudClient.getTrackListFromId(userId);
                    }
                })
                .map(new Function<List<Track>, List<Track>>() {
                    @Override
                    public List<Track> apply(@NonNull List<Track> tracks) throws Exception {
                        Collections.sort(tracks);
                        return tracks;
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
                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        callback.onSuccess(trackList);
                    }
                });
    }

    private Observable<User> getTopNUsers(int value, Observable<UserList> userList) {
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

    private Observable<Integer> getUserIds(Observable<User> topTwentyFollowers) {
        return topTwentyFollowers
                .map(new Function<User, Integer>() {
                    @Override
                    public Integer apply(@NonNull User user) throws Exception {
                        return user.getId();
                    }
                });
    }

    public Disposable fillUserView(final ListCallback<User> callback) {
        Observable<List<User>> soundCloudUsers = soundCloudClient.getUsers("");
        return soundCloudUsers
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<User>>() {
                    @Override
                    public void onNext(@NonNull List<User> soundCloudUsers) {
                        callback.onSuccess(soundCloudUsers);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("Showing Thirty Users", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    interface ListCallback<T> {

        void onSuccess(List<T> returnedList);

        void onError(String message);
    }
}
