package car.rccontroller.network.cockpit

import car.rccontroller.electricsAPI
import car.rccontroller.network.EMPTY_STRING
import car.rccontroller.network.raspiServerIP
import car.rccontroller.network.raspiServerPort
import car.rccontroller.network.runBlockingRequest
import car.rccontroller.retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Electrics {
    @GET("/set_direction_lights")
    fun setDirectionLights(@Query("direction") direction: String): Call<String>

    @GET("/get_direction_lights")
    fun getDirectionLights(): Call<String>

    @GET("/set_main_lights_state")
    fun setMainLightsState(@Query("state") value: String): Call<String>

    @GET("/get_main_lights_state")
    fun getMainLightsState(): Call<String>

    @GET("/set_reverse_lights_state")
    fun setReverseLightsState(@Query("state") state: Boolean): Call<String>

    @GET("/get_reverse_lights_state")
    fun getReverseLightsState(): Call<Boolean>

    @GET("/set_emergency_lights_state")
    fun setEmergencyLightsState(@Query("state") state: Boolean): Call<String>

    @GET("/get_emergency_lights_state")
    fun getEmergencyLightsState(): Call<Boolean>
}

/////////
// Main Lights
/////////
const val LIGHTS_OFF = "lights_off"
const val POSITION_LIGHTS = "position_lights"
const val DRIVING_LIGHTS = "driving_lights"
const val LONG_RANGE_LIGHTS = "long_range_lights"
const val LONG_RANGE_SIGNAL_LIGHTS = "long_range_signal_lights"

fun setMainLightsState(state: String, retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.setMainLightsState(state) } ?: EMPTY_STRING
}

fun getMainLightsState(retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.getMainLightsState() } ?: EMPTY_STRING
}

/////////
// Turn Lights (Left/Right/Straight)
/////////
const val DIRECTION_LIGHTS_RIGHT = "direction_lights_right"
const val DIRECTION_LIGHTS_LEFT = "direction_lights_left"
const val DIRECTION_LIGHTS_STRAIGHT = "direction_lights_straight"

fun setDirectionLightsState(state: String, retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.setDirectionLights(state) } ?: EMPTY_STRING
}

fun getDirectionLightsState(retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.getDirectionLights() } ?: EMPTY_STRING
}

/////////
// Emergency Lights
/////////
const val EMERGENCY_LIGHTS = "emergency_lights"

fun setEmergencyLightsState(state: Boolean, retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.setEmergencyLightsState(state) } ?: EMPTY_STRING
}

fun getEmergencyLightsState(retrofitAPI: Electrics = electricsAPI): Boolean {
    return runBlockingRequest { retrofitAPI.getEmergencyLightsState() } == true
}

/////////
// Reverse Lights
/////////
const val REVERSE_LIGHTS = "reverse_lights"

fun setReverseIntention(state: Boolean, retrofitAPI: Electrics = electricsAPI): String {
    return runBlockingRequest { retrofitAPI.setReverseLightsState(state) } ?: EMPTY_STRING
}

fun getReverseIntention(retrofitAPI: Electrics = electricsAPI): Boolean {
    return runBlockingRequest { retrofitAPI.getReverseLightsState() } == true
}