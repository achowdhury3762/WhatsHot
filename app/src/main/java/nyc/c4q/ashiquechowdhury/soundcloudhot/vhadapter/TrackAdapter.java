package nyc.c4q.ashiquechowdhury.soundcloudhot.vhadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import nyc.c4q.ashiquechowdhury.soundcloudhot.R;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;

public class TrackAdapter extends RecyclerView.Adapter<TrackViewHolder> {
    private List<Track> trackList;

    public TrackAdapter(List<Track> trackList) {
        this.trackList = trackList;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.box_viewholder, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.bind(trackList.get(position));
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setTrackList(List<Track> trackList){
        this.trackList = trackList;
        notifyDataSetChanged();
    }
}
