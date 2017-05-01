package nyc.c4q.ashiquechowdhury.soundcloudhot;

import java.util.List;

import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;

interface HotView {
    void showLoading();

    void hideLoading();

    void showListError(String error);

    void showTrackList(List<Track> myList);

    void showUserList(List<User> myList);
}
