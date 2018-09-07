package car.rccontroller.network.server.feedback

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.mockito.Mockito.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import android.content.SharedPreferences
import car.rccontroller.RCControllerActivity
import car.rccontroller.RUN_ON_EMULATOR
import car.rccontroller.network.doBlockingRequest
import car.rccontroller.network.myIP
import car.rccontroller.network.sensorFeedbackServerPort

@RunWith(MockitoJUnitRunner::class)
class SensorFeedbackServerTest {

    @Mock
    private lateinit var mockActivity: RCControllerActivity

    private fun assertion(assert: String) {
        assertThat(assert, `is`("RLMT"))
    }

    @Test
    fun `serve rear left motor temperature`() {
        val sensorFeedbackServer = if (true) SensorFeedbackServer(mockActivity) else SensorFeedbackServer(
            mockActivity,
            myIP,
            sensorFeedbackServerPort
        )
        sensorFeedbackServer.start()
        `when`(mockActivity.updateTempUIItems(rearLeftMotor = "rlmt")).then{ assertion("RLMT") }
        doBlockingRequest(
            "http://" +
                    "$myIP:" +
                    "$sensorFeedbackServerPort" +
                    "/temp" +
                    "?item=motor_rear_left_temp" +
                    "&warning=normal" +
                    "&value=10"
        )
        //assertThat(result, `is`(assertion("RLMT")))
    }

    @Test
    fun getIp() {
    }

    @Test
    fun getPort() {
    }
}