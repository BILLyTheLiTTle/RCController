package car.rccontroller.network.cockpit

import car.rccontroller.R
import car.rccontroller.RCControllerActivity
import car.rccontroller.RUN_ON_EMULATOR
import car.rccontroller.network.*
import car.rccontroller.network.server.feedback.SensorFeedbackServer
import car.rccontroller.retrofit
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
}

private val api: Engine by lazy { retrofit.create<Engine>(Engine::class.java) }

fun isEngineStarted(retrofitApi: Engine = api): Boolean {
    return runBlockingRequest { retrofitApi.getEngineState() } == true
}

fun startEngine(retrofitApi: Engine = api, context: RCControllerActivity?, serverIp: String?, serverPort: Int?): String{
    //reset and get ready for new requests
    if(context != null) {
        throttleBrakeActionId = context.resources.getInteger(R.integer.default_throttleBrakeActionId).toLong()
        steeringDirectionId = context.resources.getInteger(R.integer.default_steeringDirectionId).toLong()
    }

    /* The server will know when car is moving backward and not when the car is going to move
        backward in the next throttle action. The default state for this “ImageView” will be
        false (means not backward). So, this local variable must be reset at every start.
     */
    reverseIntention = false

    raspiServerIP = serverIp
    raspiServerPort = serverPort
    car.rccontroller.network.context = context

    if (context != null) {
        if (sensorFeedbackServer != null) sensorFeedbackServer!!.stop()
        sensorFeedbackServer = if (RUN_ON_EMULATOR) SensorFeedbackServer(context) else SensorFeedbackServer(
            context,
            myIP,
            sensorFeedbackServerPort
        )
        sensorFeedbackServer!!.start()
    }

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    return runBlockingRequest {
        retrofitApi.startEngine(
            if (sensorFeedbackServer != null) sensorFeedbackServer!!.ip else myIP,
            if (sensorFeedbackServer != null) sensorFeedbackServer!!.port else sensorFeedbackServerPort
        )
    } ?: EMPTY_STRING
}

fun stopEngine(retrofitApi: Engine = api): String {
    val msg = runBlockingRequest { retrofitApi.stopEngine() } ?: EMPTY_STRING

    if(msg == OK_STRING) {
        raspiServerIP = null
        raspiServerPort = null
    }

    // TODO if I don't want to save manual setup settings
    previousFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
    previousRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED

    if(sensorFeedbackServer != null) sensorFeedbackServer!!.stop()

    return msg
}