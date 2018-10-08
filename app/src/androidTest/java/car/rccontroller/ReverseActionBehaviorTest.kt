package car.rccontroller

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.UiDevice
import car.rccontroller.network.isEngineStarted
import junit.framework.Assert
import org.junit.Before
import org.junit.BeforeClass
import android.support.test.uiautomator.UiSelector
import org.junit.After
import android.view.KeyEvent
import kotlinx.coroutines.experimental.launch


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
        if (isEngineStarted) {
            onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.engineStartStop_imageView))
            .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
            .check(matches(isDisplayed()))
        onView(withText(R.string.server_dialog_ok_button))
            .perform(click())
    }

    @After
    fun stopEngine() {
        if (isEngineStarted) {
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