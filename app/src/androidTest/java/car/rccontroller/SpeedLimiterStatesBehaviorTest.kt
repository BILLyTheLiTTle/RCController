package car.rccontroller

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.isEngineStarted
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class SpeedLimiterStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    private val drawables = arrayOf(
        R.drawable.speed_limiter_manual_000, R.drawable.speed_limiter_manual_020,
        R.drawable.speed_limiter_manual_040, R.drawable.speed_limiter_manual_060,
        R.drawable.speed_limiter_manual_070, R.drawable.speed_limiter_manual_080,
        R.drawable.speed_limiter_manual_090, R.drawable.speed_limiter_manual_100)

    private val maxClicks = 8

    @Test
    fun increaseSpeedLimit() {
        decreaseSpeedLimit()

        for (drawable in drawables) {
            onView(withId(R.id.motor_speed_limiter_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(longClick())
            Thread.sleep(200)
        }
    }

    @Test
    fun decreaseSpeedLimit() {
        for (drawable in drawables.reversedArray()) {
            onView(withId(R.id.motor_speed_limiter_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(click())
            Thread.sleep(200)
        }
    }

    @Test
    fun showToastWhenSingleClickIsUnavailable() {
        for(i in 0 until maxClicks) {
            onView(withId(R.id.motor_speed_limiter_imageView))
                .perform(click())
            Thread.sleep(200)
        }
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.motor_speed_limiter_none_warning
                    )
                )
            )
        )
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenLongClickIsUnavailable() {
        onView(withId(R.id.motor_speed_limiter_imageView))
            .perform(longClick())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.motor_speed_limiter_full_warning
                    )
                )
            )
        )
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

            // Make sure the speed limiter is 100%
            for(i in 0 until maxClicks) {
                onView(withId(R.id.motor_speed_limiter_imageView))
                    .perform(click())
                Thread.sleep(200)
            }
        }
    }
}