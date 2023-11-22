package com.driverskr.sunlitweather.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.format.Formatter;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Created by shiju.wang on 2017/11/10.
 */

public class FileUtil {

    /**
     * 取Asset文件夹下文件
     *
     * @param paramContext
     * @param paramString
     * @return
     * @throws IOException
     */
    public static InputStream getAssetsInputStream(Context paramContext,
                                                   String paramString) throws IOException {
        return paramContext.getResources().getAssets().open(paramString);
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static String generateSize(File file) {
        long result;
        if (file.isFile()) {
            result = file.length();
        } else {
            result = getFolderSize(file);
        }
        return getSizeString(result);
    }

    public static String getSizeString(long size) {
        long gb = 2 << 29;
        long mb = 2 << 19;
        long kb = 2 << 9;
        if (size < kb) {
            return size + "B";
        } else if (size >= kb && size < mb) {
            return String.format(Locale.CHINESE, "%.2fKB", size / (double) kb);
        } else if (size >= mb && size < gb) {
            return String.format(Locale.CHINESE, "%.2fMB", size / (double) mb);
        } else if (size >= gb) {
            return String.format(Locale.CHINESE, "%.2fGB", size / (double) gb);
        }
        return null;
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     */
    private static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取sd卡总大小
     *
     * @param context
     * @return
     */
    public static String getSdTotalSize(Context context) {
        StatFs sf = new StatFs("/mnt/sdcard");
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获取sd卡可用大小
     *
     * @param context
     * @return
     */
    public static String getSdAvailableSize(Context context) {
        StatFs sf = new StatFs("/mnt/sdcard");
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * Try to return the absolute file path from the given Uri  兼容了file:///开头的 和 content://开头的情况
     * Uri转File路径
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * @param path 传入路径字符串
     * @return File
     */
    public static File createFileIfNotExist(String path) {
//        System.out.println("cr");
        File file = new File(path);
        if (!file.exists()) {
            try {
                new File(path.substring(0, path.lastIndexOf(File.separator)))
                        .mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return file;
    }

    /**
     * @param path 传入路径字符串
     * @return File
     */
    public static File creatDirIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * @param path
     * @return
     */
    public static boolean IsFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建新的文件，如果有旧文件，先删除再创建
     *
     * @param path
     * @return
     */
    public static File creatNewFile(String path) {
        File file = new File(path);
        if (IsFileExist(path)) {
            file.delete();
        }
        createFileIfNotExist(path);
        return file;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (IsFileExist(path)) {
            file.delete();
        }
        return true;
    }

    // 删除一个目录
    public static boolean deleteFileDir(String path) {
        File file = new File(path);
        if (!IsFileExist(path)) {
            return false;
        }
        if (!file.isDirectory()) {
            file.delete();
            return true;
        }
        String[] filelist = file.list();
        File temp = null;
        for (int i = 0; i < filelist.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + filelist[i]);
            } else {
                temp = new File(path + File.separator + filelist[i]);
            }
            if (temp.isFile()) {

                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteFileDir(path + "/" + filelist[i]);// 先删除文件夹里面的文件
            }
        }
        file.delete();
        return true;
    }

    /**
     * 外部存储是否可读写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String[] getFolderFileList(String rootpath) {
        File root = new File(rootpath);
        File[] filesOrDirs = root.listFiles();
        if (filesOrDirs != null) {
            String[] dir = new String[filesOrDirs.length];
            int num = 0;
            for (int i = 0; i < filesOrDirs.length; i++) {
                if (filesOrDirs[i].isDirectory()) {
                    dir[i] = filesOrDirs[i].getName();

                    num++;
                }
            }
            String[] dir_r = new String[num];
            num = 0;
            for (int i = 0; i < dir.length; i++) {
                if (dir[i] != null && !dir[i].equals("")) {
                    dir_r[num] = dir[i];
                    num++;
                }
            }
            return dir_r;
        } else
            return null;
    }

    /**
     * //获得流
     *
     * @param path
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static BufferedWriter getWriter(String path) throws FileNotFoundException,
            UnsupportedEncodingException {
        FileOutputStream fileout = null;
        fileout = new FileOutputStream(new File(path));
        OutputStreamWriter writer = null;
        writer = new OutputStreamWriter(fileout, StandardCharsets.UTF_8);
        return new BufferedWriter(writer);
    }

    public static InputStream getInputStream(String path) throws FileNotFoundException {
        FileInputStream filein = null;
        File file = new File(path);
        filein = new FileInputStream(file);
        BufferedInputStream in = null;
        if (filein != null)
            in = new BufferedInputStream(filein);
        return in;
    }

    /**
     * 将InputStream转换成byte数组
     *
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamToByte(InputStream in) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[6 * 1024];
        int count = -1;
        while ((count = in.read(data, 0, 4 * 1024)) != -1)
            outStream.write(data, 0, count);
        data = null;
        return outStream.toByteArray();
    }

    /**
     * 将OutputStream转换成byte数组
     * <p>
     * OutputStream
     *
     * @return byte[]
     * @throws IOException
     */
    public static byte[] OutputStreamToByte(OutputStream out)
            throws IOException {

        byte[] data = new byte[6 * 1024];
        out.write(data);
        return data;
    }

    /**
     * 将byte数组转换成InputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static InputStream byteToInputStream(byte[] in) {
        return new ByteArrayInputStream(in);
    }

    /**
     * 将byte数组转换成OutputStream
     *
     * @param in
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static OutputStream byteToOutputStream(byte[] in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(in);
        return out;
    }

    /**
     * 把输入流中的数据输入到Path里的文件里
     *
     * @param path
     * @param inputStream
     * @return
     */
    public static File writeFromInputToSD(String path, InputStream inputStream) {
        File file = null;
        OutputStream output = null;
        try {
            file = new FileUtil().createFileIfNotExist(path);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int temp;
            while ((temp = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 把数据输入到Path里的文件里
     *
     * @param path
     * @return
     */
    public static File writeFromInputToSD(String path, byte[] b) {
        File file = null;
        OutputStream output = null;
        try {
            file = new FileUtil().createFileIfNotExist(path);
            output = new FileOutputStream(file);
            output.write(b);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 方法：把一段文本保存到给定的路径中.
     */
    public static void writeStr2File(String filePath, String text) {
        try {
            // 首先构建一个文件输出流,用于向文件中写入数据.
            createFileIfNotExist(filePath);
            String txt = readStrFromFile(filePath);
            text = text + txt;
            FileOutputStream out = new FileOutputStream(filePath);
            // 构建一个写入器,用于向流中写入字符数据
            OutputStreamWriter writer = new OutputStreamWriter(out, "gb2312");
            writer.write(text);
            // 关闭Writer,关闭输出流
            writer.close();
            out.close();
        } catch (Exception e) {
            String ext = e.getLocalizedMessage();
            // Toast.makeText(this, ext, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 读取一个给定的文本文件内容,并把内容以一个字符串的形式返回
     * @param textFile
     * @return
     */
    public static String readStrFromFile(String textFile) {
        try {
            // 首先构建一个文件输入流,该流用于从文本文件中读取数据
            FileInputStream input = new FileInputStream(textFile);
            // 为了能够从流中读取文本数据,我们首先要构建一个特定的Reader的实例,
            // 因为我们是从一个输入流中读取数据,所以这里适合使用InputStreamReader.
            InputStreamReader streamReader = new InputStreamReader(input);
            // 为了能够实现一次读取一行文本的功能,我们使用了 LineNumberReader类,
            // 要构建LineNumberReader的实例,必须要传一个Reader实例做参数,
            // 我们传入前面已经构建好的Reder.
            LineNumberReader reader = new LineNumberReader(streamReader);
            // 字符串line用来保存每次读取到的一行文本.
            String line = null;
            // 这里我们使用一个StringBuilder来存储读取到的每一行文本,
            // 之所以不用String,是因为它每次修改都会产生一个新的实例,
            // 所以浪费空间,效率低.
            StringBuilder allLine = new StringBuilder();
            // 每次读取到一行,直到读取完成
            while ((line = reader.readLine()) != null) {
                allLine.append(line);
                // 这里每一行后面,加上一个换行符,LINUX中换行是”\n”,
                // windows中换行是”\r\n”.
                allLine.append("\n");
            }
            // 把Reader和Stream关闭
            streamReader.close();
            reader.close();
            input.close();
            // 把读取的字符串返回
            return allLine.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // 根据文件路径获取文件名
    public static String getNameFromPath(String path) {
        String[] pathParts = path.split(File.separator);
        return pathParts[pathParts.length - 1];
    }

    // 取前面的名字　"."
    public static String getNameByFlag(String source, String flag) {
        // String[] source_spli = source.split(flag);
        String s = source.toLowerCase().replace(flag, "");
        return s.trim();
    }
}