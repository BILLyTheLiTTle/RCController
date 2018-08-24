package car.rccontroller

import android.content.Context
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
import car.rccontroller.network.isEngineStarted
import android.net.wifi.WifiManager
import android.support.test.InstrumentationRegistry
import car.rccontroller.api.RCControllerActivityBehaviorTestImpl


@RunWith(AndroidJUnit4::class)
@LargeTest
class EngineStatesRCControllerActivityBehaviorTest: RCControllerActivityBehaviorTestImpl() {

    @Test
    fun showDialog_onEngineStart() {
        if (isEngineStarted) {
            onView(withId(R.id.engineStartStop_imageView))
                    .perform(longClick())
        }
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
                .check(matches(isDisplayed()))

    }

    @Test
    fun startEngineFromDialog() {
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
        // TODO check every UI item
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withTagValue(equalTo(R.drawable.engine_started_stop_action))))
        onView(withId(R.id.steering_seekBar))
                .check(matches(isEnabled()))
        onView(withId(R.id.steering_seekBar))
                .check(matches(withProgress(R.integer.default_steering)))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(isEnabled()))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(withProgress(R.integer.default_throttle_n_brake)))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.emergency_imageView))
            .check(matches(withDrawable(R.drawable.emergency_lights_off)))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_off)))
        onView(withId(R.id.reverse_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.reverse_off))))
        onView(withId(R.id.lights_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.lights_off))))
        onView(withId(R.id.cruiseControl_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cruise_control_off))))
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
        onView(withId(R.id.handbrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handbrake_off))))
        onView(withId(R.id.raspiTemp_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.raspi_temp_high)),
                withTagValue(equalTo(R.drawable.raspi_temp_medium)),
                withTagValue(equalTo(R.drawable.raspi_temp_normal)))))
        onView(withId(R.id.shiftRegisterTemp_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.shift_register_temp_high)),
                withTagValue(equalTo(R.drawable.shift_register_temp_medium)),
                withTagValue(equalTo(R.drawable.shift_register_temp_normal)))))
        onView(withId(R.id.batteryTemp_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.batteries_temp_high)),
                withTagValue(equalTo(R.drawable.batteries_temp_medium)),
                withTagValue(equalTo(R.drawable.batteries_temp_normal)))))
        onView(withId(R.id.carTemps_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.car_temps_on))))
        onView(withId(R.id.rearRightMotorTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.motor_temp_high)),
                withTagValue(equalTo(R.drawable.motor_temp_medium)),
                withTagValue(equalTo(R.drawable.motor_temp_normal)))))
        onView(withId(R.id.rearLeftMotorTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.motor_temp_high)),
                withTagValue(equalTo(R.drawable.motor_temp_medium)),
                withTagValue(equalTo(R.drawable.motor_temp_normal)))))
        onView(withId(R.id.rearHbridgeTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.h_bridge_temp_high)),
                withTagValue(equalTo(R.drawable.h_bridge_temp_medium)),
                withTagValue(equalTo(R.drawable.h_bridge_temp_normal)))))
        onView(withId(R.id.frontRightMotorTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.motor_temp_high)),
                withTagValue(equalTo(R.drawable.motor_temp_medium)),
                withTagValue(equalTo(R.drawable.motor_temp_normal)))))
        onView(withId(R.id.frontLeftMotorTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.motor_temp_high)),
                withTagValue(equalTo(R.drawable.motor_temp_medium)),
                withTagValue(equalTo(R.drawable.motor_temp_normal)))))
        onView(withId(R.id.frontHbridgeTemps_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.h_bridge_temp_high)),
                withTagValue(equalTo(R.drawable.h_bridge_temp_medium)),
                withTagValue(equalTo(R.drawable.h_bridge_temp_normal)))))
        onView(withId(R.id.cdm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.cdm_off)),
                withTagValue(equalTo(R.drawable.cdm_idle)),
                withTagValue(equalTo(R.drawable.cdm_on)))))
        onView(withId(R.id.odm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.odm_off)),
                withTagValue(equalTo(R.drawable.odm_idle)),
                withTagValue(equalTo(R.drawable.odm_on)))))
        onView(withId(R.id.udm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.udm_off)),
                withTagValue(equalTo(R.drawable.udm_idle)),
                withTagValue(equalTo(R.drawable.udm_on)))))
        onView(withId(R.id.esm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.esm_off)),
                withTagValue(equalTo(R.drawable.esm_idle)),
                withTagValue(equalTo(R.drawable.esm_on)))))
        onView(withId(R.id.abm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.abm_off)),
                withTagValue(equalTo(R.drawable.abm_idle)),
                withTagValue(equalTo(R.drawable.abm_on)))))
        onView(withId(R.id.tcm_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.tcm_off)),
                withTagValue(equalTo(R.drawable.tcm_idle)),
                withTagValue(equalTo(R.drawable.tcm_on)))))
        onView(withId(R.id.vehicle_speed_textView))
            .check(matches(withRegex("\\d+") {
                    itemText: String?, expectedRegex: String ->
                    itemText?.contains(expectedRegex.toRegex()) ?: false
            }
            ))
        onView(withId(R.id.handling_assistance_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.handling_assistance_manual)),
                withTagValue(equalTo(R.drawable.handling_assistance_warning)),
                withTagValue(equalTo(R.drawable.handling_assistance_full)))))
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_front_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_front_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_front_manual_4_locked)),
                withTagValue(equalTo(R.drawable.differential_front_auto)))))
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.differential_rear_manual_0_open)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_1_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_2_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_3_medi)),
                withTagValue(equalTo(R.drawable.differential_rear_manual_4_locked)),
                withTagValue(equalTo(R.drawable.differential_rear_auto)))))
        onView(withId(R.id.motor_speed_limiter_imageView))
            .check(matches(anyOf(
                withTagValue(equalTo(R.drawable.speed_limiter_manual_000)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_020)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_040)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_060)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_070)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_080)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_090)),
                withTagValue(equalTo(R.drawable.speed_limiter_manual_100)),
                withTagValue(equalTo(R.drawable.speed_limiter_auto)))))
        // TODO for suspensions when implemented

        // stop the engine at the end
        onView(withId(R.id.engineStartStop_imageView))
            .perform(longClick())
    }

    @Test
    fun stopEngine() {
        if (!isEngineStarted) {
            onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
            onView(withId(R.id.server_connection_dialog_layout))
                .check(matches(isDisplayed()))
            onView(withText(R.string.server_dialog_ok_button))
                .perform(click())
        }
        onView(withId(R.id.engineStartStop_imageView))
            .perform(longClick())
        // TODO check every UI item
        onView(withId(R.id.engineStartStop_imageView))
                .check(matches(withTagValue(equalTo(R.drawable.engine_stopped_start_action))))
        onView(withId(R.id.steering_seekBar))
                .check(matches(not(isEnabled())))
        onView(withId(R.id.throttleNbrake_mySeekBar))
                .check(matches(not(isEnabled())))
        onView(withId(R.id.leftTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_on)))
        onView(withId(R.id.emergency_imageView))
            .check(matches(withDrawable(R.drawable.emergency_lights_off)))
        onView(withId(R.id.rightTurn_imageView))
            .check(matches(withDrawable(R.drawable.turn_light_on)))
        onView(withId(R.id.reverse_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.reverse_off))))
        onView(withId(R.id.lights_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.lights_off))))
        onView(withId(R.id.cruiseControl_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.cruise_control_off))))
        onView(withId(R.id.parkingBrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.parking_brake_off))))
        onView(withId(R.id.handbrake_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handbrake_off))))
        onView(withId(R.id.raspiTemp_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.raspi_temp_off))))
        onView(withId(R.id.shiftRegisterTemp_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.shift_register_temp_off))))
        onView(withId(R.id.batteryTemp_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.batteries_temp_off))))
        onView(withId(R.id.carTemps_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.car_temps_off))))
        onView(withId(R.id.rearRightMotorTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
        onView(withId(R.id.rearLeftMotorTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
        onView(withId(R.id.rearHbridgeTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
        onView(withId(R.id.frontRightMotorTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
        onView(withId(R.id.frontLeftMotorTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
        onView(withId(R.id.frontHbridgeTemps_imageView))
            .check(matches(withTagValue(equalTo(android.R.color.transparent))))
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
        onView(withId(R.id.vehicle_speed_textView))
            .check(matches(withText(containsString(
                activityRule.activity.getString(R.string.tachometer_null_value)))))
        onView(withId(R.id.handling_assistance_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.handling_assistance_off))))
        onView(withId(R.id.differential_slippery_limiter_front_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.differential_front_off))))
        onView(withId(R.id.differential_slippery_limiter_rear_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.differential_rear_off))))
        onView(withId(R.id.motor_speed_limiter_imageView))
            .check(matches(withTagValue(equalTo(R.drawable.speed_limiter_off))))
        // TODO for suspensions when implemented
    }

    @Test
    fun startEngineFailed() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.context
        val wManager:WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wManager.isWifiEnabled = false
        onView(withId(R.id.engineStartStop_imageView))
                .perform(longClick())
        onView(withId(R.id.server_connection_dialog_layout))
                .check(matches(isDisplayed()))
        onView(withText(R.string.server_dialog_ok_button))
                .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.error))))
                .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
                .check(matches(isDisplayed()))

        wManager.isWifiEnabled = true
        Thread.sleep(8000)
    }

    @Test
    fun showToastOnClick() {
        onView(withId(R.id.engineStartStop_imageView))
                .perform(click())
        onView(withText(containsString(activityRule.activity.resources.getString(R.string.long_click_info))))
                .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
                .check(matches(isDisplayed()))

    }
}