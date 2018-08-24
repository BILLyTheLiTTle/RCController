package car.rccontroller.api

import android.view.View
import car.rccontroller.api.mymatchers.ProgressMatcher
import car.rccontroller.api.mymatchers.RegexMatcher
import car.rccontroller.mymatchers.DrawableMatcher
import org.hamcrest.Matcher

interface BehaviorTest {

    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun withProgress(expectedId: Int): Matcher<View> {
        return ProgressMatcher(expectedId)
    }

    fun withRegex(expectedRegex: String, predicate: (String?, String) -> Boolean): Matcher<View> {
        return RegexMatcher(expectedRegex, predicate)
    }
}