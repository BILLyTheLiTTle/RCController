package car.rccontroller.network.cockpit

import car.rccontroller.network.launchRequest
import car.rccontroller.network.raspiServerIP
import car.rccontroller.network.raspiServerPort
import car.rccontroller.network.runBlockingRequest
import car.rccontroller.retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ThrottleBrake {
    @GET("/set_throttle_brake_system")
    fun setThrottleBrakeAction(@Query("id") id: Long,
                               @Query("action") action: String,
                               @Query("value") value: Int): Call<String>

    @GET("/get_parking_brake_state")
    fun getParkingBrakeState(): Call<Boolean>

    @GET("/get_handbrake_state")
    fun getHandbrakeState(): Call<Boolean>

    @GET("/get_motion_state")
    fun getMotionState(): Call<String>
}

private val api: ThrottleBrake by lazy { retrofit.create<ThrottleBrake>(ThrottleBrake::class.java) }

const val ACTION_MOVE_FORWARD = "forward"
const val ACTION_MOVE_BACKWARD = "backward"
const val ACTION_NEUTRAL = "neutral"
const val ACTION_BRAKING_STILL = "braking_still"
const val ACTION_PARKING_BRAKE = "parking_brake"
const val ACTION_HANDBRAKE = "handbrake"
// Initial value should be 0 cuz in server is -1
var throttleBrakeActionId = 0L

//---- Parking Brake ----
fun isParkingBrakeActive(retrofitApi: ThrottleBrake = api): Boolean {
    return runBlockingRequest { retrofitApi.getParkingBrakeState() } == true
}

fun activateParkingBrake(state: Boolean) = if (state)
    runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_PARKING_BRAKE" +
            "&value=100")
else
    runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_PARKING_BRAKE" +
            "&value=0")

//---- Handbrake ----
val isHandbrakeActive: Boolean
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_handbrake_state").toBoolean()

fun activateHandbrake(state: Boolean) = if (state)
    launchRequest("http://$raspiServerIP:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=100")
else
    launchRequest("http://$raspiServerIP:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=0")

//---- Throttle / Brake / Neutral / Reverse ----
var reverseIntention: Boolean
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_reverse_lights_state").toBoolean()
    set(value) {
        runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
                "set_reverse_lights_state?" +
                "state=$value")
    }

val motionState: String
    get() = runBlockingRequest("http://$raspiServerIP:$raspiServerPort/" +
            "get_motion_state")

fun setNeutral() = launchRequest("http://$raspiServerIP:$raspiServerPort/" +
        "set_throttle_brake_system?id=${throttleBrakeActionId++}&action=$ACTION_NEUTRAL")

fun setBrakingStill() = launchRequest("http://$raspiServerIP:$raspiServerPort/" +
        "set_throttle_brake_system?" +
        "id=${throttleBrakeActionId++}" +
        "&action=$ACTION_BRAKING_STILL")

fun setThrottleBrake(direction: String, value: Int) =
    launchRequest("http://$raspiServerIP:$raspiServerPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$direction" +
            "&value=$value")