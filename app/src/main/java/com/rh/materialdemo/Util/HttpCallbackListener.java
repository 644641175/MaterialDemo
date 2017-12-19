package com.rh.materialdemo.Util;

/**
 * Created by RH on 2017/12/14.
 */

public interface HttpCallbackListener {
    void onFinish(String string);

    void onError(Exception e);
}
