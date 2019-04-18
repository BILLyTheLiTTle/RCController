package car.rccontroller.network.cockpit

import car.rccontroller.network.EMPTY_STRING
import car.rccontroller.network.runBlockingRequest
import car.rccontroller.setupAPI
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

}

// Handling Assistance
const val ASSISTANCE_NULL = EMPTY_STRING
const val ASSISTANCE_NONE = "assistance_none"
const val ASSISTANCE_WARNING = "assistance_warning"
const val ASSISTANCE_FULL = "assistance_full"

fun setHandlingAssistanceState(state: String, retrofitAPI: Setup = setupAPI): String {
    return runBlockingRequest { retrofitAPI.setHandlingAssistance(state) } ?: EMPTY_STRING
}

fun getHandlingAssistanceState(retrofitAPI: Setup = setupAPI): String {
    return runBlockingRequest { retrofitAPI.getHandlingAssistanceState() } ?: EMPTY_STRING
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

fun setMotorSpeedLimiter(value: Double, retrofitAPI: Setup = setupAPI): String {
    return runBlockingRequest { retrofitAPI.setMotorSpeedLimiter(value) } ?: EMPTY_STRING
}

fun getMotorSpeedLimiter(retrofitAPI: Setup = setupAPI): Double {
    return runBlockingRequest { retrofitAPI.getMotorSpeedLimiter() } ?: MOTOR_SPEED_LIMITER_ERROR_SPEED
}

// Differential Slippery Limiter
const val DIFFERENTIAL_SLIPPERY_LIMITER_ERROR = -1
const val DIFFERENTIAL_SLIPPERY_LIMITER_OPEN = 0
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 = 1
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 = 2
const val DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 = 3
const val DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED = 4
const val DIFFERENTIAL_SLIPPERY_LIMITER_AUTO = 10

//---- Front ----
var previousFrontDifferentialSlipperyLimiter: Int = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED

fun setFrontDifferentialSlipperyLimiter(value: Int, retrofitAPI: Setup = setupAPI): String {
    return runBlockingRequest { retrofitAPI.setFrontDifferentialSlipperyLimiter(value) } ?: EMPTY_STRING
}

fun getFrontDifferentialSlipperyLimiter(retrofitAPI: Setup = setupAPI): Int {
    return runBlockingRequest { retrofitAPI.getFrontDifferentialSlipperyLimiter() } ?: DIFFERENTIAL_SLIPPERY_LIMITER_ERROR
}

//---- Rear ----
var previousRearDifferentialSlipperyLimiter: Int = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED

fun setRearDifferentialSlipperyLimiter(value: Int, retrofitAPI: Setup = setupAPI): String {
    return runBlockingRequest { retrofitAPI.setRearDifferentialSlipperyLimiter(value) } ?: EMPTY_STRING
}

fun getRearDifferentialSlipperyLimiter(retrofitAPI: Setup = setupAPI): Int {
    return runBlockingRequest { retrofitAPI.getRearDifferentialSlipperyLimiter() } ?: DIFFERENTIAL_SLIPPERY_LIMITER_ERROR
}