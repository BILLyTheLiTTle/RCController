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

    companion object {
        val throttleBrakeAPI: ThrottleBrake by lazy { retrofit.create<ThrottleBrake>(ThrottleBrake::class.java) }
    }
}

const val ACTION_MOVE_FORWARD = "forward"
const val ACTION_MOVE_BACKWARD = "backward"
const val ACTION_NEUTRAL = "neutral"
const val ACTION_BRAKING_STILL = "braking_still"
const val ACTION_PARKING_BRAKE = "parking_brake"
const val ACTION_HANDBRAKE = "handbrake"
// Initial value should be 0 cuz in server is -1
var throttleBrakeActionId = 0L

//---- Parking Brake ----
fun isParkingBrakeActive(): Boolean {
    return runBlockingRequest { ThrottleBrake.throttleBrakeAPI.getParkingBrakeState() } == true
}

fun activateParkingBrake(state: Boolean): String {
    return runBlockingRequest {
        if (state)
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, ACTION_PARKING_BRAKE, 100)
        else
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, ACTION_PARKING_BRAKE, 0)
    } ?: EMPTY_STRING
}

//---- Handbrake ----
fun isHandbrakeActive(retrofitAPI: ThrottleBrake = ThrottleBrake.throttleBrakeAPI): Boolean {
    return runBlockingRequest { retrofitAPI.getHandbrakeState() } == true
}

fun activateHandbrake(state: Boolean): Job? {
    return launchRequest {
        if (state)
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, ACTION_HANDBRAKE, 100)
        else
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, ACTION_HANDBRAKE, 0)
    }
}

//---- Throttle / Brake / Neutral ----
fun  getMotionState(): String {
    return runBlockingRequest {
        ThrottleBrake.throttleBrakeAPI.getMotionState()
    } ?: EMPTY_STRING
}

fun setNeutral(): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI.
        setThrottleBrakeAction(throttleBrakeActionId++, ACTION_NEUTRAL) }
}

fun setBrakingStill(): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI
        .setThrottleBrakeAction(throttleBrakeActionId++, ACTION_BRAKING_STILL) }
}

fun setThrottleBrake(direction: String, value: Int): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI
        .setThrottleBrakeAction(throttleBrakeActionId++, direction, value) }
}