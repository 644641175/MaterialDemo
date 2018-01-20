package com.rh.neihan.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author RH
 * @date 2018/1/19
 */
public class DataUtils {
    /**
     * @return 时间戳转换为当前时间年月（时间戳单位为秒）
     */
    public static String millis2CurrentTime(long millis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM--dd",Locale.getDefault());
        return simpleDateFormat.format(new Date(millis*1000));
    }

    /**
     * @return 获取当前时间
     */
    private static String grtCurrentFormatTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM—dd HH:mm", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
}
