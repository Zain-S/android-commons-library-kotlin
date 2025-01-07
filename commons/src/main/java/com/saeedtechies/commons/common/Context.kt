package com.saeedtechies.commons.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.saeedtechies.commons.R
import com.saeedtechies.commons.databinding.DialogLoaderBinding
import com.saeedtechies.commons.databinding.DialogShowImageBinding
import com.saeedtechies.commons.databinding.ItemEdittextBinding
import com.saeedtechies.commons.utils.Config
import com.saeedtechies.commons.utils.DEBUGGING
import com.saeedtechies.commons.utils.DEFAULT_FOLDER_NAME
import com.saeedtechies.commons.utils.DOCUMENTS_MIME_TYPES
import com.tecjaunt.esanschool.utils.FileUtil
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.startCallIntent(phoneNumber: String?) {
    if (phoneNumber != null)
        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)))
    else
        showToast(R.string.phone_number_not_found)
}

fun Context.startSmsIntent(phoneNumber: String?) {
    if (phoneNumber != null)
        startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${phoneNumber}")))
    else
        showToast(R.string.phone_number_not_found)
}

fun Context.startDirectionIntent(latitude: String?, longitude: String?) {
    if (latitude != null && longitude != null)
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${latitude},${longitude}&mode=l")
            ).setPackage("com.google.android.apps.maps")
        )
    else
        showToast(R.string.location_not_found)
}

fun Context.startDirectionIntent(locationsList: List<String?>) {
    var url = "https://www.google.com/maps/dir/?api=1&travelmode=l&waypoints="
    val tempLocationList = locationsList.take(10)
    tempLocationList.forEachIndexed { index, location ->
        if (index < tempLocationList.size - 1) {
            val latitude = location?.split(",")?.get(0)?.toDoubleOrNull()
            val longitude = location?.split(",")?.get(1)?.toDoubleOrNull()
            if (latitude != null && longitude != null) {
                url += "$latitude,$longitude|"
            }
        }
        if (tempLocationList.size - 1 == index) {
            url.removeSuffix("|")
            val latitude = location?.split(",")?.get(0)
            val longitude = location?.split(",")?.get(1)
            if (latitude != null && longitude != null) {
                url += "&destination=$latitude,$longitude"
            }
        }
    }
    Log.d(DEBUGGING, "Selected url: $url")
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    startActivity(intent)
}

fun Context.startDirectionIntent(customerLocation: String?) {
    val latitude = customerLocation?.split(",")?.get(0)
    val longitude = customerLocation?.split(",")?.get(1)
    if (latitude != null && longitude != null)
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=${latitude},${longitude}&mode=l")
            ).setPackage("com.google.android.apps.maps")
        )
    else
        showToast(R.string.location_not_found)
}

fun Context.startLocationIntent(customerLocation: String?, name: String) {
    val latitude = customerLocation?.split(",")?.get(0)
    val longitude = customerLocation?.split(",")?.get(1)
    if (latitude != null && longitude != null)
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${latitude},${longitude}?z=15&q=${latitude},${longitude}($name)")
            )
        )
    else
        showToast(R.string.location_not_found)
}

fun Context.sendEmail(email: String?, subject: String? = null, details: String? = null) {
    if (email != null) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$email")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        subject?.let {
            intent.putExtra(Intent.EXTRA_SUBJECT, it)
        }
        details?.let {
            intent.putExtra(Intent.EXTRA_TEXT, it)
        }
        try {
            startActivity(Intent.createChooser(intent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            showToast(R.string.no_email_app_installed)
        }
    } else
        showToast(R.string.email_not_found)
}

fun Context.setTextColor(colorAttr: Int, textViews: List<TextView>) {
    val color = attributeToColor(colorAttr)
    textViews.forEach {
        it.setTextColor(color)
    }
}

fun Context.showDialog(
    title: String,
    message: String? = null,
    icon: Int? = null,
    onPositiveButton: (() -> Unit)? = null,
    onNegativeButton: ((dialog: DialogInterface) -> Unit)? = null,
    onCancelButton: ((dialog: DialogInterface) -> Unit)? = null,
    isCancelable: Boolean = true
) {
    MaterialAlertDialogBuilder(
        this@showDialog,
        com.google.android.material.R.style.MaterialAlertDialog_Material3
    ).setCancelable(false)
        .create().apply {
            setTitle(title)
            setMessage(message)
            icon?.let {
                setIcon(it)
            }
            setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay)) { _, _ ->
                onPositiveButton?.invoke()
                dismiss()
            }
            onNegativeButton?.let {
                setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
                    onNegativeButton.invoke(dialog)
                    dismiss()
                }
            }
            onCancelButton?.let {
                setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel)) { dialog, _ ->
                    onCancelButton.invoke(dialog)
                    dismiss()
                }
            }
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(isCancelable)
        }.show()
}

suspend fun Context.updateLocation(): Location? {
    val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 6000).apply {
        setMinUpdateDistanceMeters(10F)
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setWaitForAccurateLocation(true)
    }.build()
    return if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationResult = fusedLocationClient.awaitLocationUpdates(this, mLocationRequest)
        locationResult?.lastLocation
    } else
        null
}

private suspend fun FusedLocationProviderClient.awaitLocationUpdates(
    context: Context,
    locationRequest: LocationRequest,
    looper: Looper? = Looper.myLooper(),
) = suspendCancellableCoroutine { continuation ->
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            continuation.resumeWith(Result.success(locationResult))
            removeLocationUpdates(this)
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            if (!locationAvailability.isLocationAvailable) {
                continuation.resumeWith(Result.success(null))
                removeLocationUpdates(this)
            }
        }
    }
    if (context.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && context.checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) {
        requestLocationUpdates(locationRequest, locationCallback, looper)
        continuation.invokeOnCancellation { removeLocationUpdates(locationCallback) }
    }
}

suspend fun Context.getLastKnownLocation(): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    var bestLocation = fusedLocationClient.lastLocation.await()
    if (bestLocation == null) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        for (provider in providers) {
            if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                val location = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }
        }
    }
    return bestLocation
}

suspend fun Context.getCurrentLocation(): Location? {
    updateLocation()
    return getLastKnownLocation()
}

suspend fun Context.getDistanceFromCurrentLocation(location: Location) =
    getDistanceFromCurrentLocation(location.latitude, location.longitude)

suspend fun Context.getDistanceFromCurrentLocation(latitude: Double?, longitude: Double?): Float? {
    return if (checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) && latitude != null && longitude != null
    ) {
        val location = Location("").also { location ->
            location.latitude = latitude
            location.longitude = longitude
        }
        getLastKnownLocation()?.distanceTo(location)
    } else
        null
}

fun Context.startWhatsappChatIntent(number: String?) {
    if (number != null) {
        val url = "https://api.whatsapp.com/send?phone=$number"
        startActivity(Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(url)
        })
    } else
        showToast(R.string.phone_number_not_found)
}

fun Context.openLink(url: String) {
    if (URLUtil.isValidUrl(url)) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}

fun Context.showEnableGPSDialog() {
    showDialog(getString(R.string.enable_gps), onNegativeButton = {}, onPositiveButton = {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }, isCancelable = false)
}

fun Context.showEnableLocationForAppDialog() {
    showDialog(getString(R.string.allow_location_for_app), onPositiveButton = {
        openAppSettings()
    }, isCancelable = false)
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}

fun Context.getColorCustom(colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.generateFile(fileName: String): File? {
    val folder = File(cacheDir.path + "/" + DEFAULT_FOLDER_NAME + "/")
    if (!folder.exists()) {
        folder.mkdirs()
    }
    val csvFile = File("$folder/", fileName)
    try {
        csvFile.createNewFile()
    } catch (exception: Exception) {
        Log.e(DEBUGGING, exception.message.toString())
    }
    return if (csvFile.exists()) {
        csvFile
    } else {
        null
    }
}

fun Context.openFile(fileName: String, buildConfigApplicationId: String) {
    val myFile: File
    try {
        myFile = File(cacheDir.path + "/" + DEFAULT_FOLDER_NAME + "/" + fileName)
        try {
            val uri =
                FileProvider.getUriForFile(this, "$buildConfigApplicationId.provider", myFile)
            val fileIntent = Intent(Intent.ACTION_VIEW)
            fileIntent.data = uri
            fileIntent.putExtra(Intent.EXTRA_MIME_TYPES, DOCUMENTS_MIME_TYPES)
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(fileIntent)
        } catch (e: Exception) {
            Log.e(DEBUGGING, e.message.toString())
        }
    } catch (e: ActivityNotFoundException) {
        Log.e(DEBUGGING, e.message.toString())
        showToast(R.string.cant_find_file)
    }
}

fun Context.showEditTextDialog(
    title: String,
    message: String? = null,
    onPositiveButton: ((text: Int) -> Unit)? = null,
    onNegativeButton: ((dialog: DialogInterface) -> Unit)? = null,
) {
    ItemEdittextBinding.inflate(LayoutInflater.from(this)).apply {
        edittext.hint = getString(R.string.days)
        MaterialAlertDialogBuilder(this@showEditTextDialog)
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                onNegativeButton?.invoke(dialog)
            }
            .setView(root)
            .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                onPositiveButton?.invoke(edittext.text.toString().toInt())
            }
            .show()
    }
}

fun Context.getColorById(id: Int) = ContextCompat.getColor(this, id)


fun AppCompatActivity.setHomeFragment(navGraphId: Int, homeFragmentId: Int) {
    val navHostFragment = supportFragmentManager
        .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    val graphInflater = navHostFragment.navController.navInflater
    val navGraph = graphInflater.inflate(navGraphId)
    val navController = navHostFragment.navController
    navGraph.setStartDestination(homeFragmentId)
    navController.graph = navGraph
}

fun Context.installApk(apkUri: Uri) {
    config.isLoggedIn = false
    val installIntent = Intent(Intent.ACTION_VIEW)
    installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
    installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(installIntent)
}

fun Context.copyText(text: String?) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("text", text)
    clipboardManager.setPrimaryClip(clipData)
    showToast(R.string.copied)
}

fun Context.getContextDialogLoader(isCancelable: Boolean = true): AlertDialog {
    var dialog: AlertDialog
    DialogLoaderBinding.inflate(LayoutInflater.from(this)).apply {
        MaterialAlertDialogBuilder(this@getContextDialogLoader).setView(root).create()
            .apply {
                setCancelable(isCancelable)
                setCanceledOnTouchOutside(isCancelable)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog = this
            }
    }
    return dialog
}

fun Context.showFullPicture(
    file: Any?,
    title: String? = null,
    message: String? = null,
    icon: Int? = null,
    onPositiveButton: (() -> Unit)? = null,
    onNegativeButton: ((dialog: DialogInterface) -> Unit)? = null,
    isCancelable: Boolean = false
) {
    DialogShowImageBinding.inflate(LayoutInflater.from(this)).apply {
        MaterialAlertDialogBuilder(this@showFullPicture).setView(root).create().apply {
            cpiLoader.visible()
            Glide.with(this@showFullPicture)
                .load(file)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        cpiLoader.gone()
                        return false
                    }

                })
                .into(goProDialogImageProfile)
            dismissBtnProfile.setOnClickListener {
                dismiss()
            }
            setTitle(title)
            setMessage(message)
            icon?.let {
                setIcon(it)
            }
            onPositiveButton?.let {
                setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay)) { _, _ ->
                    it.invoke()
                    dismiss()
                }
            }
            onNegativeButton?.let {
                setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
                    it.invoke(dialog)
                    dismiss()
                }
            }
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(isCancelable)
        }.show()
    }
}

fun Context.initSpinner(
    spinner: Spinner,
    list: Array<String>,
    onItemSelected: (selectedPoint: String) -> Unit
) {
    //setting spinner
    val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    spinner.adapter = arrayAdapter
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            onItemSelected(list[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }
}

fun Context.initListPopUpView(
    autoCompleteTextView: AutoCompleteTextView,
    textInputLayout: TextInputLayout,
    list: List<Any>,
    onItemSelected: (selectedPoint: String) -> Unit
) {
    val listPopupWindow =
        ListPopupWindow(this, null, androidx.appcompat.R.attr.listPopupWindowStyle)
    listPopupWindow.anchorView = autoCompleteTextView
    listPopupWindow.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, list))

    listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
        onItemSelected(list[position].toString())
        autoCompleteTextView.setText(list[position].toString(), false)
        listPopupWindow.dismiss()
    }

    autoCompleteTextView.setOnClickListener {
        listPopupWindow.show()
    }

    textInputLayout.setOnClickListener {
        listPopupWindow.show()
    }
}

fun Context.takeScreenshot(view: View): File? {
    val now = Date()
    DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
    try {
        // create bitmap screen capture
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        val imageFile = generateFile("$now.jpg")
        val outputStream: FileOutputStream = FileOutputStream(imageFile)
        val quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()
        return imageFile
    } catch (e: Throwable) {
        // Several error may come out with file handling or DOM
        e.printStackTrace()
        return null
    }
}

fun Context.openCamera(registerForActivityResult: ActivityResultLauncher<Intent>): Uri? {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "New Picture")
    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
    registerForActivityResult.launch(cameraIntent)
    return imageUri
}

fun Context.checkMultiplePermissions(permission: Array<String>): Boolean {
    permission.forEach {
        if (!checkPermission(it))
            return false
    }
    return true
}

fun Context.checkPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.showSnackBar(message: Int) {
    if (this is FragmentActivity) {
        Snackbar.make(
            (this as? FragmentActivity)?.findViewById(android.R.id.content) ?: return,
            message,
            Snackbar.LENGTH_SHORT
        ).apply {
            makeDraggable()
        }.show()
    } else {
        Snackbar.make(
            (this as? Activity)?.findViewById(android.R.id.content) ?: return,
            message,
            Snackbar.LENGTH_SHORT
        ).apply {
            makeDraggable()
        }.show()
    }
}

fun Context.showNotAllowed() {
    showSnackBar(R.string.not_allowed)
}

fun Context.showSnackBar(message: String) {
    if (this is FragmentActivity) {
        Snackbar.make(
            (this as? FragmentActivity)?.findViewById(android.R.id.content) ?: return,
            message,
            Snackbar.LENGTH_SHORT
        ).apply {
            makeDraggable()
        }.show()
    } else {
        Snackbar.make(
            (this as? Activity)?.findViewById(android.R.id.content) ?: return,
            message,
            Snackbar.LENGTH_SHORT
        ).apply {
            makeDraggable()
        }.show()
    }
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showComingSoon() {
    showSnackBar(R.string.coming_soon)
}

fun Context.getSharedPrefs(): SharedPreferences =
    getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

fun Context.isInternetAvailable(): Boolean {
    var result: Boolean
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    val networkCapabilities = connectivityManager?.activeNetwork ?: return false
    val activeNetwork =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

fun Context.attributeToColor(attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
}

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(stringId: Int) {
    Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
fun Context.getAddress(
    latitude: Double,
    longitude: Double,
    callback: ((address: String) -> Unit)? = null
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        //Variables
        val local = Locale("en_us", "United States")
        val geocoder = Geocoder(this, local)
        val maxResult = 1

        geocoder.getFromLocation(latitude, longitude, maxResult) {
            callback?.invoke(it[0].getAddressLine(0))
        }
    } else {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            callback?.invoke(addressList?.get(0)?.getAddressLine(0).toString())
        } catch (e: Exception) {
            Log.d("debugging", e.message.toString())
        }
    }
}