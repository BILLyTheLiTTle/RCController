package car.rccontroller.network.server.feedback

import car.rccontroller.RCControllerActivity
import car.rccontroller.network.EMPTY_STRING
import car.rccontroller.network.OK_STRING
import fi.iki.elonen.NanoHTTPD

class AdvancedSensorFeedbackServer(
        private val activity: RCControllerActivity,
        val ip: String = "localhost",
        val port: Int = 8091
) : NanoHTTPD(ip, port) {

    private val ECU_URI = "/ecu"
    private val ECU_PARAM_KEY_ITEM = "item"
    private val ECU_PARAM_KEY_VALUE = "value"
    private val TRACTION_CONTROL_MODULE = "TCM"
    private val ANTILOCK_BRAKING_MODULE = "ABM"
    private val ELECTRONIC_STABILITY_MODULE = "ESM"
    private val UNDERSTEER_DETECTOR_MODULE = "UDM"
    private val OVERSTEER_DETECTOR_MODULE = "ODM"
    private val COLLISION_DETECTION_MODULE = "CDM"

    override fun serve(session: IHTTPSession): Response {
        val params = session.parms
        val uri = session.uri

        when (uri) {
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
                    UNDERSTEER_DETECTOR_MODULE -> activity.updateAdvancedSensorUIItems(
                            udmState = params[ECU_PARAM_KEY_VALUE] ?: MODULE_UNCHANGED_STATE
                    )
                    OVERSTEER_DETECTOR_MODULE -> activity.updateAdvancedSensorUIItems(
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
        const val MODULE_NOTHING_STATE = EMPTY_STRING
        const val MODULE_OFF_STATE = "module_off_state"
        const val MODULE_ON_STATE = "module_on_state"
        const val MODULE_IDLE_STATE = "module_idle_state"
        const val MODULE_UNCHANGED_STATE = "module_unchanged_state"
    }
}