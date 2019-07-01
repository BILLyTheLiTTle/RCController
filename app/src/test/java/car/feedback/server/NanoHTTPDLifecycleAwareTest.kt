package car.feedback.server

import car.feedback.EMPTY_STRING
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import car.feedback.runBlockingRequest

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
    private fun doTempRequest(hardware: String, warning: String) =
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
        val ret = doTempRequest("", "")
        assertThat(ret, `is`(
            NanoHTTPDLifecycleAware.formatResponse("ERROR TEMP",
                TemperatureWarning.NOTHING_TEMPERATURE.name)))
    }
    @Test
    fun `validate normal rear left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
                `is`(
                    NanoHTTPDLifecycleAware.formatResponse(
                        TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
                        TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium rear left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high rear left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_REAR_LEFT_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate normal rear right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium rear right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high rear right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_REAR_RIGHT_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate normal front left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium front left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high front left motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_LEFT_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate normal front right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium front right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high front right motor temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.MOTOR_FRONT_RIGHT_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    // temperature - h bridges
    @Test
    fun `validate normal rear h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium rear h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high rear h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_REAR_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate normal front h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium front h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high front h bridge temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.H_BRIDGE_FRONT_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    // temperature - raspberry
    @Test
    fun `validate normal raspberry temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.RASPBERRY_PI_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.RASPBERRY_PI_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium raspberry temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.RASPBERRY_PI_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.RASPBERRY_PI_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high raspberry temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.RASPBERRY_PI_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.RASPBERRY_PI_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    // temperature - batteries
    @Test
    fun `validate normal batteries temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.BATTERIES_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.BATTERIES_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium batteries temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.BATTERIES_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.BATTERIES_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high batteries temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.BATTERIES_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.BATTERIES_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
                )))
    }
    // temperature - shift registers
    @Test
    fun `validate normal shift registers temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
            TemperatureWarning.NORMAL_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
                    TemperatureWarning.NORMAL_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate medium shift registers temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
            TemperatureWarning.MEDIUM_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
                    TemperatureWarning.MEDIUM_TEMPERATURE.name
                )))
    }
    @Test
    fun `validate high shift registers temperature`() {
        val ret = doTempRequest(
            TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
            TemperatureWarning.HIGH_TEMPERATURE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    TemperatureDevice.SHIFT_REGISTERS_TEMP.name,
                    TemperatureWarning.HIGH_TEMPERATURE.name
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
                    ModuleState.NOTHING_STATE.name
                )))
    }
    @Test
    fun `validate traction control module is on`() {
        val ret = doEcuRequest(
            Module.TRACTION_CONTROL.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.TRACTION_CONTROL.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate traction control module is idle`() {
        val ret = doEcuRequest(
            Module.TRACTION_CONTROL.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.TRACTION_CONTROL.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
    @Test
    fun `validate antilock braking module is on`() {
        val ret = doEcuRequest(
            Module.ANTILOCK_BRAKING.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.ANTILOCK_BRAKING.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate antilock braking module is idle`() {
        val ret = doEcuRequest(
            Module.ANTILOCK_BRAKING.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.ANTILOCK_BRAKING.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
    @Test
    fun `validate electronic stability module is on`() {
        val ret = doEcuRequest(
            Module.ELECTRONIC_STABILITY.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.ELECTRONIC_STABILITY.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate electronic stability module is idle`() {
        val ret = doEcuRequest(
            Module.ELECTRONIC_STABILITY.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.ELECTRONIC_STABILITY.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
    @Test
    fun `validate understeer detection module is on`() {
        val ret = doEcuRequest(
            Module.UNDERSTEER_DETECTION.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.UNDERSTEER_DETECTION.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate understeer detection module is idle`() {
        val ret = doEcuRequest(
            Module.UNDERSTEER_DETECTION.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.UNDERSTEER_DETECTION.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
    @Test
    fun `validate oversteer detection module is on`() {
        val ret = doEcuRequest(
            Module.OVERSTEER_DETECTION.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.OVERSTEER_DETECTION.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate oversteer detection module is idle`() {
        val ret = doEcuRequest(
            Module.OVERSTEER_DETECTION.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.OVERSTEER_DETECTION.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
    @Test
    fun `validate collision detection module is on`() {
        val ret = doEcuRequest(
            Module.COLLISION_DETECTION.name,
            ModuleState.ON_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.COLLISION_DETECTION.name,
                    ModuleState.ON_STATE.name
                )))
    }
    @Test
    fun `validate collision detection module is idle`() {
        val ret = doEcuRequest(
            Module.COLLISION_DETECTION.name,
            ModuleState.IDLE_STATE.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    Module.COLLISION_DETECTION.name,
                    ModuleState.IDLE_STATE.name
                )))
    }
}