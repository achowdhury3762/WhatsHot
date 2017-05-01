package nyc.c4q.ashiquechowdhury.soundcloudhot.model;

public class Track {
    public int playback_count;

    public String title;

    public String artwork_url;

    public String getTitle() {
        return title;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    public int getPlaybackCount(){
        return playback_count;
    }
}
