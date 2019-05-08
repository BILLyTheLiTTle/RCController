package car.rccontroller

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.BeforeClass
import androidx.test.uiautomator.UiSelector
import car.R
import car.feedback.cockpit.isEngineStarted
import org.junit.After


@RunWith(AndroidJUnit4::class)
@MediumTest
class ReverseActionBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun successToSetUnsetReverse() {
        onView(withId(R.id.reverse_imageView))
            .perform(longClick())
        onView(withId(R.id.reverse_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.reverse_on))))
    }

    @Test
    fun failToSetUnsetReverseDueToThrottle() {
        mDevice.findObject(UiSelector().resourceId("car.rccontroller:id/throttleNbrake_mySeekBar"))
            .swipeUp(30)
        onView(withId(R.id.reverse_imageView))
            .perform(longClick())
        onView(withId(R.id.reverse_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.reverse_off))))
    }

    @Before
    fun startEngine() {
        /*if (isEngineStarted()) {
            onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        }*/
        onView(withId(R.id.engineStartStop_imageView))
            .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
            .check(matches(isDisplayed()))
        onView(withText(R.string.server_dialog_ok_button))
            .perform(click())
    }

    @After
    fun stopEngine() {
        if (isEngineStarted()) {
            onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        }
    }

    companion object {
        private lateinit var mDevice: UiDevice

        @BeforeClass
        @JvmStatic
        fun setUp() {
            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        }
    }
}