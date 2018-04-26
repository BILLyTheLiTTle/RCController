package car.rccontroller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_rccontroller.*
import car.rccontroller.network.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class RCControllerActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rccontroller)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.hide()

        //setup engine start-n-stop
        engineStartStop_imageView.setOnLongClickListener { _ ->
            if(isEngineStarted) {
                val status = stopEngine()
                if (status == OK_DATA) {
                    engineStartStop_imageView.setImageResource(R.drawable.engine_stopped_start_action)
                } else {
                    Toast.makeText(this, status, Toast.LENGTH_LONG).show()
                }
            }
            else {
                //start it
                showServerConnectionDialog()
            }

            true
        }
        engineStartStop_imageView.setOnClickListener {_ ->
            Toast.makeText(this, getString(R.string.engine_info), Toast.LENGTH_SHORT).show()
            true
        }

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

    private fun showServerConnectionDialog(){
        val alert = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_server_connection, null)
        alert.apply {
            setView(dialogView)
            setTitle(getString(R.string.server_dialog_title))
            setMessage(getString(R.string.server_dialog_msg))
            setPositiveButton(getString(R.string.server_dialog_ok_button)){ _, _ ->
                val serverIp =  dialogView.findViewById<EditText>(R.id.serverIp_editText).text
                        .toString()
                val serverPort = dialogView.findViewById<EditText>(R.id.serverPort_editText2).text
                        .toString().toIntOrNull()

                val status = startEngine(serverIp, serverPort)
                if (status == OK_DATA) {
                    engineStartStop_imageView.setImageResource(R.drawable.engine_started_stop_action)
                } else {
                    Toast.makeText(context, status, Toast.LENGTH_LONG).show()
                }
            }
            setNegativeButton(getString(R.string.server_dialog_cancel_button)) { _, _ ->
                //TODO a cancel job or not todo?
            }
            show()
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            stopEngine()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.exit_info), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

}
