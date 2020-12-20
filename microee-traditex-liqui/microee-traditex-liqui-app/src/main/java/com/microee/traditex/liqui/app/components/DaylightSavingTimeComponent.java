package com.microee.traditex.liqui.app.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DaylightSavingTimeComponent {

    @Value("${diskTradTime.DaylightSavingTime.week}")
    private String diskTradTimeDaylightSavingTime;
    
    @Value("${diskTradTime.StandardTime.week}")
    private String diskTradTimeStandardTime;

    public Boolean isDiskTradOpen() {
        
        String timeZoneString = "America/Chicago";
        
        Function<String, Map<String, String[]>> func = (str) -> {
            String[] diskTradTimeExp = str.split(",");
            Map<String, String[]> expMapExp = new HashMap<>();
            for (int i=0; i<diskTradTimeExp.length; i++) {
                String[] arr1 = diskTradTimeExp[i].split("/");
                String[] arr2 = arr1[1].split("-");
                expMapExp.put(arr1[0], new String[] { arr2[0], arr2[1] });
            }
            return expMapExp;
        };
        
        Date d1 = dst(timeZoneString, null, 3, 2); // 3月第2周
        Date d2 = dst(timeZoneString, null, 11, 1); // 11月第1周
        Long[] dstArr = new Long[] { d1.getTime(), d2.getTime() };
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeZone(TimeZone.getTimeZone(timeZoneString));
        Integer currWeekDayNum = nowCal.get(Calendar.DAY_OF_WEEK);
        Long now = nowCal.getTimeInMillis();
        Map<String, String[]> map = null;
        
        if ( now >= dstArr[0] && now <= dstArr[1]) {
            map = func.apply(diskTradTimeDaylightSavingTime); // 夏令时
        } else {
            map = func.apply(diskTradTimeStandardTime); // 冬令时
        }
        
        if (!map.containsKey(currWeekDayNum.toString())) {
            return false;
        }
        
        String[] hour= map.get(currWeekDayNum.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneString));
        sdf1.setTimeZone(TimeZone.getTimeZone(timeZoneString));
        try {
            long startFixTime = sdf1.parse(sdf.format(nowCal.getTime()) + " " + hour[0]).getTime();
            long endFixTime = sdf1.parse(sdf.format(nowCal.getTime()) + " " + hour[1]).getTime();
            DateFormat df = DateFormat.getInstance();
            df.setTimeZone(TimeZone.getTimeZone(timeZoneString));
            if (now >= startFixTime && now <= endFixTime) {
                return true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        
        return false;
    }

    public static Date dst(String zone, Integer _year, Integer _month, Integer weekCount) {
        Calendar cd1 = Calendar.getInstance();
        cd1.setTimeZone(TimeZone.getTimeZone(zone));
        if (_year != null) {
            cd1.set(Calendar.YEAR, _year); // 每年
        }
        cd1.set(Calendar.MONTH, _month - 1); // 月
        cd1.set(Calendar.DAY_OF_MONTH, 1); // 天
        cd1.set(Calendar.HOUR_OF_DAY, 2); // 凌晨2点
        cd1.set(Calendar.MINUTE, 0);
        cd1.set(Calendar.SECOND, 0);
        cd1.set(Calendar.MILLISECOND, 0);
        int dayOfWeek = cd1.get(Calendar.DAY_OF_WEEK); // 1周日, 2周一
        if (dayOfWeek == 1) {
            // 本月第一周周日
        } else {
            // 本月第一周周一
            cd1.add(Calendar.DAY_OF_MONTH, 8 - (dayOfWeek));
        }
        if (weekCount == 2) {
            // 在加一周
            cd1.add(Calendar.DAY_OF_MONTH, 7);
        }
        cd1.set(Calendar.HOUR_OF_DAY, 2); // 凌晨2点
        return cd1.getTime();
    }
    
}
