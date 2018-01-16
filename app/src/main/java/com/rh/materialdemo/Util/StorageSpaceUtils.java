package com.rh.materialdemo.Util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author RH
 * @date 2018/1/15
 */
public class StorageSpaceUtils {
    private static final String TAG = "StorageSpaceUtils";
    /**
     * 外置存储卡不存在的情况，返回错误码-1
     */

    private static final int ERROR = -1;

    /**
     * @return 外置SdCard是否可用
     */
    private static boolean isSdCardAvailable() {
        Map<String,String> evn=System.getenv();
        for (Map.Entry<String,String> entry:evn.entrySet()){
            Log.i(TAG,entry.getKey()+"-->"+entry.getValue());
        }
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * @return 获取外置SdCard根目录
     */
    private static String getSdCardRoot(){
        Map<String,String> evn=System.getenv();
        return evn.get("SECONDARY_STORAGE");
    }


    /**
     * @return  获取内置存储的可用空间
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.i(TAG,path.getAbsolutePath());
        StatFs stat = new StatFs(path.getAbsolutePath());
        long blockSize;
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }else {
            blockSize=stat.getBlockSize();
            availableBlocks=stat.getAvailableBlocks();
        }

        return blockSize * availableBlocks;
    }

    /**
     * @return 获取内置存储的总空间
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getAbsolutePath());
        long blockSize;
        long totalBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        }else{
            blockSize=stat.getBlockSize();
            totalBlocks=stat.getBlockCount();
        }
        return blockSize * totalBlocks;
    }

    /**
     * @return 获取外部存储的可用空间
     */

    public static long getAvailableExternalMemorySize() {
        if (isSdCardAvailable()) {
            StatFs stat = new StatFs(getSdCardRoot());
            long blockSize;
            long availableBlocks;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }else{
                blockSize=stat.getBlockSize();
                availableBlocks=stat.getAvailableBlocks();
            }

            return blockSize * availableBlocks;
        } else {
            return ERROR;
        }
    }


    /**
     * @return 获取外部存储的总存储空间
     */

    public static long getTotalExternalMemorySize() {
        if (isSdCardAvailable()) {
            StatFs stat = new StatFs(getSdCardRoot());
            long blockSize;
            long totalBlocks ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            }else{
                blockSize=stat.getBlockSize();
                totalBlocks=stat.getBlockCount();
            }

            return blockSize * totalBlocks;
        } else {
            return ERROR;
        }
    }

    /**
     * 格式化十进制数据为整形
     */

    private static DecimalFormat fileIntegerFormate = new DecimalFormat("#0");
    /**
     * 格式化十进制数据为浮点型
     */
    private static DecimalFormat fileDecimalFormate = new DecimalFormat("#0.#");


    /**
     * 转换单位
     * @param size 要转换的long型数据
     * @param isInteger 是否要转换成整形数据
     * @return 返回转换完成之后的字符串数据
     */

    public static String formateFileSize(long size, boolean isInteger) {
        DecimalFormat decimalFormat = isInteger ? fileIntegerFormate : fileDecimalFormate;
        String fileSizeString;
        if (size > 0 && size < 1024) {
            fileSizeString = decimalFormat.format((double) size) + "B ";
        } else if (size < 1024 * 1024) {
            fileSizeString = decimalFormat.format((double) size/1024)+"KB";
        }else if(size<1024*1024*1024){
            fileSizeString=decimalFormat.format((double) size/(1024*1024))+"MB";
        }else{
            fileSizeString=decimalFormat.format((double)size/(1024*1024*1024))+"GB";
        }
        return fileSizeString;
    }

}
