package nyc.c4q.ashiquechowdhury.soundcloudhot.vhadapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import nyc.c4q.ashiquechowdhury.soundcloudhot.R;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;


class TrackViewHolder extends RecyclerView.ViewHolder {
    private ImageView trackImageV;
    private TextView trackNameTextV;
    private TextView trackPlaybackTextV;

    public TrackViewHolder(View itemView) {
        super(itemView);

        trackImageV = (ImageView) itemView.findViewById(R.id.box_image);
        trackNameTextV = (TextView) itemView.findViewById(R.id.box_upper_tview);
        trackPlaybackTextV = (TextView) itemView.findViewById(R.id.box_lower_tview);
    }

    public void bind(Track track) {
        Picasso.with(itemView.getContext()).load(track.getArtwork_url()).fit().into(trackImageV);
        trackNameTextV.setText(track.getTitle());
        trackPlaybackTextV.setHint(String.valueOf(track.getPlaybackCount() + " playbacks"));
    }
}
