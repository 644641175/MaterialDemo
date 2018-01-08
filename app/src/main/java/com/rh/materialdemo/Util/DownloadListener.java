package com.rh.materialdemo.Util;

/**
 *
 * @author RH
 * @date 2017/12/19
 */

public interface DownloadListener {
    void onProcess(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
