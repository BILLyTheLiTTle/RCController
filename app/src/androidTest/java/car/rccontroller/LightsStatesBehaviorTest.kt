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
class LightsStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    private val drawables = arrayOf(
        R.drawable.lights_off, R.drawable.lights_position,
        R.drawable.lights_driving, R.drawable.lights_long_range)

    private val maxClicks = 4

    @Test
    fun increaseLightBeam() {
        for (drawable in drawables) {
            onView(withId(R.id.lights_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(longClick())
            Thread.sleep(200)
        }
    }

    @Test
    fun decreaseLightBeam() {
        increaseLightBeam()

        for (drawable in drawables.reversedArray()) {
            onView(withId(R.id.lights_imageView))
                .check(matches(withTagValue(equalTo(drawable))))
                .perform(click())
            Thread.sleep(200)
        }
    }

    @Test
    fun showToastWhenSingleClickIsUnavailable() {
        onView(withId(R.id.lights_imageView))
            .perform(click())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.lights_off_warning
                    )
                )
            )
        )
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenLongClickIsUnavailable() {
        for(i in 0 until maxClicks)
            onView(withId(R.id.lights_imageView))
                .perform(longClick())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.long_range_lights_warning
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

        // Make sure the lights are off
        for(i in 0 until maxClicks) {
            onView(withId(R.id.lights_imageView))
                .perform(click())
            Thread.sleep(200)
        }
    }
}