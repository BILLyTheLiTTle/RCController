package car.rccontroller.network.server.feedback

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import car.rccontroller.RCControllerApplication
import car.rccontroller.RCControllerViewModel
import car.rccontroller.RUN_ON_EMULATOR
import car.rccontroller.network.*
import car.rccontroller.network.server.feedback.data.CarModule
import car.rccontroller.network.server.feedback.data.CarPartTemperature
import car.rccontroller.network.server.feedback.data.ModuleState
import car.rccontroller.network.server.feedback.data.TemperatureWarningType
import fi.iki.elonen.NanoHTTPD


class NanoHTTPDLifecycleAware(private val model: RCControllerViewModel): LifecycleObserver {
    private var server: SensorFeedbackServer? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startServer() {
        server = if (RUN_ON_EMULATOR)
            SensorFeedbackServer(model)
        else
            SensorFeedbackServer(
            model,
            ip,
            port
        )
        server?.start()
        ip = server?.ip ?: RCControllerApplication.instance!!.myIP
        port = server?.port ?: port
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopServer() = server?.stop()

    // constructor default parameters are for emulator
    private class SensorFeedbackServer(
        private val model: RCControllerViewModel,
        val ip: String = "localhost",
        val port: Int = 8090
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
                            CarPartTemperature.MOTOR_REAR_LEFT.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.MOTOR_REAR_LEFT.id, warningType.name
                                )
                            }
                            CarPartTemperature.MOTOR_REAR_RIGHT.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.MOTOR_REAR_RIGHT.id, warningType.name
                                )
                            }
                            CarPartTemperature.MOTOR_FRONT_LEFT.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontLeftMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.MOTOR_FRONT_LEFT.id, warningType.name
                                )
                            }
                            CarPartTemperature.MOTOR_FRONT_RIGHT.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontRightMotorTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.MOTOR_FRONT_RIGHT.id, warningType.name
                                )
                            }
                            CarPartTemperature.H_BRIDGE_REAR.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.frontHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.H_BRIDGE_REAR.id, warningType.name
                                )
                            }
                            CarPartTemperature.H_BRIDGE_FRONT.id -> {
                                val warningType = TemperatureWarningType.valueOf(
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.name
                                )
                                model.rearHBridgeTemperatureLiveData.postValue(warningType)

                                formatResponse(
                                    CarPartTemperature.H_BRIDGE_FRONT.id, warningType.name
                                )
                            }
                            CarPartTemperature.RASPBERRY_PI.id -> {
                                /*activity.updateTempUIItems(
                                    raspberryPi = params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: WARNING_TYPE_UNCHANGED
                                )*/
                                formatResponse(
                                    CarPartTemperature.RASPBERRY_PI.id,
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.id
                                )
                            }
                            CarPartTemperature.BATTERIES.id -> {
                                /*activity.updateTempUIItems(
                                    batteries = params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: WARNING_TYPE_UNCHANGED
                                )*/
                                formatResponse(
                                    CarPartTemperature.BATTERIES.id,
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.id
                                )
                            }
                            CarPartTemperature.SHIFT_REGISTERS.id -> {
                                /*activity.updateTempUIItems(
                                    shiftRegisters = params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: WARNING_TYPE_UNCHANGED
                                )*/
                                formatResponse(
                                    CarPartTemperature.SHIFT_REGISTERS.id,
                                    params[TEMP_PARAM_KEY_WARNING]
                                        ?.get(0) ?: TemperatureWarningType.UNCHANGED.id
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