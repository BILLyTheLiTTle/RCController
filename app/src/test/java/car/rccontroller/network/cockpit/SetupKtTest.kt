package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

@RunWith(MockitoJUnitRunner::class)
class SetupKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    private lateinit var retrofit: Retrofit
    private lateinit var engineAPI: Engine
    private lateinit var electricsAPI: Electrics
    private lateinit var setupAPI: Setup

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineAPI = retrofit.create<Engine>(Engine::class.java)
        electricsAPI = retrofit.create<Electrics>(Electrics::class.java)
        setupAPI = retrofit.create<Setup>(Setup::class.java)
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
        Assert.assertNotNull(mockedActivity)
    }

    // Handling Assistance
    @Test
    fun `validate that handling assistance is deactivated`(){
        setHandlingAssistanceState(ASSISTANCE_NONE, setupAPI)
        assertThat(getHandlingAssistanceState(setupAPI), `is`(ASSISTANCE_NONE))
    }
    @Test
    fun `validate that handling assistance is set to warning`(){
        setHandlingAssistanceState(ASSISTANCE_WARNING, setupAPI)
        assertThat(getHandlingAssistanceState(setupAPI), `is`(ASSISTANCE_WARNING))
    }
    @Test
    fun `validate that handling assistance is set to full`(){
        setHandlingAssistanceState(ASSISTANCE_FULL, setupAPI)
        assertThat(getHandlingAssistanceState(setupAPI), `is`(ASSISTANCE_FULL))
    }

    // Speed limiter
    @Test
    fun `validate that the motor speed limiter is deactivated`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_NO_SPEED, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_NO_SPEED))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_1, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_2, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_1, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_2, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in full speed`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FULL_SPEED, setupAPI)
        assertThat(getMotorSpeedLimiter(setupAPI), `is`(MOTOR_SPEED_LIMITER_FULL_SPEED))
    }

    // Front Differential
    @Test
    fun `validate that the front differential is in error state`(){
        // TODO should be tested with mocking
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN))
    }
    @Test
    fun `validate that the front differential is in open state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN))
    }
    @Test
    fun `validate that the front differential is in medi 0 state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0))
    }
    @Test
    fun `validate that the front differential is in medi 1 state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1))
    }
    @Test
    fun `validate that the front differential is in medi 2 state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2))
    }
    @Test
    fun `validate that the front differential is in locked state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(
            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
    @Test
    fun `validate that the front differential is in auto state`(){
        setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO, setupAPI)
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO))
    }
    @Test
    fun `validate that the front differential default state is locked`(){
        assertThat(getFrontDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }

    // Rear Differential
    @Test
    fun `validate that the rear differential is in open state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN))
    }
    @Test
    fun `validate that the rear differential is in medi 0 state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0))
    }
    @Test
    fun `validate that the rear differential is in medi 1 state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1))
    }
    @Test
    fun `validate that the rear differential is in medi 2 state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2))
    }
    @Test
    fun `validate that the rear differential is in locked state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
    @Test
    fun `validate that the rear differential is in auto state`(){
        setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO, setupAPI)
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO))
    }
    @Test
    fun `validate that the rear differential default state is locked`(){
        assertThat(getRearDifferentialSlipperyLimiter(setupAPI), `is`(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED))
    }
}