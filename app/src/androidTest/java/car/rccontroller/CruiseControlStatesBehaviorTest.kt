package car.rccontroller

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.isEngineStarted
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class CruiseControlStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun activateCruiseControl(){

    }

    @Test
    fun deactivateCruiseControl() {

    }

    @Test
    fun deactivateCruiseControlOnThrottle() {
        
    }

    @Before
    fun startEngine() {
        if (isEngineStarted) {
            Espresso.onView(ViewMatchers.withId(R.id.engineStartStop_imageView))
                .perform(ViewActions.longClick())
        }
        Espresso.onView(ViewMatchers.withId(R.id.engineStartStop_imageView))
            .perform(ViewActions.longClick())
        Espresso.onView(ViewMatchers.withId(R.id.server_connection_dialog_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.server_dialog_ok_button))
            .perform(ViewActions.click())
    }

    @After
    fun stopEngine() {
        if (isEngineStarted) {
            Espresso.onView(ViewMatchers.withId(R.id.engineStartStop_imageView))
                .perform(ViewActions.longClick())
        }
    }
}