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

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.vhadapter.TrackAdapter;
import nyc.c4q.ashiquechowdhury.soundcloudhot.vhadapter.UserAdapter;

public class HotActivity extends AppCompatActivity implements HotView, UserAdapter.UserPressedListener {
    private static final String USER_ID_KEY = "nyc.c4q.ashiquechowdhury.USER_ID";
    private static final long ONE_SECOND = 1000;
    private int currentUserId = 0;
    private HotPresenter hotPresenter;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.user_input)
    EditText userInputName;
    @BindView(R.id.user_recycler)
    RecyclerView userRecyclerList;
    @BindView(R.id.track_recycler)
    RecyclerView trackRecyclerList;

    private UserAdapter userAdapter;
    private TrackAdapter trackAdapter;
    private Unbinder unBinder;
    private HotModel hotModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);

        init();
        hotModel = new HotModel();
        hotPresenter = new HotPresenter(hotModel, this);
        Observable<String> textChanges = createTextChangeObservable(userInputName, ONE_SECOND, TimeUnit.MILLISECONDS);
        hotPresenter.getUserList(textChanges);

        if(savedInstanceState == null) {
            hotPresenter.fillInitialUserList();
        }
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

    private void init() {
        unBinder = ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);

        trackRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        userRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showListError(String error) {
        Log.d("Error", error);
    }

    @Override
    public void showTrackList(List<Track> myList) {
        trackAdapter = new TrackAdapter(myList);
        trackRecyclerList.setAdapter(trackAdapter);
    }

    @Override
    public void showUserList(List<User> userList) {
        userAdapter = new UserAdapter(userList, this);
        userRecyclerList.setAdapter(userAdapter);
    }

    @Override
    public void onUserPressed(User user) {
        currentUserId = user.getId();
        hotPresenter.getTrackList(user.getId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        hotPresenter.getTrackList(savedInstanceState.getInt(USER_ID_KEY));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        unBinder.unbind();
        hotPresenter.onStop();

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(USER_ID_KEY, currentUserId);
        super.onSaveInstanceState(outState);
    }
}
