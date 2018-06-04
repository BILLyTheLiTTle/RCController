package car.rccontroller.network

import car.rccontroller.RCControllerActivity
import fi.iki.elonen.NanoHTTPD

// constructor default parameters are for emulator
class Server(
    private val activity: RCControllerActivity,
    val ip: String = "localhost",
    val port: Int = 8081
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

    override fun serve(session: IHTTPSession): Response {
        val params = session.parms
        val uri = session.uri

        when (uri) {
            TEMP_URI -> {
                when (params[TEMP_PARAM_KEY_ITEM]) {
                    MOTOR_REAR_LEFT_TEMP -> activity.updateTempUIItems(
                        rearLeftMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    MOTOR_REAR_RIGHT_TEMP -> activity.updateTempUIItems(
                        rearRightMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    MOTOR_FRONT_LEFT_TEMP -> activity.updateTempUIItems(
                        frontLeftMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    MOTOR_FRONT_RIGHT_TEMP -> activity.updateTempUIItems(
                        frontRightMotor = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    H_BRIDGE_REAR_TEMP -> activity.updateTempUIItems(
                        rearHBridge = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    H_BRIDGE_FRONT_TEMP -> activity.updateTempUIItems(
                        frontHBridge = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    RASPBERRY_PI_TEMP -> activity.updateTempUIItems(
                        raspberryPi = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    BATTERIES_TEMP -> activity.updateTempUIItems(
                        batteries = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
                    SHIFT_REGISTERS_TEMP -> activity.updateTempUIItems(
                        shiftRegisters = params[TEMP_PARAM_KEY_WARNING] ?: WARNING_TYPE_UNCHANGED)
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
    }
}