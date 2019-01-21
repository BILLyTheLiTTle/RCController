package car.rccontroller.network

import android.content.Context
import car.rccontroller.R
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
import car.rccontroller.network.server.feedback.SensorFeedbackServer
import kotlinx.coroutines.*


const val OK_STRING = "OK"
const val EMPTY_STRING = "NULL"
const val sensorFeedbackServerPort= 8080

var raspiServerIp: String? = null
var raspiServerPort: Int? = null
var context: RCControllerActivity? = null

private lateinit var sensorFeedbackServer: SensorFeedbackServer

/////////
// Engine
/////////
val isEngineStarted: Boolean
get() = doBlockingRequest("http://${car.rccontroller.network.raspiServerIp}:" +
            "${car.rccontroller.network.raspiServerPort}/get_engine_state").toBoolean()

fun startEngine(context: RCControllerActivity?, serverIp: String?, serverPort: Int?): String{
    //reset and get ready for new requests
    if(context != null) {
        throttleBrakeActionId = context.resources.getInteger(R.integer.default_throttleBrakeActionId).toLong()
        steeringDirectionId = context.resources.getInteger(R.integer.default_steeringDirectionId).toLong()
    }

    /* The server will know when car is moving backward and not when the car is going to move
        backward in the next throttle action. The default state for this “ImageView” will be
        false (means not backward). So, this local variable must be reset at every start.
     */
    reverseIntention = false

    car.rccontroller.network.raspiServerIp = serverIp
    car.rccontroller.network.raspiServerPort = serverPort
    car.rccontroller.network.context = context

    if (context != null) {
        if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.stop()
        sensorFeedbackServer = if (RUN_ON_EMULATOR) SensorFeedbackServer(context) else SensorFeedbackServer(
                context,
                myIP,
                sensorFeedbackServerPort
        )
        sensorFeedbackServer.start()
    }

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    return doBlockingRequest("http://${car.rccontroller.network.raspiServerIp}:" +
            "${car.rccontroller.network.raspiServerPort}/start_engine?" +
            "nanohttp_client_ip=${if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.ip else myIP}" +
            "&nanohttp_client_port=${if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.port else sensorFeedbackServerPort}")
}

fun stopEngine(): String {
    val msg = doBlockingRequest("http://$raspiServerIp:" +
            "$raspiServerPort/stop_engine")

    if(msg == OK_STRING) {
        raspiServerIp = null
        raspiServerPort = null
    }

    // TODO if I don't want to save manual setup settings
    previousFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
    previousRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED

    if(::sensorFeedbackServer.isInitialized) sensorFeedbackServer.stop()


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
var throttleBrakeActionId = 0L

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

val motionState: String
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_motion_state")

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
var steeringDirectionId = 0L
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
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_motor_speed_limiter?" +
                "value=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
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
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_front_differential_slippery_limiter?" +
                "value=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_front_differential_slippery_limiter").toIntOrNull()

//---- Rear ----
var previousRearDifferentialSlipperyLimiter: Int? = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
var currentRearDifferentialSlipperyLimiter: Int?
    set(value) {
        doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
                "set_rear_differential_slippery_limiter?" +
                "value=$value")
    }
    get() = doBlockingRequest("http://$raspiServerIp:$raspiServerPort/" +
            "get_rear_differential_slippery_limiter").toIntOrNull()


/////////
// General use
/////////
// In case of second Activity existence this should be implemented correctly for a CoroutineScope usage
private fun doNonBlockingRequest(url:String) = CoroutineScope(Dispatchers.IO).launch { doRequest(url) }

internal fun doBlockingRequest(url:String) = runBlocking(Dispatchers.IO) { doRequest(url) }

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