package car.rccontroller.network

import car.rccontroller.RCControllerActivity
import junit.framework.Assert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.InjectMocks



@RunWith(MockitoJUnitRunner::class)
class ClientTriggeredRequestsKtTest {

    @InjectMocks
    var injectedActivity: RCControllerActivity? = null

    @Mock
    var mockedActivity: RCControllerActivity? = null

    @Test
    fun sanityCheck() {
        Assert.assertNotNull(injectedActivity)
        Assert.assertNotNull(mockedActivity)
    }

    @Test
    fun startEngine() {
        car.rccontroller.network.startEngine(null, "192.168.1.8", 8080)
        assertThat(isEngineStarted, `is`(true))
    }

    @Test
    fun stopEngine() {
        car.rccontroller.network.stopEngine()
        assertThat(isEngineStarted, `is`(false))
    }
}

