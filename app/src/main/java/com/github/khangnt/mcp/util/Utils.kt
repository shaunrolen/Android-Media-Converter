package com.github.khangnt.mcp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.khangnt.mcp.DEFAULT_IO_BUFFER_LENGTH
import com.github.khangnt.mcp.KB
import com.github.khangnt.mcp.MB
import io.reactivex.Observable
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Khang NT on 1/1/18.
 * Email: khang.neon.1997@gmail.com
 */

// close and catch all error, different with .use extension
fun Closeable?.closeQuietly() {
    catchAll { this?.close() }
}

inline fun catchAll(printLog: Boolean = false, action: () -> Unit) {
    try {
        action()
    } catch (ignore: Throwable) {
        if (printLog) Timber.d(ignore)
    }
}

fun copy(
        input: InputStream,
        output: OutputStream,
        bufferLength: Int = DEFAULT_IO_BUFFER_LENGTH
) {
    val buffer = ByteArray(bufferLength)
    var readLength = 0
    while (input.read(buffer).apply { readLength = this } > 0) {
        output.write(buffer, 0, readLength)
    }
}

fun JSONArray.toListString(): List<String> {
    return (0 until this.length()).map { this.getString(it) }
}

fun JSONObject.toMapString(): Map<String, String> {
    val res = mutableMapOf<String, String>()
    this.keys().forEach { res.put(it, this.opt(it).toString()) }
    return res
}

fun <T> Observable<T>.ignoreError(printLog: Boolean = false): Observable<T> =
        this.onErrorResumeNext { error: Throwable ->
            if (printLog) Timber.d(error)
            Observable.empty<T>()
        }

fun Int.toConverterSpeed(): String =
        when {
            this < KB -> "${this}B/s"
            this < MB -> "${this / KB}KB/s"
            else -> "${this / MB}MB/s"
        }

fun String.toJsonOrNull(): JSONObject? {
    catchAll { return JSONObject(this) }
    return null
}

fun <T> List<T>.toImmutable(): List<T> {
    // clone original then wrap inside unmodifiable list
    return Collections.unmodifiableList(ArrayList(this))
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(Intent.createChooser(intent, "Open $url"))
}