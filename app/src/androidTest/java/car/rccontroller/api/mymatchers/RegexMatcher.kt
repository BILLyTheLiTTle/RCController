package car.rccontroller.api.mymatchers

import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class RegexMatcher internal constructor(
    private val expectedRegex: String,
    private val predicate: (String?, String) -> Boolean
) : TypeSafeMatcher<View>(View::class.java) {

    private var itemText: String? = null

    override fun matchesSafely(item: View?): Boolean {
        if (item !is TextView) {
            return false
        }
        itemText = item.text.toString()
        return predicate(itemText, expectedRegex)
    }

    override fun describeTo(description: Description?) {
        description?.appendText("Item Text: ")
        description?.appendValue(itemText)
        description?.appendText(" should match expected value: ")
        description?.appendValue(expectedRegex)
    }
}