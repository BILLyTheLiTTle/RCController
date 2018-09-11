package car.rccontroller.network.server.feedback

import car.rccontroller.RCControllerActivity
import car.rccontroller.network.EMPTY_STRING
import car.rccontroller.network.OK_STRING
import fi.iki.elonen.NanoHTTPD

// constructor default parameters are for emulator
class SensorFeedbackServer(
    private val activity: RCControllerActivity,
    val ip: String = "localhost",
    val port: Int = 8090
) : NanoHTTPD(ip, port) {

    private var receivedSpeed: String? = "0"
    private var publishedSpeed = "0"

    override fun serve(session: IHTTPSession): Response {
        val params = session.parameters
        val uri = session.uri

        return newFixedLengthResponse(
            when (uri) {
                TEMP_URI -> {
                    when (params[TEMP_PARAM_KEY_ITEM]?.get(0)) {
                        MOTOR_REAR_LEFT_TEMP -> {
                            activity.updateTempUIItems(
                                rearLeftMotor = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(
                                MOTOR_REAR_LEFT_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        MOTOR_REAR_RIGHT_TEMP -> {
                            activity.updateTempUIItems(
                                rearRightMotor = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(MOTOR_REAR_RIGHT_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        MOTOR_FRONT_LEFT_TEMP -> {
                            activity.updateTempUIItems(
                                frontLeftMotor = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(MOTOR_FRONT_LEFT_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        MOTOR_FRONT_RIGHT_TEMP -> {
                            activity.updateTempUIItems(
                                frontRightMotor = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(MOTOR_FRONT_RIGHT_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        H_BRIDGE_REAR_TEMP -> {
                            activity.updateTempUIItems(
                                rearHBridge = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(H_BRIDGE_REAR_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        H_BRIDGE_FRONT_TEMP -> {
                            activity.updateTempUIItems(
                                frontHBridge = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(H_BRIDGE_FRONT_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        RASPBERRY_PI_TEMP -> {
                            activity.updateTempUIItems(
                                raspberryPi = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(RASPBERRY_PI_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        BATTERIES_TEMP -> {
                            activity.updateTempUIItems(
                                batteries = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(BATTERIES_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        SHIFT_REGISTER_TEMP -> {
                            activity.updateTempUIItems(
                                shiftRegisters = params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                            formatResponse(SHIFT_REGISTER_TEMP,
                                params[TEMP_PARAM_KEY_WARNING]
                                    ?.get(0) ?: WARNING_TYPE_UNCHANGED
                            )
                        }
                        else ->
                            formatResponse("ERROR TEMP", WARNING_TYPE_NOTHING)
                    }
                }
                SPEED_URI -> {
                    receivedSpeed = params[SPEED_PARAM_KEY_VALUE]?.get(0)
                    publishedSpeed = receivedSpeed ?: publishedSpeed
                    activity.updateSpeedUIItem(publishedSpeed)
                    formatResponse("SPEED", publishedSpeed)
                }
                ECU_URI -> {
                    when (params[ECU_PARAM_KEY_ITEM]?.get(0)) {
                        TRACTION_CONTROL_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                tcmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                TRACTION_CONTROL_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        ANTILOCK_BRAKING_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                abmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                ANTILOCK_BRAKING_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        ELECTRONIC_STABILITY_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                esmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                ELECTRONIC_STABILITY_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        UNDERSTEER_DETECTION_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                udmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                UNDERSTEER_DETECTION_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        OVERSTEER_DETECTION_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                odmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                OVERSTEER_DETECTION_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        COLLISION_DETECTION_MODULE -> {
                            activity.updateAdvancedSensorUIItems(
                                cdmState = params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                            formatResponse(
                                COLLISION_DETECTION_MODULE,
                                params[ECU_PARAM_KEY_VALUE]
                                    ?.get(0) ?: MODULE_UNCHANGED_STATE
                            )
                        }
                        else ->
                            formatResponse("ERROR ECU", MODULE_NOTHING_STATE)
                    }
                }
                else ->
                    formatResponse("ERROR SERVE", EMPTY_STRING)
            }
        )
    }


    companion object {
        const val WARNING_TYPE_NOTHING = EMPTY_STRING
        const val WARNING_TYPE_UNCHANGED = "unchanged"
        const val WARNING_TYPE_NORMAL = "normal"
        const val WARNING_TYPE_MEDIUM = "medium"
        const val WARNING_TYPE_HIGH = "high"

        const val MODULE_NOTHING_STATE = EMPTY_STRING
        const val MODULE_OFF_STATE = "module_off_state"
        const val MODULE_ON_STATE = "module_on_state"
        const val MODULE_IDLE_STATE = "module_idle_state"
        const val MODULE_UNCHANGED_STATE = "module_unchanged_state"

        const val MOTOR_REAR_LEFT_TEMP = "motor_rear_left_temp"
        const val MOTOR_REAR_RIGHT_TEMP = "motor_rear_right_temp"
        const val MOTOR_FRONT_LEFT_TEMP = "motor_front_left_temp"
        const val MOTOR_FRONT_RIGHT_TEMP = "motor_front_right_temp"
        const val H_BRIDGE_REAR_TEMP = "h_bridge_rear_temp"
        const val H_BRIDGE_FRONT_TEMP = "h_bridge_front_temp"
        const val RASPBERRY_PI_TEMP = "raspberry_pi_temp"
        const val BATTERIES_TEMP = "batteries_temp"
        const val SHIFT_REGISTER_TEMP = "shift_registers_temp"
        const val TEMP_URI = "/temp"
        const val TEMP_PARAM_KEY_ITEM = "item"
        const val TEMP_PARAM_KEY_WARNING = "warning"
        const val TEMP_PARAM_KEY_VALUE = "value"

        const val SPEED_URI = "/speed"
        const val SPEED_PARAM_KEY_VALUE = "value"

        const val ECU_URI = "/ecu"
        const val ECU_PARAM_KEY_ITEM = "item"
        const val ECU_PARAM_KEY_VALUE = "value"
        const val TRACTION_CONTROL_MODULE = "TCM"
        const val ANTILOCK_BRAKING_MODULE = "ABM"
        const val ELECTRONIC_STABILITY_MODULE = "ESM"
        const val UNDERSTEER_DETECTION_MODULE = "UDM"
        const val OVERSTEER_DETECTION_MODULE = "ODM"
        const val COLLISION_DETECTION_MODULE = "CDM"

        fun formatResponse(item: String, warningType: String, delimiter: String = ":") =
            String.format("%s $delimiter %s", item, warningType)
    }
}