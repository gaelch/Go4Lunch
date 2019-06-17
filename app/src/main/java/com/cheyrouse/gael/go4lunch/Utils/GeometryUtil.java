package com.cheyrouse.gael.go4lunch.Utils;

import android.content.Context;
import android.util.Log;

import com.cheyrouse.gael.go4lunch.models.Location;
import com.cheyrouse.gael.go4lunch.models.Result;

import java.text.DecimalFormat;

public class GeometryUtil {

    private static Location getDistances(Context context) {
        Location location = new Location();
        GPSTracker gps = new GPSTracker(context);
        if(gps.canGetLocation()){
            Log.e("testGpsTrue", "true");
            double latitude = gps.getLatitude(); // returns latitude
            double lng = gps.getLongitude(); // returns longitude
            location.setLng(lng);
            location.setLat(latitude);
        }
        return location;
    }

    public static double calculateDistance(Context context,
                                     double toLong, double toLat) {
        double fromLat = getDistances(context).getLat();
        double fromLong = getDistances(context).getLng();
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    public static String getString1000Less(double distance){
        double d = distance / 1000;
        double dR = Math.round(d*10.0/10.0);
        return dR + " km";
    }

}
