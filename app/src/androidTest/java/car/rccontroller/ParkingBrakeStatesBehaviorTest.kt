package car.rccontroller

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
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl


@RunWith(AndroidJUnit4::class)
@LargeTest
class ParkingBrakeStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun applyHandbrake() {


    }

    @Test
    fun releaseHandbrake() {


    }

    @Test
    fun handbrakeFailed() {

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