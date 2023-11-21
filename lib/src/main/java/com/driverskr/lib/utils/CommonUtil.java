package com.driverskr.lib.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Locale;

/**
 * 工具类，所有通用
 */
public class CommonUtil {

    public static String getVersionName(Context context) {
        return getPackageInfo(context, 1);
    }

    public static int getVersionCode(Context context) {
        return getPackageInfo(context, 2);
    }

    /**
     * 获取PackageInfo信息
     *
     * @param context
     * @param flag    1: versionName  2:versionCode
     * @return
     */
    public static <T> T getPackageInfo(Context context, int flag) {
        try {
            //applicationId 获取
            String pkName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    pkName, 0);
            if (flag == 1) {
                //versionName获取
                return (T) packageInfo.versionName;
            } else if (flag == 2) {
                //versionCode获取
                return (T) Integer.valueOf(packageInfo.versionCode);
            } else {
                return (T) packageInfo.versionName;
            }
        } catch (Exception e) {
        }
        return (T) "";
    }

    /**
     * 判断是否安装某apk
     *
     * @param context
     * @param PACK_NAME
     * @return
     */
    public static boolean isInstallApp(Context context, String PACK_NAME) {
        final PackageManager packageManager = context.getApplicationContext().getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals(PACK_NAME)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * List深拷贝
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /**
     * get the name of process
     *
     * @return
     */
    public static String getProcessName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName();
        } else {
            try {
                File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
                BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
                String processName = mBufferedReader.readLine().trim();
                mBufferedReader.close();
                return processName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 将字符串的首字母变为大写
     *
     * @param str
     * @return
     */
    public static String upCaseFirstChar(String str) {
        if (str.isEmpty()) return null;
        return str.substring(0, 1).toUpperCase().concat(str.substring(1));
    }
}