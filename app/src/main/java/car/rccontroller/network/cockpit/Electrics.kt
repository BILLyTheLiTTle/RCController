package car.rccontroller.network.cockpit

import car.rccontroller.network.EMPTY_STRING
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

    companion object {
        val electricsAPI: Electrics by lazy { retrofit.create<Electrics>(Electrics::class.java) }
    }
}

/////////
// Main Lights
/////////
const val LIGHTS_OFF = "lights_off"
const val POSITION_LIGHTS = "position_lights"
const val DRIVING_LIGHTS = "driving_lights"
const val LONG_RANGE_LIGHTS = "long_range_lights"
const val LONG_RANGE_SIGNAL_LIGHTS = "long_range_signal_lights"

fun setMainLightsState(state: String): String {
    return runBlockingRequest { Electrics.electricsAPI.setMainLightsState(state) } ?: EMPTY_STRING
}

fun getMainLightsState(): String {
    return runBlockingRequest { Electrics.electricsAPI.getMainLightsState() } ?: EMPTY_STRING
}

/////////
// Turn Lights (Left/Right/Straight)
/////////
const val DIRECTION_LIGHTS_RIGHT = "direction_lights_right"
const val DIRECTION_LIGHTS_LEFT = "direction_lights_left"
const val DIRECTION_LIGHTS_STRAIGHT = "direction_lights_straight"

fun setDirectionLightsState(state: String): String {
    return runBlockingRequest { Electrics.electricsAPI.setDirectionLights(state) } ?: EMPTY_STRING
}

fun getDirectionLightsState(): String {
    return runBlockingRequest { Electrics.electricsAPI.getDirectionLights() } ?: EMPTY_STRING
}

/////////
// Emergency Lights
/////////
const val EMERGENCY_LIGHTS = "emergency_lights"

fun setEmergencyLightsState(state: Boolean): String {
    return runBlockingRequest { Electrics.electricsAPI.setEmergencyLightsState(state) } ?: EMPTY_STRING
}

fun getEmergencyLightsState(): Boolean {
    return runBlockingRequest { Electrics.electricsAPI.getEmergencyLightsState() } == true
}

/////////
// Reverse Lights
/////////
const val REVERSE_LIGHTS = "reverse_lights"

fun setReverseIntention(state: Boolean): String {
    return runBlockingRequest { Electrics.electricsAPI.setReverseLightsState(state) } ?: EMPTY_STRING
}

fun getReverseIntention(): Boolean {
    return runBlockingRequest { Electrics.electricsAPI.getReverseLightsState() } == true
}