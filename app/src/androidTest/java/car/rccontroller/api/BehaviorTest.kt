package car.rccontroller.api

import android.view.View
import car.rccontroller.api.mymatchers.ProgressMatcher
import car.rccontroller.mymatchers.DrawableMatcher
import org.hamcrest.Matcher

interface BehaviorTest {

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun withProgress(expectedId: Int): Matcher<View> {
        return ProgressMatcher(expectedId)
    }
}