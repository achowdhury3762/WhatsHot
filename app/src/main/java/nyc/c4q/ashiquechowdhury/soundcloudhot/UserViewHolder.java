package nyc.c4q.ashiquechowdhury.soundcloudhot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;

class UserViewHolder extends RecyclerView.ViewHolder {
    private ImageView userProfileImageV;
    private TextView usernameTextV;
    private TextView followersCountTextV;

    public UserViewHolder(View itemView) {
        super(itemView);

        userProfileImageV = (ImageView) itemView.findViewById(R.id.box_image);
        usernameTextV = (TextView) itemView.findViewById(R.id.box_upper_tview);
        followersCountTextV = (TextView) itemView.findViewById(R.id.box_lower_tview);
    }

    public void bind(User user) {
        Picasso.with(itemView.getContext()).load(user.getAvatar_url()).fit().into(userProfileImageV);
        usernameTextV.setText(user.getFull_name());
        followersCountTextV.setHint(String.valueOf(user.getFollowers_count()) + " followers");
    }
}
