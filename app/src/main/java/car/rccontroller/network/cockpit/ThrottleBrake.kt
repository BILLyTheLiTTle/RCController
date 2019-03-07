package car.rccontroller.network.cockpit

import car.rccontroller.network.*
import car.rccontroller.retrofit
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ThrottleBrake {
    @GET("/set_throttle_brake_system")
    fun setThrottleBrakeAction(@Query("id") id: Long,
                               @Query("action") action: String,
                               @Query("value") value: Int = 0): Call<String>

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

fun activateParkingBrake(retrofitApi: ThrottleBrake = api, state: Boolean): String {
    return runBlockingRequest {
        if (state)
            retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_PARKING_BRAKE, 100)
        else
            retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_PARKING_BRAKE, 0)
    } ?: EMPTY_STRING
}

//---- Handbrake ----
fun isHandbrakeActive(retrofitApi: ThrottleBrake = api): Boolean {
    return runBlockingRequest { retrofitApi.getHandbrakeState() } == true
}

fun activateHandbrake(retrofitApi: ThrottleBrake = api, state: Boolean): Job? {
    return launchRequest {
        if (state)
            retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_HANDBRAKE, 100)
        else
            retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_HANDBRAKE, 0)
    }
}

//---- Throttle / Brake / Neutral ----
fun  getMotionState(retrofitApi: ThrottleBrake = api): String {
    return runBlockingRequest {
        retrofitApi.getMotionState()
    } ?: EMPTY_STRING
}

fun setNeutral(retrofitApi: ThrottleBrake = api): Job? {
    return launchRequest { retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_NEUTRAL) }
}

fun setBrakingStill(retrofitApi: ThrottleBrake = api): Job? {
    return launchRequest { retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, ACTION_BRAKING_STILL) }
}

fun setThrottleBrake(retrofitApi: ThrottleBrake = api, direction: String, value: Int): Job? {
    return launchRequest { retrofitApi.setThrottleBrakeAction(throttleBrakeActionId++, direction, value) }
}