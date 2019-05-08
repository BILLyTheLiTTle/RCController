package car.feedback.cockpit

import car.rccontroller.RCControllerActivity
import car.rccontroller.retrofit
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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        startEngine( null)
        throttleBrakeActionId = System.currentTimeMillis()
        //steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine()
    }

    @Test
    fun sanityCheck() {
        Assert.assertNotNull(mockedActivity)
    }

    // Handling Assistance
    @Test
    fun `validate that handling assistance is deactivated`(){
        setHandlingAssistanceState(HandlingAssistance.MANUAL.id)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.MANUAL.id))
    }
    @Test
    fun `validate that handling assistance is set to warning`(){
        setHandlingAssistanceState(HandlingAssistance.WARNING.id)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.WARNING.id))
    }
    @Test
    fun `validate that handling assistance is set to full`(){
        setHandlingAssistanceState(HandlingAssistance.FULL.id)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.FULL.id))
    }

    // Speed limiter
    @Test
    fun `validate that the motor speed limiter is deactivated`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_NO_SPEED)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_NO_SPEED))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_SLOW_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 1`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 2`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_FAST_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in full speed`(){
        setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FULL_SPEED)
        assertThat(getMotorSpeedLimiter(), `is`(MOTOR_SPEED_LIMITER_FULL_SPEED))
    }

    // Front Differential
    @Test
    fun `validate that the front differential is in error state`(){
        // TODO should be tested with mocking
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.OPEN.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.OPEN.value))
    }
    @Test
    fun `validate that the front differential is in open state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.OPEN.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.OPEN.value))
    }
    @Test
    fun `validate that the front differential is in medi 0 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_0.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiterState.MEDI_0.value))
    }
    @Test
    fun `validate that the front differential is in medi 1 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_1.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiterState.MEDI_1.value))
    }
    @Test
    fun `validate that the front differential is in medi 2 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_2.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiterState.MEDI_2.value))
    }
    @Test
    fun `validate that the front differential is in locked state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.LOCKED.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiterState.LOCKED.value))
    }
    @Test
    fun `validate that the front differential is in auto state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.AUTO.value)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.AUTO.value))
    }
    @Test
    fun `validate that the front differential default state is locked`(){
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.LOCKED.value))
    }

    // Rear Differential
    @Test
    fun `validate that the rear differential is in open state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.OPEN.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.OPEN.value))
    }
    @Test
    fun `validate that the rear differential is in medi 0 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_0.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.MEDI_0.value))
    }
    @Test
    fun `validate that the rear differential is in medi 1 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_1.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.MEDI_1.value))
    }
    @Test
    fun `validate that the rear differential is in medi 2 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.MEDI_2.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.MEDI_2.value))
    }
    @Test
    fun `validate that the rear differential is in locked state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.LOCKED.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.LOCKED.value))
    }
    @Test
    fun `validate that the rear differential is in auto state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiterState.AUTO.value)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.AUTO.value))
    }
    @Test
    fun `validate that the rear differential default state is locked`(){
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiterState.LOCKED.value))
    }
}