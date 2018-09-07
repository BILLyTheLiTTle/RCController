package car.rccontroller.network.server.feedback

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import car.rccontroller.network.doBlockingRequest

@RunWith(MockitoJUnitRunner::class)
class SensorFeedbackServerTest {

    private val ip = "192.168.1.2"
    private val port = "8080"

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
                `is`(formatResponse(
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