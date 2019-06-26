package car.feedback.cockpit

import car.enumContains
import car.feedback.EMPTY_STRING
import car.feedback.runBlockingRequest
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
    val light = runBlockingRequest {
        Electrics.electricsAPI.setMainLightsState(state.name)
    } ?: EMPTY_STRING

    return if (enumContains<MainLight>(light)) MainLight.valueOf(light) else MainLight.NOTHING
}

fun getMainLightsState(): MainLight {
    val light = runBlockingRequest {
        Electrics.electricsAPI.getMainLightsState()
    } ?: EMPTY_STRING

    return if (enumContains<MainLight>(light)) MainLight.valueOf(light) else MainLight.NOTHING
}

/////////
// Turn Lights (Left/Right/Straight)
/////////
fun setDirectionLightsState(state: CorneringLight): CorneringLight {
    val light = runBlockingRequest {
        Electrics.electricsAPI.setDirectionLights(state.name)
    } ?: EMPTY_STRING

    return if (enumContains<CorneringLight>(light))
        CorneringLight.valueOf(light)
    else
        CorneringLight.NOTHING
}

fun getDirectionLightsState(): CorneringLight {
    val light = runBlockingRequest {
        Electrics.electricsAPI.getDirectionLights()
    } ?: EMPTY_STRING

    return if (enumContains<CorneringLight>(light))
        CorneringLight.valueOf(light)
    else
        CorneringLight.NOTHING
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

enum class MainLight {
    NOTHING,
    LIGHTS_OFF,
    POSITION_LIGHTS, DRIVING_LIGHTS, LONG_RANGE_LIGHTS,
    LONG_RANGE_SIGNAL_LIGHTS
}

enum class CorneringLight {
    NOTHING,
    RIGHT_LIGHTS, LEFT_LIGHTS, STRAIGHT_LIGHTS
}