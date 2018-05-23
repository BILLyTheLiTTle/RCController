package car.rccontroller.network

import android.content.Context
import car.rccontroller.R
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.text.format.Formatter


const val OK_DATA = "OK"
const val NO_DATA = "NULL"

var serverIp: String? = null
var serverPort: Int? = null
var context: Context? = null

/////////
// Engine
/////////
val isEngineStarted: Boolean
get() = doBlockingRequest("http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/get_engine_state").toBoolean()

fun startEngine(context: Context, serverIp: String?, serverPort: Int?): String{
    //reset and get ready for new requests
    throttleBrakeActionId = context.resources.getInteger(R.integer.default_throttleBrakeActionId)
    steeringDirectionId = context.resources.getInteger(R.integer.default_steeringDirectionId)

    /* The server will know when car is moving backward and not when the car is going to move
        backward in the next throttle action. The default state for this “ImageView” will be
        false (means not backward). So, this local variable must be reset at every start.
     */
    reverseIntention = false

    car.rccontroller.network.serverIp = serverIp
    car.rccontroller.network.serverPort = serverPort
    car.rccontroller.network.context = context

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    return doBlockingRequest("http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/start_engine?" +
            "nanohttp_client_ip=$myIP" +
            "&nanohttp_client_port=${-1}")
}

fun stopEngine(): String {
    val msg = doBlockingRequest("http://$serverIp:" +
            "$serverPort/stop_engine")

    if(msg == OK_DATA) {
        serverIp = null
        serverPort = null
    }

    return msg
}


/////////
// Throttle -n- Brakes
/////////
const val ACTION_MOVE_FORWARD = "forward"
const val ACTION_MOVE_BACKWARD = "backward"
const val ACTION_NEUTRAL = "neutral"
const val ACTION_BRAKING_STILL = "braking_still"
const val ACTION_PARKING_BRAKE = "parking_brake"
const val ACTION_HANDBRAKE = "handbrake"
// Initial value should be 0 cuz in server is -1
var throttleBrakeActionId = 0

//---- Parking Brake ----
val isParkingBrakeActive: Boolean
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_parking_brake_state").toBoolean()

fun activateParkingBrake(state: Boolean) = if (state)
        doBlockingRequest("http://$serverIp:$serverPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=100")
    else
        doBlockingRequest("http://$serverIp:$serverPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=0")

//---- Handbrake ----
val isHandbrakeActive: Boolean
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_handbrake_state").toBoolean()

fun activateHandbrake(state: Boolean) = if (state)
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=100")
else
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=0")

//---- Throttle / Brake / Neutral / Reverse ----
var reverseIntention: Boolean
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_reverse_lights_state").toBoolean()
    set(value) {doBlockingRequest("http://$serverIp:$serverPort/" +
            "set_reverse_lights_state?" +
            "state=$value")}

fun setNeutral() = doNonBlockingRequest("http://$serverIp:$serverPort/" +
        "set_throttle_brake_system?id=${throttleBrakeActionId++}&action=$ACTION_NEUTRAL")

fun setBrakingStill() = doNonBlockingRequest("http://$serverIp:$serverPort/" +
        "set_throttle_brake_system?" +
        "id=${throttleBrakeActionId++}" +
        "&action=$ACTION_BRAKING_STILL")

fun setThrottleBrake(direction: String, value: Int) =
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
        "set_throttle_brake_system?" +
        "id=${throttleBrakeActionId++}" +
        "&action=$direction" +
        "&value=$value")


/////////
// Steering
/////////
const val ACTION_TURN_RIGHT = "right"
const val ACTION_TURN_LEFT = "left"
const val ACTION_STRAIGHT = "straight"
// Initial value should be 0 cuz in server is -1
var steeringDirectionId = 0
val steeringDirection
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_steering_direction")

fun setSteering(direction: String, value: Int = 0) =
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
            "set_steering_system?" +
            "id=${steeringDirectionId++}" +
            "&direction=$direction" +
            "&value=$value")


/////////
// Main Lights
/////////
const val LIGHTS_OFF = "lights_off"
const val POSITION_LIGHTS = "position_lights"
const val DRIVING_LIGHTS = "driving_lights"
const val LONG_RANGE_LIGHTS = "long_range_lights"
const val LONG_RANGE_SIGNAL_LIGHTS = "long_range_signal_lights"
var mainLightsState: String
    set(value) {
        doBlockingRequest("http://$serverIp:$serverPort/" +
                "set_main_lights_state?" +
                "value=$value")
    }
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
                "get_main_lights_state")



/////////
// General use
/////////
private fun doNonBlockingRequest(url:String) = launch { doRequest(url) }

private fun doBlockingRequest(url:String) = runBlocking(CommonPool) { doRequest(url) }

private fun doRequest(url: String): String {
    var con: HttpURLConnection?
    val urlGet: URL
    var requestInputStream: InputStream? = null
    val sb =  StringBuilder()
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        requestInputStream = con.run {
            readTimeout = 10000 /* milliseconds */
            connectTimeout = 2000 /* milliseconds */
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
        sb.append(e.message ?: NO_DATA)
    }
    return sb.toString()
}

val myIP:String
    get() {
        val wifiMgr = context?.getSystemService(WIFI_SERVICE) as WifiManager?
        val wifiInfo = wifiMgr?.connectionInfo
        return if (wifiInfo != null) {
            Formatter.formatIpAddress(wifiInfo.ipAddress)
        }
        else
            NO_DATA
    }
