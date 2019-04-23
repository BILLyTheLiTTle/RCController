package car.rccontroller.network.cockpit

import car.rccontroller.RCControllerActivity
import car.rccontroller.network.server.feedback.data.CarPart
import car.rccontroller.retrofit
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

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://$serverIp:$port/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        car.rccontroller.network.cockpit.startEngine(null)
        //throttleBrakeActionId = System.currentTimeMillis()
        //steeringDirectionId = System.currentTimeMillis()
    }

    @After
    fun tearDown() {
        stopEngine()
    }

    @Test
    fun sanityCheck() {
        assertNotNull(mockedActivity)
    }

    // Main Lights
    @Test
    fun `validate that main lights are off`(){
        setMainLightsState(CarPart.MainLight.LIGHTS_OFF)
        assertThat(getMainLightsState(), `is`(CarPart.MainLight.LIGHTS_OFF))
    }
    @Test
    fun `validate that position lights are on`(){
        setMainLightsState(CarPart.MainLight.POSITION_LIGHTS)
        assertThat(getMainLightsState(), `is`(CarPart.MainLight.POSITION_LIGHTS))
    }
    @Test
    fun `validate that driving lights are on`(){
        setMainLightsState(CarPart.MainLight.POSITION_LIGHTS)
        setMainLightsState(CarPart.MainLight.DRIVING_LIGHTS)
        assertThat(getMainLightsState(), `is`(CarPart.MainLight.DRIVING_LIGHTS))
    }
    @Test
    fun `validate that long range lights are on`(){
        setMainLightsState(CarPart.MainLight.POSITION_LIGHTS)
        setMainLightsState(CarPart.MainLight.DRIVING_LIGHTS)
        setMainLightsState(CarPart.MainLight.LONG_RANGE_LIGHTS)
        assertThat(getMainLightsState(), `is`(CarPart.MainLight.LONG_RANGE_LIGHTS))
    }

    // Turn Lights
    @Test
    fun `validate that turn lights are off`(){
        setDirectionLightsState(CarPart.DirectionLight.DIRECTION_LIGHTS_STRAIGHT)
        assertThat(getDirectionLightsState(), `is`(CarPart.DirectionLight.DIRECTION_LIGHTS_STRAIGHT))
    }
    @Test
    fun `validate that left turn lights are on`(){
        setDirectionLightsState(CarPart.DirectionLight.DIRECTION_LIGHTS_LEFT)
        assertThat(getDirectionLightsState(), `is`(CarPart.DirectionLight.DIRECTION_LIGHTS_LEFT))
    }
    @Test
    fun `validate that right turn lights are on`(){
        setDirectionLightsState(CarPart.DirectionLight.DIRECTION_LIGHTS_RIGHT)
        assertThat(getDirectionLightsState(), `is`(CarPart.DirectionLight.DIRECTION_LIGHTS_RIGHT))
    }

    // Emergency Lights
    @Test
    fun `validate that emergency lights are off`(){
        setEmergencyLightsState(false)
        assertThat(getEmergencyLightsState(), `is`(false))
    }
    @Test
    fun `validate that emergency lights are on`(){
        setEmergencyLightsState(true)
        assertThat(getEmergencyLightsState(), `is`(true))
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
}

