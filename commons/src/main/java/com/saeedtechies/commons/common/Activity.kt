package com.saeedtechies.commons.common

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.saeedtechies.commons.R
import com.saeedtechies.commons.utils.CAPTURE_IMAGE_PERMISSIONS
import com.saeedtechies.commons.utils.DEBUGGING
import com.saeedtechies.commons.utils.ResultData
import com.saeedtechies.commons.utils.SELECT_FROM_GALLERY
import com.saeedtechies.commons.utils.SELECT_IMAGE_LIST
import com.saeedtechies.commons.utils.TAKE_IMAGE
import com.saeedtechies.commons.utils.UN_AUTHENTICATED
import java.text.DateFormat
import java.util.Date

/**
 *Sets up a MaterialDatePicker dialog.
 *@param onPositiveListener A listener for when the user selects a date.
 *@param onNegativeListener A listener for when the user taps the "Cancel" button.
 *@param onCancelListener A listener for when the user dismisses the dialog by tapping outside of it.
 */
fun AppCompatActivity.setUpDatePicker(
    dateValidator: CalendarConstraints.DateValidator? = null,
    onPositiveListener: ((date: Date) -> Unit)? = null,
    onNegativeListener: ((view: View) -> Unit)? = null,
    onCancelListener: ((dialogInterface: DialogInterface) -> Unit)? = null
) {
    // datePicker builder
    val builder = MaterialDatePicker.Builder.datePicker()
    // setting constraints
    dateValidator?.let {
        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setValidator(dateValidator)
        builder.setCalendarConstraints(constraintsBuilder.build())
    }
    // datePicker
    val datePicker = builder.build()
    datePicker.show(supportFragmentManager, "DatePicker")
    datePicker.addOnPositiveButtonClickListener {
        DateFormat.getDateInstance().parse(datePicker.headerText)?.let {
            onPositiveListener?.invoke(it)
        }
    }
    datePicker.addOnNegativeButtonClickListener {
        Log.d(DEBUGGING, "Date picker cancelled")
        onNegativeListener?.invoke(it)
    }
    datePicker.addOnCancelListener {
        Log.d(DEBUGGING, "Date picker cancelled")
        onCancelListener?.invoke(it)
    }
}

/*fun <T> AppCompatActivity.initCompleteResponse(
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) {
    when (resultData) {
        is ResultData.Loading -> showProgress()
        is ResultData.Success -> {
            if (onSuccess != null)
                resultData.data?.let { data ->
                    onSuccess.invoke(data)
                }

        }
        is ResultData.Failed -> {
            hideProgress()
            resultData.message?.let { errorMessage ->
                Log.e(DEBUGGING, errorMessage)
                if (onError != null)
                    onError.invoke(errorMessage)
                else
                    showToast(errorMessage)
            }
        }
        is ResultData.Exception -> {
            hideProgress()
            resultData.exception?.let { exception ->
                Log.e(DEBUGGING, exception.message.toString())
                if (onError != null)
                    onError.invoke(exception.message.toString())
                else
                    showToast(exception.message.toString())
            }
        }
        is ResultData.Complete -> {
            hideProgress()
            showToast(R.string.data_fetched_successfully)
        }
        else -> showToast(R.string.something_went_wrong)
    }
}*/

/*fun AppCompatActivity.getAttachedFragment(): Fragment {
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    return navHostFragment.childFragmentManager.fragments[0]
}*/

fun Activity.initLocationPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    if (!isLocationEnabled())
        showEnableGPSDialog()
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        locationPermissionChecksSDK23(requestPermissionLauncher, onSuccess)
    } else
        locationPermissionChecks(requestPermissionLauncher, onSuccess)
}

fun Activity.locationPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            onSuccess.invoke()
        }

        else -> {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
}

fun Activity.locationPermissionChecksSDK23(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            onSuccess.invoke()
        }

        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            showDialog(getString(R.string.allow_location_permission_warning), isCancelable = false, onPositiveButton = {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            })
        }

        else -> {
            if (config.noOfTimesPermissionAsked < 2) {
                config.noOfTimesPermissionAsked++
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            else
                showEnableLocationForAppDialog()
        }
    }
}

fun Activity.installApkPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Intent>, onSuccess: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val hasInstallPermission = packageManager.canRequestPackageInstalls()
        if (!hasInstallPermission) {
            val installIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
            requestPermissionLauncher.launch(installIntent)
            return
        } else
            onSuccess.invoke()
    } else
        onSuccess.invoke()
}

fun Activity.initPrinterPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        printerPermissionChecksSDK31(requestPermissionLauncher, onSuccess)
    } else
        printerPermissionChecks(requestPermissionLauncher, onSuccess)
}

fun Activity.printerPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        checkMultiplePermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)) -> {
            onSuccess.invoke()
        }

        else -> {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
fun Activity.printerPermissionChecksSDK31(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        checkMultiplePermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        ) -> {
            onSuccess.invoke()
        }

        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            runOnUiThread { showToast(R.string.allow_location_permission_warning) }
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }

        shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN) -> {
            runOnUiThread { showToast(R.string.allow_bluetooth_permission_warning) }
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN))
        }

        shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT) -> {
            runOnUiThread { showToast(R.string.allow_bluetooth_permission_warning) }
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT))
        }

        else -> {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }
    }
}

fun Activity.initTakeImagePermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    takeImagePermissionChecksSDK23(requestPermissionLauncher, onSuccess)
}

fun Activity.takeImagePermissionChecksSDK23(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        checkMultiplePermissions(CAPTURE_IMAGE_PERMISSIONS) -> {
            onSuccess.invoke()
        }

        shouldShowRequestPermissionRationale(CAMERA) -> {
            runOnUiThread { showToast(R.string.allow_camera_access_warning) }
            requestPermissionLauncher.launch(CAPTURE_IMAGE_PERMISSIONS)
        }

        else -> {
            requestPermissionLauncher.launch(CAPTURE_IMAGE_PERMISSIONS)
        }
    }
}

fun Activity.initReadStoragePermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    readStoragePermissionChecksSDK23(requestPermissionLauncher, onSuccess)
}

fun Activity.readStoragePermissionChecksSDK23(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        checkMultiplePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)) -> {
            onSuccess.invoke()
        }

        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
            runOnUiThread { showToast(R.string.allow_storage_permission_warning) }
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }

        else -> {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }
}

fun Activity.selectImageOrTakeImageDialog(
    takeImageLauncher: ActivityResultLauncher<Intent>,
    takeImagePermissionLauncher: ActivityResultLauncher<Array<String>>,
    selectImageLauncher: ActivityResultLauncher<Intent>,
    selectImagePermissionLauncher: ActivityResultLauncher<Array<String>>,
    callback: (uri: Uri?) -> Unit,
) {
    var selected = 0
    MaterialAlertDialogBuilder(this).setTitle(getString(R.string.upload_image))
        .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            when (SELECT_IMAGE_LIST[selected]) {
                TAKE_IMAGE -> {
                    initTakeImagePermissionChecks(takeImagePermissionLauncher) {
                        callback.invoke(openCamera(takeImageLauncher))
                    }
                }

                SELECT_FROM_GALLERY -> {
                    initReadStoragePermissionChecks(selectImagePermissionLauncher) {
                        selectImageLauncher.launch(Intent.createChooser(
                            getSelectImageIntent(), getString(
                                R.string.select_picture)))
                    }
                }
            }
        }.setSingleChoiceItems(SELECT_IMAGE_LIST, selected) { _, which ->
            selected = which
        }.show()
}

fun Activity.openCamera(registerForActivityResult: ActivityResultLauncher<Intent>): Uri? {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "New Picture")
    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    registerForActivityResult.launch(cameraIntent)
    return imageUri
}

fun getSelectImageIntent(): Intent {
    val intent = Intent()
    val mimeTypes = arrayOf("image/*")
    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
    intent.action = Intent.ACTION_GET_CONTENT
    return intent
}

fun AppCompatActivity.showProgress(isSwipeToRefresh: Boolean) {
    findViewById<LinearProgressIndicator>(R.id.linear_progress_indicator)?.visible()
    if (isSwipeToRefresh)
        findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshLayout)?.isRefreshing = true
}

fun AppCompatActivity.hideProgress(isSwipeToRefresh: Boolean) {
    findViewById<LinearProgressIndicator>(R.id.linear_progress_indicator)?.gone()
    if (isSwipeToRefresh)
        findViewById<SwipeRefreshLayout>(R.id.swipeToRefreshLayout)?.isRefreshing = false
}

fun <T> AppCompatActivity.initResponse(
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
    onInternetFailure: (() -> Unit)? = null
) = initResponse(resultData, false, onSuccess, onError, onInternetFailure)

fun <T> AppCompatActivity.initResponse(
    resultData: ResultData<T>? = null,
    isSwipeToRefresh: Boolean = false,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
    onInternetFailure: (() -> Unit)? = null
) {
    when (resultData) {
        is ResultData.Loading -> showProgress(isSwipeToRefresh)
        is ResultData.Success -> {
            hideProgress(isSwipeToRefresh)
            if (onSuccess != null)
                resultData.data?.let { data ->
                    onSuccess.invoke(data)
                }
//            else
//                showToast(R.string.data_fetched_successfully)

        }

        is ResultData.Failed -> {
            hideProgress(isSwipeToRefresh)
            resultData.message?.let { errorMessage ->
                Log.e(DEBUGGING, errorMessage)
                if (errorMessage.contains(UN_AUTHENTICATED)) {
                    showToast(R.string.re_log_in)
                } else if (onError != null)
                    onError.invoke(errorMessage)
                else
//                    showToast(R.string.something_went_wrong)
                    showToast(errorMessage)
            }
        }

        is ResultData.Exception -> {
            hideProgress(isSwipeToRefresh)
            resultData.exception?.let { exception ->
                Log.e(DEBUGGING, exception.message.toString())
                if (exception.message.toString().contains(UN_AUTHENTICATED)) {
                    showToast(R.string.re_log_in)
                }
                if (exception is java.net.ConnectException) {
                    if (onInternetFailure != null) onInternetFailure.invoke()
                    else showSnackBar(R.string.failed_to_connect_toInternet)
                } else if (exception is java.net.UnknownHostException) {
                    if (onInternetFailure != null) onInternetFailure.invoke()
                    else showSnackBar(R.string.failed_to_connect_toInternet)
                } else if (exception is java.net.SocketTimeoutException) {
                    if (onInternetFailure != null) onInternetFailure.invoke()
                    else showSnackBar(R.string.please_check_your_internet_connection)
                } else {
                    if (onError != null) onError.invoke(exception.message.toString())
                    else showToast(exception.message.toString())
                }
            }
        }

        else -> showToast(R.string.something_went_wrong)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameLayout: FrameLayout) {
    supportFragmentManager.beginTransaction().replace(frameLayout.id, fragment).commit()
}

fun AppCompatActivity.navController(fragmentContainerViewId: Int): NavController? {
    val navHostFragment = (supportFragmentManager.findFragmentById(fragmentContainerViewId) as NavHostFragment?)
    return navHostFragment?.navController
}

fun AppCompatActivity.setNavController(bottomNavigationViewId: Int, fragmentContainerViewId: Int) {
    navController(fragmentContainerViewId)?.let { findViewById<BottomNavigationView>(bottomNavigationViewId)?.setupWithNavController(it) }
}