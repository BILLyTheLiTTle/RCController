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
        car.rccontroller.network.cockpit.startEngine(null)
        throttleBrakeActionId = System.currentTimeMillis()
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
}

