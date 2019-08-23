package com.cheyrouse.gael.go4lunch;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.support.v4.app.Fragment;
import android.test.ActivityTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.ImageView;

import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.controller.fragment.MapsFragment;
import com.cheyrouse.gael.go4lunch.controller.fragment.RestauDetailFragment;
import com.cheyrouse.gael.go4lunch.models.OpeningHours;
import com.cheyrouse.gael.go4lunch.models.Restaurant;
import com.cheyrouse.gael.go4lunch.models.Result;
import com.cheyrouse.gael.go4lunch.models.ResultDetail;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.DateUtils;
import com.cheyrouse.gael.go4lunch.utils.GeometryUtil;
import com.cheyrouse.gael.go4lunch.utils.ListUtils;
import com.cheyrouse.gael.go4lunch.utils.Prefs;
import com.cheyrouse.gael.go4lunch.utils.RegexUtil;
import com.cheyrouse.gael.go4lunch.utils.StarsUtils;
import com.cheyrouse.gael.go4lunch.utils.StringHelper;
import com.cheyrouse.gael.go4lunch.utils.UserHelper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UtilsUnitTest {

    private Context context = mock(Context.class);

    @Test
    public void notification_date_is_ok() {
        Calendar now = Calendar.getInstance(Locale.FRANCE);
        Calendar calendar = Calendar.getInstance(Locale.FRANCE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(calendar.before(now)){
            calendar.add(Calendar.DATE,1);
        }
        assertEquals(calendar, DateUtils.getCalendarPresetsToResetChoice());
    }

    @Test
    public void location_get_string_is_correct(){
        assertEquals("1.0 km", GeometryUtil.getString1000Less(1000));
    }

    @Test
    public void email_is_correct(){
        assertTrue(RegexUtil.isValidEmail("name@email.com"));
    }

    @Test
    public void rate_calcul_is_correct(){
        List<User> userListTest = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        User user4 = new User();
        userListTest.add(user1);
        userListTest.add(user2);
        userListTest.add(user3);
        userListTest.add(user4);
        assertEquals(2, StarsUtils.getRate(2, userListTest));
    }

    @Test
    public void test_convert_location_in_string(){
        assertEquals("46.3,2.033", StringHelper.convertInString(46.3, 2.033));
    }

    @Test
    public void get_good_number_of_coworkers(){
        List<User> userList = new ArrayList<>();
        User jojo = new User();
        User john = new User();
        jojo.setChoice("Le Seth");
        john.setChoice("Le Zinc");
        userList.add(jojo);
        userList.add(john);
        assertEquals("(2)", StringHelper.getNumberOfCoworkers(userList));
        List<User> empty_list = new ArrayList<>();
        assertEquals("(0)", StringHelper.getNumberOfCoworkers(empty_list));
    }

    @Test
    public void joining_coWorkers_good(){
        List<User> userList = new ArrayList<>();
        User jojo = new User();
        User john = new User();
        jojo.setChoice("Le Seth");
        john.setChoice("Le Zinc");
        userList.add(jojo);
        userList.add(john);
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setName("Le Seth");
        assertEquals(1, (ListUtils.getJoiningCoWorkers(userList, resultDetail)).size());
    }

    @Test
    public void test_setStars_return_good_number_of_stars(){
        ImageView imageView = null;
        List<User> users = new ArrayList<>();
        User user = new User();
        users.add(user);
        users.add(user);
        users.add(user);
        Restaurant restaurant = new Restaurant();
        List<String> rate = new ArrayList<>();
        rate.add("Stevie");
        restaurant.setRate(rate);
        assertEquals("1", StarsUtils.setStars(context, imageView, imageView, imageView, users, restaurant));
        rate.add("Jojo");
        assertEquals("2", StarsUtils.setStars(context, imageView, imageView, imageView, users, restaurant));
        rate.add("John");
        assertEquals("3", StarsUtils.setStars(context, imageView, imageView, imageView, users, restaurant));
    }

    @Test
    public void findUserInRateList_is_ok(){
        List<String> rateList = new ArrayList<>();
        rateList.add("A");
        rateList.add("B");
        rateList.add("C");
        String userId = "B";
        assertTrue(ListUtils.findUserInRateList(userId, rateList));
    }

    @Test
    public void findGoodRestaurantToDisplayYourChoiceToday_is_good(){
        List<ResultDetail> resultDetailList = new ArrayList<>();
        ResultDetail resultDetail = new ResultDetail();
        resultDetail.setName("Lunch");
        ResultDetail resultDetail1 = new ResultDetail();
        resultDetail1.setName("Diner");
        resultDetailList.add(resultDetail);
        resultDetailList.add(resultDetail1);
        String choice = "Lunch";
        ListUtils.getRestaurantToDisplayYourChoiceIfExist(resultDetailList, choice);
    }

    @Test
    public void test_makeListResultRestaurantWithResultList(){
        List<Result> results = new ArrayList<>();
        Result result = new Result(null, null, null, "Lunch",
                null, null,null,null,null,
                null,null,null,null,null);
        Result result1 = new Result(null, null, null, "Diner",
                null, null,null,null,null,
                null,null,null,null,null);
        results.add(result);
        results.add(result1);
        List<Restaurant> restaurants = new ArrayList<>();
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Lunch");
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setRestaurantName("Les Zinc");
        ListUtils.makeListResultRestaurantWithResultList(results, restaurants);
    }

    @Test
    public void test_makeListResultRestaurantWithResultDetailList(){
        List<ResultDetail> results = new ArrayList<>();
        ResultDetail result = new ResultDetail();
        result.setName("Lunch");
        ResultDetail result1 = new ResultDetail();
        result1.setName("Le Zinc");
        results.add(result);
        results.add(result1);
        List<Restaurant> restaurants = new ArrayList<>();
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName("Lunch");
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setRestaurantName("Les Zinc");
        ListUtils.makeListResultRestaurantWithResultDetailList(results, restaurants);
    }

    @Test
    public void test_if_hour_are_return_good(){
        assertEquals("22:00", DateUtils.convertStringToHours("2200"));
    }

    @Test
    public void test_getOpenHours(){
        ResultDetail res1 = new ResultDetail();
        OpeningHours openingHours1 = new OpeningHours();
        openingHours1.setOpenNow(true);
        res1.setOpeningHours(openingHours1);
        ResultDetail res2 = new ResultDetail();
        assertEquals(2, DateUtils.getOpenHours(res2));
        ResultDetail res3 = new ResultDetail();
        OpeningHours openingHours2 = new OpeningHours();
        openingHours2.setOpenNow(false);
        res3.setOpeningHours(openingHours2);
        assertEquals(3, DateUtils.getOpenHours(res3));
    }
}