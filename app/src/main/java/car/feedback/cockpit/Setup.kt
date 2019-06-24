package car.feedback.cockpit

import car.enumContains
import car.feedback.EMPTY_STRING
import car.feedback.runBlockingRequest
import car.rccontroller.retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Setup {
    @GET("/set_handling_assistance")
    fun setHandlingAssistance(@Query("state") state: String): Call<String>

    @GET("/get_handling_assistance_state")
    fun getHandlingAssistanceState(): Call<String>

    @GET("/set_motor_speed_limiter")
    fun setMotorSpeedLimiter(@Query("value") value: String): Call<String>

    @GET("/get_motor_speed_limiter")
    fun getMotorSpeedLimiter(): Call<String>

    @GET("/set_front_differential_slippery_limiter")
    fun setFrontDifferentialSlipperyLimiter(@Query("value") value: String): Call<String>

    @GET("/get_front_differential_slippery_limiter")
    fun getFrontDifferentialSlipperyLimiter(): Call<String>

    @GET("/set_rear_differential_slippery_limiter")
    fun setRearDifferentialSlipperyLimiter(@Query("value") value: String): Call<String>

    @GET("/get_rear_differential_slippery_limiter")
    fun getRearDifferentialSlipperyLimiter(): Call<String>

    companion object {
        val setupAPI: Setup by lazy { retrofit.create<Setup>(Setup::class.java) }
    }
}

fun setHandlingAssistanceState(state: HandlingAssistance): String {
    return runBlockingRequest { Setup.setupAPI.setHandlingAssistance(state.name) } ?: EMPTY_STRING
}

fun getHandlingAssistanceState(): HandlingAssistance {
    val value = runBlockingRequest { Setup.setupAPI.getHandlingAssistanceState() } ?: EMPTY_STRING
    return if (enumContains<HandlingAssistance>(value))
        HandlingAssistance.valueOf(value)
    else
        HandlingAssistance.NULL
}

fun setMotorSpeedLimiter(value: MotorSpeedLimiter): String {
    return runBlockingRequest { Setup.setupAPI.setMotorSpeedLimiter(value.name) } ?: EMPTY_STRING
}

fun getMotorSpeedLimiter(): MotorSpeedLimiter {
    val value = runBlockingRequest { Setup.setupAPI.getMotorSpeedLimiter() } ?: EMPTY_STRING
    return if (enumContains<MotorSpeedLimiter>(value))
        MotorSpeedLimiter.valueOf(value)
    else
        MotorSpeedLimiter.ERROR_SPEED
}

//---- Front ----
var previousFrontDifferentialSlipperyLimiter = DifferentialSlipperyLimiter.LOCKED

fun setFrontDifferentialSlipperyLimiter(value: DifferentialSlipperyLimiter): String {
    return runBlockingRequest {
        Setup.setupAPI.setFrontDifferentialSlipperyLimiter(value.name)
    } ?: EMPTY_STRING
}

fun getFrontDifferentialSlipperyLimiter(): DifferentialSlipperyLimiter {
    val value = runBlockingRequest {
        Setup.setupAPI.getFrontDifferentialSlipperyLimiter()
    } ?: EMPTY_STRING
    return if (enumContains<DifferentialSlipperyLimiter>(value))
        DifferentialSlipperyLimiter.valueOf(value)
    else
        DifferentialSlipperyLimiter.ERROR
}

//---- Rear ----
var previousRearDifferentialSlipperyLimiter = DifferentialSlipperyLimiter.LOCKED

fun setRearDifferentialSlipperyLimiter(value: DifferentialSlipperyLimiter): String {
    return runBlockingRequest {
        Setup.setupAPI.setRearDifferentialSlipperyLimiter(value.name)
    } ?: EMPTY_STRING
}

fun getRearDifferentialSlipperyLimiter(): DifferentialSlipperyLimiter {
    val value = runBlockingRequest {
        Setup.setupAPI.getRearDifferentialSlipperyLimiter()
    } ?: EMPTY_STRING

    return if (enumContains<DifferentialSlipperyLimiter>(value))
        DifferentialSlipperyLimiter.valueOf(value)
    else
        DifferentialSlipperyLimiter.ERROR
}

enum class DifferentialSlipperyLimiter {
    NULL, // for local use only
    OPEN, MEDI_0, MEDI_1, MEDI_2, LOCKED, AUTO, ERROR,
}

enum class HandlingAssistance {
    NULL, // for local use only
    MANUAL, WARNING, FULL
}

enum class MotorSpeedLimiter {
    NULL, // for local use only
    ERROR_SPEED, NO_SPEED, SLOW_SPEED_1, SLOW_SPEED_2, MEDIUM_SPEED_1, MEDIUM_SPEED_2,
    FAST_SPEED_1, FAST_SPEED_2, FULL_SPEED
}