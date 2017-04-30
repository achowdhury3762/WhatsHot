package nyc.c4q.ashiquechowdhury.soundcloudhot.model;

public class User {
    public int id;

    public String avatar_url;

    public String full_name;

    public int followers_count;

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getFollowers_count() {
        return followers_count;
    }


    public int getId(){
        return id;
    }
}