package car.rccontroller

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import car.R
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.feedback.cockpit.isEngineStarted
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
        onView(withId(R.id.handling_assistance_imageView))
            .perform(longClick())

        warningState()
    }

    @Test
    fun fromWarningDownToManual() {
        onView(withId(R.id.handling_assistance_imageView))
            .perform(longClick())
        onView(withId(R.id.handling_assistance_imageView))
            .perform(click())

        manualState()
    }

    @Test
    fun fromWarningToFull() {
        for(i in 0..1)
            onView(withId(R.id.handling_assistance_imageView))
                .perform(longClick())

        fullState()
    }

    @Test
    fun fromFullDownToWarning() {
        for(i in 0..1)
            onView(withId(R.id.handling_assistance_imageView))
                .perform(longClick())
        onView(withId(R.id.handling_assistance_imageView))
            .perform(click())

        warningState()
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

    private fun manualState(){
        onView(withId(R.id.handling_assistance_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handling_assistance_manual))))
        onView(withId(R.id.cdm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cdm_off))))
        onView(withId(R.id.odm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.odm_off))))
        onView(withId(R.id.udm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.udm_off))))
        onView(withId(R.id.esm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.esm_off))))
        onView(withId(R.id.abm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.abm_off))))
        onView(withId(R.id.tcm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.tcm_off))))
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_front_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_front_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_4_locked)))))
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_rear_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_4_locked)))))
        // TODO test the suspensions when implemented
        onView(withId(R.id.suspension_front_imageView))
            .check(matches(withDrawable(R.drawable.suspension_front_off)))
        onView(withId(R.id.suspension_rear_imageView))
            .check(matches(withDrawable(R.drawable.suspension_rear_off)))
    }

    private fun warningState(){
        onView(withId(R.id.handling_assistance_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handling_assistance_warning))))
        onView(withId(R.id.cdm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cdm_idle))))
        onView(withId(R.id.odm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.odm_idle))))
        onView(withId(R.id.udm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.udm_idle))))
        onView(withId(R.id.esm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.esm_idle))))
        onView(withId(R.id.abm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.abm_idle))))
        onView(withId(R.id.tcm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.tcm_idle))))
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_front_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_front_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_4_locked)))))
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_rear_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_4_locked)))))
        // TODO test the suspensions when implemented
        onView(withId(R.id.suspension_front_imageView))
            .check(matches(withDrawable(R.drawable.suspension_front_off)))
        onView(withId(R.id.suspension_rear_imageView))
            .check(matches(withDrawable(R.drawable.suspension_rear_off)))
    }

    private fun fullState(){
        onView(withId(R.id.handling_assistance_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handling_assistance_full))))
        onView(withId(R.id.cdm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cdm_idle))))
        onView(withId(R.id.odm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.odm_idle))))
        onView(withId(R.id.udm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.udm_idle))))
        onView(withId(R.id.esm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.esm_idle))))
        onView(withId(R.id.abm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.abm_idle))))
        onView(withId(R.id.tcm_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.tcm_idle))))
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.differential_front_auto))))
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.differential_rear_auto))))
        // TODO test the suspensions when implemented
        onView(withId(R.id.suspension_front_imageView))
            .check(matches(withDrawable(R.drawable.suspension_front_off)))
        onView(withId(R.id.suspension_rear_imageView))
            .check(matches(withDrawable(R.drawable.suspension_rear_off)))
    }
}