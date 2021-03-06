package car.feedback.cockpit

import car.rccontroller.RCControllerActivity
import car.rccontroller.retrofit
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
class SteeringKtTest {

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
        startEngine(null)
        //throttleBrakeActionId = System.currentTimeMillis()
        steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine()
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    @Test
    fun `validate that car is turning left`(){
        runBlocking {
            setSteering(Turn.LEFT, SteeringValue.VALUE_20)?.join()
        }
        assertThat(getSteeringDirection(), `is`(Turn.LEFT))
    }
    @Test
    fun `validate that car is turning right`(){
        runBlocking {
            setSteering(Turn.RIGHT, SteeringValue.VALUE_80)?.join()
        }
        assertThat(getSteeringDirection(), `is`(Turn.RIGHT))
    }
    @Test
    fun `validate that car is going straight with value`(){
        runBlocking {
            setSteering(Turn.STRAIGHT, SteeringValue.VALUE_20)?.join()
        }
        assertThat(getSteeringDirection(), `is`(Turn.STRAIGHT))
    }
    @Test
    fun `validate that car is going straight without value`(){
        runBlocking {
            setSteering(Turn.STRAIGHT)?.join()
        }
        assertThat(getSteeringDirection(), `is`(Turn.STRAIGHT))
    }
}