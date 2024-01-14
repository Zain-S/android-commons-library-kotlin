package com.saeedtechies.commons.common

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.saeedtechies.commons.R
import com.saeedtechies.commons.utils.Config
import com.saeedtechies.commons.utils.DEBUGGING
import com.saeedtechies.commons.utils.ResultData
import com.saeedtechies.commons.utils.UN_AUTHENTICATED
import java.text.DateFormat
import java.util.Date

val Fragment.config: Config get() = Config.newInstance(requireActivity().applicationContext)

fun Fragment.startCallIntent(phoneNumber: String?) = requireContext().startCallIntent(phoneNumber)

fun Fragment.startSmsIntent(phoneNumber: String?) = requireContext().startSmsIntent(phoneNumber)

fun Fragment.startDirectionIntent(latitude: String?, longitude: String?) = requireContext().startDirectionIntent(latitude, longitude)

fun Fragment.startLocationIntent(customerLocation: String?, name: String) = requireContext().startLocationIntent(customerLocation, name)

fun Fragment.initResponse(
    resultData: ResultData<Nothing>,
    onSuccess: (() -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) = initResponse(null, resultData, onSuccess, onError)

fun Fragment.initResponse(
    loader: LinearProgressIndicator?,
    resultData: ResultData<Nothing>,
    onSuccess: (() -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) {
    when (resultData) {
        is ResultData.Loading -> loader?.visible()
        is ResultData.Success -> {
            loader?.gone()
            onSuccess?.invoke()
        }

        is ResultData.Failed -> {
            loader?.gone()
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
            loader?.gone()
            resultData.exception?.let { exception ->
                Log.e(DEBUGGING, exception.message.toString())
                if (onError != null)
                    onError.invoke(exception.message.toString())
                else
                    showToast(R.string.something_went_wrong)
            }
        }

        else -> showToast(R.string.something_went_wrong)
    }
}

fun <T> Fragment.initResponse(
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) = initResponse(null, null, resultData, onSuccess, onError)

fun <T> Fragment.initResponse(
    swipeRefreshLayout: SwipeRefreshLayout,
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) = initResponse(null, swipeRefreshLayout, resultData, onSuccess, onError)

fun <T> Fragment.initResponse(
    loader: LinearProgressIndicator,
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) = initResponse(loader, null, resultData, onSuccess, onError)

fun <T> Fragment.initResponse(
    loader: LinearProgressIndicator?,
    swipeRefreshLayout: SwipeRefreshLayout?,
    resultData: ResultData<T>? = null,
    onSuccess: ((data: T) -> Unit)? = null,
    onError: ((message: String) -> Unit)? = null,
) {
    when (resultData) {
        is ResultData.Loading -> {
            loader?.visible()
            swipeRefreshLayout?.isRefreshing = true
        }

        is ResultData.Success -> {
            loader?.gone()
            swipeRefreshLayout?.isRefreshing = false
            resultData.data?.let { data ->
                onSuccess?.invoke(data)
            }
        }

        is ResultData.Failed -> {
            loader?.gone()
            swipeRefreshLayout?.isRefreshing = false
            resultData.message?.let { errorMessage ->
                Log.e(DEBUGGING, errorMessage)
                if (errorMessage.contains(UN_AUTHENTICATED)) {
                    showToast(R.string.re_log_in)
                } else if (onError != null)
                    onError.invoke(errorMessage)
                else
                    showToast(errorMessage)
            }
        }

        is ResultData.Exception -> {
            loader?.gone()
            swipeRefreshLayout?.isRefreshing = false
            resultData.exception?.let { exception ->
                Log.e(DEBUGGING, exception.message.toString())
                if (onError != null)
                    onError.invoke(exception.message.toString())
                else
                    showToast(R.string.something_went_wrong)
            }
        }

        else -> {
            loader?.gone()
            swipeRefreshLayout?.isRefreshing = false
            showToast(R.string.something_went_wrong)
        }
    }
}

/**
 *Sets up a MaterialDatePicker dialog.
 *@param onPositiveListener A listener for when the user selects a date.
 *@param onNegativeListener A listener for when the user taps the "Cancel" button.
 *@param onCancelListener A listener for when the user dismisses the dialog by tapping outside of it.
 */
fun Fragment.setUpDatePicker(
    dateValidator: DateValidator? = null,
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
    datePicker.show(childFragmentManager, "DatePicker")
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

fun Fragment.showDialog(
    title: String,
    message: String? = null,
    icon: Int? = null,
    onPositiveButton: (() -> Unit)? = null,
    onNegativeButton: ((dialog: DialogInterface) -> Unit)? = null
) =
    requireContext().showDialog(title, message, icon, onPositiveButton, onNegativeButton)

suspend fun Fragment.getCurrentLocation() = requireContext().getCurrentLocation()

fun Fragment.isLocationEnabled() = requireContext().isLocationEnabled()
fun Fragment.showEnableGPSDialog() = requireContext().showEnableGPSDialog()
fun Fragment.showEnableLocationForAppDialog() = requireContext().showEnableLocationForAppDialog()
fun Fragment.initLocationPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    if (!isLocationEnabled())
        showEnableGPSDialog()
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        locationPermissionChecksSDK23(requestPermissionLauncher, onSuccess)
    } else
        locationPermissionChecks(requestPermissionLauncher, onSuccess)
}

fun Fragment.locationPermissionChecks(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            onSuccess.invoke()
        }

        else -> {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun Fragment.locationPermissionChecksSDK23(requestPermissionLauncher: ActivityResultLauncher<Array<String>>, onSuccess: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            onSuccess.invoke()
        }

        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            requireContext().showDialog(getString(R.string.allow_location_permission_warning), isCancelable = false, onPositiveButton = {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            })
        }

        else -> {
            if (config.noOfTimesPermissionAsked < 3) {
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

fun Fragment.showSnackBar(message: Int) = requireContext().showSnackBar(message)

fun Fragment.showSnackBar(message: String) = requireContext().showSnackBar(message)

fun Fragment.refreshCurrentFragment() {
    val id = findNavController().currentDestination?.id
    findNavController().popBackStack(id!!, true)
    findNavController().navigate(id)
}

fun Fragment.showComingSoon() {
    requireContext().showComingSoon()
}

fun Fragment.openCamera(registerForActivityResult: ActivityResultLauncher<Intent>) = requireContext().openCamera(registerForActivityResult)

fun Fragment.replaceChildFragment(fragment: Fragment, frameLayout: FrameLayout) {
    childFragmentManager.beginTransaction().replace(frameLayout.id, fragment)
        .setReorderingAllowed(true)
        .addToBackStack(null)
        .commit()
}

fun Fragment.checkPermission(permission: String) = requireContext().checkPermission(permission)

fun Fragment.checkMultiplePermissions(permission: Array<String>) = requireContext().checkMultiplePermissions(permission)

fun Fragment.showToast(string: String) {
    Toast.makeText(requireContext(), string, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(stringId: Int) {
    Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show()
}

fun Fragment.hideKeyboard(view: View) {
    requireActivity().hideKeyboard(view)
}