package car.rccontroller

import androidx.test.espresso.Espresso.*
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
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import car.feedback.cockpit.*
import junit.framework.Assert
import org.junit.Before
import org.junit.BeforeClass
import androidx.test.uiautomator.UiSelector
import org.junit.After
import android.view.KeyEvent


@RunWith(AndroidJUnit4::class)
@MediumTest
class DeviceActionsBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun pressBackOnce() {
        mDevice.pressBack()
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.exit_info))))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
        Assert.assertFalse(activityRule.activity.isDestroyed || activityRule.activity.isFinishing)
    }

    @Test
    fun pressBackTwice() {
        mDevice.pressBack()
        mDevice.pressBack()
        Assert.assertTrue(activityRule.activity.isDestroyed || activityRule.activity.isFinishing)
    }

    @Test
    fun pressHomeAndReturnToApp() {
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
        mDevice.pressHome()
        mDevice.pressRecentApps()
        mDevice.findObject(UiSelector().description("RCController")).click()
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_on))))
    }

    @Test
    fun pressRecentAppsAndReturnToApp() {
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
        mDevice.pressRecentApps()
        mDevice.findObject(UiSelector().description("RCController")).click()
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_on))))
    }

    @Test
    fun turnOffScreenAndReturnToApp() {
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
        mDevice.pressKeyCode(KeyEvent.KEYCODE_POWER)
        Thread.sleep(1000)
        mDevice.pressKeyCode(KeyEvent.KEYCODE_POWER)
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_on))))
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