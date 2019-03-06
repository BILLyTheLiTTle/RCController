package car.rccontroller

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.isEngineStarted
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CruiseControlStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun activateCruiseControl(){
        onView(withId(R.id.cruiseControl_imageView))
            .perform(longClick()) // activate it
            .check(matches(withTagValue(equalTo(R.drawable.cruise_control_on))))
    }

    @Test
    fun showToastOnDeactivationLongClick() {
        onView(withId(R.id.cruiseControl_imageView))
            .perform(longClick()) // activate it
            .perform(longClick()) // deactivate it
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.cruise_control_info))))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deactivateCruiseControlOnThrottle() {
        activateCruiseControl()

        mDevice.findObject(UiSelector().resourceId("car.rccontroller:id/throttleNbrake_mySeekBar"))
            .swipeUp(30)
        onView(withId(R.id.cruiseControl_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cruise_control_off))))
    }

    @Test
    fun showToastOnClick() {
        onView(withId(R.id.cruiseControl_imageView))
            .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.long_click_info))))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))

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