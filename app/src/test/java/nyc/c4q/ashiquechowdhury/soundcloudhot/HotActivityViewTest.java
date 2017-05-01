package nyc.c4q.ashiquechowdhury.soundcloudhot;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HotActivityViewTest {
    private HotActivity hotActivity;
    @Mock
    HotPresenter hotPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        hotActivity = Robolectric.setupActivity(HotActivity.class);
        hotActivity.setPresenter(hotPresenter);
    }
}
