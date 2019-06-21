package car.feedback.cockpit

import car.enumContains
import car.feedback.*
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
                .setThrottleBrakeAction(throttleBrakeActionId++, Motion.PARKING_BRAKE.name, 100)
        else
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, Motion.PARKING_BRAKE.name, 0)
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
                .setThrottleBrakeAction(throttleBrakeActionId++, Motion.HANDBRAKE.name, 100)
        else
            ThrottleBrake.throttleBrakeAPI
                .setThrottleBrakeAction(throttleBrakeActionId++, Motion.HANDBRAKE.name, 0)
    }
}

//---- Throttle / Brake / Neutral ----
fun  getMotionState(): Motion {
    val motionState = runBlockingRequest {
        ThrottleBrake.throttleBrakeAPI.getMotionState()
    } ?: EMPTY_STRING

    return if (enumContains<Motion>(motionState)) Motion.valueOf(motionState) else Motion.NOTHING
}

fun setNeutral(): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI.
        setThrottleBrakeAction(throttleBrakeActionId++, Motion.NEUTRAL.name) }
}

fun setBrakingStill(): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI
        .setThrottleBrakeAction(throttleBrakeActionId++, Motion.BRAKING_STILL.name) }
}

fun setThrottleBrake(direction: Motion, value: Int): Job? {
    return launchRequest { ThrottleBrake.throttleBrakeAPI
        .setThrottleBrakeAction(throttleBrakeActionId++, direction.name, value) }
}

enum class Motion {
    NOTHING, FORWARD, BACKWARD, NEUTRAL, BRAKING_STILL, PARKING_BRAKE, HANDBRAKE
}