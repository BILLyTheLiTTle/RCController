package car.rccontroller.network.server.feedback

import android.util.Log
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

    private val TEMP_URI = "/temp"
    private val TEMP_PARAM_KEY_ITEM = "item"
    private val TEMP_PARAM_KEY_WARNING = "warning"
    private val TEMP_PARAM_KEY_VALUE = "value"
    private val MOTOR_REAR_LEFT_TEMP = "motor_rear_left_temp"
    private val MOTOR_REAR_RIGHT_TEMP = "motor_rear_right_temp"
    private val MOTOR_FRONT_LEFT_TEMP = "motor_front_left_temp"
    private val MOTOR_FRONT_RIGHT_TEMP = "motor_front_right_temp"
    private val H_BRIDGE_REAR_TEMP = "h_bridge_rear_temp"
    private val H_BRIDGE_FRONT_TEMP = "h_bridge_front_temp"
    private val RASPBERRY_PI_TEMP = "raspberry_pi_temp"
    private val BATTERIES_TEMP = "batteries_temp"
    private val SHIFT_REGISTERS_TEMP = "shift_registers_temp"

    private val SPEED_URI = "/speed"
    private val SPEED_PARAM_KEY_VALUE = "value"
    private var receivedSpeed: String? = "0"
    private var publishedSpeed = "0"

    private val ECU_URI = "/ecu"
    private val ECU_PARAM_KEY_ITEM = "item"
    private val ECU_PARAM_KEY_VALUE = "value"
    private val TRACTION_CONTROL_MODULE = "TCM"
    private val ANTILOCK_BRAKING_MODULE = "ABM"
    private val ELECTRONIC_STABILITY_MODULE = "ESM"
    private val UNDERSTEER_DETECTION_MODULE = "UDM"
    private val OVERSTEER_DETECTION_MODULE = "ODM"
    private val COLLISION_DETECTION_MODULE = "CDM"

    override fun serve(session: IHTTPSession): Response {
        val params = session.parms
        val uri = session.uri

        when (uri) {
            TEMP_URI -> {
                when (params[TEMP_PARAM_KEY_ITEM]) {
                    MOTOR_REAR_LEFT_TEMP -> activity.updateTempUIItems(
                        rearLeftMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    MOTOR_REAR_RIGHT_TEMP -> activity.updateTempUIItems(
                        rearRightMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    MOTOR_FRONT_LEFT_TEMP -> activity.updateTempUIItems(
                        frontLeftMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    MOTOR_FRONT_RIGHT_TEMP -> activity.updateTempUIItems(
                        frontRightMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    H_BRIDGE_REAR_TEMP -> activity.updateTempUIItems(
                        rearHBridge = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    H_BRIDGE_FRONT_TEMP -> activity.updateTempUIItems(
                        frontHBridge = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    RASPBERRY_PI_TEMP -> activity.updateTempUIItems(
                        raspberryPi = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    BATTERIES_TEMP -> activity.updateTempUIItems(
                        batteries = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                    SHIFT_REGISTERS_TEMP -> activity.updateTempUIItems(
                        shiftRegisters = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED
                    )
                }
            }
            SPEED_URI -> {
                receivedSpeed = params[SPEED_PARAM_KEY_VALUE]
                publishedSpeed = receivedSpeed ?: publishedSpeed
                activity.updateSpeedUIItem(publishedSpeed)
            }
            ECU_URI -> {
                when (params[ECU_PARAM_KEY_ITEM]) {
                    TRACTION_CONTROL_MODULE -> activity.updateAdvancedSensorUIItems(
                        tcmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    ANTILOCK_BRAKING_MODULE -> activity.updateAdvancedSensorUIItems(
                        abmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    ELECTRONIC_STABILITY_MODULE -> activity.updateAdvancedSensorUIItems(
                        esmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    UNDERSTEER_DETECTION_MODULE -> activity.updateAdvancedSensorUIItems(
                        udmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    OVERSTEER_DETECTION_MODULE -> activity.updateAdvancedSensorUIItems(
                        odmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    COLLISION_DETECTION_MODULE -> activity.updateAdvancedSensorUIItems(
                        cdmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                }
            }
        }

        return newFixedLengthResponse(OK_STRING)
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
    }
}