package car.rccontroller.api

import androidx.test.rule.ActivityTestRule
import car.rccontroller.RCControllerActivity
import org.junit.Rule

open class RCControllerActivityBehaviorTestImpl: BehaviorTest {

    @get:Rule
    var activityRule: ActivityTestRule<RCControllerActivity> = ActivityTestRule(
            RCControllerActivity::class.java,
            false,
            true)
}