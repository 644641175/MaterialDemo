package com.rh.materialdemo.Util;

/**
 * Created by RH on 2017/12/19.
 */

public interface DownloadListener {
    void onProcess(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
