package car.rccontroller

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.RootMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.util.Log
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl
import car.rccontroller.network.*
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
        if(turnLights == TURN_LIGHTS_LEFT) {
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
        if(turnLights == TURN_LIGHTS_STRAIGHT) {
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
        if(turnLights == TURN_LIGHTS_RIGHT) {
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
        if(turnLights == TURN_LIGHTS_STRAIGHT) {
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
        if(turnLights == TURN_LIGHTS_STRAIGHT) {
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
        if(turnLights == TURN_LIGHTS_STRAIGHT) {
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

    companion object {
        private lateinit var mDevice: UiDevice

        @BeforeClass
        @JvmStatic
        fun setUp() {
            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        }
    }
}