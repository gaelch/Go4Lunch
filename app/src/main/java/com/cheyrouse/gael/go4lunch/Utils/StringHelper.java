package com.cheyrouse.gael.go4lunch.Utils;

import android.text.style.URLSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.firebase.ui.auth.ui.email.CheckEmailFragment.TAG;

public class StringHelper {

    public static String extractValueFromHtml(String s) {
        String urlStr ="";
        /*String str = '<a href="example.com"></a><a href="example2.com"></a><a href="example3.com"></a>';
        String pattern = /href="([^"]+)/g;
        String match = pattern.exec(str); while(match !=null)


            console.log(match[1]);
            match = pattern.exec(str);*/


       // s = s.replace(, "");
        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);

        while(m.find())
        {
            urlStr = m.group();

            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
        }
        Log.e("url ", urlStr);

        return urlStr;
    }


}
