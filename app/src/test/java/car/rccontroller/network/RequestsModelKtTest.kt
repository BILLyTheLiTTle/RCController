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

    // Engine
    /*@Test
    fun `validate that engine has started`() {
        assertThat(isEngineStarted(engineApi), `is`(true))
    }
    @Test
    fun `validate that engine has stopped`() {
        stopEngine(engineApi)
        assertThat(isEngineStarted(engineApi), `is`(false))
    }

    /*@Test
    fun `validate that local server has started`() {
        car.rccontroller.network.startEngine(null, "192.168.200.245", 8080)
        assertThat(if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.isAlive else false, `is`(true))
    }*/
    @Test
    fun `validate that local server has stopped`() {

    }*/

    // Parking brake
    /*@Test
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
    }*/

    // Steering
    @Test
    fun `validate that car is turning left`(){
        runBlocking {
            car.rccontroller.network.setSteering(ACTION_TURN_LEFT, 20).join()
        }
        assertThat(steeringDirection, `is`(ACTION_TURN_LEFT))
    }
    @Test
    fun `validate that car is turning right`(){
        runBlocking {
            car.rccontroller.network.setSteering(ACTION_TURN_RIGHT, 80).join()
        }
        assertThat(steeringDirection, `is`(ACTION_TURN_RIGHT))
    }
    @Test
    fun `validate that car is going straight with value`(){
        runBlocking {
            car.rccontroller.network.setSteering(ACTION_STRAIGHT, 20).join()
        }
        assertThat(steeringDirection, `is`(ACTION_STRAIGHT))
    }
    @Test
    fun `validate that car is going straight without value`(){
        runBlocking {
            car.rccontroller.network.setSteering(ACTION_STRAIGHT).join()
        }
        assertThat(steeringDirection, `is`(ACTION_STRAIGHT))
    }

    // Main Lights
    @Test
    fun `validate that main lights are off`(){
        mainLightsState = LIGHTS_OFF
        assertThat(mainLightsState, `is`(LIGHTS_OFF))
    }
    @Test
    fun `validate that position lights are on`(){
        mainLightsState = POSITION_LIGHTS
        assertThat(mainLightsState, `is`(POSITION_LIGHTS))
    }
    @Test
    fun `validate that driving lights are on`(){
        mainLightsState = POSITION_LIGHTS
        mainLightsState = DRIVING_LIGHTS
        assertThat(mainLightsState, `is`(DRIVING_LIGHTS))
    }
    @Test
    fun `validate that long range lights are on`(){
        mainLightsState = POSITION_LIGHTS
        mainLightsState = DRIVING_LIGHTS
        mainLightsState = LONG_RANGE_LIGHTS
        assertThat(mainLightsState, `is`(LONG_RANGE_LIGHTS))
    }

    // Turn Lights
    @Test
    fun `validate that turn lights are off`(){
        turnLights = TURN_LIGHTS_STRAIGHT
        assertThat(turnLights, `is`(TURN_LIGHTS_STRAIGHT))
    }
    @Test
    fun `validate that left turn lights are on`(){
        turnLights = TURN_LIGHTS_LEFT
        assertThat(turnLights, `is`(TURN_LIGHTS_LEFT))
    }
    @Test
    fun `validate that right turn lights are on`(){
        turnLights = TURN_LIGHTS_RIGHT
        assertThat(turnLights, `is`(TURN_LIGHTS_RIGHT))
    }

    // Emergency Lights
    @Test
    fun `validate that emergency lights are off`(){
        emergencyLights = false
        assertThat(emergencyLights, `is`(false))
    }
    @Test
    fun `validate that emergency lights are on`(){
        emergencyLights = true
        assertThat(emergencyLights, `is`(true))
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

    // Reverse Lights
    @Test
    fun `validate that reverse is activated`() {
        setReverseIntention (true)
        assertThat(getReverseIntention(), `is`(true))
    }

    @Test
    fun `validate that reverse is deactivated`() {
        setReverseIntention (false)
        assertThat(getReverseIntention(), `is`(false))
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

