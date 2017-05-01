package nyc.c4q.ashiquechowdhury.soundcloudhot.model;

import android.support.annotation.NonNull;

public class Track implements Comparable<Track> {
    public int playback_count;

    public String title;

    public String artwork_url;

    public int favoritings_count;

    public String getTitle() {
        return title;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    public int getPlaybackCount(){
        return playback_count;
    }

    public int getFavoritings_count() { return favoritings_count; }

    @Override
    public int compareTo(@NonNull Track track) {
        float thisRatioOfPlaybackToFavorites = this.getPlaybackCount()/ this.getFavoritings_count();
        float otherRatioOfPlaybackToFavorites = track.getPlaybackCount()/this.getFavoritings_count();

        if(thisRatioOfPlaybackToFavorites < otherRatioOfPlaybackToFavorites) {
            return 1;
        }
        if(thisRatioOfPlaybackToFavorites > otherRatioOfPlaybackToFavorites) {
            return -1;
        }
        else
            return 0;
    }

}
