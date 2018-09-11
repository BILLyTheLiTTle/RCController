package car.rccontroller.network.server.feedback

import car.rccontroller.network.EMPTY_STRING
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import car.rccontroller.network.doBlockingRequest

@RunWith(MockitoJUnitRunner::class)
class SensorFeedbackServerTest {

    private val ip = "localhost" // ip for real device, localhost for emulator
    private val port = "8090" // 8080 for real device, 8090 for emulator

    ////////
    //serve function tests
    ////////
    @Test
    fun `validate no option in serve`() {
        val ret = doBlockingRequest("http://$ip:$port")
        assertThat(ret, `is`(SensorFeedbackServer.formatResponse("ERROR SERVE", EMPTY_STRING)))
    }
    // temperature - motors
    private fun doTempRequest(hardware: String = "", warning: String = "") =
        doBlockingRequest(
            "http://" +
            "$ip:$port" +
            SensorFeedbackServer.TEMP_URI +
            "?${SensorFeedbackServer.TEMP_PARAM_KEY_ITEM}=$hardware" +
            "&${SensorFeedbackServer.TEMP_PARAM_KEY_WARNING}=$warning" +
            "&${SensorFeedbackServer.TEMP_PARAM_KEY_VALUE}=10"
        )
    @Test
    fun `validate error temperatures`() {
        val ret = doTempRequest()
        assertThat(ret, `is`(SensorFeedbackServer.formatResponse("ERROR TEMP",
            SensorFeedbackServer.WARNING_TYPE_NOTHING)))
    }
    @Test
    fun `validate normal rear left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
                `is`(
                    SensorFeedbackServer.formatResponse(
                        SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
                        SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal rear right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_REAR_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front left motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_LEFT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front right motor temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.MOTOR_FRONT_RIGHT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    // temperature - h bridges
    @Test
    fun `validate normal rear h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_REAR_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front h bridge temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.H_BRIDGE_FRONT_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    // temperature - raspberry
    @Test
    fun `validate normal raspberry temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.RASPBERRY_PI_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.RASPBERRY_PI_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium raspberry temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.RASPBERRY_PI_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.RASPBERRY_PI_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high raspberry temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.RASPBERRY_PI_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.RASPBERRY_PI_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    // temperature - batteries
    @Test
    fun `validate normal batteries temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.BATTERIES_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.BATTERIES_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium batteries temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.BATTERIES_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.BATTERIES_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high batteries temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.BATTERIES_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.BATTERIES_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    // temperature - shift registers
    @Test
    fun `validate normal shift registers temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.SHIFT_REGISTER_TEMP,
            SensorFeedbackServer.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.SHIFT_REGISTER_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium shift registers temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.SHIFT_REGISTER_TEMP,
            SensorFeedbackServer.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.SHIFT_REGISTER_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high shift registers temperature`() {
        val ret = doTempRequest(SensorFeedbackServer.SHIFT_REGISTER_TEMP,
            SensorFeedbackServer.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.SHIFT_REGISTER_TEMP,
                    SensorFeedbackServer.WARNING_TYPE_HIGH
                )))
    }
    // for speed
    @Test
    fun `validate speed`() {
        val ret = doBlockingRequest(
            "http://" +
                    "$ip:$port" +
                    SensorFeedbackServer.SPEED_URI +
                    "?${SensorFeedbackServer.SPEED_PARAM_KEY_VALUE}=150"
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    "SPEED","150"
                )))
    }
    // for ecu modules
    private fun doEcuRequest(hardware: String = "", value: String = "") =
        doBlockingRequest(
            "http://" +
                    "$ip:$port" +
                    SensorFeedbackServer.ECU_URI +
                    "?${SensorFeedbackServer.ECU_PARAM_KEY_ITEM}=$hardware" +
                    "&${SensorFeedbackServer.TEMP_PARAM_KEY_VALUE}=$value"
        )
    @Test
    fun `validate error in ecu modules`() {
        val ret = doEcuRequest()
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse("ERROR ECU",
                    SensorFeedbackServer.MODULE_NOTHING_STATE
                )))
    }
    @Test
    fun `validate traction control module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.TRACTION_CONTROL_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.TRACTION_CONTROL_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate traction control module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.TRACTION_CONTROL_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.TRACTION_CONTROL_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate antilock braking module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.ANTILOCK_BRAKING_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.ANTILOCK_BRAKING_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate antilock braking module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.ANTILOCK_BRAKING_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.ANTILOCK_BRAKING_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate electronic stability module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.ELECTRONIC_STABILITY_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.ELECTRONIC_STABILITY_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate electronic stability module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.ELECTRONIC_STABILITY_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.ELECTRONIC_STABILITY_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate understeer detection module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.UNDERSTEER_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.UNDERSTEER_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate understeer detection module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.UNDERSTEER_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.UNDERSTEER_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate oversteer detection module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.OVERSTEER_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.OVERSTEER_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate oversteer detection module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.OVERSTEER_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.OVERSTEER_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate collision detection module is on`() {
        val ret = doEcuRequest(SensorFeedbackServer.COLLISION_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.COLLISION_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate collision detection module is idle`() {
        val ret = doEcuRequest(SensorFeedbackServer.COLLISION_DETECTION_MODULE,
            SensorFeedbackServer.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                SensorFeedbackServer.formatResponse(
                    SensorFeedbackServer.COLLISION_DETECTION_MODULE,
                    SensorFeedbackServer.MODULE_IDLE_STATE
                )))
    }
}