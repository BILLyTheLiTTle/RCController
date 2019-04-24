package car.feedback.cockpit

import car.R
import car.RCControllerApplication
import car.rccontroller.*
import car.feedback.*
import car.feedback.server.NanoHTTPDLifecycleAware
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface Engine {
    @GET("start_engine")
    fun startEngine(@Query("nanohttp_client_ip") nanohttpClientIp: String,
                    @Query("nanohttp_client_port") nanohttpClientPort: Int): Call<String>

    @GET("/get_engine_state")
    fun getEngineState(): Call<Boolean>

    @GET("/stop_engine")
    fun stopEngine(): Call<String>

    companion object {
        val engineAPI: Engine by lazy { retrofit.create<Engine>(Engine::class.java) }
    }
}

fun isEngineStarted(): Boolean {
    return runBlockingRequest { Engine.engineAPI.getEngineState() } == true
}

fun startEngine(context: RCControllerApplication?): String{
    //reset and get ready for new requests
    if(context != null) {
        throttleBrakeActionId = context.resources.getInteger(R.integer.default_throttleBrakeActionId).toLong()
        steeringDirectionId = context.resources.getInteger(R.integer.default_steeringDirectionId).toLong()
    }

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    return runBlockingRequest {
        Engine.engineAPI.startEngine(
            NanoHTTPDLifecycleAware.ip,
            NanoHTTPDLifecycleAware.port
        )
    } ?: EMPTY_STRING
}

fun stopEngine(): String {
    val msg = runBlockingRequest { Engine.engineAPI.stopEngine() } ?: EMPTY_STRING

    // TODO if I don't want to save manual setup settings
    previousFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
    previousRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED

    return msg
}
