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
class RequestsModelKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    private lateinit var retrofit: Retrofit
    private lateinit var engineApi: Engine
    private lateinit var electricsAPI: Electrics

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineApi = retrofit.create<Engine>(Engine::class.java)
        electricsAPI = retrofit.create<Electrics>(Electrics::class.java)
        car.rccontroller.network.cockpit.startEngine(null, serverIp, port, engineApi, electricsAPI)
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

    // Handling Assistance
    @Test
    fun `validate that handling assistance is deactivated`(){
        handlingAssistanceState = ASSISTANCE_NONE
        assertThat(handlingAssistanceState, `is`(ASSISTANCE_NONE))
    }
    @Test
    fun `validate that handling assistance is set to warning`(){
        handlingAssistanceState = ASSISTANCE_WARNING
        assertThat(handlingAssistanceState, `is`(ASSISTANCE_WARNING))
    }
    @Test
    fun `validate that handling assistance is set to full`(){
        handlingAssistanceState = ASSISTANCE_FULL
        assertThat(handlingAssistanceState, `is`(ASSISTANCE_FULL))
    }

    // Speed limiter
    @Test
    fun `validate that the motor speed limiter is deactivated`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_NO_SPEED
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_NO_SPEED))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 1`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_1
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 2`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_2
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 1`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 2`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 1`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_1
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 2`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_2
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in full speed`(){
        motorSpeedLimiter = MOTOR_SPEED_LIMITER_FULL_SPEED
        assertThat(motorSpeedLimiter, `is`(MOTOR_SPEED_LIMITER_FULL_SPEED))
    }

    // Front Differential
    @Test
    fun `validate that the front differential is in open state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_OPEN
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN))
    }
    @Test
    fun `validate that the front differential is in medi 0 state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0))
    }
    @Test
    fun `validate that the front differential is in medi 1 state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1))
    }
    @Test
    fun `validate that the front differential is in medi 2 state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2))
    }
    @Test
    fun `validate that the front differential is in locked state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
    @Test
    fun `validate that the front differential is in auto state`(){
        currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_AUTO
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO))
    }
    @Test
    fun `validate that the front differential default state is locked`(){
        assertThat(currentFrontDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }

    // Rear Differential
    @Test
    fun `validate that the rear differential is in open state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_OPEN
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN))
    }
    @Test
    fun `validate that the rear differential is in medi 0 state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0))
    }
    @Test
    fun `validate that the rear differential is in medi 1 state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1))
    }
    @Test
    fun `validate that the rear differential is in medi 2 state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2))
    }
    @Test
    fun `validate that the rear differential is in locked state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
    @Test
    fun `validate that the rear differential is in auto state`(){
        currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_AUTO
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO))
    }
    @Test
    fun `validate that the rear differential default state is locked`(){
        assertThat(currentRearDifferentialSlipperyLimiter, `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
}

