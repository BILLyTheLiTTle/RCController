package car.rccontroller

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_rccontroller.*
import car.rccontroller.network.*
import car.rccontroller.network.server.feedback.SensorFeedbackServer


val RUN_ON_EMULATOR = Build.FINGERPRINT.contains("generic")
/**
 * An full-screen activity in landscape mode.
 */
class RCControllerActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private var cruiseControlActive = false

    private val leftTurnLightsAnimation = AnimationDrawable()
    private val rightTurnLightsAnimation= AnimationDrawable()
    private val emergencyLightsAnimation= AnimationDrawable()

    override fun onCreate(savedInstanceState: Bundle?) {
        // declare local function
        fun throttle(progress: Int){
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
                    if (status == OK_STRING) {
                        /* This code block works even after I set the server ip and port to null
                            because "bullshit".toBoolean() equals false!

                            A problem is when the client waits real String data
                            (for example lights_off). In these situation I fall into the
                            "else" code blocks.

                            TODO or not TODO, that's the question
                            1. There will be another function which resets the states of the variables
                            used for updating the UI items,
                            2. then call the changeInteractiveUIItemsStatus() to update the
                            UI items
                            3. and then call the stopEngine().
                         */
                        resetUI()
                    } else {
                        Toast.makeText(context, "${resources.getString(R.string.error)}: $status",
                                Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    //start the engine
                    showServerConnectionDialog(this@RCControllerActivity)
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
                    if (status == OK_STRING) {
                        updateMotionUIItems()
                    } else {
                        Toast.makeText(context, "${resources.getString(R.string.error)}: $status",
                                Toast.LENGTH_LONG).show()
                    }
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
                    if(raspiServerIp != null) {
                        handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
                        activateHandbrake(true)
                    }
                } else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if(raspiServerIp != null) {
                        handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_off)
                        activateHandbrake(false)
                        // re-throttle automatically to start moving the rear wheels again
                        throttle(throttleNbrake_mySeekBar.progress)
                    }
                }
                false;
            }
            //The blocking actions should not interfere with driving,
            // that's why they are on different listener
            setOnClickListener {_ ->
                updateMotionUIItems()
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
                    reverse_imageView.setImageResourceWithTag(R.drawable.reverse_on)
                else
                    reverse_imageView.setImageResourceWithTag(R.drawable.reverse_off)
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
        cruiseControl_imageView. apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    if (cruiseControlActive) {
                        Toast.makeText(context, getString(R.string.cruise_control_info), Toast.LENGTH_SHORT).show()
                    }
                    cruiseControlActive = true
                }
                if (cruiseControlActive)
                    cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_on)
                else
                    cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_off)

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
                    // If, for any reason, engine is stopped I should not do anything
                    if(isEngineStarted) {
                        mainLightsState = LONG_RANGE_SIGNAL_LIGHTS
                    }
                    updateMainLightsUIItems()
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
                        updateMainLightsUIItems()
                    }
                    return true
                }
        })
        lights_imageView. apply {
            setOnTouchListener{_, event -> gestureDetector.onTouchEvent(event);}
            setOnLongClickListener { _ ->
                if (isEngineStarted){
                    when (mainLightsState) {
                        LIGHTS_OFF -> mainLightsState = POSITION_LIGHTS
                        POSITION_LIGHTS -> mainLightsState = DRIVING_LIGHTS
                        DRIVING_LIGHTS -> mainLightsState = LONG_RANGE_LIGHTS
                        LONG_RANGE_LIGHTS -> Toast.makeText(car.rccontroller.network.context,
                                getString(R.string.long_range_lights_warning),
                                Toast.LENGTH_SHORT).show()
                    }
                    // update the icon using server info for verification
                    updateMainLightsUIItems()
                }
                true
            }
        }

        //////
        // setup left turn lights
        //////
        leftTurnLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        leftTurnLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        leftTurnLightsAnimation.isOneShot = false
        leftTurn_imageView. apply {
            setBackgroundDrawable(leftTurnLightsAnimation)
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    turnLights = TURN_LIGHTS_LEFT
                }
                updateTurnLightsUIItems()
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
        rightTurnLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        rightTurnLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        rightTurnLightsAnimation.isOneShot = false
        rightTurn_imageView. apply {
            setBackgroundDrawable(rightTurnLightsAnimation)
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    turnLights = TURN_LIGHTS_RIGHT
                }
                updateTurnLightsUIItems()
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
                if (emergencyLights) {
                    emergencyLightsAnimation.start()
                }
                else {
                    emergencyLightsAnimation.stop()
                    emergencyLightsAnimation.selectDrawable(0)
                }
                true
            }
            setOnClickListener {_ ->
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                true
            }
        }

        //////
        // setup handling assistance
        //////
        handling_assistance_imageView.apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    when (handlingAssistanceState) {
                        ASSISTANCE_NONE -> handlingAssistanceState = ASSISTANCE_WARNING
                        ASSISTANCE_WARNING -> handlingAssistanceState = ASSISTANCE_FULL
                        ASSISTANCE_FULL ->
                            Toast.makeText(context,
                                    getString(R.string.handling_assistance_full_warning),
                                    Toast.LENGTH_SHORT).show()
                    }
                    updateHandlingAssistanceUIItem()
                }
                true
            }
            setOnClickListener {_ ->
                when (handlingAssistanceState) {
                    ASSISTANCE_FULL -> handlingAssistanceState = ASSISTANCE_WARNING
                    ASSISTANCE_WARNING -> handlingAssistanceState = ASSISTANCE_NONE
                    ASSISTANCE_NONE ->
                        Toast.makeText(context,
                                getString(R.string.handling_assistance_none_warning),
                                Toast.LENGTH_SHORT).show()
                }
                updateHandlingAssistanceUIItem()
                true
            }
        }

        //////
        // setup motor speed limiter
        //////
        motor_speed_limiter_imageView.apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    when (motorSpeedLimiter) {
                        MOTOR_SPEED_LIMITER_NO_SPEED ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_1
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_2
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_1
                        MOTOR_SPEED_LIMITER_FAST_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_2
                        MOTOR_SPEED_LIMITER_FAST_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_FULL_SPEED
                        MOTOR_SPEED_LIMITER_FULL_SPEED -> Toast.
                            makeText(context,
                            getString(R.string.motor_speed_limiter_full_warning),
                            Toast.LENGTH_SHORT
                            ).show()
                    }
                    updateMotorSpeedLimiterUIItem()
                }
                true
            }
            setOnClickListener {_ ->
                if(isEngineStarted) {
                    when (motorSpeedLimiter) {
                        MOTOR_SPEED_LIMITER_FULL_SPEED ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_2
                        MOTOR_SPEED_LIMITER_FAST_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_FAST_SPEED_1
                        MOTOR_SPEED_LIMITER_FAST_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_2
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_2 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_SLOW_SPEED_1
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_1 ->
                            motorSpeedLimiter = MOTOR_SPEED_LIMITER_NO_SPEED
                        MOTOR_SPEED_LIMITER_NO_SPEED -> Toast.
                            makeText(context,
                                getString(R.string.motor_speed_limiter_none_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    updateMotorSpeedLimiterUIItem()
                }
                true
            }
        }

        //////
        // setup front differential slippery limiter
        //////
        differential_slippery_limiter_front_imageView.apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    if (handlingAssistanceState != ASSISTANCE_FULL) {
                        when (currentFrontDifferentialSlipperyLimiter) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_OPEN
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_open_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousFrontDifferentialSlipperyLimiter =
                                    currentFrontDifferentialSlipperyLimiter
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateFrontDifferentialSlipperyLimiterUIItem()
                }
                true
            }
            setOnClickListener {_ ->
                if(isEngineStarted) {
                    if (handlingAssistanceState != ASSISTANCE_FULL) {
                        when (currentFrontDifferentialSlipperyLimiter) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                currentFrontDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_locked_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousFrontDifferentialSlipperyLimiter =
                                currentFrontDifferentialSlipperyLimiter
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateFrontDifferentialSlipperyLimiterUIItem()
                }
                true
            }
        }

        //////
        // setup rear differential slippery limiter
        //////
        differential_slippery_limiter_rear_imageView.apply {
            setOnLongClickListener { _ ->
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted) {
                    if (handlingAssistanceState != ASSISTANCE_FULL) {
                        when (currentRearDifferentialSlipperyLimiter) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_OPEN
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_open_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousRearDifferentialSlipperyLimiter =
                                currentRearDifferentialSlipperyLimiter
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateRearDifferentialSlipperyLimiterUIItem()
                }
                true
            }
            setOnClickListener {_ ->
                if(isEngineStarted) {
                    if (handlingAssistanceState != ASSISTANCE_FULL) {
                        when (currentRearDifferentialSlipperyLimiter) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                currentRearDifferentialSlipperyLimiter =
                                        DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_locked_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousRearDifferentialSlipperyLimiter =
                                currentRearDifferentialSlipperyLimiter
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateRearDifferentialSlipperyLimiterUIItem()
                }
                true
            }
        }

        steering_seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.progress = resources.getInteger(R.integer.default_steering)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //changeInteractiveUIItemsStatus()
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
                        updateTurnLightsUIItems()
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
                /* It would be better to let the car be at braking still status than neutral
                    because the car may be stopped in uphill or downhill and could slide down
                    when the user touches the slider.
                */
                setBrakingStill()

                updateMotionUIItems()
            }
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean){
                throttle(progress)
            }
        })
    }

    private fun showServerConnectionDialog(activity: RCControllerActivity){
        val alert = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_server_connection, null)
        alert.apply {
            setView(dialogView)
            setTitle(getString(R.string.server_dialog_title))
            setMessage(getString(R.string.server_dialog_msg))
            setPositiveButton(getString(R.string.server_dialog_ok_button)){ _, _ ->
                val raspiServerIp =
                        if (RUN_ON_EMULATOR)
                            "10.0.2.2"
                        else
                            dialogView.findViewById<EditText>(R.id.serverIp_editText).text
                                    .toString()
                val raspiServerPort = dialogView.findViewById<EditText>(R.id.serverPort_editText).text
                        .toString().toIntOrNull()

                val status = startEngine(activity, raspiServerIp, raspiServerPort)
                if (status == OK_STRING) {
                    resetUI()
                } else {
                    Toast.makeText(context, "${resources.getString(R.string.error)}: $status",
                            Toast.LENGTH_LONG).show()
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

        This function should be called for initial set-up at the beginning and
        resetting at the end.
     */
    private fun resetUI(){
        if (isEngineStarted) {
            engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_started_stop_action)

            steering_seekBar.isEnabled = true
            steering_seekBar.progress = resources.getInteger(R.integer.default_steering)

            throttleNbrake_mySeekBar.isEnabled = true
            throttleNbrake_mySeekBar.progress = resources.
                getInteger(R.integer.default_throttle_n_brake);

            if (reverseIntention)
                reverse_imageView.setImageResourceWithTag(R.drawable.reverse_on)
            else
                reverse_imageView.setImageResourceWithTag(R.drawable.reverse_off)

            if (emergencyLights) {
                emergencyLightsAnimation.start()
            }
            else {
                emergencyLightsAnimation.stop()
                emergencyLightsAnimation.selectDrawable(0)
            }

            updateSpeedUIItem("${resources.getString(R.string.tachometer_value)}")
        }
        else {
            engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_stopped_start_action)

            steering_seekBar.isEnabled = false
            throttleNbrake_mySeekBar.isEnabled = false

            reverse_imageView.setImageResourceWithTag(R.drawable.reverse_off)

            //reset the cruise control flag
            cruiseControlActive = false
            cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_off)

            emergencyLightsAnimation.stop()
            emergencyLightsAnimation.selectDrawable(0)

            updateTempUIItems(
                rearLeftMotor = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                rearRightMotor = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                frontLeftMotor = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                frontRightMotor = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                rearHBridge = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                frontHBridge = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                raspberryPi = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                batteries = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                shiftRegisters = SensorFeedbackServer.WARNING_TYPE_NOTHING
            )

            updateSpeedUIItem(getString(R.string.tachometer_null_value))
        }

        updateMotionUIItems()
        updateMainLightsUIItems()
        updateTurnLightsUIItems()
        // The following function is updating some other ImageViews and more
        updateHandlingAssistanceUIItem()
        updateMotorSpeedLimiterUIItem()
    }

    /* Motion interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotionUIItems(){
        if(isParkingBrakeActive)
            parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_on)
        else
            parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_off)

        if(isHandbrakeActive)
            handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
        else
            handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_off)

        if (cruiseControlActive)
            cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_on)
        else
            cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_off)
    }

    /* Rear differential slippery limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateRearDifferentialSlipperyLimiterUIItem(){
        when (currentRearDifferentialSlipperyLimiter) {
            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_manual_0_open)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_manual_1_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_manual_2_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_manual_3_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_manual_4_locked)
            DIFFERENTIAL_SLIPPERY_LIMITER_AUTO -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_auto)
        //null -> differential_slippery_limiter_rear_imageView.
        //   setImageResourceWithTag(R.drawable.differential_rear_off)
            else -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_off)
        }
    }

    /* Front differential slippery limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateFrontDifferentialSlipperyLimiterUIItem(){
        when (currentFrontDifferentialSlipperyLimiter) {
            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_manual_0_open)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_manual_1_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_manual_2_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_manual_3_medi)
            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_manual_4_locked)
            DIFFERENTIAL_SLIPPERY_LIMITER_AUTO -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_auto)
            //null -> differential_slippery_limiter_front_imageView.
             //   setImageResourceWithTag(R.drawable.differential_front_off)
            else -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_off)
        }
    }

    /* Motor speed limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotorSpeedLimiterUIItem(){
        when (motorSpeedLimiter) {
            MOTOR_SPEED_LIMITER_FULL_SPEED -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_100)
            MOTOR_SPEED_LIMITER_FAST_SPEED_2 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_090)
            MOTOR_SPEED_LIMITER_FAST_SPEED_1 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_080)
            MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_070)
            MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_060)
            MOTOR_SPEED_LIMITER_SLOW_SPEED_2 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_040)
            MOTOR_SPEED_LIMITER_SLOW_SPEED_1 -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_020)
            MOTOR_SPEED_LIMITER_NO_SPEED -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_manual_000)
            //null -> motor_speed_limiter_imageView.
            //    setImageResourceWithTag(R.drawable.speed_limiter_off)
            else -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_off)
        }
    }

    /* Handling assistance interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.

        Also, here are updated the ImageViews of the items, which are connected
        to handling assistance (ex. differential, suspension) and some of their
        functionality values.
     */
    private fun updateHandlingAssistanceUIItem(){
        when (handlingAssistanceState) {
            ASSISTANCE_FULL -> {
                currentFrontDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_AUTO
                currentRearDifferentialSlipperyLimiter = DIFFERENTIAL_SLIPPERY_LIMITER_AUTO
                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_full)
                updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                abmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                esmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                udmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                odmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                cdmState = SensorFeedbackServer.MODULE_IDLE_STATE)
            }
            ASSISTANCE_WARNING -> {
                currentFrontDifferentialSlipperyLimiter = previousFrontDifferentialSlipperyLimiter
                currentRearDifferentialSlipperyLimiter = previousRearDifferentialSlipperyLimiter
                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_warning)
                updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        abmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        esmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        udmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        odmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        cdmState = SensorFeedbackServer.MODULE_IDLE_STATE)
            }
            ASSISTANCE_NONE -> {
                currentFrontDifferentialSlipperyLimiter = previousFrontDifferentialSlipperyLimiter
                currentRearDifferentialSlipperyLimiter = previousRearDifferentialSlipperyLimiter
                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_manual)
                updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        abmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        esmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        udmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        odmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        cdmState = SensorFeedbackServer.MODULE_OFF_STATE)
            }
            else -> {
                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_off)
                updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        abmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        esmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        udmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        odmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        cdmState = SensorFeedbackServer.MODULE_OFF_STATE)
            }
        }
        updateFrontDifferentialSlipperyLimiterUIItem()
        updateRearDifferentialSlipperyLimiterUIItem()
    }

    /* Advanced sensor non-interactive images are updated by server actions
        which are sent to the client when the Raspi have to.

        Also, here we update the ImageViews of the items, of the advanced sensors
        according to the initial (off, idle) state only.
     */
    fun updateAdvancedSensorUIItems(
            tcmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE,
            abmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE,
            esmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE,
            udmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE,
            odmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE,
            cdmState: String = SensorFeedbackServer.MODULE_UNCHANGED_STATE){

        fun updateItems(
                moduleState: String,
                moduleUI: ImageView,
                moduleOnDrawable: Int,
                moduleIdleDrawable: Int,
                moduleOffDrawable: Int) {
            runOnUiThread {
                when (moduleState) {
                    SensorFeedbackServer.MODULE_ON_STATE ->
                        moduleUI.setImageResourceWithTag(moduleOnDrawable)
                    SensorFeedbackServer.MODULE_IDLE_STATE ->
                        moduleUI.setImageResourceWithTag(moduleIdleDrawable)
                    SensorFeedbackServer.MODULE_OFF_STATE ->
                        moduleUI.setImageResourceWithTag(moduleOffDrawable)
                    SensorFeedbackServer.MODULE_NOTHING_STATE ->
                        moduleUI.visibility = View.INVISIBLE
                }
            }
        }

        updateItems(tcmState, tcm_imageView,
                R.drawable.tcm_on, R.drawable.tcm_idle, R.drawable.tcm_off)
        updateItems(abmState, abm_imageView,
                R.drawable.abm_on, R.drawable.abm_idle, R.drawable.abm_off)
        updateItems(esmState, esm_imageView,
                R.drawable.esm_on, R.drawable.esm_idle, R.drawable.esm_off)
        updateItems(udmState, udm_imageView,
                R.drawable.udm_on, R.drawable.udm_idle, R.drawable.udm_off)
        updateItems(odmState, odm_imageView,
                R.drawable.odm_on, R.drawable.odm_idle, R.drawable.odm_off)
        updateItems(cdmState, cdm_imageView,
                R.drawable.cdm_on, R.drawable.cdm_idle, R.drawable.cdm_off)
    }

    /* Main lights interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMainLightsUIItems(){
        when (mainLightsState) {
            LONG_RANGE_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_long_range)
            DRIVING_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_driving)
            POSITION_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_position)
            LIGHTS_OFF -> lights_imageView.setImageResourceWithTag(R.drawable.lights_off)
            else -> lights_imageView.setImageResourceWithTag(R.drawable.lights_off)
        }
    }

    /* Turn lights interactive items must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateTurnLightsUIItems() {
        when (turnLights) {
            TURN_LIGHTS_STRAIGHT -> {
                leftTurnLightsAnimation.stop()
                leftTurnLightsAnimation.selectDrawable(0)
                rightTurnLightsAnimation.stop()
                rightTurnLightsAnimation.selectDrawable(0)
            }
            TURN_LIGHTS_LEFT -> {
                rightTurnLightsAnimation.stop()
                rightTurnLightsAnimation.selectDrawable(0)
                leftTurnLightsAnimation.start()
            }
            TURN_LIGHTS_RIGHT -> {
                leftTurnLightsAnimation.stop()
                leftTurnLightsAnimation.selectDrawable(0)
                rightTurnLightsAnimation.start()
            }
            else -> {
                leftTurnLightsAnimation.stop()
                leftTurnLightsAnimation.selectDrawable(1)
                rightTurnLightsAnimation.stop()
                rightTurnLightsAnimation.selectDrawable(1)
            }
        }
    }

    /* This function is for setting and resetting purposes.
     */
    fun updateTempUIItems(
        rearLeftMotor: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        rearRightMotor: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        frontLeftMotor: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        frontRightMotor: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        rearHBridge: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        frontHBridge: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        raspberryPi: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        batteries: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED,
        shiftRegisters: String = SensorFeedbackServer.WARNING_TYPE_UNCHANGED
    ) {
        runOnUiThread {
            if (rearLeftMotor == SensorFeedbackServer.WARNING_TYPE_NOTHING &&
                rearRightMotor == SensorFeedbackServer.WARNING_TYPE_NOTHING &&
                frontLeftMotor == SensorFeedbackServer.WARNING_TYPE_NOTHING &&
                frontRightMotor == SensorFeedbackServer.WARNING_TYPE_NOTHING &&
                rearHBridge == SensorFeedbackServer.WARNING_TYPE_NOTHING)
                carTemps_imageView.setImageResourceWithTag(R.drawable.car_temps_off)
            else
                carTemps_imageView.setImageResourceWithTag(R.drawable.car_temps_on)

            when (rearLeftMotor) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    rearLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    rearLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    rearLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    rearLeftMotorTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (rearRightMotor) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    rearRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    rearRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    rearRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    rearRightMotorTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (frontLeftMotor) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    frontLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    frontLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    frontLeftMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    frontLeftMotorTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (frontRightMotor) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    frontRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    frontRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    frontRightMotorTemps_imageView.setImageResourceWithTag(R.drawable.motor_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    frontRightMotorTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (rearHBridge) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    rearHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    rearHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    rearHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    rearHbridgeTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (frontHBridge) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    frontHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    frontHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    frontHbridgeTemps_imageView.setImageResourceWithTag(R.drawable.h_bridge_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    frontHbridgeTemps_imageView.setImageResourceWithTag(android.R.color.transparent)
            }

            when (raspberryPi) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    raspiTemp_imageView.setImageResourceWithTag(R.drawable.raspi_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    raspiTemp_imageView.setImageResourceWithTag(R.drawable.raspi_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    raspiTemp_imageView.setImageResourceWithTag(R.drawable.raspi_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    raspiTemp_imageView.setImageResourceWithTag(R.drawable.raspi_temp_off)
            }

            when (batteries) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    batteryTemp_imageView.setImageResourceWithTag(R.drawable.batteries_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    batteryTemp_imageView.setImageResourceWithTag(R.drawable.batteries_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    batteryTemp_imageView.setImageResourceWithTag(R.drawable.batteries_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    batteryTemp_imageView.setImageResourceWithTag(R.drawable.batteries_temp_off)
            }

            when (shiftRegisters) {
                SensorFeedbackServer.WARNING_TYPE_NORMAL ->
                    shiftRegisterTemp_imageView.
                        setImageResourceWithTag(R.drawable.shift_register_temp_normal)
                SensorFeedbackServer.WARNING_TYPE_MEDIUM ->
                    shiftRegisterTemp_imageView.
                        setImageResourceWithTag(R.drawable.shift_register_temp_medium)
                SensorFeedbackServer.WARNING_TYPE_HIGH ->
                    shiftRegisterTemp_imageView.
                        setImageResourceWithTag(R.drawable.shift_register_temp_high)
                SensorFeedbackServer.WARNING_TYPE_NOTHING ->
                    shiftRegisterTemp_imageView.
                        setImageResourceWithTag(R.drawable.shift_register_temp_off)
            }
        }
    }

    /* This function is for setting and resetting purposes.
    */
    fun updateSpeedUIItem(speed: String){
        runOnUiThread {
            vehicle_speed_textView.text = "$speed ${resources.getString(R.string.tachometer_unit)}"
        }
    }

    override fun onPause() {
        super.onPause()
        val status = activateParkingBrake(true)
        if(status == OK_STRING)
            parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_on)
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

    // It is better for testing if every ImageView has a tag
    private fun ImageView.setImageResourceWithTag(resId: Int) {
        setImageResource(resId)
        tag = resId
    }

}
