package car.rccontroller.network

import car.rccontroller.RCControllerActivity
import junit.framework.Assert.*
import kotlinx.coroutines.experimental.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock



@RunWith(MockitoJUnitRunner::class)
class ClientTriggeredRequestsKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    @Before
    fun setUp() {
        car.rccontroller.network.startEngine(null, serverIp, port)
        throttleBrakeActionId++
    }

    @After
    fun tearDown() {
        car.rccontroller.network.stopEngine()
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    @Test
    fun `validate that engine has started`() {
        assertThat(isEngineStarted, `is`(true))
    }
    @Test
    fun `validate that engine has stopped`() {
        car.rccontroller.network.stopEngine()
        assertThat(isEngineStarted, `is`(false))
    }

    /*@Test
    fun `validate that local server has started`() {
        car.rccontroller.network.startEngine(null, "192.168.200.245", 8080)
        assertThat(if (::sensorFeedbackServer.isInitialized) sensorFeedbackServer.isAlive else false, `is`(true))
    }
    @Test
    fun `validate that local server has stopped`() {

    }*/

    @Test
    fun `validate that parking brake is activated`() {
        car.rccontroller.network.activateParkingBrake(true)
        assertThat(isParkingBrakeActive, `is`(true))
    }
    @Test
    fun `validate that parking brake is deactivated`() {
        car.rccontroller.network.activateParkingBrake(false)
        assertThat(isParkingBrakeActive, `is`(false))
    }

    @Test
    fun `validate that handbrake is activated`() {
        runBlocking {
            car.rccontroller.network.activateHandbrake(true).join()
        }
        assertThat(isHandbrakeActive, `is`(true))
    }
    @Test
    fun `validate that handbrake is deactivated`() {
        runBlocking {
            car.rccontroller.network.activateHandbrake(false).join()
        }
        assertThat(isHandbrakeActive, `is`(false))
    }

    @Test
    fun `validate that reverse is activated`() {
        car.rccontroller.network.reverseIntention = true
        assertThat(reverseIntention, `is`(true))
    }
    @Test
    fun `validate that reverse is deactivated`() {
        car.rccontroller.network.reverseIntention = false
        assertThat(reverseIntention, `is`(false))
    }

    @Test
    fun `validate that car is in neutral`() {
        runBlocking {
            car.rccontroller.network.setNeutral().join()
        }
        assertThat(motionState, `is`(ACTION_NEUTRAL))
    }

    @Test
    fun `validate that car is braking still`() {
        runBlocking {
            car.rccontroller.network.setBrakingStill().join()
        }
        assertThat(motionState, `is`(ACTION_BRAKING_STILL))
    }

    @Test
    fun `validate that car is throttling forward or braking`() {
        runBlocking {
            car.rccontroller.network.setThrottleBrake(ACTION_MOVE_FORWARD, 60).join()
        }
        assertThat(motionState, `is`(ACTION_MOVE_FORWARD))
    }
    @Test
    fun `validate that car is throttling backward or braking`() {
        runBlocking {
            car.rccontroller.network.setThrottleBrake(ACTION_MOVE_BACKWARD, 40).join()
        }
        assertThat(motionState, `is`(ACTION_MOVE_BACKWARD))
    }

    @Test
    fun `validate that car is turning left`(){

    }
    @Test
    fun `validate that car is turning right`(){

    }
    @Test
    fun `validate that car is going straight`(){

    }

    @Test
    fun `validate that main lights are off`(){

    }
    @Test
    fun `validate that position lights are on`(){

    }
    @Test
    fun `validate that driving lights are on`(){

    }
    @Test
    fun `validate that long range lights are on`(){

    }


    @Test
    fun `validate that turn lights are off`(){

    }
    @Test
    fun `validate that left turn lights are on`(){

    }
    @Test
    fun `validate that right turn lights are on`(){

    }

    @Test
    fun `validate that emergency lights are off`(){

    }
    @Test
    fun `validate that emergency lights are on`(){

    }

    @Test
    fun `validate that handling assistance is deactivated`(){

    }
    @Test
    fun `validate that handling assistance is set to warning`(){

    }
    @Test
    fun `validate that handling assistance is set to full`(){

    }

    @Test
    fun `validate that the motor speed limiter is deactivated`(){

    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 1`(){

    }
    @Test
    fun `validate that the motor speed limiter is in slow speed 2`(){

    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 1`(){

    }
    @Test
    fun `validate that the motor speed limiter is in medium speed 2`(){

    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 1`(){

    }
    @Test
    fun `validate that the motor speed limiter is in fast speed 2`(){

    }
    @Test
    fun `validate that the motor speed limiter is in full speed`(){

    }

    @Test
    fun `validate that the front differential is in open state`(){

    }
    @Test
    fun `validate that the front differential is in medi 0 state`(){

    }
    @Test
    fun `validate that the front differential is in medi 1 state`(){

    }
    @Test
    fun `validate that the front differential is in medi 2 state`(){

    }
    @Test
    fun `validate that the front differential is in locked state`(){

    }
    @Test
    fun `validate that the front differential is in auto state`(){

    }

    @Test
    fun `validate that the rear differential is in open state`(){

    }
    @Test
    fun `validate that the rear differential is in medi 0 state`(){

    }
    @Test
    fun `validate that the rear differential is in medi 1 state`(){

    }
    @Test
    fun `validate that the rear differential is in medi 2 state`(){

    }
    @Test
    fun `validate that the rear differential is in locked state`(){

    }
    @Test
    fun `validate that the rear differential is in auto state`(){

    }
}

