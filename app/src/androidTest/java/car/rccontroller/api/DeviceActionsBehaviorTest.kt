package car.rccontroller

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.*
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl


@RunWith(AndroidJUnit4::class)
@MediumTest
class DeviceActionsBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun pressBackOnce() {
        onView(withId(R.id.engineStartStop_imageView))
            .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.exit_info))))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun pressBackTwice() {


    }

    @Test
    fun pressHomeAndReturnToApp() {

    }

    @Test
    fun pressMenuAndReturnToApp() {


    }

    @Test
    fun turnOffScreenAndReturnToApp() {


    }
}