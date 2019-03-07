package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
import junit.framework.Assert.*
import kotlinx.coroutines.*
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
class ThrottleBrakeKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    private lateinit var retrofit: Retrofit
    private lateinit var engineAPI: Engine
    private lateinit var electricsAPI: Electrics
    private lateinit var throttleBrakeAPI: ThrottleBrake

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineAPI = retrofit.create<Engine>(Engine::class.java)
        electricsAPI = retrofit.create<Electrics>(Electrics::class.java)
        throttleBrakeAPI = retrofit.create<ThrottleBrake>(ThrottleBrake::class.java)
        car.rccontroller.network.cockpit.startEngine( null, serverIp, port, engineAPI, electricsAPI)
        throttleBrakeActionId = System.currentTimeMillis()
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

    /*@Test
    fun `validate that local server has started`() {
        car.rccontroller.network.startEngine(null, "192.168.200.245", 8080)
        assertThat(if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.isAlive else false, `is`(true))
    }
    @Test
    fun `validate that local server has stopped`() {

    }*/

    // Parking brake
    @Test
    fun `validate that parking brake is activated`() {
        activateParkingBrake(true, throttleBrakeAPI)
        assertThat(isParkingBrakeActive(throttleBrakeAPI), `is`(true))
    }

    @Test
    fun `validate that parking brake is deactivated`() {
        activateParkingBrake(false, throttleBrakeAPI)
        assertThat(isParkingBrakeActive(throttleBrakeAPI), `is`(false))
    }

    // Handbrake
    @Test
    fun `validate that handbrake is activated`() {
        runBlocking {
            activateHandbrake(true, throttleBrakeAPI)?.join()
        }
        assertThat(isHandbrakeActive(throttleBrakeAPI), `is`(true))
    }

    @Test
    fun `validate that handbrake is deactivated`() {
        runBlocking {
            activateHandbrake(false, throttleBrakeAPI)?.join()
        }
        assertThat(isHandbrakeActive(throttleBrakeAPI), `is`(false))
    }

    // Neutral
    @Test
    fun `validate that car is in neutral`() {
        runBlocking {
            setNeutral(throttleBrakeAPI)?.join()
        }
        assertThat(getMotionState(throttleBrakeAPI), `is`(ACTION_NEUTRAL))
    }

    // Braking still
    @Test
    fun `validate that car is braking still`() {
        runBlocking {
            setBrakingStill(throttleBrakeAPI)?.join()
        }
        assertThat(getMotionState(throttleBrakeAPI), `is`(ACTION_BRAKING_STILL))
    }

    // Throttle -n- Brake
    @Test
    fun `validate that car is throttling forward or braking`() {
        runBlocking {
            setThrottleBrake(ACTION_MOVE_FORWARD, 60, throttleBrakeAPI)?.join()
        }
        assertThat(getMotionState(throttleBrakeAPI), `is`(ACTION_MOVE_FORWARD))
    }

    @Test
    fun `validate that car is throttling backward or braking`() {
        runBlocking {
            setThrottleBrake(ACTION_MOVE_BACKWARD, 40, throttleBrakeAPI)?.join()
        }
        assertThat(getMotionState(throttleBrakeAPI), `is`(ACTION_MOVE_BACKWARD))
    }
}