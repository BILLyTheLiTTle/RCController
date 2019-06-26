package car.rccontroller

import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import car.R
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.feedback.cockpit.*
import org.hamcrest.Matchers.*
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
        if(getDirectionLightsState() == CorneringLight.LEFT_LIGHTS) {
            // turn them off
            onView(withId(R.id.leftTurn_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.leftTurn_imageView))
            .perform(longClick())
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(anyOf(
                withDrawable(R.drawable.turn_light_on),
                withDrawable(R.drawable.turn_light_off)
            )))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
    }

    @Test
    fun turnOffLeftDirectionLight() {
        if(getDirectionLightsState() == CorneringLight.STRAIGHT_LIGHTS) {
            // turn them on
            onView(withId(R.id.leftTurn_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.leftTurn_imageView))
            .perform(longClick())
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(anyOf(
                withDrawable(R.drawable.turn_light_on),
                withDrawable(R.drawable.turn_light_off)
            )))
    }

    @Test
    fun turnOnRightDirectionLight(){
        if(getDirectionLightsState() == CorneringLight.RIGHT_LIGHTS) {
            // turn them off
            onView(withId(R.id.rightTurn_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.rightTurn_imageView))
            .perform(longClick())
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(anyOf(
                withDrawable(R.drawable.turn_light_on),
                withDrawable(R.drawable.turn_light_off)
            )))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
    }

    @Test
    fun turnOffRightDirectionLight() {
        if(getDirectionLightsState() == CorneringLight.STRAIGHT_LIGHTS) {
            // turn them on
            onView(withId(R.id.rightTurn_imageView))
                .perform(longClick())
        }
        onView(withId(R.id.rightTurn_imageView))
            .perform(longClick())
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(anyOf(
                withDrawable(R.drawable.turn_light_on),
                withDrawable(R.drawable.turn_light_off)
            )))
    }

    @Test
    fun turnOffLeftDirectionLightWhenSteering(){
        if(getDirectionLightsState() == CorneringLight.STRAIGHT_LIGHTS) {
            // turn them on
            onView(withId(R.id.leftTurn_imageView))
                .perform(click())
            onView(withId(R.id.leftTurn_imageView))
                .perform(longClick())
        }
        mDevice.findObject(UiSelector().resourceId("car.rccontroller:id/steering_seekBar"))
            .swipeLeft(30)
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
    }

    @Test
    fun turnOffRightDirectionLightWhenSteering(){
        if(getDirectionLightsState() == CorneringLight.STRAIGHT_LIGHTS) {
            // turn them on
            onView(withId(R.id.rightTurn_imageView))
                .perform(click())
            onView(withId(R.id.rightTurn_imageView))
                .perform(longClick())
        }
        mDevice.findObject(UiSelector().resourceId("car.rccontroller:id/steering_seekBar"))
            .swipeRight(30)
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
    }


    @Test
    fun showToastOnClickLeftDirectionLight() {
        onView(withId(R.id.leftTurn_imageView))
            .perform(click())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.long_click_info
                    )
                )
            )
        )
            .inRoot(RootMatchers.withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showToastOnClickRightDirectionLight() {
        onView(withId(R.id.rightTurn_imageView))
            .perform(click())
        onView(
            withText(
                containsString(
                    activityRule.activity.resources.getString(
                        R.string.long_click_info
                    )
                )
            )
        )
            .inRoot(RootMatchers.withDecorView(not(activityRule.activity.window.decorView)))
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

    companion object {
        private lateinit var mDevice: UiDevice

        @BeforeClass
        @JvmStatic
        fun setUp() {
            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        }
    }
}