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
        setHandlingAssistanceState(HandlingAssistance.MANUAL)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.MANUAL))
    }
    @Test
    fun `validate that handling assistance is set to warning`(){
        setHandlingAssistanceState(HandlingAssistance.WARNING)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.WARNING))
    }
    @Test
    fun `validate that handling assistance is set to full`(){
        setHandlingAssistanceState(HandlingAssistance.FULL)
        assertThat(getHandlingAssistanceState(), `is`(HandlingAssistance.FULL))
    }

    // Speed limiter
    @Test
    fun `validate that the motor speed limiter is deactivated`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.NO_SPEED)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.NO_SPEED))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 1`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.SLOW_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 2`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.SLOW_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 1`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.MEDIUM_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 2`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.MEDIUM_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 1`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_1)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.FAST_SPEED_1))
    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 2`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_2)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.FAST_SPEED_2))
    }
    @Test
    fun `validate that the motor speed limiter is in full speed`(){
        setMotorSpeedLimiter(MotorSpeedLimiter.FULL_SPEED)
        assertThat(getMotorSpeedLimiter(), `is`(MotorSpeedLimiter.FULL_SPEED))
    }

    // Front Differential
    @Test
    fun `validate that the front differential is in error state`(){
        // TODO should be tested with mocking
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.OPEN)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.OPEN))
    }
    @Test
    fun `validate that the front differential is in open state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.OPEN)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.OPEN))
    }
    @Test
    fun `validate that the front differential is in medi 0 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiter.MEDI_0))
    }
    @Test
    fun `validate that the front differential is in medi 1 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiter.MEDI_1))
    }
    @Test
    fun `validate that the front differential is in medi 2 state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiter.MEDI_2))
    }
    @Test
    fun `validate that the front differential is in locked state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.LOCKED)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(
            DifferentialSlipperyLimiter.LOCKED))
    }
    @Test
    fun `validate that the front differential is in auto state`(){
        setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.AUTO)
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.AUTO))
    }
    @Test
    fun `validate that the front differential default state is locked`(){
        assertThat(getFrontDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.LOCKED))
    }

    // Rear Differential
    @Test
    fun `validate that the rear differential is in open state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.OPEN)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.OPEN))
    }
    @Test
    fun `validate that the rear differential is in medi 0 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.MEDI_0))
    }
    @Test
    fun `validate that the rear differential is in medi 1 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.MEDI_1))
    }
    @Test
    fun `validate that the rear differential is in medi 2 state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.MEDI_2))
    }
    @Test
    fun `validate that the rear differential is in locked state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.LOCKED)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.LOCKED))
    }
    @Test
    fun `validate that the rear differential is in auto state`(){
        setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.AUTO)
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.AUTO))
    }
    @Test
    fun `validate that the rear differential default state is locked`(){
        assertThat(getRearDifferentialSlipperyLimiter(), `is`(DifferentialSlipperyLimiter.LOCKED))
    }
}