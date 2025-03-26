package com.zzt.loadfiledirimage.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.zzt.loadfiledirimage.MyApplication;
import com.zzt.utilcode.util.ZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author: zeting
 * @date: 2025/3/6
 * 对下载图片进行操作
 */
public class DownFileImgUtil {
    private static final String TAG = "FileDir-Util-java";

    private static class InnerClass {
        private static final DownFileImgUtil INSTANCE = new DownFileImgUtil();
    }

    private DownFileImgUtil() {
    }

    public static DownFileImgUtil getInstance() {
        return InnerClass.INSTANCE;
    }

    /**
     * 获取 解压保存目录
     */
    public String getUnzipDir() {
        File filesDir = MyApplication.getInstance().getExternalFilesDir("");
        if (filesDir != null && filesDir.exists()) {
            File resDir = new File(filesDir.getAbsolutePath(), "/res");
            if (!resDir.exists()) {
                boolean mkdir = resDir.mkdir();
                if (mkdir) {
                    return resDir.getAbsolutePath();
                } else {
                    return filesDir.getAbsolutePath();
                }
            } else {
                return resDir.getAbsolutePath();
            }
        }
        return MyApplication.getInstance().getFilesDir().getAbsolutePath();
    }


    /**
     * 拷贝 文件到当前应用目录类
     */
    public void copyExtFileToCacheDir(String findName) {
        try {
            File extDownDir = MyApplication.getInstance().getExternalFilesDir("");
            File findFile = new File(extDownDir, findName);
            if (findFile.exists()) {
                // 创建解压目录
                String unzipDir = DownFileImgUtil.getInstance().getUnzipDir();
                // 文件存在就解压
                List<File> files = ZipUtils.unzipFile(findFile, new File(unzipDir));
                Log.d(TAG, "解压文件目录 ：" + files);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param urlFile
     */
    public void LoadImgPath(Context context, File urlFile, CustomTarget<Drawable> customTarget) {
        Glide.with(context)
                .load(urlFile)
                .into(customTarget);
    }

    public void LoadImg(Context context, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new Target<Bitmap>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onDestroy() {

                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {

                    }

                    @Override
                    public void setRequest(@Nullable Request request) {

                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }
                });
    }


    /**
     * 扫描整个下载目录
     */
    public void scanDownloadDir() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                String[] paths = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    paths[i] = files[i].getAbsolutePath();
                }
                MediaScannerConnection.scanFile(MyApplication.getInstance(),
                        paths,
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                // 扫描完成后的回调
                                Log.d(TAG, "扫描完成: " + path);
                                Log.w(TAG, "URI: " + uri);
                            }
                        });
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void queryDownloads29() {
        ContentResolver resolver = MyApplication.getInstance().getContentResolver();
        String[] projection = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE
        };
        try {
//            String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
//            String[] selectionArgs = new String[]{"dddd.txt"};
//            Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
//            Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, projection, null, null, null);
            Cursor cursor = resolver.query(MediaStore.Downloads.EXTERNAL_CONTENT_URI, projection, null, null, null);
            if (cursor != null) {
                Log.d(TAG, "下载目录信息 cursor: " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                    int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                    int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
                    int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);

                    while (!cursor.isAfterLast()) {
                        long id = cursor.getLong(idColumn);
                        String name = cursor.getString(nameColumn);
                        String data = cursor.getString(dataColumn);
                        long size = cursor.getLong(sizeColumn);
                        String mimeType = cursor.getString(mimeTypeColumn);

                        // 处理文件信息
                        Log.d(TAG, "下载目录信息 ID: " + id + ", Name: " + name + ", Data: " + data + ", Size: " + size + ", MIME Type: " + mimeType);

                        cursor.moveToNext();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void downloadWrite(InputStream is, String destFileName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, destFileName);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri uri = MyApplication.getInstance().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            Log.d(TAG, "下载文件 uri：" + uri);
            if (uri != null) {
                OutputStream fos = MyApplication.getInstance().getContentResolver().openOutputStream(uri);
                if (fos != null) {
                    int len = 0;
                    try {
                        byte[] buf = new byte[2048];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
