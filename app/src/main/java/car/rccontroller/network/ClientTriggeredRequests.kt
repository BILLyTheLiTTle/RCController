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
import android.net.wifi.WifiManager
import android.text.format.Formatter
import car.rccontroller.RCControllerActivity
import car.rccontroller.RUN_ON_EMULATOR


const val OK_STRING = "OK"
const val EMPTY_STRING = "NULL"

var raspiServerIp: String? = null
var raspiServerPort: Int? = null
var context: RCControllerActivity? = null

private lateinit var androidWebServer:Server

/////////
// Engine
/////////
val isEngineStarted: Boolean
get() = doBlockingRequest("http://${car.rccontroller.network.raspiServerIp}:" +
            "${car.rccontroller.network.raspiServerPort}/get_engine_state").toBoolean()

fun startEngine(context: RCControllerActivity, serverIp: String?, serverPort: Int?): String{
    //reset and get ready for new requests
    throttleBrakeActionId = context.resources.getInteger(R.integer.default_throttleBrakeActionId)
    steeringDirectionId = context.resources.getInteger(R.integer.default_steeringDirectionId)

    /* The server will know when car is moving backward and not when the car is going to move
        backward in the next throttle action. The default state for this “ImageView” will be
        false (means not backward). So, this local variable must be reset at every start.
     */
    reverseIntention = false

    car.rccontroller.network.raspiServerIp = serverIp
    car.rccontroller.network.raspiServerPort = serverPort
    car.rccontroller.network.context = context

    if(::androidWebServer.isInitialized) androidWebServer.stop()
    androidWebServer = if (RUN_ON_EMULATOR) Server(context) else Server(context, myIP, 8080)
    androidWebServer.start()

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    return doBlockingRequest("http://${car.rccontroller.network.raspiServerIp}:" +
            "${car.rccontroller.network.raspiServerPort}/start_engine?" +
            "nanohttp_client_ip=${androidWebServer.ip}" +
            "&nanohttp_client_port=${androidWebServer.port}")
}

fun stopEngine(): String {
    val msg = doBlockingRequest("http://$raspiServerIp:" +
            "$raspiServerPort/stop_engine")

    if(msg == OK_STRING) {
        raspiServerIp = null
        raspiServerPort = null
    }

    if(::androidWebServer.isInitialized) androidWebServer.stop()

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
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_parking_brake_state").toBoolean()

fun activateParkingBrake(state: Boolean) = if (state)
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=100")
    else
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=0")

//---- Handbrake ----
val isHandbrakeActive: Boolean
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_handbrake_state").toBoolean()

fun activateHandbrake(state: Boolean) = if (state)
    doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=100")
else
    doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=0")

//---- Throttle / Brake / Neutral / Reverse ----
var reverseIntention: Boolean
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_reverse_lights_state").toBoolean()
    set(value) {doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "set_reverse_lights_state?" +
            "state=$value")}

fun setNeutral() = doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
        "set_throttle_brake_system?id=${throttleBrakeActionId++}&action=$ACTION_NEUTRAL")

fun setBrakingStill() = doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
        "set_throttle_brake_system?" +
        "id=${throttleBrakeActionId++}" +
        "&action=$ACTION_BRAKING_STILL")

fun setThrottleBrake(direction: String, value: Int) =
    doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
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
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_steering_direction")

fun setSteering(direction: String, value: Int = 0) =
    doNonBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
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
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_main_lights_state?" +
                "value=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "get_main_lights_state")


/////////
// Turn Lights (Left/Right/Straight)
/////////
const val TURN_LIGHTS_RIGHT = "turn_lights_right"
const val TURN_LIGHTS_LEFT = "turn_lights_left"
const val TURN_LIGHTS_STRAIGHT = "turn_lights_straight"
var turnLights: String
    set(value) {
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_direction_lights?" +
                "direction=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_direction_lights")


/////////
// Emergency Lights
/////////
const val EMERGENCY_LIGHTS = "emergency_lights"
var emergencyLights: Boolean
    set(value) {
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_emergency_lights_state?" +
                "state=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_emergency_lights_state").toBoolean()


/////////
// Handling Assistance
/////////
const val ASSISTANCE_NULL = EMPTY_STRING
const val ASSISTANCE_NONE = "assistance_none"
const val ASSISTANCE_WARNING = "assistance_warning"
const val ASSISTANCE_FULL = "assistance_full"
var handlingAssistanceState: String
    set(value) {
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_handling_assistance?" +
                "state=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_handling_assistance_state")


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