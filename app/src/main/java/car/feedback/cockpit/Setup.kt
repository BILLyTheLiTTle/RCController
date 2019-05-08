package car.feedback.cockpit

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
    fun setMotorSpeedLimiter(@Query("value") value: Double): Call<String>

    @GET("/get_motor_speed_limiter")
    fun getMotorSpeedLimiter(): Call<Double>

    @GET("/set_front_differential_slippery_limiter")
    fun setFrontDifferentialSlipperyLimiter(@Query("value") value: Int): Call<String>

    @GET("/get_front_differential_slippery_limiter")
    fun getFrontDifferentialSlipperyLimiter(): Call<Int>

    @GET("/set_rear_differential_slippery_limiter")
    fun setRearDifferentialSlipperyLimiter(@Query("value") value: Int): Call<String>

    @GET("/get_rear_differential_slippery_limiter")
    fun getRearDifferentialSlipperyLimiter(): Call<Int>

    companion object {
        val setupAPI: Setup by lazy { retrofit.create<Setup>(Setup::class.java) }
    }
}

fun setHandlingAssistanceState(state: String): String {
    return runBlockingRequest { Setup.setupAPI.setHandlingAssistance(state) } ?: EMPTY_STRING
}

fun getHandlingAssistanceState(): String {
    return runBlockingRequest { Setup.setupAPI.getHandlingAssistanceState() } ?: EMPTY_STRING
}

// Motor Speed Limiter
const val MOTOR_SPEED_LIMITER_ERROR_SPEED = -1.00
const val MOTOR_SPEED_LIMITER_NO_SPEED = 0.00
const val MOTOR_SPEED_LIMITER_SLOW_SPEED_1 = 0.20
const val MOTOR_SPEED_LIMITER_SLOW_SPEED_2 = 0.40
const val MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 = 0.60
const val MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 = 0.70
const val MOTOR_SPEED_LIMITER_FAST_SPEED_1 = 0.80
const val MOTOR_SPEED_LIMITER_FAST_SPEED_2 = 0.90
const val MOTOR_SPEED_LIMITER_FULL_SPEED = 1.00

fun setMotorSpeedLimiter(value: Double): String {
    return runBlockingRequest { Setup.setupAPI.setMotorSpeedLimiter(value) } ?: EMPTY_STRING
}

fun getMotorSpeedLimiter(): Double {
    return runBlockingRequest { Setup.setupAPI.getMotorSpeedLimiter() } ?: MOTOR_SPEED_LIMITER_ERROR_SPEED
}

//---- Front ----
var previousFrontDifferentialSlipperyLimiter: Int = DifferentialSlipperyLimiterState.LOCKED.value

fun setFrontDifferentialSlipperyLimiter(value: Int): String {
    return runBlockingRequest { Setup.setupAPI.setFrontDifferentialSlipperyLimiter(value) } ?: EMPTY_STRING
}

fun getFrontDifferentialSlipperyLimiter(): Int {
    return runBlockingRequest { Setup.setupAPI.getFrontDifferentialSlipperyLimiter() } ?: DifferentialSlipperyLimiterState.ERROR.value
}

//---- Rear ----
var previousRearDifferentialSlipperyLimiter: Int = DifferentialSlipperyLimiterState.LOCKED.value

fun setRearDifferentialSlipperyLimiter(value: Int): String {
    return runBlockingRequest { Setup.setupAPI.setRearDifferentialSlipperyLimiter(value) } ?: EMPTY_STRING
}

fun getRearDifferentialSlipperyLimiter(): Int {
    return runBlockingRequest { Setup.setupAPI.getRearDifferentialSlipperyLimiter() } ?: DifferentialSlipperyLimiterState.ERROR.value
}

enum class DifferentialSlipperyLimiterState(val id:String, val value: Int){
    OPEN("differential_slippery_limiter_open", 0),
    MEDI_0("differential_slippery_limiter_medi_0", 1),
    MEDI_1("differential_slippery_limiter_medi_1", 2),
    MEDI_2("differential_slippery_limiter_medi_2", 3),
    LOCKED("differential_slippery_limiter_locked", 4),
    AUTO("differential_slippery_limiter_auto", 10),
    ERROR("differential_slippery_limiter_error", -1),
}

enum class HandlingAssistance(val id: String) {
    MANUAL("assistance_manual"),
    WARNING("assistance_warning"),
    FULL("assistance_full")
}