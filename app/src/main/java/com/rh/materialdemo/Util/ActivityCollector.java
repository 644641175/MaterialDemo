package com.rh.materialdemo.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author RH
 * @date 2018/1/6
 */
public class ActivityCollector {
    /**
     * 存放activity的列表
     */
    private static HashMap<Class<?>, Activity> activities = new LinkedHashMap<>();

    /**
     * 添加Activity
     *
     */
    public static void addActivity(Activity activity, Class<?> clz) {
        activities.put(clz, activity);
    }

    /**
     * 判断一个Activity 是否存在
     *
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static <T extends Activity> boolean isActivityExist(Class<T> clz) {
        boolean res;
        Activity activity = getActivity(clz);
        res = activity != null && !(activity.isFinishing() || activity.isDestroyed());
        return res;
    }

    /**
     * 获得指定activity实例
     *
     * @param clazz Activity 的类对象
     */
    private static <T extends Activity> T getActivity(Class<T> clazz) {
        return (T) activities.get(clazz);
    }

    /**
     * 移除activity,代替finish
     *
     */
    public static void removeActivity(Activity activity) {
        if (activities.containsValue(activity)) {
            activities.remove(activity.getClass());
        }
    }

    /**
     * 移除所有的Activity
     */
    public static void removeAllActivity() {
        if (activities != null && activities.size() > 0) {
            Set<Map.Entry<Class<?>, Activity>> sets = activities.entrySet();
            for (Map.Entry<Class<?>, Activity> s : sets) {
                if (!s.getValue().isFinishing()) {
                    s.getValue().finish();
                }
            }
        }
        assert activities != null;
        activities.clear();
    }

}
