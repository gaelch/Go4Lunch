package com.cheyrouse.gael.go4lunch;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.test.ActivityTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.controller.activity.MainActivity;
import com.cheyrouse.gael.go4lunch.models.User;
import com.cheyrouse.gael.go4lunch.utils.DateUtils;
import com.cheyrouse.gael.go4lunch.utils.GeometryUtil;
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

    private Context context;

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
    public void getCoworkers_return_good_person(){
        List<String> coWorkers = new ArrayList<>();
        coWorkers.add("Jojo");
        coWorkers.add("me");
        User user = new User();
        user.setUsername("me");
        String returnedByGetCoworkers = StringHelper.getCoWorkers(coWorkers, user, context);
        assertEquals("Jojo will be there with you", returnedByGetCoworkers);
    }
}