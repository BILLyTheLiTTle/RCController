package car.rccontroller.api.mymatchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.SeekBar
import org.hamcrest.Description

class ProgressMatcher internal constructor(private val expectedId: Int) : BoundedMatcher<View, SeekBar>(SeekBar::class.java) {
    private var expectedProgress: Int? = null

    override fun matchesSafely(item: SeekBar?): Boolean {
        if (item !is SeekBar) {
            return false
        }
        val resources = item.context.resources
        expectedProgress = resources.getInteger(expectedId)
        return item?.progress == expectedProgress
    }

    override fun describeTo(description: Description?) {
        description?.appendText("with expected value from resource id: ")
        description?.appendValue(expectedId)
        if (expectedProgress != null) {
            description?.appendText("[")
            description?.appendText("$expectedProgress")
            description?.appendText("]")
        }
    }
}