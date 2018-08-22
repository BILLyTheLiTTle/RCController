package car.rccontroller

import android.content.Context
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import car.rccontroller.network.isEngineStarted
import android.net.wifi.WifiManager
import android.support.test.InstrumentationRegistry
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl


@RunWith(AndroidJUnit4::class)
@LargeTest
class EngineStatesRCControllerActvityBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun showDialog_onEngineStart() {
        if (isEngineStarted) {
            onView(withId(R.id.engineStartStop_imageView))
                    .perform(longClick())
        }
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
                .check(matches(isDisplayed()))

    }

    @Test
    fun startEngineFromDialog() {
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
        // TODO check every UI item
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withDrawable(R.drawable.engine_started_stop_action)))
        onView(withId(R.id.steering_seekBar))
                .check(matches(isEnabled()))
        onView(withId(R.id.steering_seekBar))
                .check(matches(withProgress(R.integer.default_steering)))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(isEnabled()))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(withProgress(R.integer.default_throttle_n_brake)))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))

    }

    @Test
    fun stopEngine() {
        if (!isEngineStarted) {
            startEngineFromDialog()
        }
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        // TODO check every UI item
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withDrawable(R.drawable.engine_stopped_start_action)))
        onView(withId(R.id.steering_seekBar))
                .check(matches(not(isEnabled())))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(not(isEnabled())))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_on)))

    }

    @Test
    fun startEngineFailed() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.context
        val wManager:WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wManager.isWifiEnabled = false
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
                .check(matches(isDisplayed()))
        onView(withText(R.string.server_dialog_ok_button))
                .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.error))))
                .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
                .check(matches(isDisplayed()))

        wManager.isWifiEnabled = true
        Thread.sleep(8000)
    }

    @Test
    fun showToast_onClick() {
        onView(withId(R.id.engineStartStop_imageView))
                .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.long_click_info))))
                .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
                .check(matches(isDisplayed()))

    }
}