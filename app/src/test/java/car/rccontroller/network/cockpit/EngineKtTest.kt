package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
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

    private lateinit var retrofit: Retrofit
    private lateinit var engineAPI: Engine
    private lateinit var electricsAPI: Electrics

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineAPI = retrofit.create<Engine>(Engine::class.java)
        electricsAPI = retrofit.create<Electrics>(Electrics::class.java)
        car.rccontroller.network.cockpit.startEngine(null, serverIp, port, engineAPI, electricsAPI)
        //throttleBrakeActionId = System.currentTimeMillis()
        //steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine(engineAPI)
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    // Engine
    @Test
    fun `validate that engine has started`() {
        assertThat(isEngineStarted(engineAPI), `is`(true))
    }
    @Test
    fun `validate that engine has stopped`() {
        stopEngine(engineAPI)
        assertThat(isEngineStarted(engineAPI), `is`(false))
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

