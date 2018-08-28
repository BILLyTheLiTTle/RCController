package car.rccontroller

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.isEngineStarted
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class DirectionLightsStatesBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun turnOnLeftDirectionLight(){

    }

    @Test
    fun turnOffLeftDirectionLight() {

    }

    @Test
    fun turnOffLeftDirectionLightWhenSteering(){

    }

    @Test
    fun turnOnRightDirectionLight(){

    }

    @Test
    fun turnOffRightDirectionLight() {

    }

    @Test
    fun turnOffRightDirectionLightWhenSteering(){

    }

    @Test
    fun showToastOnClickRightDirectionLight() {
        Espresso.onView(ViewMatchers.withId(R.id.leftTurn_imageView))
            .perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                Matchers.containsString(
                    activityRule.activity.resources.getString(
                        R.string.long_click_info
                    )
                )
            )
        )
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun showToastOnClickLeftDirectionLight() {
        Espresso.onView(ViewMatchers.withId(R.id.leftTurn_imageView))
            .perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                Matchers.containsString(
                    activityRule.activity.resources.getString(
                        R.string.long_click_info
                    )
                )
            )
        )
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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

    companion object {
        private lateinit var mDevice: UiDevice

        @BeforeClass
        @JvmStatic
        fun setUp() {
            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        }
    }
}