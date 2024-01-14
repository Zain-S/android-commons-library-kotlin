package com.saeedtechies.commons.utils

import android.Manifest
import android.Manifest.permission.CAMERA
import java.util.Locale


const val ADMIN_ROLE = "Admin"
const val ORDER_TAKER_ROLE = "Order Taker"
const val SELLER_TAKER_ROLE = "Seller"
const val SALES_MAN = "Salesman"
const val CUSTOMER_ROLE = "Customer"
const val NEW_ORDER_STATUS = "Pending"
const val SALESMAN_ORDER_STATUS = "Salesman"
const val PROCESSED_ORDER_STATUS = "Processed"
const val COMPLETED_ORDER_STATUS = "Completed"
const val DEFAULT_FOLDER_NAME = "PDS"
const val NOT_FOUND = "N/A"

const val TAG = "debugging"
const val DEBUGGING = "debugging"

private const val SECOND_MILLIS = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


var usLocale: Locale = Locale.Builder().setLanguage("en").setRegion("US").build()

// shared prefs
const val USER_NAME = "userName"
const val USER_EMAIL = "userEmail"
const val USER_PASSWORD = "User Password"
const val ID = "id"
const val USER_OF = "User Of"
const val USER_ID = "userId"
const val USER_ROLE = "userRole"
const val IS_LOGGED_IN = "isLoggedIn"
const val IS_NEW_ORDER_CREATED = "isNewOrderCreated"
const val IS_AREA_CLEARED = "isAreaCleared"
const val FAILED = "failed"
const val NOT_FOUND_IN_DATABASE = "Not found in database"
const val NULL_RESPONSE = "Null Response"
const val API_TOKEN = "apiToken"
const val SHOW_ALL_POINTS = "Show All Points"
const val SHOW_ALL_POINTS_WITH_ORDER_TAKER_LOCATION = "Show All Points With OrderTaker Location"
const val SHOW_ROUTE_9_CUSTOMERS = "Show Route (9 Customers)"
const val SHOW_ROUTE_ALL_CUSTOMERS = "Show Route (All Customers)"
const val NAME = "Name"
const val CUSTOMER_NAME = "Customer Name"
const val SHOP_NAME = "Shop Name"
const val PHONE = "Phone"
const val DATE_CREATED = "Date Created"
const val ORDER_DATE = "Order Date"
const val LAST_ORDER = "Last Order"
const val LAST_ORDER_DATE = "Last Order Date"
const val LAST_VISIT = "Last Visit"
const val LAST_VISIT_DATE = "Last Visit Date"
const val NEAREST_LOCATION = "Nearest Location"
const val AREA = "Area"
const val AMOUNT = "Amount"
const val NEW_ORDERS = "New Orders"
const val SELLER_ORDERS = "Seller Orders"
const val PROCESSED_ORDERS = "Processed Orders"
const val APP_THEME = "appTheme"
const val CLEAR_VISIT = "Clear Visit"
const val CREATE_ORDER = "Create Order"
const val IS_RELOAD_DATA = "isReloadData"
const val NO_OF_CUSTOMERS = "Number of Customers"
const val NO_OF_INVOICES = "Number of Invoices"
const val NO_OF_ORDERS = "Number of Orders"
const val REMAINING = "Remaining"
const val EMAIL_ADDRESS = "Email Address"
const val ADDRESS = "Address"
const val LOCATION = "Location"
const val TAKE_IMAGE = "Take Image"
const val SELECT_FROM_GALLERY = "Select from Gallery"
const val SEND_TO_UNAPPROVED_INVOICES = "Send to Unapproved Invoices"
const val ASSIGN_SELLER = "Assign Seller"
const val SELLER = "Seller"
const val NEW = "New"
const val CUSTOMER_ORDERS = "Customer Orders"
const val NO_OF_TIMES_PERMISSION_ASKED = "noOfTimesPermissionAsked"

//mysql
const val SUPER_ADMIN_ROLE_API = "1"
const val SUB_ADMIN_ROLE_API = "2"
const val SELLER_ROLE_API = "3"
const val CUSTOMER_ROLE_API = "4"
const val ORDER_TAKER_ROLE_API = "5"
const val MANAGER_ROLE_API = "10"

//lists
val CUSTOMERS_SORTING_LIST = arrayOf(SHOP_NAME, CUSTOMER_NAME, DATE_CREATED, LAST_ORDER, LAST_VISIT, NEAREST_LOCATION, AREA)
val AREA_CUSTOMERS_SORTING_LIST = CUSTOMERS_SORTING_LIST.filter { !it.contains(AREA) }.toTypedArray()
val ORDERS_SORTING_LIST = arrayOf(SHOP_NAME, CUSTOMER_NAME, ORDER_DATE, AMOUNT, NEAREST_LOCATION)
val LOCATION_OPTIONS_LIST = arrayOf(SHOW_ALL_POINTS, SHOW_ROUTE_9_CUSTOMERS, SHOW_ROUTE_ALL_CUSTOMERS)
val LOCATION_OPTIONS_LIST_ORDERS = arrayOf(SHOW_ALL_POINTS, SHOW_ALL_POINTS_WITH_ORDER_TAKER_LOCATION, SHOW_ROUTE_9_CUSTOMERS, SHOW_ROUTE_ALL_CUSTOMERS)
val AREAS_SORTING_LIST = arrayOf(NAME, NO_OF_CUSTOMERS)
val categoriesSortingList = arrayOf("Name", "Date Created")
val PRIORITIES_LIST = arrayOf("High", "Medium", "Low")
val ORDER_OR_VISIT_LIST = arrayOf(CREATE_ORDER, CLEAR_VISIT)
val PAID_HISTORY_SORTING_LIST = arrayOf(DATE_CREATED, REMAINING)
val CUSTOMER_REPORT_PARAMETERS_LIST = arrayOf(ID, NAME, SHOP_NAME, EMAIL_ADDRESS, PHONE, AREA, ADDRESS, LOCATION, NO_OF_ORDERS, NO_OF_INVOICES, LAST_ORDER_DATE, LAST_VISIT_DATE, DATE_CREATED)
val POINTS_LIST = arrayOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100")
val SELECT_IMAGE_LIST = arrayOf(TAKE_IMAGE, SELECT_FROM_GALLERY)
val ORDERS_ASSIGNING_LIST = arrayOf(ASSIGN_SELLER, SEND_TO_UNAPPROVED_INVOICES)

const val UN_AUTHENTICATED = "Unauthenticated"

// firebase
const val TASKS_COLLECTION = "tasks"
const val APP_VERSION_COLLECTION = "app_version"
const val LIVE_LOCATION_COLLECTION = "live_location"
const val CUSTOMERS_IMAGES_COLLECTION = "customers_images"


//live

//const val BASE_URL = "https://developers.distributorflow.com/api/"
const val BASE_URL = "https://distributorflow.com/api/"

const val ORDER_SEARCH_QUERY =
    "AND (name LIKE '%' || :search || '%' OR shop_name LIKE '%' || :search || '%' OR phone LIKE '%' || :search || '%' OR address LIKE '%' || :search || '%' OR amount LIKE '%' || :search || '%' OR order_comments LIKE '%' || :search || '%' OR cancel_reason LIKE '%' || :search || '%' OR order_date LIKE '%' || :search || '%' OR area_name LIKE '%' || :search || '%')"
const val CUSTOMER_SEARCH_QUERY =
    "WHERE (name LIKE '%' || :name || '%' OR shop_name LIKE '%' || :name || '%' OR phone LIKE '%' || :name || '%' OR address LIKE '%' || :name || '%' OR area_name LIKE '%' || :name || '%' OR email LIKE '%' || :name || '%')"
const val CUSTOM_CUSTOMER_SEARCH_QUERY =
    "DISTINCT c.*, (SELECT COUNT(*) FROM tbl_orders o WHERE o.customer_id = c.id AND o.seller_processed_order IS NULL) > 0 AS has_order FROM tbl_customer c LEFT JOIN tbl_orders o ON c.id = o.customer_id WHERE (c.name LIKE '%' || :name || '%' OR c.shop_name LIKE '%' || :name || '%' OR c.phone LIKE '%' || :name || '%' OR c.address LIKE '%' || :name || '%' OR c.area_name LIKE '%' || :name || '%' OR c.email LIKE '%' || :name || '%')"
const val INVOICE_SEARCH_QUERY =
    "(name LIKE '%' || :search || '%' OR shop_name LIKE '%' || :search || '%' OR amount LIKE '%' || :search || '%')"


//extras
const val EXTRA_CUSTOMER_LIST = "Customer List"
const val EXTRA_ORDER_LIST = "Order List"
const val EXTRA_AREA = "Area"
const val EXTRA_CUSTOMER = "Customer"
const val EXTRA_ORDER = "order"
const val EXTRA_IS_INVOICE = "isInvoice"
const val EXTRA_IS_CLEAR_VISIT = "isClearVisit"
const val EXTRA_CUSTOMER_IDS = "Customer Ids"
const val EXTRA_ORDER_IDS = "Order Ids"
const val EXTRA_IS_SHOW_ORDER_TAKER = "isShowOrderTaker"
const val EXTRA_IS_SHOW_ROUTE = "isShowRoute"
const val EXTRA_ORDER_TAKER = "orderTaker"
const val EXTRA_FROM = "from"

//date formats
const val DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss" //last order date, invoice approved date
const val DATE_FORMAT_2 = "dd-MMM-yyyy - hh:mm a" //last order date shown to user
const val DATE_FORMAT_3 = "yyyy-MM-dd" //order date
const val DATE_FORMAT_4 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" //created at date
const val DATE_FORMAT_5 = "dd MMMM yyyy" //created at date shown to user
const val DATE_FORMAT_6 = "d MMM yyyy" //datePicker format in few devices

val DOCUMENTS_MIME_TYPES = arrayOf("text/csv")

// permissions
val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
val CAPTURE_IMAGE_PERMISSIONS = arrayOf(CAMERA)

const val MAP_API_KEY = "AIzaSyA5HCWsqE07SGFkTvcvMd9c5k2P_G560j8"

const val CUSTOMER_AND_ORDER_TAKER_DISTANCE_ALLOWED = 50


const val MAP_CAMERA_PADDING = 100

const val ORDER_DAYS_ORDER_TAKER = 1
const val VISIT_DAYS_ORDER_TAKER = 1