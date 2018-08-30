package car.rccontroller.network

import car.rccontroller.RCControllerActivity
import junit.framework.Assert.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock



@RunWith(MockitoJUnitRunner::class)
class ClientTriggeredRequestsKtTest {

    @Mock
    var mockedActivity: RCControllerActivity? = null

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    @Test
    fun `validate that engine has started`() {
        car.rccontroller.network.startEngine(null, "192.168.200.245", 8080)
        assertThat(isEngineStarted, `is`(true))
    }
    @Test
    fun `validate that engine has stopped`() {
        car.rccontroller.network.stopEngine()
        assertThat(isEngineStarted, `is`(false))
    }

    @Test
    fun `validate that local server has started`() {

    }
    @Test
    fun `validate that local server has stopped`() {

    }

    @Test
    fun `validate that parking brake is activated`() {

    }
    @Test
    fun `validate that parking brake is deactivated`() {

    }

    @Test
    fun `validate that handbrake is activated`() {

    }
    @Test
    fun `validate that handbrake is deactivated`() {

    }

    @Test
    fun `validate that reverse is activated`() {

    }
    @Test
    fun `validate that reverse is deactivated`() {

    }

    @Test
    fun `validate that car is in neutral`() {

    }

    @Test
    fun `validate that car is braking still`() {

    }

    @Test
    fun `validate that car is throttling or braking`() {

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

