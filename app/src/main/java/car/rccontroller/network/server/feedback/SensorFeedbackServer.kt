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

    private val motorRearRightTemp = "motor_rear_right_temp"
    private val motorFrontLeftTemp = "motor_front_left_temp"
    private val motorFrontRightTemp = "motor_front_right_temp"
    private val hBridgeRearTemp = "h_bridge_rear_temp"
    private val hBridgeFrontTemp = "h_bridge_front_temp"
    private val raspberryPiTemp = "raspberry_pi_temp"
    private val batteriesTemp = "batteries_temp"
    private val shiftRegistersTemp = "shift_registers_temp"

    private val speedUri = "/speed"
    private val speedParamKeyValue = "value"
    private var receivedSpeed: String? = "0"
    private var publishedSpeed = "0"

    private val ecuUri = "/ecu"
    private val ecuParamKeyItem = "item"
    private val ecuParamKeyValue = "value"
    private val tractionControlModule = "TCM"
    private val antilockBrakingModule = "ABM"
    private val electronicStabilityModule = "ESM"
    private val understeerDetectionModule = "UDM"
    private val oversteerDetectionModule = "ODM"
    private val collisionDetectionModule = "CDM"

    override fun serve(session: IHTTPSession): Response {
        val params = session.parameters
        val uri = session.uri

        when (uri) {
            TEMP_URI -> {
                when (params[TEMP_PARAM_KEY_ITEM]?.get(0)) {
                    MOTOR_REAR_LEFT_TEMP -> { activity.updateTempUIItems(
                        rearLeftMotor = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    return newFixedLengthResponse(formatResponse(MOTOR_REAR_LEFT_TEMP, params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED))
                    }
                    motorRearRightTemp -> activity.updateTempUIItems(
                        rearRightMotor = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    motorFrontLeftTemp -> activity.updateTempUIItems(
                        frontLeftMotor = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    motorFrontRightTemp -> activity.updateTempUIItems(
                        frontRightMotor = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    hBridgeRearTemp -> activity.updateTempUIItems(
                        rearHBridge = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    hBridgeFrontTemp -> activity.updateTempUIItems(
                        frontHBridge = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    raspberryPiTemp -> activity.updateTempUIItems(
                        raspberryPi = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    batteriesTemp -> activity.updateTempUIItems(
                        batteries = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                    shiftRegistersTemp -> activity.updateTempUIItems(
                        shiftRegisters = params[TEMP_PARAM_KEY_WARNING]?.get(0) ?: WARNING_TYPE_UNCHANGED
                    )
                }
            }
            speedUri -> {
                receivedSpeed = params[speedParamKeyValue]?.get(0)
                publishedSpeed = receivedSpeed ?: publishedSpeed
                activity.updateSpeedUIItem(publishedSpeed)
            }
            ecuUri -> {
                when (params[ecuParamKeyItem]?.get(0)) {
                    tractionControlModule -> activity.updateAdvancedSensorUIItems(
                        tcmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
                    )
                    antilockBrakingModule -> activity.updateAdvancedSensorUIItems(
                        abmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
                    )
                    electronicStabilityModule -> activity.updateAdvancedSensorUIItems(
                        esmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
                    )
                    understeerDetectionModule -> activity.updateAdvancedSensorUIItems(
                        udmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
                    )
                    oversteerDetectionModule -> activity.updateAdvancedSensorUIItems(
                        odmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
                    )
                    collisionDetectionModule -> activity.updateAdvancedSensorUIItems(
                        cdmState = params[ecuParamKeyValue]?.get(0) ?: MODULE_UNCHANGED_STATE
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

        const val MOTOR_REAR_LEFT_TEMP = "motor_rear_left_temp"
        const val TEMP_URI = "/temp"
        const val TEMP_PARAM_KEY_ITEM = "item"
        const val TEMP_PARAM_KEY_WARNING = "warning"
        const val TEMP_PARAM_KEY_VALUE = "value"

        fun formatResponse(item: String, warningType: String) =
            String.format("%s : %s", item, warningType)
    }
}