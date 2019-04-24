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
            TemperatureWarningType.NOTHING.name)))
    }
    @Test
    fun `validate normal rear left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_LEFT.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
                `is`(
                    NanoHTTPDLifecycleAware.formatResponse(
                        ThermometerDevice.MOTOR_REAR_LEFT.name,
                        TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium rear left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_LEFT.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_REAR_LEFT.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high rear left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_LEFT.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_REAR_LEFT.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    @Test
    fun `validate normal rear right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_RIGHT.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_REAR_RIGHT.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium rear right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_RIGHT.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_REAR_RIGHT.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high rear right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_REAR_RIGHT.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_REAR_RIGHT.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    @Test
    fun `validate normal front left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_LEFT.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_LEFT.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium front left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_LEFT.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_LEFT.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high front left motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_LEFT.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_LEFT.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    @Test
    fun `validate normal front right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_RIGHT.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_RIGHT.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium front right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_RIGHT.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_RIGHT.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high front right motor temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.MOTOR_FRONT_RIGHT.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.MOTOR_FRONT_RIGHT.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    // temperature - h bridges
    @Test
    fun `validate normal rear h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_REAR.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_REAR.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium rear h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_REAR.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_REAR.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high rear h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_REAR.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_REAR.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    @Test
    fun `validate normal front h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_FRONT.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_FRONT.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium front h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_FRONT.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_FRONT.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high front h bridge temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.H_BRIDGE_FRONT.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.H_BRIDGE_FRONT.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    // temperature - raspberry
    @Test
    fun `validate normal raspberry temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.RASPBERRY_PI.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.RASPBERRY_PI.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium raspberry temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.RASPBERRY_PI.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.RASPBERRY_PI.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high raspberry temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.RASPBERRY_PI.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.RASPBERRY_PI.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    // temperature - batteries
    @Test
    fun `validate normal batteries temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.BATTERIES.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.BATTERIES.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium batteries temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.BATTERIES.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.BATTERIES.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high batteries temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.BATTERIES.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.BATTERIES.name,
                    TemperatureWarningType.HIGH.name
                )))
    }
    // temperature - shift registers
    @Test
    fun `validate normal shift registers temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.SHIFT_REGISTERS.name,
            TemperatureWarningType.NORMAL.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.SHIFT_REGISTERS.name,
                    TemperatureWarningType.NORMAL.name
                )))
    }
    @Test
    fun `validate medium shift registers temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.SHIFT_REGISTERS.name,
            TemperatureWarningType.MEDIUM.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.SHIFT_REGISTERS.name,
                    TemperatureWarningType.MEDIUM.name
                )))
    }
    @Test
    fun `validate high shift registers temperature`() {
        val ret = doTempRequest(
            ThermometerDevice.SHIFT_REGISTERS.name,
            TemperatureWarningType.HIGH.name
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    ThermometerDevice.SHIFT_REGISTERS.name,
                    TemperatureWarningType.HIGH.name
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
                    ModuleState.NOTHING.id
                )))
    }
    @Test
    fun `validate traction control module is on`() {
        val ret = doEcuRequest(
            CarModule.TRACTION_CONTROL.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.TRACTION_CONTROL.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate traction control module is idle`() {
        val ret = doEcuRequest(
            CarModule.TRACTION_CONTROL.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.TRACTION_CONTROL.id,
                    ModuleState.IDLE.id
                )))
    }
    @Test
    fun `validate antilock braking module is on`() {
        val ret = doEcuRequest(
            CarModule.ANTILOCK_BRAKING.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.ANTILOCK_BRAKING.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate antilock braking module is idle`() {
        val ret = doEcuRequest(
            CarModule.ANTILOCK_BRAKING.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.ANTILOCK_BRAKING.id,
                    ModuleState.IDLE.id
                )))
    }
    @Test
    fun `validate electronic stability module is on`() {
        val ret = doEcuRequest(
            CarModule.ELECTRONIC_STABILITY.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.ELECTRONIC_STABILITY.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate electronic stability module is idle`() {
        val ret = doEcuRequest(
            CarModule.ELECTRONIC_STABILITY.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.ELECTRONIC_STABILITY.id,
                    ModuleState.IDLE.id
                )))
    }
    @Test
    fun `validate understeer detection module is on`() {
        val ret = doEcuRequest(
            CarModule.UNDERSTEER_DETECTION.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.UNDERSTEER_DETECTION.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate understeer detection module is idle`() {
        val ret = doEcuRequest(
            CarModule.UNDERSTEER_DETECTION.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.UNDERSTEER_DETECTION.id,
                    ModuleState.IDLE.id
                )))
    }
    @Test
    fun `validate oversteer detection module is on`() {
        val ret = doEcuRequest(
            CarModule.OVERSTEER_DETECTION.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.OVERSTEER_DETECTION.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate oversteer detection module is idle`() {
        val ret = doEcuRequest(
            CarModule.OVERSTEER_DETECTION.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.OVERSTEER_DETECTION.id,
                    ModuleState.IDLE.id
                )))
    }
    @Test
    fun `validate collision detection module is on`() {
        val ret = doEcuRequest(
            CarModule.COLLISION_DETECTION.id,
            ModuleState.ON.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.COLLISION_DETECTION.id,
                    ModuleState.ON.id
                )))
    }
    @Test
    fun `validate collision detection module is idle`() {
        val ret = doEcuRequest(
            CarModule.COLLISION_DETECTION.id,
            ModuleState.IDLE.id
        )
        assertThat(ret,
            `is`(
                NanoHTTPDLifecycleAware.formatResponse(
                    CarModule.COLLISION_DETECTION.id,
                    ModuleState.IDLE.id
                )))
    }
}