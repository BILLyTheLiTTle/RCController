package car.rccontroller.network.server.feedback

import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import car.rccontroller.RCControllerApplication
import car.rccontroller.RCControllerViewModel
import car.rccontroller.network.*
import car.rccontroller.network.server.feedback.data.CarModule
import car.rccontroller.network.server.feedback.data.CarPart
import car.rccontroller.network.server.feedback.data.ModuleState
import car.rccontroller.network.server.feedback.data.TemperatureWarningType
import fi.iki.elonen.NanoHTTPD

class NanoHTTPDLifecycleAware(private val model: RCControllerViewModel): LifecycleObserver {
    private var server: SensorFeedbackServer? = null
    private var isAlive: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startServer() {
        if(isAlive) return
        server = if (RUN_ON_EMULATOR)
            SensorFeedbackServer(model, "localhost", 8090)
        else
            SensorFeedbackServer(
                model,
                ip,
                port
            )
        server?.start()
        ip = server?.ip ?: RCControllerApplication.instance!!.myIP
        port = server?.port ?: port
        isAlive = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopServer() {
        if (!isAlive) return
        server?.stop()
        isAlive = false
    }

    fun changeServerState(keepAlive: Boolean) {
        if(keepAlive && !isAlive) startServer()
        else if(!keepAlive && isAlive) stopServer()
    }

    /* I don't have a public constructor for the server because I don't
    need to know more info about the server. The only thing I want to do is
    to start and stop the server.
    If for any reason I make this constructor public I have to decide at the
    instantiation point if I want to run a server on an emulator or in a device
    and this has nothing to do with the whole application's development process
    and should not be left into the developer's decision.
    I made this procedure automatic to help me at development in different computers
    so this process must be as secret as possible to other classes,
    apart from this, there are no advantages for other classes knowing that the app is
    running on an emulator or in a real device.
     */
    private class SensorFeedbackServer(
        private val model: RCControllerViewModel,
        val ip: String,
        val port: Int
    ) : NanoHTTPD(ip, port) {

        private var receivedSpeed: String? = "0"
        private var publishedSpeed = "0"

        override fun serve(session: IHTTPSession): Response {
            val params = session.parameters
            val uri = session.uri
            // TODO instead of id (example: "unchanged") use the name (example: UNCHANGED) of the enums
            return newFixedLengthResponse(
                when (uri) {
                    TEMP_URI -> {
                        when (params[TEMP_PARAM_KEY_ITEM]?.get(0)) {
                            CarPart.Thermometer.MOTOR_REAR_LEFT.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.MOTOR_REAR_LEFT.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.MOTOR_REAR_RIGHT.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.MOTOR_REAR_RIGHT.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.MOTOR_FRONT_LEFT.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.MOTOR_FRONT_LEFT.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.MOTOR_FRONT_RIGHT.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.MOTOR_FRONT_RIGHT.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.H_BRIDGE_REAR.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.H_BRIDGE_REAR.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.H_BRIDGE_FRONT.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.H_BRIDGE_FRONT.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.RASPBERRY_PI.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.raspberryPiTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.RASPBERRY_PI.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.BATTERIES.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.batteriesTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.BATTERIES.name, warningType.name
                                )
                            }
                            CarPart.Thermometer.SHIFT_REGISTERS.name -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.shiftRegistersTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPart.Thermometer.SHIFT_REGISTERS.name, warningType.name
                                )
                            }
                            else ->
                                formatResponse("ERROR TEMP", TemperatureWarningType.NOTHING.id)
                        }
                    }
                    SPEED_URI -> {
                        receivedSpeed = params[SPEED_PARAM_KEY_VALUE]?.get(0)
                        publishedSpeed = receivedSpeed ?: publishedSpeed
                        model.speedLiveData.postValue(publishedSpeed)
                        formatResponse("SPEED", publishedSpeed)
                    }
                    ECU_URI -> {
                        when (params[ECU_PARAM_KEY_ITEM]?.get(0)) {
                            CarModule.TRACTION_CONTROL.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    tcmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.TRACTION_CONTROL.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            CarModule.ANTILOCK_BRAKING.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    abmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.ANTILOCK_BRAKING.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            CarModule.ELECTRONIC_STABILITY.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    esmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.ELECTRONIC_STABILITY.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            CarModule.UNDERSTEER_DETECTION.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    udmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.UNDERSTEER_DETECTION.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            CarModule.OVERSTEER_DETECTION.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    odmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.OVERSTEER_DETECTION.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            CarModule.COLLISION_DETECTION.id -> {
                                /*activity.updateAdvancedSensorUIItems(
                                    cdmState = params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: MODULE_UNCHANGED_STATE
                                )*/
                                formatResponse(
                                    CarModule.COLLISION_DETECTION.id,
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.id
                                )
                            }
                            else ->
                                formatResponse("ERROR ECU", ModuleState.NOTHING.id)
                        }
                    }
                    else ->
                        formatResponse("ERROR SERVE", EMPTY_STRING)
                }
            )
        }
    }

    companion object {
        val RUN_ON_EMULATOR: Boolean by lazy { Build.FINGERPRINT.contains("generic") }

        var ip = RCControllerApplication.instance?.myIP ?: EMPTY_STRING
            private set
        var port = 8080
            private set

        const val TEMP_URI = "/temp"
        const val TEMP_PARAM_KEY_ITEM = "item"
        const val TEMP_PARAM_KEY_WARNING = "warning"
        const val TEMP_PARAM_KEY_VALUE = "value"

        const val SPEED_URI = "/speed"
        const val SPEED_PARAM_KEY_VALUE = "value"

        const val ECU_URI = "/ecu"
        const val ECU_PARAM_KEY_ITEM = "item"
        const val ECU_PARAM_KEY_VALUE = "value"

        fun formatResponse(item: String, warningType: String, delimiter: String = ":") =
            String.format("%s $delimiter %s", item, warningType)
    }
}