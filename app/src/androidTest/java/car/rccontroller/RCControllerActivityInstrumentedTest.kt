package car.rccontroller

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.widget.ImageView
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
@LargeTest
class RCControllerActivityInstrumentedTest {

    private fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    @get:Rule
    var activityRule: ActivityTestRule<RCControllerActivity> = ActivityTestRule(RCControllerActivity::class.java)

    @Test
    fun `iconUpdated_EngineStarted`() {
        Espresso.onView(ViewMatchers.withId(R.id.engineStartStop_imageView))
                .perform(ViewActions.longClick())
        Thread.sleep(1000)
        Espresso.onView(withId(titleId))
                .inRoot(isDialog())
                .check(matches(withText(R.string.my_title)))
                .check(matches(isDisplayed()));

        Espresso.onView(ViewMatchers.withText("Server"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }
}