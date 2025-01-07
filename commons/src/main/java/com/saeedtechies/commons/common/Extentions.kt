package com.saeedtechies.commons.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.text.Editable
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.saeedtechies.commons.R
import com.saeedtechies.commons.utils.DATE_FORMAT_1
import com.saeedtechies.commons.utils.DATE_FORMAT_2
import com.saeedtechies.commons.utils.DEBUGGING
import com.saeedtechies.commons.utils.IMAGE_EXTENSION
import com.saeedtechies.commons.utils.ResultData
import com.tecjaunt.esanschool.utils.FileUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


// shared preferences
const val PREFS_KEY = "Prefs"

fun showSnackBar(string: String, contextView: View) {
    Snackbar.make(contextView, string, Snackbar.LENGTH_SHORT)
//            .setAnchorView(contextView)
        .setAction(R.string.ok) {
            // Responds to click on the action
        }
        .show()
}

@SuppressLint("RestrictedApi")
fun Snackbar.makeDraggable() {
    val snackBarLayout = view as Snackbar.SnackbarLayout
    val touchListener = object : View.OnTouchListener {
        private var startY = 0f
        private var translationY = 0f
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                    translationY = snackBarLayout.translationY
                }

                MotionEvent.ACTION_MOVE -> {
                    val newTranslationY = event.rawY - startY + translationY
                    if (newTranslationY >= 0) {
                        snackBarLayout.translationY = newTranslationY
                    }
                }

                MotionEvent.ACTION_UP -> {
                    val maxTranslation = snackBarLayout.height.toFloat()
                    if (snackBarLayout.translationY >= maxTranslation) {
                        dismiss()
                    } else {
                        snackBarLayout.animate().translationY(0f).start()
                    }
                }
            }
            return true
        }
    }
    snackBarLayout.setOnTouchListener(touchListener)
}

fun isValidEmail(email: String): Boolean = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun <T> LiveData<T>.toMutableLiveData(): MutableLiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    mediatorLiveData.addSource(this) {
        mediatorLiveData.value = it
    }
    return mediatorLiveData
}

fun getResultDataFailed(body: ResponseBody?): ResultData.Failed {
    val jObjError = JSONObject(body!!.string())
    var valueString = ""
    try {
        val string = jObjError.getString("validation_params_error")
        val obj = JSONObject(string)
        val keys: Iterator<String> = obj.keys()
        while (keys.hasNext()) {
            val keyValue: String = keys.next()
            valueString = obj.getString(keyValue)
            val re = Regex("[^A-Za-z0-9 ]")
            valueString = re.replace(valueString, "")
            break
        }
    } catch (e: Exception) {
        valueString = jObjError.getString("message")
    }
    return ResultData.Failed(valueString)
}

fun monthNumberToName(month: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, month - 1) // Calendar month is 0-based
    return SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun clearErrors(tilList: Array<TextInputLayout>) {
    tilList.forEach {
        it.error = null
    }
}

fun String?.toLocation(): Location {
    val latitude = this?.split(",")?.get(0)?.toDoubleOrNull()
    val longitude = this?.split(",")?.get(1)?.toDoubleOrNull()
    return Location("").also {
        if (latitude != null && longitude != null) {
            it.latitude = latitude
            it.longitude = longitude
        }
    }
}

fun Float.roundTo(decimalPlaces: Int) = String.format("%.${decimalPlaces}f", this).toDouble()

fun Date.toCustomFormat(): String {
    val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return format.format(this)
}

fun <T> List<T>.toCommaSeparatedString(): String {
    val commaSeparatedString = StringBuilder()
    for (i in this.indices) {
        commaSeparatedString.append(this[i])
        if (i != this.size - 1) {
            commaSeparatedString.append(",")
        }
    }
    return commaSeparatedString.toString()
}

fun getDaysDifference(dateString: String?, format: String): Long? {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    try {
        if (dateString != null) {
            val date = simpleDateFormat.parse(dateString)
            return if (date != null) {
                val currentDate = Calendar.getInstance()
                currentDate[Calendar.HOUR_OF_DAY] = 0
                currentDate[Calendar.MINUTE] = 0
                currentDate[Calendar.SECOND] = 0
                currentDate[Calendar.MILLISECOND] = 0
                val diffInMillis = currentDate.timeInMillis - date.time
                TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
            } else
                null
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return null
}

fun convertDateFromFormatToFormat(dateTimeString: String?, inputFormat: String, outputFormat: String): String? {
    val inputSdf = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputSdf = SimpleDateFormat(outputFormat, Locale.getDefault())

    return try {
        if (dateTimeString != null) {
            val dateTime = inputSdf.parse(dateTimeString)
            if (dateTime != null)
                outputSdf.format(dateTime)
            else
                null
        } else
            null
    } catch (exception: java.lang.Exception) {
        null
    }
}

fun Date.toFormat(outputFormat: String): String? {
    val outputSdf = SimpleDateFormat(outputFormat, Locale.getDefault())

    return try {
        outputSdf.format(this)
    } catch (exception: java.lang.Exception) {
        null
    }
}

fun String.toDate(inputFormat: String): Date? {
    try {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        return inputDateFormat.parse(this)
    } catch (e: ParseException) {
        Log.e(DEBUGGING, e.message.toString())
    }
    return null
}

fun getCustomerLastOrderDate(dateString: String?, inputFormat: String = DATE_FORMAT_1): String? {
    dateString?.let {
        return "${convertDateFromFormatToFormat(dateString, inputFormat, DATE_FORMAT_2)} (${
            getDaysDifference(
                dateString,
                inputFormat
            )
        } Days)"
    }
    return null
}

fun getCSVFileName(name: String): String {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    val time = "${calendar.get(Calendar.DATE)}/${calendar.get(Calendar.MONTH)}/${calendar.get(Calendar.YEAR)}"
    return name + "_$time.csv"
}

fun getDaysDifferenceFromDate(dateString: String?, inputFormat: String): Long? {
    return if (dateString != null) {
        try {
            val dateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
            val currentDateCalender = getCurrentDateOnly()
            val targetDate = dateFormat.parse(dateString)
            if (targetDate != null) {
                val targetDateCalender = getCurrentDateOnly().apply {
                    time = targetDate
                }
                val millisecondsPerDay = 24 * 60 * 60 * 1000
                (targetDateCalender.timeInMillis - currentDateCalender.timeInMillis) / millisecondsPerDay
            } else {
                null
            }
        } catch (exception: Exception) {
            Log.e(DEBUGGING, exception.message.toString())
            null
        }
    } else {
        null
    }
}

fun getCurrentDateOnly(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun String?.toFloatOrZero(): Float = this?.toFloatOrNull() ?: 0F
fun Float?.toFloatOrZero(): Float = this ?: 0F
fun String?.toDoubleOrZero(): Double = this?.toDoubleOrNull() ?: 0.0
fun Float?.toIntOrZero(): Int = this?.toInt() ?: 0

fun Float?.toIntOrNull(): Int? = this?.toInt()

fun Location(latitude: Double?, longitude: Double?): Location {
    return Location("").also { location ->
        location.latitude = latitude ?: 0.0
        location.longitude = longitude ?: 0.0
    }
}

fun getCurrentDateTime(format: String): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun Int?.toIntOrZero() = this ?: 0

fun String?.toIntOrZero() = this?.toIntOrNull() ?: 0

fun <T> List<T>.getOrNull(index: Int): T? {
    return if (this.size > index)
        this[index]
    else
        null
}

fun screenShot(view: View): Bitmap? {
    val bitmap = Bitmap.createBitmap(
        view.width,
        view.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun createNumberList(from: Int, n: Int): List<Int> {
    return (from..n).toList()
}

fun List<Int>.toStringList(): List<String> {
    val stringList = ArrayList<String>()
    for (i in this.indices) {
        stringList.add(i.toString())
    }
    return stringList
}

fun List<String>.toIntList(): List<Int> {
    val intArrayList = ArrayList<Int>()
    this.forEach {
        intArrayList.add(it.toIntOrZero())
    }
    return intArrayList
}

fun Date.isToday(): Boolean {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

fun Date.isSameDay(date: Date): Boolean {
    val firstCalendar = Calendar.getInstance()
    val secondCalender = Calendar.getInstance()
    secondCalender.time = date
    firstCalendar.time = this
    return firstCalendar.get(Calendar.YEAR) == secondCalender.get(Calendar.YEAR) &&
            firstCalendar.get(Calendar.DAY_OF_YEAR) == secondCalender.get(Calendar.DAY_OF_YEAR)
}

fun Boolean.toInt() = if (this) 1 else 0

fun getSelectDocumentIntent(mimeTypes: Array<String>): Intent {
    val intent = Intent()
    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    intent.action = Intent.ACTION_GET_CONTENT
    return intent
}

fun Uri.isImage(context: Context) = this.toName(context)?.isImage()

fun Uri.toName(context: Context): String? = FileUtil.from(context, this)?.name

fun String.isImage(): Boolean {
    for (extension in IMAGE_EXTENSION) {
        if (this.lowercase(Locale.getDefault()).endsWith(extension)) {
            return true
        }
    }
    return false
}

fun Uri.toMultiPart(context: Context, paramName: String): MultipartBody.Part? {
    val file = FileUtil.from(context, this)
    return if (file != null) {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    }
    else
        null
}

