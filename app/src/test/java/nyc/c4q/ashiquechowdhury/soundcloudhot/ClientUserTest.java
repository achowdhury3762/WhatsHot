package nyc.c4q.ashiquechowdhury.soundcloudhot;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.observers.TestObserver;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.Track;
import nyc.c4q.ashiquechowdhury.soundcloudhot.model.User;
import nyc.c4q.ashiquechowdhury.soundcloudhot.network.SoundCloudClient;

import static org.junit.Assert.assertTrue;

public class ClientUserTest {
    private static final String USER = "Bob Mould";
    SoundCloudClient soundCloudClient;

    @Before
    public void setUp() {
        soundCloudClient = soundCloudClient.getInstance();
    }

    @Test
    public void testGetUserProfile() throws Exception {
        TestObserver<List<User>> observer = TestObserver.create();
        soundCloudClient.getUsers("Bob").subscribe(observer);
        observer.assertNoErrors();
        observer.assertComplete();

        List<User> testUser = (List) observer.getEvents().get(0).get(0);
        assertTrue(testUser.get(0).getFull_name().equals("Bob Dylan"));
    }

    @Test
    public void testGetFavorites() throws Exception {
        TestObserver<List<Track>> observer = TestObserver.create();
        soundCloudClient.getTrackListFromId(1).subscribe(observer);
        observer.assertNoErrors();
        observer.assertComplete();

        List<Track> testTrack = (List) observer.getEvents().get(0).get(0);
        assertTrue(testTrack.get(0).getTitle().equals("Almighty Ruffnex present Live at BoomBox"));
    }
}
