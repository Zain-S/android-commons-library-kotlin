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
import android.graphics.Canvas
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
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.productdistributionsystem.BuildConfig
import com.example.productdistributionsystem.data.model.UserResponse
import com.example.productdistributionsystem.data.model.area.Area
import com.example.productdistributionsystem.data.model.customer.Customer
import com.example.productdistributionsystem.data.model.employee.OrderTaker
import com.example.productdistributionsystem.data.model.liveLocation.UserLocation
import com.example.productdistributionsystem.data.model.liveLocation.UserWithLocation
import com.example.productdistributionsystem.data.model.order.Order
import com.example.productdistributionsystem.data.model.order.OrderDetail
import com.example.productdistributionsystem.databinding.DialogLoaderBinding
import com.example.productdistributionsystem.databinding.DialogShowImageBinding
import com.example.productdistributionsystem.databinding.ItemAddCustomerBinding
import com.example.productdistributionsystem.databinding.ItemEdittextBinding
import com.example.productdistributionsystem.presentation.area.AreaDetailActivity
import com.example.productdistributionsystem.presentation.createOrder.CreateOrderActivity
import com.example.productdistributionsystem.presentation.customer.CustomerDetailActivity
import com.example.productdistributionsystem.presentation.customerOrders.CustomerOrdersActivity
import com.example.productdistributionsystem.presentation.employee.OrderProcessingHistoryActivity
import com.example.productdistributionsystem.presentation.employee.OrderTakerDetailActivity
import com.example.productdistributionsystem.presentation.login.LoginActivity
import com.example.productdistributionsystem.presentation.map.MapsActivity
import com.example.productdistributionsystem.presentation.order.OrderDetailsActivity
import com.example.productdistributionsystem.presentation.orderMap.OrderMapActivity
import com.example.productdistributionsystem.presentation.product.ProductDetailActivity
import com.example.productdistributionsystem.utils.ADDRESS
import com.example.productdistributionsystem.utils.AREA
import com.example.productdistributionsystem.utils.CUSTOMER_REPORT_PARAMETERS_LIST
import com.example.productdistributionsystem.utils.CUSTOMER_ROLE_API
import com.example.productdistributionsystem.utils.Config
import com.example.productdistributionsystem.utils.DATE_CREATED
import com.example.productdistributionsystem.utils.DATE_FORMAT_1
import com.example.productdistributionsystem.utils.DATE_FORMAT_2
import com.example.productdistributionsystem.utils.DEBUGGING
import com.example.productdistributionsystem.utils.DEFAULT_FOLDER_NAME
import com.example.productdistributionsystem.utils.DOCUMENTS_MIME_TYPES
import com.example.productdistributionsystem.utils.EMAIL_ADDRESS
import com.example.productdistributionsystem.utils.EXTRA_AREA
import com.example.productdistributionsystem.utils.EXTRA_CUSTOMER
import com.example.productdistributionsystem.utils.EXTRA_CUSTOMER_IDS
import com.example.productdistributionsystem.utils.EXTRA_IS_CLEAR_VISIT
import com.example.productdistributionsystem.utils.EXTRA_IS_INVOICE
import com.example.productdistributionsystem.utils.EXTRA_IS_SHOW_ORDER_TAKER
import com.example.productdistributionsystem.utils.EXTRA_IS_SHOW_ROUTE
import com.example.productdistributionsystem.utils.EXTRA_ORDER
import com.example.productdistributionsystem.utils.EXTRA_ORDER_IDS
import com.example.productdistributionsystem.utils.EXTRA_ORDER_TAKER
import com.example.productdistributionsystem.utils.ID
import com.example.productdistributionsystem.utils.LAST_ORDER_DATE
import com.example.productdistributionsystem.utils.LAST_VISIT_DATE
import com.example.productdistributionsystem.utils.LOCATION
import com.example.productdistributionsystem.utils.LOCATION_PERMISSIONS
import com.example.productdistributionsystem.utils.MANAGER_ROLE_API
import com.example.productdistributionsystem.utils.NAME
import com.example.productdistributionsystem.utils.NOT_FOUND
import com.example.productdistributionsystem.utils.NO_OF_INVOICES
import com.example.productdistributionsystem.utils.NO_OF_ORDERS
import com.example.productdistributionsystem.utils.ORDER_TAKER_ROLE_API
import com.example.productdistributionsystem.utils.PHONE
import com.example.productdistributionsystem.utils.SELLER_ROLE_API
import com.example.productdistributionsystem.utils.SHOP_NAME
import com.example.productdistributionsystem.utils.SUB_ADMIN_ROLE_API
import com.example.productdistributionsystem.utils.SUPER_ADMIN_ROLE_API
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.saeedtechies.commons.R
import com.saeedtechies.commons.utils.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

fun Context.startMapActivity(customerIds: List<Int?>, isShowRoute: Boolean = false) {
    if (customerIds.isNotEmpty())
        startActivity(Intent(this, MapsActivity::class.java).apply {
            putExtra(EXTRA_CUSTOMER_IDS, Gson().toJson(customerIds))
            putExtra(EXTRA_IS_SHOW_ROUTE, isShowRoute)
        })
    else
        showSnackBar(getString(R.string.no_customers_found))
}

fun Context.startMapActivityWithOrders(
    orderList: List<Order>,
    isShowOrderTaker: Boolean = false,
    isShowRoute: Boolean = false
) {
    if (orderList.isNotEmpty())
        startActivity(Intent(this, OrderMapActivity::class.java).apply {
            putExtra(EXTRA_ORDER_IDS, Gson().toJson(orderList.map { it.order_id }))
            putExtra(EXTRA_IS_SHOW_ORDER_TAKER, isShowOrderTaker)
            putExtra(EXTRA_IS_SHOW_ROUTE, isShowRoute)
        })
    else
        showSnackBar(getString(R.string.no_customers_found))
}

fun Context.startAreaDetailActivity(area: Area) {
    startActivity(Intent(this, AreaDetailActivity::class.java).apply {
        putExtra(EXTRA_AREA, Gson().toJson(area))
    })
}

fun Context.startCustomerOrdersInvoicesActivity(customer: Customer, isInvoices: Boolean = false) {
    startActivity(Intent(this, CustomerOrdersActivity::class.java).apply {
        putExtra(EXTRA_CUSTOMER, Gson().toJson(customer))
        putExtra(EXTRA_IS_INVOICE, isInvoices)
    })
}

fun Context.startOrderTakerOrdersActivity(orderTaker: OrderTaker?) {
    startActivity(Intent(this, CustomerOrdersActivity::class.java).apply {
        putExtra(EXTRA_ORDER_TAKER, Gson().toJson(orderTaker))
        putExtra(EXTRA_IS_INVOICE, false)
    })
}

fun Context.startOrderDetailsActivity(order: Order, isInvoices: Boolean = false) {
    startActivity(Intent(this, OrderDetailsActivity::class.java).apply {
        putExtra(EXTRA_ORDER, Gson().toJson(order))
        putExtra(EXTRA_IS_INVOICE, isInvoices)
    })
}

fun Context.startCreateOrderActivity(selectedCustomer: Customer) {
    if (checkMultiplePermissions(LOCATION_PERMISSIONS))
        isCreateOrderAllowed {
            startActivity(Intent(this, CreateOrderActivity::class.java).apply {
                putExtra(EXTRA_CUSTOMER, Gson().toJson(selectedCustomer))
                putExtra(EXTRA_IS_CLEAR_VISIT, false)
            })
        }
}

fun Context.startClearVisitActivity(selectedCustomer: Customer) {
    if (checkMultiplePermissions(LOCATION_PERMISSIONS))
        isClearVisitAllowed {
            if (getDaysDifference(selectedCustomer.visit_date, DATE_FORMAT_1) == 0L)
                showSnackBar(getString(R.string.visit_already_cleared))
            else
                startActivity(Intent(this, CreateOrderActivity::class.java).apply {
                    putExtra(EXTRA_CUSTOMER, Gson().toJson(selectedCustomer))
                    putExtra(EXTRA_IS_CLEAR_VISIT, true)
                })
        }
}

fun Context.startCustomerDetailActivity(customer: Customer) {
    startActivity(Intent(this, CustomerDetailActivity::class.java).apply {
        putExtra(EXTRA_CUSTOMER, Gson().toJson(customer))
    })
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

fun Context.getColorAndTextColorForDays(
    days: Long?,
    callback: (color: Int, textColor: Int) -> Unit
) {
    val color: Int
    var textColor = ContextCompat.getColor(this, R.color.white)
    if (days != null)
        when (days) {
            in 0..2 -> {
                color = ContextCompat.getColor(this, android.R.color.transparent)
                textColor = this.attributeToColor(com.google.android.material.R.attr.colorOnSurface)
                callback(color, textColor)
            }

            3L -> {
                color = ContextCompat.getColor(this, R.color.red_50)
                textColor = ContextCompat.getColor(this, R.color.black)
                callback(color, textColor)
            }

            4L -> {
                color = ContextCompat.getColor(this, R.color.red_100)
                textColor = ContextCompat.getColor(this, R.color.black)
                callback(color, textColor)
            }

            5L -> {
                color = ContextCompat.getColor(this, R.color.red_200)
                textColor = ContextCompat.getColor(this, R.color.black)
                callback(color, textColor)
            }

            6L -> {
                color = ContextCompat.getColor(this, R.color.red_300)
                callback(color, textColor)
            }

            7L -> {
                color = ContextCompat.getColor(this, R.color.red_400)
                callback(color, textColor)
            }

            8L -> {
                color = ContextCompat.getColor(this, R.color.red_500)
                callback(color, textColor)
            }

            9L -> {
                color = ContextCompat.getColor(this, R.color.red_600)
                callback(color, textColor)
            }

            10L -> {
                color = ContextCompat.getColor(this, R.color.red_700)
                callback(color, textColor)
            }

            11L -> {
                color = ContextCompat.getColor(this, R.color.red_800)
                callback(color, textColor)
            }

            else -> {
                color = ContextCompat.getColor(this, R.color.red_900)
                callback(color, textColor)
            }
        }
    else {
        color = ContextCompat.getColor(this, R.color.red_900)
        callback(color, textColor)
    }
}

fun Context.getColorCustom(colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.exportDatabaseToCSVFile(
    fileName: String,
    customerParameterList: List<String>,
    customerList: List<Customer>,
    callback: (() -> Unit)? = null
) {
    val csvFile = generateFile(fileName)
    if (csvFile != null) {
        exportDataBaseToCSVFile(csvFile, customerParameterList, customerList)
        callback?.invoke()
        Log.i(DEBUGGING, "CSV file generated.")
    } else {
        showToast("CSV file not generated.")
    }
}

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

private fun exportDataBaseToCSVFile(
    csvFile: File,
    customerParameterList: List<String>,
    customerList: List<Customer>
) {
    csvWriter().open(csvFile, append = false) {
//        writeRow(listOf("User Id", " ", constant.getUserId()))
//        writeRow(listOf("User Name", " ",  constant.getUserName()))
//        writeRow(listOf("User Role", " ", constant.getUserRole()))
//        writeRow(listOf("User Center"," ",  constant.getUserCenter()))
//        writeRow(listOf("Updated At", " ", constant.getCurrentDateTime().toString()))

        if (customerList.isNotEmpty()) {
            val listOfParameters = ArrayList<Any?>()
            customerParameterList.forEach {
                when (it) {
                    ID -> listOfParameters.add(ID)
                    NAME -> listOfParameters.add(NAME)
                    SHOP_NAME -> listOfParameters.add(SHOP_NAME)
                    EMAIL_ADDRESS -> listOfParameters.add(EMAIL_ADDRESS)
                    PHONE -> listOfParameters.add(PHONE)
                    AREA -> listOfParameters.add(AREA)
                    ADDRESS -> listOfParameters.add(ADDRESS)
                    LOCATION -> listOfParameters.add(LOCATION)
                    LAST_ORDER_DATE -> listOfParameters.add(LAST_ORDER_DATE)
                    LAST_VISIT_DATE -> listOfParameters.add(LAST_VISIT_DATE)
                    DATE_CREATED -> listOfParameters.add(DATE_CREATED)
                    NO_OF_ORDERS -> listOfParameters.add(NO_OF_ORDERS)
                    NO_OF_INVOICES -> listOfParameters.add(NO_OF_INVOICES)
                }
            }
            writeRow(listOfParameters)
            customerList.forEach { customer ->
                val listOfValues = ArrayList<Any?>()
                customerParameterList.forEach {
                    when (it) {
                        ID -> listOfValues.add(customer.id)
                        NAME -> listOfValues.add(customer.name)
                        SHOP_NAME -> listOfValues.add(customer.shop_name)
                        EMAIL_ADDRESS -> listOfValues.add(customer.email)
                        PHONE -> listOfValues.add(customer.phone)
                        AREA -> listOfValues.add(customer.area_name)
                        ADDRESS -> listOfValues.add(customer.address)
                        LOCATION -> listOfValues.add(customer.location_url)
                        LAST_ORDER_DATE -> listOfValues.add(customer.last_order_date)
                        LAST_VISIT_DATE -> listOfValues.add(customer.visit_date)
                        DATE_CREATED -> listOfValues.add(customer.created_at)
                        NO_OF_ORDERS -> listOfValues.add(customer.num_orders)
                        NO_OF_INVOICES -> listOfValues.add(customer.num_invoices)
                    }
                }
                writeRow(listOfValues)
            }
        }
    }
}

fun Context.openFile(fileName: String) {
    val myFile: File
    try {
        myFile = File(cacheDir.path + "/" + DEFAULT_FOLDER_NAME + "/" + fileName)
        try {
            val uri =
                FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", myFile)
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

fun Context.getPrintableOrder(orderForPrint: Order, orderDetailList: List<OrderDetail>) =
    ArrayList<Printable>().apply {
        orderDetailList.let {
            add(
                TextPrintable.Builder()
                    .setText("Scoops Creamery")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Phone: 0305 7513559")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Sales Invoice")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Date:      ${orderForPrint.order_date}")
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Bill No:   ${orderForPrint.order_id}")
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Shop Name: ${orderForPrint.name}")
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Phone:     ${orderForPrint.phone}")
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Address:   ${orderForPrint.address}")
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        String.format(
                            "%1$-12s %2$5s %3$5s %4$6s",
                            "Item    ",
                            "  Qty",
                            " Rate",
                            "Amount"
                        )
                    )
                    .setNewLinesAfter(1)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .build()
            )
            it.forEach { orderDetail ->
                if (orderDetail.name.toString().length > 12) {
                    add(
                        TextPrintable.Builder()
                            .setText(orderDetail.name.toString())
                            .setNewLinesAfter(1)
                            .build()
                    )
                    add(
                        TextPrintable.Builder()
                            .setText(
                                String.format(
                                    "%1$-12s %2$5s %3$5s %4$6s",
                                    "",
                                    "${orderDetail.unit}",
                                    "${orderDetail.price}",
                                    "${orderDetail.amount}"
                                )
                            )
                            .setNewLinesAfter(1)
                            .build()
                    )
                } else
                    add(
                        TextPrintable.Builder()
                            .setText(
                                String.format(
                                    "%1$-12s %2$5s %3$5s %4$6s",
                                    "${orderDetail.name}",
                                    "${orderDetail.unit}",
                                    "${orderDetail.price}",
                                    "${orderDetail.amount}"
                                )
                            )
                            .setNewLinesAfter(1)
                            .build()
                    )
            }
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        String.format(
                            "%1$-12s %2$5s %3$5s %4$6s",
                            "Sub Total",
                            "${orderForPrint.unit}",
                            "",
                            "${orderForPrint.subtotal}"
                        )
                    )
                    .setNewLinesAfter(1)
                    .build()
            )
            val balance = orderForPrint.amount?.minus(orderForPrint.subtotal ?: 0f) ?: 0
            add(
                TextPrintable.Builder()
                    .setText(
                        String.format(
                            "%1$-12s %2$5s %3$5s %4$6s",
                            "Balance",
                            "",
                            "",
                            "$balance"
                        )
                    )
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText(
                        String.format(
                            "%1$-12s %2$5s %3$5s %4$6s",
                            "Discount",
                            "",
                            "",
                            "-${orderForPrint.discount}"
                        )
                    )
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            val total = orderForPrint.amount?.minus(orderForPrint.discount ?: 0f) ?: 0
            add(
                TextPrintable.Builder()
                    .setText(String.format("%1$-5s %2$9s", "TOTAL", "$total"))
                    .setNewLinesAfter(1)
                    .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                    .setFontSize(DefaultPrinter.FONT_SIZE_LARGE)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                ImagePrintable.Builder(R.drawable.order_warning200, resources)
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("--------------------------------")
                    .setNewLinesAfter(1)
                    .build()
            )
            add(
                TextPrintable.Builder()
                    .setText("Thank You")
                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                    .build()
            )
            add(
                RawPrintable.Builder(byteArrayOf(27, 100, 4)).build()
            ) // feed lines example in raw mode
        }
        /*add(
            TextPrintable.Builder()
                .setText(String.format("%1$-12s %2$5s %3$5s %4$6s", "Qulfi 20", "25", "16", "400"))
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(String.format("%1$-12s %2$5s %3$5s %4$6s", "Qulfi 30", "75", "24", "1800"))
                .setNewLinesAfter(1)
                .build()
        )
        add(
            TextPrintable.Builder()
                .setText(String.format("%1$-12s %2$5s %3$5s %4$6s", "1L Kulfa", "3", "150", "450"))
                .setNewLinesAfter(1)
                .build()
        )*/


        /*val qr: Bitmap = QRCode.from("RRN: : 234566dfgg4456\nAmount: NGN\$200,000\n")
            .withSize(200, 200).bitmap()

        add(
            ImagePrintable.Builder(qr)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())*/

    }

fun Context.getColorById(id: Int) = ContextCompat.getColor(this, id)


suspend fun Context.sortOrderByDistance(orderList: List<Order>): List<Order> {
    updateLocation()
    orderList.forEach { order ->
        val latitude = order.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = order.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
        order.distance = getDistanceFromCurrentLocation(latitude, longitude)
    }
    return orderList.sortedBy { order ->
        order.distance
    }
}

suspend fun Context.sortOrderByDistance2ndAlgorithm(_orderList: List<Order>): List<Order> {
    var previousLocation = getCurrentLocation()
    val orderList: ArrayList<Order> = ArrayList(_orderList)
    val soredOrderList: ArrayList<Order> = arrayListOf()
    for (i in _orderList.indices) {
        orderList.forEach { order ->
            val latitude = order.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
            val longitude = order.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
            order.distance = previousLocation?.distanceTo(Location(latitude, longitude))
        }
        val nearestOrder = orderList.sortedBy { order -> order.distance }.first()
        val latitude = nearestOrder.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = nearestOrder.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
        previousLocation = Location(latitude, longitude)
        soredOrderList.add(nearestOrder)
        orderList.remove(nearestOrder)
    }
    soredOrderList.forEach { order ->
        val latitude = order.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = order.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
        order.distance = getDistanceFromCurrentLocation(latitude, longitude)
    }
    return soredOrderList
}

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

suspend fun Context.sortCustomersByDistance(customerList: List<Customer>): List<Customer> {
    updateLocation()
    customerList.forEach { order ->
        val latitude = order.location_url?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = order.location_url?.split(",")?.get(1)?.toDoubleOrNull()
        order.distance = getDistanceFromCurrentLocation(latitude, longitude)
    }
    return customerList.sortedBy { order ->
        order.distance
    }
}

suspend fun Context.sortCustomersByDistance2ndAlgorithm(_customerList: List<Customer>): List<Customer> {
    var previousLocation: Location?
    withContext(Dispatchers.Main) {
        previousLocation = getCurrentLocation()
    }
    val customerList: ArrayList<Customer> = ArrayList(_customerList)
    val soredCustomerList: ArrayList<Customer> = arrayListOf()
    for (i in _customerList.indices) {
        customerList.forEach { customer ->
            val latitude = customer.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
            val longitude = customer.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
            customer.distance = previousLocation?.distanceTo(Location(latitude, longitude))
        }
        val nearestOrder = customerList.sortedBy { customer -> customer.distance }.first()
        val latitude = nearestOrder.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = nearestOrder.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
        previousLocation = Location(latitude, longitude)
        soredCustomerList.add(nearestOrder)
        customerList.remove(nearestOrder)
    }
    soredCustomerList.forEach { customer ->
        val latitude = customer.customer_location?.split(",")?.get(0)?.toDoubleOrNull()
        val longitude = customer.customer_location?.split(",")?.get(1)?.toDoubleOrNull()
        customer.distance = getDistanceFromCurrentLocation(latitude, longitude)
    }
    return soredCustomerList
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

fun Context.createCustomerReport(customerList: List<Customer>) {
    isShowCustomerReport {
        showEditTextDialog(
            getString(R.string.enter_allowed_days),
            getString(R.string.enter_allowed_days_detail),
            onPositiveButton = { number ->
                val tempList = if (number == 0)
                    customerList
                else
                    customerList.filter {
                        (getDaysDifference(it.last_order_date ?: "", DATE_FORMAT_1) ?: 0) > number
                    }.map { customer ->
                        customer.created_at = customer.created_at.toString().substringBefore("T")
                        customer
                    }
                showSelectCustomerParameterDialog(tempList)
            })
    }
}

private fun Context.showSelectCustomerParameterDialog(customerList: List<Customer>) {
    val customerParameterList = ArrayList<String>()
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.select_customer_parameters)
        .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            exportDatabaseToCSVFile(
                "CustomerReport.csv",
                customerParameterList.distinct(),
                customerList.sortedBy { it.id }) {
                openFile("CustomerReport.csv")
            }
        }
        // Single-choice items (initialized with checked item)
        .setMultiChoiceItems(CUSTOMER_REPORT_PARAMETERS_LIST, null) { _, which, isChecked ->
            if (isChecked)
                customerParameterList.add(CUSTOMER_REPORT_PARAMETERS_LIST[which])
            else
                customerParameterList.remove(CUSTOMER_REPORT_PARAMETERS_LIST[which])
        }
        .show()
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
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
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

fun Context.bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(this, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
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

fun Context.startEmployeeDetailActivity(orderTakerName: String?) {
    isEmployeeDetailAllowed {
        startActivity(Intent(this, OrderTakerDetailActivity::class.java).apply {
            putExtra(NAME, orderTakerName)
        })
    }
}

fun Context.startOrderProcessingHistoryActivity(sellerId: Int?) {
    isSellerOrderProcessingHistoryAllowed {
        startActivity(Intent(this, OrderProcessingHistoryActivity::class.java).apply {
            putExtra(ID, sellerId)
        })
    }
}

fun Context.saveLogInData(response: UserResponse, password: String? = null) {
    val user = response.data!!
    Log.d(DEBUGGING, user.toString())
    config.userId = user.id.toString()
    config.userName = user.name.toString()
    config.userEmail = user.email.toString()
    password?.let {
        config.userPassword = it
    }
    config.userRole = user.role.toString()
    config.userOf = when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> user.id.toIntOrZero()
        SUB_ADMIN_ROLE_API -> user.id.toIntOrZero()
        MANAGER_ROLE_API -> user.sellerOf.toIntOrZero()
        ORDER_TAKER_ROLE_API -> user.otOf.toIntOrZero()
        SELLER_ROLE_API -> user.sellerOf.toIntOrZero()
        else -> 0
    }
    config.apiToken = user.apiToken.toString()
    config.isLoggedIn = true
}

fun Context.setCustomerData(
    customerBinding: ItemAddCustomerBinding,
    customersResponseData: Customer?,
    isChecked: Boolean = true,
    detailVisibility: Int = View.GONE,
    order: Order? = null,
    isCustomerButtonVisible: Boolean = true
) {
    customersResponseData?.apply {
        val customer = this
        customerBinding.apply {
            gpDetails.visibility = detailVisibility
            mainLayout.isChecked = isChecked
            mainLayout.isFocusable = false
            tvShopName.text = shop_name ?: NOT_FOUND
            tvCustomerName.text = name ?: NOT_FOUND
            tvAddress.text = address ?: NOT_FOUND
            tvArea.text = area_name ?: NOT_FOUND
            tvLastOrder.text = last_order_date?.let {
                convertDateFromFormatToFormat(
                    it,
                    DATE_FORMAT_1,
                    DATE_FORMAT_2
                )
            } ?: NOT_FOUND
            tvLastVisit.text =
                visit_date?.let { convertDateFromFormatToFormat(it, DATE_FORMAT_1, DATE_FORMAT_2) }
                    ?: NOT_FOUND
            last_order_date.let {
                val days = getDaysDifference(it, DATE_FORMAT_1)
                if (days != null) {
                    val daysString = "($days Days)"
                    tvLastOrderDays.text = daysString
                    getColorAndTextColorForDays(days) { color, textColor ->
                        tvLastOrderDays.setBackgroundColor(color)
                        tvLastOrderDays.setTextColor(textColor)
                    }
                    tvLastOrder.text =
                        convertDateFromFormatToFormat(it, DATE_FORMAT_1, DATE_FORMAT_2) ?: NOT_FOUND
                } else {
                    tvLastOrderDays.text = ""
                    getColorAndTextColorForDays(0) { color, textColor ->
                        tvLastOrderDays.setBackgroundColor(color)
                        tvLastOrderDays.setTextColor(textColor)
                    }
                }
            }
            visit_date.let {
                val days = getDaysDifference(it, DATE_FORMAT_1)
                if (days != null) {
                    if (days > 3) icLastVisit.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@setCustomerData,
                            R.drawable.ic_close
                        )
                    )
                    else icLastVisit.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@setCustomerData,
                            R.drawable.ic_check
                        )
                    )
                    val daysString = "($days Days)"
                    tvLastVisitDays.text = daysString
                    getColorAndTextColorForDays(days) { color, textColor ->
                        tvLastVisitDays.setBackgroundColor(color)
                        tvLastVisitDays.setTextColor(textColor)
                    }
                    tvLastVisit.text =
                        convertDateFromFormatToFormat(it, DATE_FORMAT_1, DATE_FORMAT_2) ?: NOT_FOUND
                } else {
                    icLastVisit.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@setCustomerData,
                            R.drawable.ic_close
                        )
                    )
                    tvLastVisitDays.text = ""
                    getColorAndTextColorForDays(0) { color, textColor ->
                        tvLastVisitDays.setBackgroundColor(color)
                        tvLastVisitDays.setTextColor(textColor)
                    }
                }
            }
            if (this@setCustomerData is FragmentActivity)
                if (checkMultiplePermissions(LOCATION_PERMISSIONS))
                    lifecycleScope.launch {
                        val distance =
                            "${
                                (getDistanceFromCurrentLocation(customer.customer_location.toLocation())?.div(
                                    1000
                                ))?.roundTo(2)
                            }+"
                        tvDistance.text = distance
                    }

            tvDistance.gone()
            ivDistance.gone()
            btCall.setOnClickListener {
                startCallIntent(phone)
            }
            btSms.setOnClickListener {
                startSmsIntent(phone)
            }
            btWhatsapp.setOnClickListener {
                startWhatsappChatIntent(phone)
            }
            btDirection.setOnClickListener {
                startDirectionIntent(location_url)
            }
            btCustomer.setOnClickListener {
                startCustomerDetailActivity(customer)
            }
            btPrint.setOnClickListener {
                if (this@setCustomerData is OrderDetailsActivity) {
                    order?.let {
                        printReceipt(it)
                    }
                }
            }
            if (detailVisibility == View.GONE)
                mainLayout.setOnClickListener {
                    if (gpDetails.isVisible) {
                        gpDetails.gone()
                        tvDistance.gone()
                        ivDistance.gone()
                    } else {
                        gpDetails.visible()
                        if (distance != null) {
                            val distance = "${(distance!! / 1000).roundTo(2)}+KM"
                            tvDistance.text = distance
                            tvDistance.visible()
                            ivDistance.visible()
                        } else {
                            tvDistance.gone()
                            ivDistance.gone()
                        }
                    }
                }
            else
                btPrint.visible()
            if (isCustomerButtonVisible)
                btCustomer.visible()
            else
                btCustomer.gone()
        }
    }
}

fun Context.showCustomerRoutesDialog(customerList: List<Customer>, sortBy: String) {
    MaterialAlertDialogBuilder(this)
        .setTitle("${getString(R.string.select_9_customers)}\n${getString(R.string.filter)}: $sortBy")
        .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
        .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            startDirectionIntent(customerList.filter { it.isChecked }.map { it.customer_location })
            customerList.forEach {
                it.isChecked = false
            }
        }
        // Single-choice items (initialized with checked item)
        .setMultiChoiceItems(
            customerList.map { it.name }.toTypedArray(),
            null
        ) { _, which, isChecked ->
            customerList[which].isChecked = isChecked
        }
        .show()
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
    var result = false
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager?.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager?.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }
    return result
}

fun Context.attributeToColor(attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
}

fun Context.logout() {
    config.isLoggedIn = false
    mAuth.signOut()
    val intent = Intent(this, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(intent)
}

fun Context.showToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(stringId: Int) {
    Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
}

suspend fun Context.startSendingLocation(firebaseUserWithLocation: UserWithLocation) {
    while (true) {
        delay(5000)
        val userLocationList = ArrayList<UserLocation>()
        userLocationList.addAll(firebaseUserWithLocation.location_log)
        getCurrentLocation()?.let { currentLocation ->
            userLocationList.add(
                UserLocation(
                    accuracy = currentLocation.accuracy,
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude
                )
            )
            firebaseUserWithLocation.location_log = userLocationList
            firebaseService.updateLocation(firebaseUserWithLocation)
        }
        delay(15000)
    }
}


fun Context.isAssignSellerAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isAssignSellerAllowed(onSuccess: (() -> Unit)) {
    if (isAssignSellerAllowed()) {
        onSuccess()
    } else
        showNotAllowed()
}

fun Context.isSendToUnApprovedInvoicesAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isSendToUnApprovedInvoicesAllowed(onSuccess: (() -> Unit)) {
    if (isSendToUnApprovedInvoicesAllowed()) {
        onSuccess()
    } else
        showNotAllowed()
}

fun Context.isCustomersAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> true
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isDeleteCustomerAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isCreateOrderAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> true
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isCreateOrderAllowed(isAssignSellerShown: Boolean = false, onSuccess: (() -> Unit)) {
    if (isCreateOrderAllowed())
        onSuccess()
    else {
        if (config.userRole == MANAGER_ROLE_API && isAssignSellerShown)
            showSnackBar(R.string.please_select_customers)
        else
            showNotAllowed()
    }
}

fun Context.isClearVisitAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> true
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isClearVisitAllowed(onSuccess: (() -> Unit)) {
    if (isClearVisitAllowed()) {
        if (checkMultiplePermissions(LOCATION_PERMISSIONS))
            onSuccess()
        else
            showEnableLocationForAppDialog()
    } else
        showNotAllowed()
}

fun Context.isShowOrderTakerLocationAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isShowCustomerReport(onSuccess: (() -> Unit)) {
    when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> onSuccess()
        SUB_ADMIN_ROLE_API -> onSuccess()
        MANAGER_ROLE_API -> showNotAllowed()
        ORDER_TAKER_ROLE_API -> showNotAllowed()
        SELLER_ROLE_API -> showNotAllowed()
        else -> showNotAllowed()
    }
}

fun Context.isCreateAreaAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isShowSelectLastOrderVisitDays(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isCreateReportAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isShowUserLocationsAllowed(onSuccess: (() -> Unit)) {
    when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> onSuccess()
        SUB_ADMIN_ROLE_API -> onSuccess()
//        MANAGER_ROLE_API -> onSuccess()
    }
}

fun Context.getUserRole(): String {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> getString(R.string.admin)
        SUB_ADMIN_ROLE_API -> getString(R.string.sub_admin)
        SELLER_ROLE_API -> getString(R.string.seller)
        ORDER_TAKER_ROLE_API -> getString(R.string.order_taker)
        CUSTOMER_ROLE_API -> getString(R.string.customer)
        MANAGER_ROLE_API -> getString(R.string.manager)
        else -> NOT_FOUND
    }
}

fun Context.getUserRole(userId: Int?): String {
    return when (userId.toString()) {
        SUPER_ADMIN_ROLE_API -> getString(R.string.admin)
        SUB_ADMIN_ROLE_API -> getString(R.string.sub_admin)
        SELLER_ROLE_API -> getString(R.string.seller)
        ORDER_TAKER_ROLE_API -> getString(R.string.order_taker)
        CUSTOMER_ROLE_API -> getString(R.string.customer)
        MANAGER_ROLE_API -> getString(R.string.manager)
        else -> NOT_FOUND
    }
}

fun Context.isCreateCategoryAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isCreateCategoryAllowed(onSuccess: (() -> Unit)) {
    if (isCreateCategoryAllowed())
        onSuccess()
    else
        showNotAllowed()
}

fun Context.isEmployeeDetailAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isEmployeeDetailAllowed(onSuccess: (() -> Unit)) {
    if (isEmployeeDetailAllowed())
        onSuccess()
    else
        showNotAllowed()
}

fun Context.isSellerOrderProcessingHistoryAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isSellerOrderProcessingHistoryAllowed(onSuccess: (() -> Unit)) {
    if (isEmployeeDetailAllowed())
        onSuccess()
    else
        showNotAllowed()
}

fun Context.isShowManagerAllowed(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isShowManagerAllowed(onSuccess: (() -> Unit)) {
    if (isEmployeeDetailAllowed())
        onSuccess()
    else
        showNotAllowed()
}

fun Context.isShowAreas(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> true
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isKeepLogIn(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isShowStock(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> true
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.isAdminOrSubAdmin(): Boolean {
    return when (config.userRole) {
        SUPER_ADMIN_ROLE_API -> true
        SUB_ADMIN_ROLE_API -> true
        MANAGER_ROLE_API -> false
        ORDER_TAKER_ROLE_API -> false
        SELLER_ROLE_API -> false
        else -> false
    }
}

fun Context.startProductDetailActivity(id: Int, isOrderDetails: Boolean = false) {
    startActivity(Intent(this, ProductDetailActivity::class.java).apply {
        putExtra("product", id)
        putExtra("isOrderDetails", isOrderDetails)
    })
}

@Suppress("DEPRECATION")
fun Context.getAddress(latitude: Double, longitude: Double, callback: ((address: String) -> Unit)? = null) {

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