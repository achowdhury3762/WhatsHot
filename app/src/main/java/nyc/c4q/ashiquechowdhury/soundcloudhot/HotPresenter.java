package nyc.c4q.ashiquechowdhury.soundcloudhot;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;

public class HotPresenter {
    private final HotModel model;
    private final HotView view;
    private CompositeDisposable compositeDisposable;

    public HotPresenter(HotModel model, HotView view) {
        this.model = model;
        this.view = view;

        compositeDisposable = new CompositeDisposable();
    }

    public void fillInitialUserList() {
        Disposable disposable = model.fillUserView(new HotModel.ListCallback<User>() {
            @Override
            public void onSuccess(List<User> returnedList) {
                view.showUserList(returnedList);
            }

            @Override
            public void onError(String message) {
                view.showListError(message);
            }
        });

        compositeDisposable.add(disposable);
    }

    public void getUserList(Observable<String> textChanges) {
        Disposable disposable = model.getUserList(textChanges, new HotModel.ListCallback<User>() {
            @Override
            public void onSuccess(List<User> returnedList) {
                view.showUserList(returnedList);
            }

            @Override
            public void onError(String message) {
                view.showListError(message);
            }
        });

        compositeDisposable.add(disposable);
    }

    public void getTrackList(int userId) {
        view.showLoading();

        Disposable disposable = model.getTrackList(userId, new HotModel.ListCallback<Track>() {
            @Override
            public void onSuccess(List<Track> returnedList) {
                view.hideLoading();
                view.showTrackList(returnedList);
            }

            @Override
            public void onError(String message) {

            }
        });

        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.dispose();
    }
}
