package car.feedback.server

import android.os.Build
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import car.RCControllerApplication
import car.rccontroller.RCControllerViewModel
import car.feedback.*
import fi.iki.elonen.NanoHTTPD

class NanoHTTPDLifecycleAware(private val model: RCControllerViewModel): LifecycleObserver {
    private var server: SensorFeedbackServer? = null
    private var isAlive: Boolean = false

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startServer() {
        if(isAlive) return
        server = if (RUN_ON_EMULATOR)
            SensorFeedbackServer(
                model,
                "localhost",
                8090
            )
        else
            SensorFeedbackServer(
                model,
                ip,
                port
            )
        server?.start()
        ip = server?.ip ?: RCControllerApplication.instance!!.myIP
        port = server?.port ?:
                port
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
                            TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.rearLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.rearRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.frontLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.frontRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.H_BRIDGE_REAR_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.frontHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.H_BRIDGE_FRONT_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.rearHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.RASPBERRY_PI_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.raspberryPiTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.RASPBERRY_PI_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.BATTERIES_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.batteriesTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.BATTERIES_TEMP.name,
                                    warningType.name
                                )
                            }
                            TemperatureDevice.SHIFT_REGISTERS_TEMP.name -> {
                                val warningType = TemperatureWarning.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarning.UNCHANGED_TEMPERATURE.name
                                )
                                model.shiftRegistersTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
                                    warningType.name
                                )
                            }
                            else ->
                                formatResponse(
                                    "ERROR TEMP",
                                    TemperatureWarning.NOTHING_TEMPERATURE.name
                                )
                        }
                    }
                    SPEED_URI -> {
                        receivedSpeed = params[SPEED_PARAM_KEY_VALUE]?.get(0)
                        publishedSpeed = receivedSpeed ?: publishedSpeed
                        model.speedLiveData.postValue(publishedSpeed)
                        formatResponse(
                            "SPEED",
                            publishedSpeed
                        )
                    }
                    ECU_URI -> {
                        when (params[ECU_PARAM_KEY_ITEM]?.get(0)) {
                            CarModule.TRACTION_CONTROL.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.tractionControlModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.TRACTION_CONTROL.name,
                                    state.name
                                )
                            }
                            CarModule.ANTILOCK_BRAKING.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.antilockBrakingModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.ANTILOCK_BRAKING.name,
                                    state.name
                                )
                            }
                            CarModule.ELECTRONIC_STABILITY.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.electronicStabilityModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.ELECTRONIC_STABILITY.name,
                                    state.name
                                )
                            }
                            CarModule.UNDERSTEER_DETECTION.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.understeerDetectionModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.UNDERSTEER_DETECTION.name,
                                    state.name
                                )
                            }
                            CarModule.OVERSTEER_DETECTION.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.oversteerDetectionModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.OVERSTEER_DETECTION.name,
                                    state.name
                                )
                            }
                            CarModule.COLLISION_DETECTION.name -> {
                                val state = ModuleState.valueOf(
                                    params[ECU_PARAM_KEY_VALUE]
                                        ?.get(0) ?: ModuleState.UNCHANGED.name
                                )
                                model.collisionDetectionModuleLiveData.postValue(state)

                                formatResponse(
                                    CarModule.COLLISION_DETECTION.name,
                                    state.name
                                )
                            }
                            else ->
                                formatResponse(
                                    "ERROR ECU",
                                    ModuleState.NOTHING.id
                                )
                        }
                    }
                    else ->
                        formatResponse(
                            "ERROR SERVE",
                            EMPTY_STRING
                        )
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

enum class TemperatureDevice {
    BATTERIES_TEMP,
    H_BRIDGE_FRONT_TEMP, H_BRIDGE_REAR_TEMP,
    MOTOR_FRONT_LEFT_TEMP, MOTOR_FRONT_RIGHT_TEMP, MOTOR_REAR_LEFT_TEMP, MOTOR_REAR_RIGHT_TEMP,
    RASPBERRY_PI_TEMP,
    SHIFT_REGISTERS_TEMP
}

// TODO Module
enum class CarModule(val id: String) {
    TRACTION_CONTROL("TCM"),
    ANTILOCK_BRAKING("ABM"),
    ELECTRONIC_STABILITY("ESM"),
    UNDERSTEER_DETECTION("UDM"),
    OVERSTEER_DETECTION("ODM"),
    COLLISION_DETECTION("CDM")
}

enum class ModuleState(val id: String) {
    NOTHING(EMPTY_STRING), // TODO NOTHING_STATE, etc for the others
    OFF("module_off_state"),
    ON("module_on_state"),
    IDLE("module_idle_state"),
    UNCHANGED("module_unchanged_state")
}

enum class TemperatureWarning {
    NOTHING_TEMPERATURE, UNCHANGED_TEMPERATURE, NORMAL_TEMPERATURE, MEDIUM_TEMPERATURE, HIGH_TEMPERATURE
}