package car.rccontroller.network

import android.content.Context
import android.util.Log
import car.rccontroller.RCControllerActivity
import fi.iki.elonen.NanoHTTPD

// constructor default parameters are for emulator
class Server(val activity: RCControllerActivity, val ip: String = "localhost", val port: Int = 8081) : NanoHTTPD(ip, port) {

    private val SUB_URL = "/temp"
    private val PARAM_KEY_ITEM = "item"
    private val PARAM_KEY_WARNING = "warning"
    private val PARAM_KEY_VALUE = "value"

    private val MOTOR_REAR_LEFT_TEMP = "motor_rear_left_temp"

    private var rearLeftMotor = WARNING_TYPE_NOTHING

    override fun serve(session: IHTTPSession): Response {
        val parms = session.parms
        Log.e("PARMS", "${parms[PARAM_KEY_ITEM]}, ${parms[PARAM_KEY_WARNING]}, ${parms[PARAM_KEY_VALUE]}")

        if (parms[PARAM_KEY_ITEM] == MOTOR_REAR_LEFT_TEMP)
            activity.updateTempUIItems(rearLeftMotor = parms[PARAM_KEY_WARNING]?: WARNING_TYPE_NOTHING)

        return newFixedLengthResponse(OK_STRING)
    }

    companion object {
        const val WARNING_TYPE_NOTHING = EMPTY_STRING
        const val WARNING_TYPE_NORMAL = "normal"
        const val WARNING_TYPE_MEDIUM = "medium"
        const val WARNING_TYPE_HIGH = "high"
    }
}