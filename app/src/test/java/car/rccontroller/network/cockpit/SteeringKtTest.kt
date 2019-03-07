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
class SteeringKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    private lateinit var retrofit: Retrofit
    private lateinit var engineApi: Engine
    private lateinit var steeringApi: Steering

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineApi = retrofit.create<Engine>(Engine::class.java)
        steeringApi = retrofit.create<Steering>(Steering::class.java)
        car.rccontroller.network.cockpit.startEngine(null, serverIp, port, engineApi)
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

    @Test
    fun `validate that car is turning left`(){
        runBlocking {
            setSteering(ACTION_TURN_LEFT, 20, steeringApi)?.join()
        }
        assertThat(getSteeringDirection(steeringApi), `is`(ACTION_TURN_LEFT))
    }
    @Test
    fun `validate that car is turning right`(){
        runBlocking {
            setSteering(ACTION_TURN_RIGHT, 80, steeringApi)?.join()
        }
        assertThat(getSteeringDirection(steeringApi), `is`(ACTION_TURN_RIGHT))
    }
    @Test
    fun `validate that car is going straight with value`(){
        runBlocking {
            setSteering(ACTION_STRAIGHT, 20, steeringApi)?.join()
        }
        assertThat(getSteeringDirection(steeringApi), `is`(ACTION_STRAIGHT))
    }
    @Test
    fun `validate that car is going straight without value`(){
        runBlocking {
            setSteering(ACTION_STRAIGHT, retrofitApi = steeringApi)?.join()
        }
        assertThat(getSteeringDirection(steeringApi), `is`(ACTION_STRAIGHT))
    }
}