package car.rccontroller

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.cockpit.*
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class DifferentialStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    private val frontDiffDrawable = arrayOf(
        R.drawable.differential_front_manual_0_open, R.drawable.differential_front_manual_1_medi,
        R.drawable.differential_front_manual_2_medi, R.drawable.differential_front_manual_3_medi,
        R.drawable.differential_front_manual_4_locked
    )

    private val rearDiffDrawable = arrayOf(
        R.drawable.differential_rear_manual_0_open, R.drawable.differential_rear_manual_1_medi,
        R.drawable.differential_rear_manual_2_medi, R.drawable.differential_rear_manual_3_medi,
        R.drawable.differential_rear_manual_4_locked
    )

    private val maxClicks = 5

    @Test
    fun increaseFrontSlipperyLimiter() {
        decreaseFrontSlipperyLimiter()

        for (drawable in frontDiffDrawable) {
            onView(withId(R.id.differential_slippery_limiter_front_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(click())
            Thread.sleep(200)
        }
    }

    @Test
    fun decreaseFrontSlipperyLimiter() {
        for (drawable in frontDiffDrawable.reversedArray()) {
            onView(withId(R.id.differential_slippery_limiter_front_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(longClick())
            Thread.sleep(200)
        }
    }

    @Test
    fun increaseRearSlipperyLimiter() {
        decreaseRearSlipperyLimiter()

        for (drawable in rearDiffDrawable) {
            onView(withId(R.id.differential_slippery_limiter_rear_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(click())
            Thread.sleep(200)
        }
    }

    @Test
    fun decreaseRearSlipperyLimiter() {
        for (drawable in rearDiffDrawable.reversedArray()) {
            onView(withId(R.id.differential_slippery_limiter_rear_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(longClick())
            Thread.sleep(200)
        }
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
}