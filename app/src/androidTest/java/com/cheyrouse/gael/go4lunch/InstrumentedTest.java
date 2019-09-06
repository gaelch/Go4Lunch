package com.cheyrouse.gael.go4lunch;

import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.fragment.app.Fragment;
import android.util.Log;
import com.bumptech.glide.RequestManager;
import com.cheyrouse.gael.go4lunch.controller.fragment.ListFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment;
import com.cheyrouse.gael.go4lunch.models.Prediction;
import com.cheyrouse.gael.go4lunch.models.Predictions;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.Go4LunchStream;
import com.cheyrouse.gael.go4lunch.utils.ListUtils;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.StringHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;
import com.cheyrouse.gael.go4lunch.views.RecyclerViewAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import static com.cheyrouse.gael.go4lunch.BuildConfig.API_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    private String userName;
    private List<Prediction> resultsPredictions;
    private Disposable disposable;


    @Test
    public void test_database()  {
        Prefs prefs = Prefs.get(InstrumentationRegistry.getTargetContext());
        User user = prefs.getPrefsUser();
        if(user != null){
            UserHelper.getUser(user.getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userName  = (String) Objects.requireNonNull(task.getResult()).get("username");
                    assertEquals(user.getUsername(), userName);
                }
            }).addOnFailureListener(e -> Log.e("fail", e.getMessage()));
        }
    }

    @Test
    public void test_predictions(){
        String query = "L'assierois";
        String location = "44.65, 1.85";
        disposable = Go4LunchStream.getPlacesAutoComplete(query, location, 5500, API_KEY)
                .subscribeWith(new DisposableObserver<Predictions>() {
            @Override
            public void onNext(Predictions predictions) {
               resultsPredictions = predictions.getPredictions();
            }
            @Override
            public void onError(Throwable e) {
                e.getMessage();
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete() {
                assertThat("The result list is not empty", !resultsPredictions.isEmpty());
            }
        });
        disposable.dispose();
    }

    @Test
    public void test_listFragment_recyclerView(){
        List<User> userList = new ArrayList<>();
        RequestManager glide = null;
        ListFragment listAdapterListener = null;
        List<ResultDetail> resultDetails = new ArrayList<>();
        ResultDetail res1 = new ResultDetail();
        ResultDetail res2 = new ResultDetail();
        ResultDetail res3 = new ResultDetail();
        resultDetails.add(res1);
        resultDetails.add(res2);
        resultDetails.add(res3);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(InstrumentationRegistry.getTargetContext(), resultDetails, userList, glide, listAdapterListener);
        assertEquals(3, recyclerViewAdapter.getItemCount());
    }

    @Test
    public void get_backStack_to_refresh_data_is_ok(){
        MapsFragment mapsFragment = new MapsFragment();
        List<Fragment> fragments = new ArrayList<>();
        RestauDetailFragment newFragment = new RestauDetailFragment();
        fragments.add(mapsFragment);
        fragments.add(newFragment);
        assertEquals(1, ListUtils.getBackStackToRefreshData(newFragment, fragments));
    }

    @Test
    public void getCoworkers_return_good_person(){
        String lang = Locale.getDefault().getLanguage();
        List<String> coWorkers = new ArrayList<>();
        coWorkers.add("Jojo");
        coWorkers.add("me");
        User user = new User();
        user.setUsername("me");
        String returnedByGetCoworkers = StringHelper.getCoWorkers(coWorkers, user, InstrumentationRegistry.getTargetContext());
        if(!lang.isEmpty() && lang.equals("en")){
            assertEquals("Jojo will be there with you.", returnedByGetCoworkers);
        }
        if(!lang.isEmpty() && lang.equals("fr")){
            assertEquals("Jojo y sera également.", returnedByGetCoworkers);
        }
    }


}
