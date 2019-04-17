package car.rccontroller.network.server.feedback

import car.rccontroller.network.EMPTY_STRING
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import car.rccontroller.network.runBlockingRequest

// Start raspi server, start the app, start the engine, close raspi server, run these tests
@RunWith(MockitoJUnitRunner::class)
class NanoHTTPDLifecycleAwareTest {

    private val ip = "localhost" // ip for real device, localhost for emulator
    private val port = "8090" // 8080 for real device, 8090 for emulator

    ////////
    //serve function tests
    ////////
    @Test
    fun `validate no option in serve`() {
        val ret = runBlockingRequest("http://$ip:$port")
        assertThat(ret, `is`(NanoHTTPDLifecycleAware.formatResponse("ERROR SERVE", EMPTY_STRING)))
    }
    // temperature - motors
    private fun doTempRequest(hardware: String = "", warning: String = "") =
        runBlockingRequest(
            "http://" +
            "$ip:$port" +
                    NanoHTTPDLifecycleAware.TEMP_URI +
            "?${NanoHTTPDLifecycleAware.TEMP_PARAM_KEY_ITEM}=$hardware" +
            "&${NanoHTTPDLifecycleAware.TEMP_PARAM_KEY_WARNING}=$warning" +
            "&${NanoHTTPDLifecycleAware.TEMP_PARAM_KEY_VALUE}=10"
        )
    @Test
    fun `validate error temperatures`() {
        val ret = doTempRequest()
        assertThat(ret, `is`(NanoHTTPDLifecycleAware.formatResponse("ERROR TEMP",
            NanoHTTPDLifecycleAware.WARNING_TYPE_NOTHING)))
    }
    @Test
    fun `validate normal rear left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
                `is`(
                    NanoHTTPDLifecycleAware.formatResponse(
                        NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
                        NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_REAR_LEFT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal rear right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_REAR_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front left motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_LEFT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front right motor temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.MOTOR_FRONT_RIGHT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    // temperature - h bridges
    @Test
    fun `validate normal rear h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium rear h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high rear h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_REAR_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    @Test
    fun `validate normal front h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium front h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high front h bridge temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.H_BRIDGE_FRONT_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    // temperature - raspberry
    @Test
    fun `validate normal raspberry temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium raspberry temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high raspberry temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.RASPBERRY_PI_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    // temperature - batteries
    @Test
    fun `validate normal batteries temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.BATTERIES_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.BATTERIES_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium batteries temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.BATTERIES_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.BATTERIES_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high batteries temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.BATTERIES_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.BATTERIES_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    // temperature - shift registers
    @Test
    fun `validate normal shift registers temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_NORMAL
                )))
    }
    @Test
    fun `validate medium shift registers temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_MEDIUM
                )))
    }
    @Test
    fun `validate high shift registers temperature`() {
        val ret = doTempRequest(NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
            NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.SHIFT_REGISTER_TEMP,
                    NanoHTTPDLifecycleAware.WARNING_TYPE_HIGH
                )))
    }
    // for speed
    @Test
    fun `validate speed`() {
        val ret = runBlockingRequest(
            "http://" +
                    "$ip:$port" +
                    NanoHTTPDLifecycleAware.SPEED_URI +
                    "?${NanoHTTPDLifecycleAware.SPEED_PARAM_KEY_VALUE}=150"
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    "SPEED","150"
                )))
    }
    // for ecu modules
    private fun doEcuRequest(hardware: String = "", value: String = "") =
        runBlockingRequest(
            "http://" +
                    "$ip:$port" +
                    NanoHTTPDLifecycleAware.ECU_URI +
                    "?${NanoHTTPDLifecycleAware.ECU_PARAM_KEY_ITEM}=$hardware" +
                    "&${NanoHTTPDLifecycleAware.TEMP_PARAM_KEY_VALUE}=$value"
        )
    @Test
    fun `validate error in ecu modules`() {
        val ret = doEcuRequest()
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse("ERROR ECU",
                    NanoHTTPDLifecycleAware.MODULE_NOTHING_STATE
                )))
    }
    @Test
    fun `validate traction control module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.TRACTION_CONTROL_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.TRACTION_CONTROL_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate traction control module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.TRACTION_CONTROL_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.TRACTION_CONTROL_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate antilock braking module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.ANTILOCK_BRAKING_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.ANTILOCK_BRAKING_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate antilock braking module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.ANTILOCK_BRAKING_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.ANTILOCK_BRAKING_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate electronic stability module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.ELECTRONIC_STABILITY_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.ELECTRONIC_STABILITY_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate electronic stability module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.ELECTRONIC_STABILITY_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.ELECTRONIC_STABILITY_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate understeer detection module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.UNDERSTEER_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.UNDERSTEER_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate understeer detection module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.UNDERSTEER_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.UNDERSTEER_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate oversteer detection module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.OVERSTEER_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.OVERSTEER_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate oversteer detection module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.OVERSTEER_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.OVERSTEER_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
    @Test
    fun `validate collision detection module is on`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.COLLISION_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_ON_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.COLLISION_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_ON_STATE
                )))
    }
    @Test
    fun `validate collision detection module is idle`() {
        val ret = doEcuRequest(NanoHTTPDLifecycleAware.COLLISION_DETECTION_MODULE,
            NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    NanoHTTPDLifecycleAware.COLLISION_DETECTION_MODULE,
                    NanoHTTPDLifecycleAware.MODULE_IDLE_STATE
                )))
    }
}