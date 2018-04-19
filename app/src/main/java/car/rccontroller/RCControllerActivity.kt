package car.rccontroller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class RCControllerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rccontroller)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.hide()

    }

}
