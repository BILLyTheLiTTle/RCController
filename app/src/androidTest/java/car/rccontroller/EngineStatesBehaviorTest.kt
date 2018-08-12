package car.rccontroller

import android.content.Context
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import car.rccontroller.mymatchers.DrawableMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import car.rccontroller.network.isEngineStarted
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.support.test.InstrumentationRegistry






@RunWith(AndroidJUnit4::class)
@LargeTest
class EngineStatesBehaviorTest {

    private fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    @get:Rule
    var activityRule: ActivityTestRule<RCControllerActivity> = ActivityTestRule(
            RCControllerActivity::class.java,
            false,
            true)

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
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withDrawable(R.drawable.engine_started_stop_action)))

    }

    @Test
    fun stopEngine() {
        if (!isEngineStarted) {
            startEngineFromDialog()
        }
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withDrawable(R.drawable.engine_stopped_start_action)))

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
                .inRoot(RootMatchers.withDecorView(not(`is`(activityRule.activity.window.decorView))))
                .check(matches(isDisplayed()))

        wManager.isWifiEnabled = true
        Thread.sleep(5000)
    }
}