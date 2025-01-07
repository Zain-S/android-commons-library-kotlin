package com.tecjaunt.esanschool.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtil {
    const val FILES_PATH = "Compressor"
    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4
    @Throws(IOException::class)
    fun from(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = getFileName(context, uri)
        val splitName = splitFileName(fileName)
        var tempFile = splitName.firstOrNull()?.let { File.createTempFile(it, splitName[1]) }
        tempFile = tempFile?.let { rename(it, fileName) }
        tempFile?.deleteOnExit()
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(tempFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream != null) {
            copy(inputStream, out)
            inputStream.close()
        }
        out?.close()
        return tempFile
    }

    fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val realPath = cursor.getString(index)
            cursor.close()
            realPath
        }
    }

    fun rename(file: File, newName: String?): File {
        val newFile = File(file.getParent(), newName)
        if (newFile != file) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Log.d("FileUtil", "Delete old $newName file")
                }
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream?): Int {
        val count = copyLarge(input, output)
        return if (count > Int.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun copyLarge(
        input: InputStream, output: OutputStream?, buffer: ByteArray? = ByteArray(
            DEFAULT_BUFFER_SIZE
        )
    ): Long {
        var count: Long = 0
        var n: Int
        while (EOF != input.read(buffer).also { n = it }) {
            output!!.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }
}
