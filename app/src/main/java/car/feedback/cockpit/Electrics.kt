package car.feedback.cockpit

import car.feedback.EMPTY_STRING
import car.feedback.runBlockingRequest
import car.feedback.CarPart
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
fun setMainLightsState(state: MainLight): MainLight {
    return MainLight.valueOf(
        runBlockingRequest { Electrics.electricsAPI.setMainLightsState(state.name) } ?: EMPTY_STRING
    )
}

fun getMainLightsState(): MainLight {
    return MainLight.valueOf(
        runBlockingRequest { Electrics.electricsAPI.getMainLightsState() } ?: EMPTY_STRING
    )
}

/////////
// Turn Lights (Left/Right/Straight)
/////////
fun setDirectionLightsState(state: DirectionLight): DirectionLight {
    return DirectionLight.valueOf(
        runBlockingRequest { Electrics.electricsAPI.setDirectionLights(state.name) } ?: EMPTY_STRING
    )
}

fun getDirectionLightsState(): DirectionLight {
    return DirectionLight.valueOf(
        runBlockingRequest { Electrics.electricsAPI.getDirectionLights() } ?: EMPTY_STRING
    )
}

/////////
// Emergency Lights
/////////
fun setEmergencyLightsState(state: Boolean): String {
    return runBlockingRequest { Electrics.electricsAPI.setEmergencyLightsState(state) } ?: EMPTY_STRING
}

fun getEmergencyLightsState(): Boolean {
    return runBlockingRequest { Electrics.electricsAPI.getEmergencyLightsState() } == true
}

/////////
// Reverse Lights
/////////
fun setReverseIntention(state: Boolean): String {
    return runBlockingRequest { Electrics.electricsAPI.setReverseLightsState(state) } ?: EMPTY_STRING
}

fun getReverseIntention(): Boolean {
    return runBlockingRequest { Electrics.electricsAPI.getReverseLightsState() } == true
}

enum class MainLight(val id: String) {
    LIGHTS_OFF("${CarPart.LIGHTS.id}_off"),
    POSITION_LIGHTS("${CarPart.LIGHTS.id}_position"),
    DRIVING_LIGHTS("${CarPart.LIGHTS.id}_driving"),
    LONG_RANGE_LIGHTS("${CarPart.LIGHTS.id}_long_range"),
    LONG_RANGE_SIGNAL_LIGHTS("${CarPart.LIGHTS.id}_long_range_signal")
}

enum class DirectionLight(val id:String){
    DIRECTION_LIGHTS_RIGHT("${CarPart.DIRECTION_LIGHTS.id}_right"),
    DIRECTION_LIGHTS_LEFT("${CarPart.DIRECTION_LIGHTS.id}_left"),
    DIRECTION_LIGHTS_STRAIGHT("${CarPart.DIRECTION_LIGHTS.id}_straight");
}

enum class OtherLight(val id:String){
    BRAKING_LIGHTS("${CarPart.LIGHTS.id}_braking"),
    REVERSE_LIGHTS("${CarPart.LIGHTS.id}_reverse"),
    EMERGENCY_LIGHTS("${CarPart.LIGHTS.id}_emergency")
}