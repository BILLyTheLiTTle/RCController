package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
import car.rccontroller.RCControllerViewModel
import car.rccontroller.network.server.feedback.NanoHTTPDLifecycleAware
import junit.framework.Assert.*
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
class ElectricsKtTest {

    @Mock
    private var mockedActivity: RCControllerActivity? = null

    private val serverIp = "192.168.200.245"
    private val port = 8080

    private lateinit var retrofit: Retrofit
    private lateinit var engineAPI: Engine
    private lateinit var electricsAPI: Electrics

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        engineAPI = retrofit.create<Engine>(Engine::class.java)
        electricsAPI = retrofit.create<Electrics>(Electrics::class.java)
        car.rccontroller.network.cockpit.startEngine(null, serverIp, port, engineAPI, electricsAPI)
        //throttleBrakeActionId = System.currentTimeMillis()
        //steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine(engineAPI)
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    // Main Lights
    @Test
    fun `validate that main lights are off`(){
        setMainLightsState(LIGHTS_OFF, electricsAPI)
        assertThat(getMainLightsState(electricsAPI), `is`(LIGHTS_OFF))
    }
    @Test
    fun `validate that position lights are on`(){
        setMainLightsState(POSITION_LIGHTS, electricsAPI)
        assertThat(getMainLightsState(electricsAPI), `is`(POSITION_LIGHTS))
    }
    @Test
    fun `validate that driving lights are on`(){
        setMainLightsState(POSITION_LIGHTS, electricsAPI)
        setMainLightsState(DRIVING_LIGHTS, electricsAPI)
        assertThat(getMainLightsState(electricsAPI), `is`(DRIVING_LIGHTS))
    }
    @Test
    fun `validate that long range lights are on`(){
        setMainLightsState(POSITION_LIGHTS, electricsAPI)
        setMainLightsState(DRIVING_LIGHTS, electricsAPI)
        setMainLightsState(LONG_RANGE_LIGHTS, electricsAPI)
        assertThat(getMainLightsState(electricsAPI), `is`(LONG_RANGE_LIGHTS))
    }

    // Turn Lights
    @Test
    fun `validate that turn lights are off`(){
        setDirectionLightsState(DIRECTION_LIGHTS_STRAIGHT, electricsAPI)
        assertThat(getDirectionLightsState(electricsAPI), `is`(DIRECTION_LIGHTS_STRAIGHT))
    }
    @Test
    fun `validate that left turn lights are on`(){
        setDirectionLightsState(DIRECTION_LIGHTS_LEFT, electricsAPI)
        assertThat(getDirectionLightsState(electricsAPI), `is`(DIRECTION_LIGHTS_LEFT))
    }
    @Test
    fun `validate that right turn lights are on`(){
        setDirectionLightsState(DIRECTION_LIGHTS_RIGHT, electricsAPI)
        assertThat(getDirectionLightsState(electricsAPI), `is`(DIRECTION_LIGHTS_RIGHT))
    }

    // Emergency Lights
    @Test
    fun `validate that emergency lights are off`(){
        setEmergencyLightsState(false, electricsAPI)
        assertThat(getEmergencyLightsState(electricsAPI), `is`(false))
    }
    @Test
    fun `validate that emergency lights are on`(){
        setEmergencyLightsState(true, electricsAPI)
        assertThat(getEmergencyLightsState(electricsAPI), `is`(true))
    }

    // Reverse Lights
    @Test
    fun `validate that reverse is activated`() {
        setReverseIntention (true, electricsAPI)
        assertThat(getReverseIntention(electricsAPI), `is`(true))
    }

    @Test
    fun `validate that reverse is deactivated`() {
        setReverseIntention (false, electricsAPI)
        assertThat(getReverseIntention(electricsAPI), `is`(false))
    }
}

