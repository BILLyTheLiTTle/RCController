package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
import car.rccontroller.retrofit
import junit.framework.Assert.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


@RunWith(MockitoJUnitRunner::class)
class EngineKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        car.rccontroller.network.cockpit.startEngine(null)
        //throttleBrakeActionId = System.currentTimeMillis()
        //steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine()
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    // Engine
    @Test
    fun `validate that engine has started`() {
        assertThat(isEngineStarted(), `is`(true))
    }
    @Test
    fun `validate that engine has stopped`() {
        stopEngine()
        assertThat(isEngineStarted(), `is`(false))
    }

    /*@Test
    fun `validate that local server has started`() {
        car.rccontroller.network.startEngine(null, "192.168.200.245", 8080)
        assertThat(if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.isAlive else false, `is`(true))
    }
    @Test
    fun `validate that local server has stopped`() {

    }*/
}

