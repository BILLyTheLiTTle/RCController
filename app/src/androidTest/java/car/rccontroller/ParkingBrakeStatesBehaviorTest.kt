package car.rccontroller

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.isParkingBrakeActive
import androidx.test.espresso.Espresso.onView
import car.rccontroller.network.isEngineStarted
import org.junit.After
import org.junit.Before
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.BeforeClass


@RunWith(AndroidJUnit4::class)
@MediumTest
class ParkingBrakeStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun applyParkingBrake() {
        if (isParkingBrakeActive) {
            onView(withId(R.id.parkingBrake_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.parkingBrake_imageView))
            .perform(longClick())
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_on))))
    }

    @Test
    fun releaseParkingBrake() {
        if (!isParkingBrakeActive) {
            onView(withId(R.id.parkingBrake_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.parkingBrake_imageView))
            .perform(longClick())
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
    }

    @Test
    fun releaseParkingBrakeOnThrottle() {
        if (!isParkingBrakeActive) {
            onView(withId(R.id.parkingBrake_imageView))
                .perform(longClick())
        }
        mDevice.findObject(UiSelector().resourceId("car.rccontroller:id/throttleNbrake_mySeekBar"))
            .swipeUp(30)
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
    }

    @Test
    fun releaseParkingBrakeOnHandbrake() {
        if (!isParkingBrakeActive) {
            onView(withId(R.id.parkingBrake_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.handbrake_imageView))
            .perform(longClick())

    }

    @Test
    fun showToastOnClick() {
        onView(withId(R.id.parkingBrake_imageView))
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