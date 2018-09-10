package car.rccontroller.network.server.feedback

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
    // temperature - motors
    @Test
    fun `validate normal rear left motor temperature`() {
        val ret = doBlockingRequest(
            "http://" +
                    "$ip:" +
                    "$port" +
                    "${SensorFeedbackServer.TEMP_URI}" +
                    "?${SensorFeedbackServer.TEMP_PARAM_KEY_ITEM}=" +
                    "${SensorFeedbackServer.MOTOR_REAR_LEFT_TEMP}" +
                    "&${SensorFeedbackServer.TEMP_PARAM_KEY_WARNING}=" +
                    "${SensorFeedbackServer.WARNING_TYPE_NORMAL}" +
                    "&${SensorFeedbackServer.TEMP_PARAM_KEY_VALUE}=10"
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

    }
    @Test
    fun `validate high rear left motor temperature`() {

    }
    @Test
    fun `validate unchanged rear left motor temperature`() {

    }
    @Test
    fun `validate normal rear right motor temperature`() {
    }
    @Test
    fun `validate medium rear right motor temperature`() {

    }
    @Test
    fun `validate high rear right motor temperature`() {

    }
    @Test
    fun `validate unchanged rear right motor temperature`() {

    }
    @Test
    fun `validate normal front left motor temperature`() {
    }
    @Test
    fun `validate medium front left motor temperature`() {

    }
    @Test
    fun `validate high front left motor temperature`() {

    }
    @Test
    fun `validate unchanged front left motor temperature`() {

    }
    @Test
    fun `validate normal front right motor temperature`() {
    }
    @Test
    fun `validate medium front right motor temperature`() {

    }
    @Test
    fun `validate high front right motor temperature`() {

    }
    @Test
    fun `validate unchanged front right motor temperature`() {

    }
    // temperature - h bridges
    @Test
    fun `validate normal rear h bridge temperature`() {
    }
    @Test
    fun `validate medium rear h bridge temperature`() {

    }
    @Test
    fun `validate high rear h bridge temperature`() {

    }
    @Test
    fun `validate unchanged rear h bridge temperature`() {

    }
    @Test
    fun `validate normal front h bridge temperature`() {
    }
    @Test
    fun `validate medium front h bridge temperature`() {

    }
    @Test
    fun `validate high front h bridge temperature`() {

    }
    @Test
    fun `validate unchanged front h bridge temperature`() {

    }
    // temperature - raspberry
    @Test
    fun `validate normal raspberry temperature`() {
    }
    @Test
    fun `validate medium raspberry temperature`() {

    }
    @Test
    fun `validate high raspberry temperature`() {

    }
    @Test
    fun `validate unchanged raspberry temperature`() {

    }
    // temperature - batteries
    @Test
    fun `validate normal batteries temperature`() {
    }
    @Test
    fun `validate medium batteries temperature`() {

    }
    @Test
    fun `validate high batteries temperature`() {

    }
    @Test
    fun `validate unchanged batteries temperature`() {

    }
    // temperature - shift registers
    @Test
    fun `validate normal shift registers temperature`() {
    }
    @Test
    fun `validate medium shift registers temperature`() {

    }
    @Test
    fun `validate high shift registers temperature`() {

    }
    @Test
    fun `validate unchanged shift registers temperature`() {

    }
    // for speed
    @Test
    fun `validate speed`() {

    }
    // for ecu modules
    @Test
    fun `validate traction control module is on`() {

    }
    @Test
    fun `validate traction control module is idle`() {

    }
    @Test
    fun `validate traction control module is unchanged`() {

    }
    @Test
    fun `validate antilock braking module is on`() {

    }
    @Test
    fun `validate antilock braking module is idle`() {

    }
    @Test
    fun `validate antilock braking module is unchanged`() {

    }
    @Test
    fun `validate electronic stability module is on`() {

    }
    @Test
    fun `validate electronic stability module is idle`() {

    }
    @Test
    fun `validate electronic stability module is unchanged`() {

    }
    @Test
    fun `validate understeer detection module is on`() {

    }
    @Test
    fun `validate understeer detection module is idle`() {

    }
    @Test
    fun `validate understeer detection module is unchanged`() {

    }
    @Test
    fun `validate oversteer detection module is on`() {

    }
    @Test
    fun `validate oversteer detection module is idle`() {

    }
    @Test
    fun `validate oversteer detection module is unchanged`() {

    }
    @Test
    fun `validate collision detection module is on`() {

    }
    @Test
    fun `validate collision detection module is idle`() {

    }
    @Test
    fun `validate collision detection module is unchanged`() {

    }
}