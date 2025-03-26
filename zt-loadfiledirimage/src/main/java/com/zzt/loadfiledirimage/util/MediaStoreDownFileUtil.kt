package com.zzt.loadfiledirimage.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * @author: zeting
 * @date: 2025/3/6
 *
 */
object MediaStoreDownFileUtil {
    val TAG = "FileDir-Util-kotlin"

    /**
     * 通过MediaStore获取文件uri
     * @return 获取失败返回null
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFileUri(context: Context, path: String): Uri? {
        // projection代表数据库中需要检索出来的列，也可以不写，query的第二个参数传null，写了性能更好
        val projection = arrayOf(
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads._ID,
            MediaStore.Downloads.RELATIVE_PATH
        )

        // SQL 语句，文件名匹配
        val selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?"
        // sql 查询
        val selectionArgs = arrayOf(path)

        val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        // 使用ContentResolver查找，获得数据库指针
//        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        Log.d(TAG, "下载目录信息 cursor: " + cursor?.count)

        var fileUri: Uri? = null
        if (cursor?.moveToFirst() == true) {
            val columnIndex = cursor.getColumnIndex(MediaStore.Downloads._ID)
            val fileId = cursor.getLong(columnIndex)
            fileUri = Uri.withAppendedPath(uri, fileId.toString())
            cursor.close()
        }
        return fileUri
    }


    /**
     * 写inputStream到公共目录Download
     * @param path 文件路径，必须以Download/开头，且不包含文件名
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun writeToDownload(
        context: Context,
        path: String,
        fileName: String,
        inputStream: InputStream
    ) {
        val contentValues = ContentValues().also {
            // 设置文件路径
            it.put(MediaStore.Downloads.RELATIVE_PATH, path)
            // 设置文件名称
            it.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        }
        // ContentUri 表示操作哪个数据库, contentValues 表示要插入的数据内容
        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
        // 向 path/filename 文件中插入数据
        val os: OutputStream = context.contentResolver.openOutputStream(uri)!!
        val bos = BufferedOutputStream(os)
        inputStream.use { istream ->
            bos.use { bos ->
                val buff = ByteArray(1024)
                var count: Int
                while (istream.read(buff).apply { count = this } != -1) {
                    bos.write(buff, 0, count)
                }
            }
        }
    }


    /**
     * 通过path检查文件存在性
     * @param context [Error type: Unresolved type for Context]
     * @param path String
     * @return Boolean
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkFileExistence(context: Context, path: String): Boolean {
        return getFileUri(context, path) != null
    }

    /**
     * 通过Uri获取文件名
     * @param context [Error type: Unresolved type for Context]
     * @param uri [Error type: Unresolved type for Uri]
     * @return String
     */
    fun getFileName(context: Context, uri: Uri): String {
        var fileName = ""
        val contentResolver = context.contentResolver
        // 此处只需要取出文件名这一项
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
            cursor.close()
        }

        return fileName
    }

    /**
     * 删除指定path文件
     * @param context [Error type: Unresolved type for Context]
     * @param path String
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteFile(context: Context, path: String) {
        val uri = getFileUri(context, path)
        if (uri == null) {
            return
        }
        context.contentResolver.delete(uri, null, null)
    }

    /**
     * 判断是否可以使用MediaStore
     * @param path String
     * @return Boolean
     */
    private fun useMediaStore(path: String): Boolean {
        return path.startsWith(Environment.DIRECTORY_DOWNLOADS) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }


}