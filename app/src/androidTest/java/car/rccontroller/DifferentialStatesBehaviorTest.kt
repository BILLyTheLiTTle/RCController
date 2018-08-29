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
class DifferentialStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    val frontDiffDrawable = arrayOf(
        R.drawable.differential_front_manual_0_open, R.drawable.differential_front_manual_1_medi,
        R.drawable.differential_front_manual_2_medi, R.drawable.differential_front_manual_3_medi,
        R.drawable.differential_front_manual_4_locked
    )

    val rearDiffDrawable = arrayOf(
        R.drawable.differential_rear_manual_0_open, R.drawable.differential_rear_manual_1_medi,
        R.drawable.differential_rear_manual_2_medi, R.drawable.differential_rear_manual_3_medi,
        R.drawable.differential_rear_manual_4_locked
    )

    val maxClicks = 5

    @Test
    fun increaseFrontSlipperyLimiter() {

    }

    @Test
    fun decreaseFrontSlipperyLimiter() {

    }

    @Test
    fun increaseRearSlipperyLimiter() {

    }

    @Test
    fun decreaseRearSlipperyLimiter() {

    }

    @Test
    fun showToastWhenSingleClickIsUnavailableFront() {
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .perform(click())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.differential_slippery_limiter_locked_warning
                    )
                )
            )
        )
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenLongClickIsUnavailableFront() {
        for (i in 0 until maxClicks) {
            onView(withId(R.id.differential_slippery_limiter_front_imageView))
                .perform(longClick())
            Thread.sleep(200)
        }
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.differential_slippery_limiter_open_warning
                    )
                )
            )
        )
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenSingleClickIsUnavailableRear() {
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .perform(click())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.differential_slippery_limiter_locked_warning
                    )
                )
            )
        )
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenLongClickIsUnavailableRear() {
        for (i in 0 until maxClicks) {
            onView(withId(R.id.differential_slippery_limiter_rear_imageView))
                .perform(longClick())
            Thread.sleep(200)
        }
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.differential_slippery_limiter_open_warning
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
        }
    }
}