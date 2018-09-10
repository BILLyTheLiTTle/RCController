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

    // serve
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
    fun getIp() {
    }

    @Test
    fun getPort() {
    }
}