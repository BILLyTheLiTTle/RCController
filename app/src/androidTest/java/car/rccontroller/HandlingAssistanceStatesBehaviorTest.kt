package car.rccontroller

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.*
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class HandlingAssistanceStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun fromManualToWarning() {

    }

    @Test
    fun fromWarningDownToManual() {

    }

    @Test
    fun fromWarningToFull() {

    }

    @Test
    fun fromFullDownToWarning() {

    }

    @Test
    fun showToastWhenSingleClickIsUnavailable() {
        onView(withId(R.id.handling_assistance_imageView))
            .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.handling_assistance_none_warning))))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastWhenLongClickIsUnavailable() {
        for(i in 0..2)
            onView(withId(R.id.handling_assistance_imageView))
                .perform(longClick())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.handling_assistance_full_warning))))
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