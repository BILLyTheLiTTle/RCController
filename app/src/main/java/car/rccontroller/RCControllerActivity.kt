package car.rccontroller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_rccontroller.*

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

        steering_seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) { seekBar.progress = 50 }
            override fun onStartTrackingTouch(seekBar: SeekBar){}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean){}
        })

        throttleNbrake_mySeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) { seekBar.progress = 50 }
            override fun onStartTrackingTouch(seekBar: SeekBar){}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean){}
        })
    }

}
