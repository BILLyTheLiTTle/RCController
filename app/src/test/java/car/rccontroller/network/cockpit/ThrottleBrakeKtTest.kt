package car.rccontroller.network

import car.rccontroller.RCControllerActivity
import car.rccontroller.network.cockpit.*
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
    private lateinit var engineApi: Engine
    private lateinit var throttleBrakeApi: ThrottleBrake

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineApi = retrofit.create<Engine>(Engine::class.java)
        throttleBrakeApi = retrofit.create<ThrottleBrake>(ThrottleBrake::class.java)
        car.rccontroller.network.cockpit.startEngine(engineApi, null, serverIp, port)
        throttleBrakeActionId = System.currentTimeMillis()
        steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine(engineApi)
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
        activateParkingBrake(true)
        assertThat(isParkingBrakeActive(throttleBrakeApi), `is`(true))
    }

    @Test
    fun `validate that parking brake is deactivated`() {
        activateParkingBrake(false)
        assertThat(isParkingBrakeActive(throttleBrakeApi), `is`(false))
    }

    // Handbrake
    @Test
    fun `validate that handbrake is activated`() {
        runBlocking {
            activateHandbrake(true).join()
        }
        assertThat(isHandbrakeActive, `is`(true))
    }

    @Test
    fun `validate that handbrake is deactivated`() {
        runBlocking {
            activateHandbrake(false).join()
        }
        assertThat(isHandbrakeActive, `is`(false))
    }

    // Reverse
    @Test
    fun `validate that reverse is activated`() {
        reverseIntention = true
        assertThat(reverseIntention, `is`(true))
    }

    @Test
    fun `validate that reverse is deactivated`() {
        reverseIntention = false
        assertThat(reverseIntention, `is`(false))
    }

    // Neutral
    @Test
    fun `validate that car is in neutral`() {
        runBlocking {
            setNeutral().join()
        }
        assertThat(motionState, `is`(ACTION_NEUTRAL))
    }

    // Braking still
    @Test
    fun `validate that car is braking still`() {
        runBlocking {
            setBrakingStill().join()
        }
        assertThat(motionState, `is`(ACTION_BRAKING_STILL))
    }

    // Throttle -n- Brake
    @Test
    fun `validate that car is throttling forward or braking`() {
        runBlocking {
            setThrottleBrake(ACTION_MOVE_FORWARD, 60).join()
        }
        assertThat(motionState, `is`(ACTION_MOVE_FORWARD))
    }

    @Test
    fun `validate that car is throttling backward or braking`() {
        runBlocking {
            setThrottleBrake(ACTION_MOVE_BACKWARD, 40).join()
        }
        assertThat(motionState, `is`(ACTION_MOVE_BACKWARD))
    }
}