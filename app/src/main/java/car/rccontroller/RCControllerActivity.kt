package car.rccontroller

import android.graphics.drawable.AnimationDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_rccontroller.*
import car.rccontroller.network.*


/**
 * An full-screen activity in landscape mode.
 */
class RCControllerActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private var cruiseControlActive = false

    private val leftTurnLightAnimation = AnimationDrawable()
    private val rightTurnLightAnimation= AnimationDrawable()
    private val emergencyLightsAnimation= AnimationDrawable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rccontroller)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            hide()
        }

        // disable them from here cuz it did not work from xml
        steering_seekBar.isEnabled = false
        throttleNbrake_mySeekBar.isEnabled = false

        //////
        //setup engine start-n-stop
        //////
        engineStartStop_imageView.apply {
            setOnLongClickListener { _ ->
                if(isEngineStarted) {
                    val status = stopEngine()
                    if (status == OK_DATA) {
                        /* This function works even after I set the server ip and port to null
                            because "bullshit".toBoolean() equals false!

                            TODO or not TODO, that's the question
                            1. There will be another function which resets the states of the variables
                            used for updating the UI items,
                            2. then call the changeInteractiveUIItemsStatus() to update the
                            UI items
                            3. and then call the stopEngine().
                         */
                        changeInteractiveUIItemsStatus()
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    //start the engine
                    showServerConnectionDialog()
                }

                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        //////
        // setup parking brake
        //////
        parkingBrake_imageView.apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    val status = activateParkingBrake(!isParkingBrakeActive)
                    if (status == OK_DATA) {
                        changeMotionInteractiveIconsStatus()
                    } else {
                        Toast.makeText(context, status, Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    changeInteractiveUIItemsStatus()
                }
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        //////
        // setup handbrake
        //////
        handbrake_imageView.apply {
            setOnTouchListener {_, event: MotionEvent ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    /* Use the serverIp variable to check if the engine is running.
                       I use the serverIp because I did not want to use a blocking network request. */
                    if(serverIp != null) {
                        handbrake_imageView.setImageResource(R.drawable.handbrake_on)
                        activateHandbrake(true)
                    }
                } else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if(serverIp != null) {
                        handbrake_imageView.setImageResource(R.drawable.handbrake_off)
                        activateHandbrake(false)
                    }
                }
                false;
            }
            //The blocking actions should not interfere with driving,
            // that's why they are on different listener
            setOnClickListener {_ ->
                changeInteractiveUIItemsStatus()
                true
            }
        }

        //////
        // setup reverse
        //////
        reverse_imageView. apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    reverseIntention = !reverseIntention
                }
                if (reverseIntention)
                    reverse_imageView.setImageResource(R.drawable.reverse_on)
                else
                    reverse_imageView.setImageResource(R.drawable.reverse_off)
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        //////
        // setup cruise control
        //////
        cc_imageView. apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    if (cruiseControlActive) {
                        Toast.makeText(context, getString(R.string.cruise_control_info), Toast.LENGTH_SHORT).show()
                    }
                    cruiseControlActive = true
                }
                if (cruiseControlActive)
                    cc_imageView.setImageResource(R.drawable.cruise_control_on)
                else
                    cc_imageView.setImageResource(R.drawable.cruise_control_off)

                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }


        //////
        // setup main lights
        //////
        val gestureDetector = GestureDetector(this,
            object: GestureDetector.SimpleOnGestureListener(){
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    if (isEngineStarted){
                        when (mainLightsState) {
                            LIGHTS_OFF -> mainLightsState = POSITION_LIGHTS
                            POSITION_LIGHTS -> mainLightsState = DRIVING_LIGHTS
                            DRIVING_LIGHTS -> mainLightsState = LONG_RANGE_LIGHTS
                            LONG_RANGE_LIGHTS -> Toast.makeText(context,
                                    getString(R.string.long_range_lights_warning),
                                    Toast.LENGTH_SHORT).show()
                        }
                        // update the icon using server info for verification
                        changeMainLightsInteractiveUIItemsStatus()
                    }
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (isEngineStarted){
                        when (mainLightsState) {
                            LONG_RANGE_LIGHTS -> mainLightsState = DRIVING_LIGHTS
                            DRIVING_LIGHTS -> mainLightsState = POSITION_LIGHTS
                            POSITION_LIGHTS -> mainLightsState = LIGHTS_OFF
                            LIGHTS_OFF ->
                                Toast.makeText(context,
                                    getString(R.string.lights_off_warning),
                                    Toast.LENGTH_SHORT).show()
                        }
                        // update the icon using server info for verification
                        changeMainLightsInteractiveUIItemsStatus()
                    }
                    return true
                }
        })
        lights_imageView. apply {
            setOnTouchListener{_, event -> gestureDetector.onTouchEvent(event);}
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    mainLightsState = LONG_RANGE_SIGNAL_LIGHTS
                }
                changeMainLightsInteractiveUIItemsStatus()
                true
            }
        }

        //////
        // setup left turn lights
        //////
        leftTurnLightAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        leftTurnLightAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        leftTurnLightAnimation.isOneShot = false
        leftTurn_imageView. apply {
            setBackgroundDrawable(leftTurnLightAnimation)
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    turnLights = TURN_LIGHTS_LEFT
                }
                changeTurnLightsInteractiveItemsStatus()
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }
        //////
        // setup right turn lights
        //////
        rightTurnLightAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        rightTurnLightAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        rightTurnLightAnimation.isOneShot = false
        rightTurn_imageView. apply {
            setBackgroundDrawable(rightTurnLightAnimation)
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    turnLights = TURN_LIGHTS_RIGHT
                }
                changeTurnLightsInteractiveItemsStatus()
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        //////
        // setup emergency lights
        //////
        emergencyLightsAnimation.addFrame(resources.getDrawable(R.drawable.emergency_lights_off),400)
        emergencyLightsAnimation.addFrame(resources.getDrawable(R.drawable.emergency_lights_on),400)
        emergencyLightsAnimation.isOneShot = false
        emergency_imageView. apply {
            setBackgroundDrawable(emergencyLightsAnimation)
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if (isEngineStarted) {
                    emergencyLights = !emergencyLights
                }
                changeInteractiveUIItemsStatus()
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        steering_seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.progress = resources.getInteger(R.integer.default_steering)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                changeInteractiveUIItemsStatus()
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> setSteering(ACTION_TURN_LEFT, progress+100) //100% left
                    10 -> setSteering(ACTION_TURN_LEFT, progress+70) //80% left
                    20 -> setSteering(ACTION_TURN_LEFT, progress+40) //60% left
                    30 -> setSteering(ACTION_TURN_LEFT, progress+10) //40% left
                    40 -> setSteering(ACTION_TURN_LEFT, progress-20) //20% left
                    50 -> {
                        setSteering(ACTION_STRAIGHT)
                        changeTurnLightsInteractiveItemsStatus()
                    } //0% means straight
                    60 -> setSteering(ACTION_TURN_RIGHT, progress-40) //20% right
                    70 -> setSteering(ACTION_TURN_RIGHT, progress-30) //40% right
                    80 -> setSteering(ACTION_TURN_RIGHT, progress-20) //60% right
                    90 -> setSteering(ACTION_TURN_RIGHT, progress-10) //80% right
                    100 -> setSteering(ACTION_TURN_RIGHT, progress) //100% right
                }
            }
        })

        throttleNbrake_mySeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if(!cruiseControlActive) {
                    seekBar.progress = resources.getInteger(R.integer.default_throttle_n_brake)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                cruiseControlActive = false

                activateHandbrake(false)
                activateParkingBrake(false)
                setBrakingStill()// or setNeutral()? TODO Will see in action

				changeInteractiveUIItemsStatus()
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean){
                val direction = if (reverseIntention) ACTION_MOVE_BACKWARD else ACTION_MOVE_FORWARD
                when (progress) {
                    0 -> setBrakingStill()
                    10 -> setNeutral()
                    20 -> setThrottleBrake(direction, progress)
                    40 -> setThrottleBrake(direction, progress)
                    50 -> setThrottleBrake(direction, progress)
                    65 -> setThrottleBrake(direction, progress)
                    75 -> setThrottleBrake(direction, progress)
                    80 -> setThrottleBrake(direction, progress)
                    85 -> setThrottleBrake(direction, progress)
                    90 -> setThrottleBrake(direction, progress)
                    95 -> setThrottleBrake(direction, progress)
                    100 -> setThrottleBrake(direction, progress)
                }
            }
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

                val status = startEngine(context, serverIp, serverPort)
                if (status == OK_DATA) {
                    changeInteractiveUIItemsStatus()
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

    /* After every interaction with interactive actions all icons must get
        their state from the server for client to be up-to-date with
        server's data.
     */
    private fun changeInteractiveUIItemsStatus(){
        if (isEngineStarted) {
            engineStartStop_imageView.setImageResource(R.drawable.engine_started_stop_action)

            steering_seekBar.isEnabled = true
            steering_seekBar.progress = resources.getInteger(R.integer.default_steering)

            throttleNbrake_mySeekBar.isEnabled = true
            throttleNbrake_mySeekBar.progress = resources.
                getInteger(R.integer.default_throttle_n_brake);

            if (reverseIntention)
                reverse_imageView.setImageResource(R.drawable.reverse_on)
            else
                reverse_imageView.setImageResource(R.drawable.reverse_off)

            if (cruiseControlActive)
                cc_imageView.setImageResource(R.drawable.cruise_control_on)
            else
                cc_imageView.setImageResource(R.drawable.cruise_control_off)

            if (emergencyLights) {
                emergencyLightsAnimation.start()
            }
            else {
                emergencyLightsAnimation.stop()
                emergencyLightsAnimation.selectDrawable(0)
            }
        }
        else {
            engineStartStop_imageView.setImageResource(R.drawable.engine_stopped_start_action)

            steering_seekBar.isEnabled = false
            throttleNbrake_mySeekBar.isEnabled = false

            reverse_imageView.setImageResource(R.drawable.reverse_off)

            //reset the cruise control flag
            cruiseControlActive = false
            cc_imageView.setImageResource(R.drawable.cruise_control_off)

            emergencyLightsAnimation.stop()
            emergencyLightsAnimation.selectDrawable(0)
        }

        changeMotionInteractiveIconsStatus()
        changeMainLightsInteractiveUIItemsStatus()
        changeTurnLightsInteractiveItemsStatus()
    }

    /* Motion interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun changeMotionInteractiveIconsStatus(){
        if(isParkingBrakeActive)
            parkingBrake_imageView.setImageResource(R.drawable.parking_brake_on)
        else
            parkingBrake_imageView.setImageResource(R.drawable.parking_brake_off)

        if(isHandbrakeActive)
            handbrake_imageView.setImageResource(R.drawable.handbrake_on)
        else
            handbrake_imageView.setImageResource(R.drawable.handbrake_off)
    }

    /* Main lights interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun changeMainLightsInteractiveUIItemsStatus(){
        when (mainLightsState) {
            LONG_RANGE_LIGHTS -> lights_imageView.setImageResource(R.drawable.lights_long_range)
            DRIVING_LIGHTS -> lights_imageView.setImageResource(R.drawable.lights_driving)
            POSITION_LIGHTS -> lights_imageView.setImageResource(R.drawable.lights_position)
            LIGHTS_OFF -> lights_imageView.setImageResource(R.drawable.lights_off)
            else -> lights_imageView.setImageResource(R.drawable.lights_off)
        }
    }

    /* Turn lights interactive items must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun changeTurnLightsInteractiveItemsStatus() {
        when (turnLights) {
            TURN_LIGHTS_STRAIGHT -> {
                leftTurnLightAnimation.stop()
                leftTurnLightAnimation.selectDrawable(0)
                rightTurnLightAnimation.stop()
                rightTurnLightAnimation.selectDrawable(0)
            }
            TURN_LIGHTS_LEFT -> {
                rightTurnLightAnimation.stop()
                rightTurnLightAnimation.selectDrawable(0)
                leftTurnLightAnimation.start()
            }
            TURN_LIGHTS_RIGHT -> {
                leftTurnLightAnimation.stop()
                leftTurnLightAnimation.selectDrawable(0)
                rightTurnLightAnimation.start()
            }
            else -> {
                leftTurnLightAnimation.stop()
                leftTurnLightAnimation.selectDrawable(1)
                rightTurnLightAnimation.stop()
                rightTurnLightAnimation.selectDrawable(1)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val status = activateParkingBrake(true)
        if(status == OK_DATA)
            parkingBrake_imageView.setImageResource(R.drawable.parking_brake_on)
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
