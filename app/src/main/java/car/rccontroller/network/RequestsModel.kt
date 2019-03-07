package car.rccontroller.network

/*
This file is designed to store and manage network-related data,
somehow like the ViewModel class is designed to store and manage UI-related data
 */

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.net.wifi.WifiManager
import android.text.format.Formatter
import car.rccontroller.RCControllerActivity
import car.rccontroller.network.server.feedback.SensorFeedbackServer
import kotlinx.coroutines.*
import retrofit2.Call


const val OK_STRING = "OK"
const val EMPTY_STRING = "NULL"
const val sensorFeedbackServerPort= 8080

var raspiServerIP: String? = null
var raspiServerPort: Int? = null
var context: RCControllerActivity? = null

var sensorFeedbackServer: SensorFeedbackServer? = null

/////////
// Handling Assistance
/////////
const val ASSISTANCE_NULL = EMPTY_STRING
const val ASSISTANCE_NONE = "assistance_none"
const val ASSISTANCE_WARNING = "assistance_warning"
const val ASSISTANCE_FULL = "assistance_full"
var handlingAssistanceState: String
    set(value) {
        runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
                "set_handling_assistance?" +
                "state=$value")
    }
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_handling_assistance_state")


/////////
// Motor Speed Limiter
/////////
const val MOTOR_SPEED_LIMITER_NO_SPEED = 0.00
const val MOTOR_SPEED_LIMITER_SLOW_SPEED_1 = 0.20
const val MOTOR_SPEED_LIMITER_SLOW_SPEED_2 = 0.40
const val MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 = 0.60
const val MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 = 0.70
const val MOTOR_SPEED_LIMITER_FAST_SPEED_1 = 0.80
const val MOTOR_SPEED_LIMITER_FAST_SPEED_2 = 0.90
const val MOTOR_SPEED_LIMITER_FULL_SPEED = 1.00
var motorSpeedLimiter: Double?
    set(value) {
        runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
                "set_motor_speed_limiter?" +
                "value=$value")
    }
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_motor_speed_limiter").toDoubleOrNull()


/////////
// Differential Slippery Limiter
/////////
const val DIFFERENTIAL_SLIPPERY_LIMITER_OPEN = 0
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 = 1
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 = 2
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 = 3
const val DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED = 4
const val DIFFERENTIAL_SLIPPERY_LIMITER_AUTO = 10

//---- Front ----
var previousFrontDifferentialSlipperyLimiter: Int? = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
var currentFrontDifferentialSlipperyLimiter: Int?
    set(value) {
        runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
                "set_front_differential_slippery_limiter?" +
                "value=$value")
    }
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_front_differential_slippery_limiter").toIntOrNull()

//---- Rear ----
var previousRearDifferentialSlipperyLimiter: Int? = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
var currentRearDifferentialSlipperyLimiter: Int?
    set(value) {
        runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
                "set_rear_differential_slippery_limiter?" +
                "value=$value")
    }
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_rear_differential_slippery_limiter").toIntOrNull()


/////////
// General use
/////////
// In case of second Activity existence this should be implemented correctly for a CoroutineScope usage
fun launchRequest(url:String) = CoroutineScope(Dispatchers.IO).launch { doRequest(url) }

fun runBlockingRequest(url:String) = runBlocking(Dispatchers.IO) { doRequest(url) }

fun <T> launchRequest(block:() -> Call<T>): Job? {
    return if(areNetworkSettingsAvailable())
        CoroutineScope(Dispatchers.IO).launch { block().execute().body() }
    else
        null
}

fun <T> runBlockingRequest(block:() -> Call<T>): T? {
    return if(areNetworkSettingsAvailable())
        runBlocking(Dispatchers.IO) { block().execute().body() }
    else
        null
}

private fun areNetworkSettingsAvailable() = (raspiServerIP != null) && (raspiServerPort != null)

private fun doRequest(url: String): String {
    val con: HttpURLConnection?
    val urlGet: URL
    val requestInputStream: InputStream?
    val sb =  StringBuilder()
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        requestInputStream = con.run {
            readTimeout = 500 /* milliseconds */
            connectTimeout = 500 /* milliseconds */
            requestMethod = "GET"
            doInput = true
            // Start the query
            connect()
            inputStream
        }

        val bufferReader = BufferedReader(InputStreamReader(requestInputStream), 4096)
        var line: String?

        line = bufferReader.readLine()
        while (line != null) {
            sb.append(line)
            line = bufferReader.readLine()
        }
        bufferReader.close()
    } catch (e: IOException) {
        //handle the exception !
        sb.append(e.message ?: EMPTY_STRING)
    }
    return sb.toString()
}

val myIP:String
    get() {
        val wifiMgr = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiMgr?.connectionInfo
        return if (wifiInfo != null) {
            Formatter.formatIpAddress(wifiInfo.ipAddress)
        }
        else
            EMPTY_STRING
    }