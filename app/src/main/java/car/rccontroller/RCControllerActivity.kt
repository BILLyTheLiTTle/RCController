package car.rccontroller

import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_rccontroller.*
import car.rccontroller.network.*
import car.rccontroller.network.cockpit.*
import car.rccontroller.network.server.feedback.NanoHTTPDLifecycleAware
import car.rccontroller.network.server.feedback.data.TemperatureWarningType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


val RUN_ON_EMULATOR = Build.FINGERPRINT.contains("generic")

lateinit var retrofit: Retrofit
val engineAPI: Engine by lazy { retrofit.create<Engine>(Engine::class.java) }
val throttleBrakeAPI: ThrottleBrake by lazy { retrofit.create<ThrottleBrake>(ThrottleBrake::class.java) }
val steeringAPI: Steering by lazy { retrofit.create<Steering>(Steering::class.java) }
val electricsAPI: Electrics by lazy { retrofit.create<Electrics>(Electrics::class.java) }
val setupAPI: Setup by lazy { retrofit.create<Setup>(Setup::class.java) }

/**
 * A full-screen activity in landscape mode.
 */
class RCControllerActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private val leftDirectionLightsAnimation = AnimationDrawable()
    private val rightDirectionLightsAnimation= AnimationDrawable()
    private val emergencyLightsAnimation= AnimationDrawable()

    private lateinit var nano: NanoHTTPDLifecycleAware

    private lateinit var viewModel: RCControllerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // declare local function
        fun throttle(progress: Int){
            val direction = if (getReverseIntention()) ACTION_MOVE_BACKWARD else ACTION_MOVE_FORWARD
            when (progress) {
                0 -> setBrakingStill()
                10 -> setNeutral()
                20, 40, 50, 65, 75, 80, 85, 90, 95, 100 -> setThrottleBrake(direction, progress)
            }
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_rccontroller)

        viewModel = ViewModelProviders.of(this).get(RCControllerViewModel::class.java)

        nano = NanoHTTPDLifecycleAware(viewModel)
        lifecycle.addObserver(nano)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            hide()
        }

        // disable them from here cuz it did not work from xml
        viewModel.steeringStatusLiveData.observe(this, Observer<Boolean> {
            steering_seekBar.isEnabled = it
        })
        viewModel.throttleStatusLiveData.observe(this, Observer<Boolean> {
            throttleNbrake_mySeekBar.isEnabled = it
        })

        viewModel.speedLiveData.observe(this, Observer<String>{
            vehicle_speed_textView.text = resources.getString(R.string.tachometer_info,
                it, resources.getString(R.string.tachometer_unit))
        })
        viewModel.rearLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearLeftMotorTemps_imageView)
        })
        viewModel.rearRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearRightMotorTemps_imageView)
        })
        viewModel.frontLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontLeftMotorTemps_imageView)
        })
        viewModel.frontRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontRightMotorTemps_imageView)
        })
        viewModel.rearHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearHbridgeTemps_imageView)
        })
        viewModel.frontHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontHbridgeTemps_imageView)
        })

        //////
        //setup engine start-n-stop
        //////
        engineStartStop_imageView.apply {
            setOnLongClickListener {
                if(::retrofit.isInitialized && isEngineStarted()) {
                    val status = stopEngine()
                    if (status == OK_STRING) {
                        /*
                            The following problem does not apply anymore due to
                            @see RequestsBackbone#areNetworkSettingsAvailable().

                            This code block works even after I set the server ip and port to null
                            because "bullshit".toBoolean() equals false!
                            A problem is when the client waits real String data
                            (for example lights_off). In these situation I fall into the
                            "else" code blocks.
                         */
                        viewModel.engineStatusLiveData.value = false
                    } else {
                        Toast.makeText(context, "${resources.getString(R.string.error)}: $status",
                                Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    //start the engine
                    showServerConnectionDialog( this@RCControllerActivity)
                }

                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }

        viewModel.engineStatusLiveData.observe(this, Observer<Boolean> {

            viewModel.steeringStatusLiveData.value = it
            steering_seekBar.progress = resources.getInteger(R.integer.default_steering)
            viewModel.throttleStatusLiveData.value = it
            throttleNbrake_mySeekBar.progress =
                    resources.getInteger(R.integer.default_throttle_n_brake)

            if (it) {
                engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_started_stop_action)

                // The values change at the same state as they are currently at the server.
                viewModel.reverseStatusLiveData.value = getReverseIntention()
                viewModel.emergencyLightsStatusLiveData.value = getEmergencyLightsState()
                viewModel.speedLiveData.value = resources.getString(R.string.tachometer_value)
            } else {
                engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_stopped_start_action)

                viewModel.reverseStatusLiveData.value = it

                //reset the cruise control flag
                viewModel.cruiseControlStatusLiveData.value = it

                viewModel.emergencyLightsStatusLiveData.value = false

                viewModel.rearLeftMotorTemperatureLiveData.value = TemperatureWarningType.NOTHING
                viewModel.rearRightMotorTemperatureLiveData.value = TemperatureWarningType.NOTHING
                viewModel.frontLeftMotorTemperatureLiveData.value = TemperatureWarningType.NOTHING
                viewModel.frontRightMotorTemperatureLiveData.value = TemperatureWarningType.NOTHING
                viewModel.frontHBridgeTemperatureLiveData.value = TemperatureWarningType.NOTHING
                viewModel.rearHBridgeTemperatureLiveData.value = TemperatureWarningType.NOTHING
                /*updateTempUIItems(
                    raspberryPi = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                    batteries = SensorFeedbackServer.WARNING_TYPE_NOTHING,
                    shiftRegisters = SensorFeedbackServer.WARNING_TYPE_NOTHING
                )*/

                viewModel.speedLiveData.value = getString(R.string.tachometer_null_value)
            }

            /*updateMotionUIItems()
            updateMainLightsUIItems()
            updateTurnLightsUIItems()
            // The following function is updating some other ImageViews and more
            updateHandlingAssistanceUIItem()
            updateMotorSpeedLimiterUIItem()*/
            })


        //////
        // setup parking brake
        //////
        parkingBrake_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    val status = activateParkingBrake(!isParkingBrakeActive())
                    if (status == OK_STRING) {
                        updateMotionUIItems()
                    } else {
                        Toast.makeText(context, "${resources.getString(R.string.error)}: $status",
                                Toast.LENGTH_LONG).show()
                    }
                }
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
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
                    if(viewModel.engineStatusLiveData.value == true) {
                        handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
                        activateHandbrake( true)
                    }
                } else if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if(viewModel.engineStatusLiveData.value == true) {
                        handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_off)
                        activateHandbrake(false)
                        // re-throttle automatically to start moving the rear wheels again
                        throttle(throttleNbrake_mySeekBar.progress)
                    }
                }
                false
            }
            //The blocking actions should not interfere with driving,
            // that's why they are on different listener
            setOnClickListener {
                updateMotionUIItems()
                //true
            }
        }

        //////
        // setup reverse
        //////
        reverse_imageView. apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    if (getMotionState() == ACTION_NEUTRAL) {
                        setReverseIntention(!getReverseIntention())
                    }
                    else {
                        Toast.makeText(context, getString(R.string.reverse_warning), Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.reverseStatusLiveData.value = getReverseIntention()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }

        viewModel.reverseStatusLiveData.observe(this, Observer<Boolean> {
            if (it)
                reverse_imageView.setImageResourceWithTag(R.drawable.reverse_on)
            else
                reverse_imageView.setImageResourceWithTag(R.drawable.reverse_off)
        })

        //////
        // setup cruise control
        //////
        cruiseControl_imageView. apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    if (viewModel.cruiseControlStatusLiveData.value == true) {
                        Toast.makeText(context, getString(R.string.cruise_control_info), Toast.LENGTH_SHORT).show()
                    }
                    viewModel.cruiseControlStatusLiveData.value = true
                }

                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }
        viewModel.cruiseControlStatusLiveData.observe(this, Observer<Boolean> {
            if (it)
                cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_on)
            else
                cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_off)
        })


        //////
        // setup main lights
        //////
        val gestureDetector = GestureDetector(this,
            object: GestureDetector.SimpleOnGestureListener(){
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    // If, for any reason, engine is stopped I should not do anything
                    if(isEngineStarted()) {
                        setMainLightsState(LONG_RANGE_SIGNAL_LIGHTS)
                    }
                    updateMainLightsUIItems()
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (isEngineStarted()){
                        when (getMainLightsState()) {
                            LONG_RANGE_LIGHTS -> setMainLightsState(DRIVING_LIGHTS)
                            DRIVING_LIGHTS -> setMainLightsState(POSITION_LIGHTS)
                            POSITION_LIGHTS -> setMainLightsState(LIGHTS_OFF)
                            LIGHTS_OFF ->
                                Toast.makeText(this@RCControllerActivity,
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
            setOnLongClickListener {
                if (isEngineStarted()){
                    when (getMainLightsState()) {
                        LIGHTS_OFF -> setMainLightsState(POSITION_LIGHTS)
                        POSITION_LIGHTS -> setMainLightsState(DRIVING_LIGHTS)
                        DRIVING_LIGHTS -> setMainLightsState(LONG_RANGE_LIGHTS)
                        LONG_RANGE_LIGHTS -> Toast.makeText(this@RCControllerActivity,
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
        leftDirectionLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        leftDirectionLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        leftDirectionLightsAnimation.isOneShot = false
        leftTurn_imageView. apply {
            setBackgroundDrawable(leftDirectionLightsAnimation)
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    setDirectionLightsState(DIRECTION_LIGHTS_LEFT)
                }
                updateTurnLightsUIItems()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }
        //////
        // setup right turn lights
        //////
        rightDirectionLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_off),400)
        rightDirectionLightsAnimation.addFrame(resources.getDrawable(R.drawable.turn_light_on),400)
        rightDirectionLightsAnimation.isOneShot = false
        rightTurn_imageView. apply {
            setBackgroundDrawable(rightDirectionLightsAnimation)
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    setDirectionLightsState(DIRECTION_LIGHTS_RIGHT)
                }
                updateTurnLightsUIItems()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
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
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if (isEngineStarted()) {
                    setEmergencyLightsState(!getEmergencyLightsState())
                }
                viewModel.emergencyLightsStatusLiveData.value = getEmergencyLightsState()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }
        viewModel.emergencyLightsStatusLiveData.observe(this, Observer<Boolean> {
            if (it) {
                emergencyLightsAnimation.start()
            }
            else {
                emergencyLightsAnimation.stop()
                emergencyLightsAnimation.selectDrawable(0)
            }
        })

        //////
        // setup handling assistance
        //////
        handling_assistance_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    when (getHandlingAssistanceState()) {
                        ASSISTANCE_NONE -> setHandlingAssistanceState(ASSISTANCE_WARNING)
                        ASSISTANCE_WARNING -> setHandlingAssistanceState(ASSISTANCE_FULL)
                        ASSISTANCE_FULL ->
                            Toast.makeText(context,
                                    getString(R.string.handling_assistance_full_warning),
                                    Toast.LENGTH_SHORT).show()
                    }
                    updateHandlingAssistanceUIItem()
                }
                true
            }
            setOnClickListener {
                when (getHandlingAssistanceState()) {
                    ASSISTANCE_FULL -> setHandlingAssistanceState(ASSISTANCE_WARNING)
                    ASSISTANCE_WARNING -> setHandlingAssistanceState(ASSISTANCE_NONE)
                    ASSISTANCE_NONE ->
                        Toast.makeText(context,
                                getString(R.string.handling_assistance_none_warning),
                                Toast.LENGTH_SHORT).show()
                }
                updateHandlingAssistanceUIItem()
                //true
            }
        }

        //////
        // setup motor speed limiter
        //////
        motor_speed_limiter_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    when (getMotorSpeedLimiter()) {
                        MOTOR_SPEED_LIMITER_NO_SPEED ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_1)
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_2)
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1)
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2)
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_1)
                        MOTOR_SPEED_LIMITER_FAST_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_2)
                        MOTOR_SPEED_LIMITER_FAST_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FULL_SPEED)
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
            setOnClickListener {
                if(isEngineStarted()) {
                    when (getMotorSpeedLimiter()) {
                        MOTOR_SPEED_LIMITER_FULL_SPEED ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_2)
                        MOTOR_SPEED_LIMITER_FAST_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_FAST_SPEED_1)
                        MOTOR_SPEED_LIMITER_FAST_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2)
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1)
                        MOTOR_SPEED_LIMITER_MEDIUM_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_2)
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_2 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_SLOW_SPEED_1)
                        MOTOR_SPEED_LIMITER_SLOW_SPEED_1 ->
                            setMotorSpeedLimiter(MOTOR_SPEED_LIMITER_NO_SPEED)
                        MOTOR_SPEED_LIMITER_NO_SPEED -> Toast.
                            makeText(context,
                                getString(R.string.motor_speed_limiter_none_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    updateMotorSpeedLimiterUIItem()
                }
                //true
            }
        }

        //////
        // setup front differential slippery limiter
        //////
        differential_slippery_limiter_front_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    if (getHandlingAssistanceState() != ASSISTANCE_FULL) {
                        when (getFrontDifferentialSlipperyLimiter()) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN)
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_open_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousFrontDifferentialSlipperyLimiter =
                                    getFrontDifferentialSlipperyLimiter()
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
            setOnClickListener {
                if(isEngineStarted()) {
                    if (getHandlingAssistanceState() != ASSISTANCE_FULL) {
                        when (getFrontDifferentialSlipperyLimiter()) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED)
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_locked_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousFrontDifferentialSlipperyLimiter =
                                getFrontDifferentialSlipperyLimiter()
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateFrontDifferentialSlipperyLimiterUIItem()
                }
                //true
            }
        }

        //////
        // setup rear differential slippery limiter
        //////
        differential_slippery_limiter_rear_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(isEngineStarted()) {
                    if (getHandlingAssistanceState() != ASSISTANCE_FULL) {
                        when (getRearDifferentialSlipperyLimiter()) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_OPEN)
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_open_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousRearDifferentialSlipperyLimiter =
                                getRearDifferentialSlipperyLimiter()
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
            setOnClickListener {
                if(isEngineStarted()) {
                    if (getHandlingAssistanceState() != ASSISTANCE_FULL) {
                        when (getRearDifferentialSlipperyLimiter()) {
                            DIFFERENTIAL_SLIPPERY_LIMITER_OPEN ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_0 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_1 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2)
                            DIFFERENTIAL_SLIPPERY_LIMITER_MEDI_2 ->
                                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED)
                            DIFFERENTIAL_SLIPPERY_LIMITER_LOCKED -> Toast.makeText(
                                context,
                                getString(R.string.differential_slippery_limiter_locked_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previousRearDifferentialSlipperyLimiter =
                                getRearDifferentialSlipperyLimiter()
                    }
                    else {
                        Toast.makeText(
                            context, getString(R.string.auto_to_manual_warning),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    updateRearDifferentialSlipperyLimiterUIItem()
                }
                //true
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
                if(viewModel.cruiseControlStatusLiveData.value == false) {
                    seekBar.progress = resources.getInteger(R.integer.default_throttle_n_brake)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                viewModel.cruiseControlStatusLiveData.value = false

                activateHandbrake( false)
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
                 val raspiServerIP =
                        if (RUN_ON_EMULATOR)
                            "10.0.2.2"
                        else
                            dialogView.findViewById<EditText>(R.id.serverIp_editText).text
                                    .toString()
                val raspiServerPort = dialogView.findViewById<EditText>(R.id.serverPort_editText).text
                        .toString().toIntOrNull()

                retrofit = Retrofit.Builder()
                    .baseUrl("http://$raspiServerIP:$raspiServerPort/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()

                val status = startEngine(applicationContext as RCControllerApplication, raspiServerIP, raspiServerPort)
                if (status == OK_STRING) {
                    viewModel.engineStatusLiveData.value = true
                } else {
                    viewModel.engineStatusLiveData.value = false
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

    private fun updateUITempItems(warningType: TemperatureWarningType, item: ImageView){
        when (warningType) {
            TemperatureWarningType.NORMAL ->
                item.setImageResourceWithTag(R.drawable.motor_temp_normal)
            TemperatureWarningType.MEDIUM ->
                item.setImageResourceWithTag(R.drawable.motor_temp_medium)
            TemperatureWarningType.HIGH ->
                item.setImageResourceWithTag(R.drawable.motor_temp_high)
            TemperatureWarningType.NOTHING ->
                item.setImageResourceWithTag(android.R.color.transparent)
        }
    }

    /* Motion interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotionUIItems(){
        if(isParkingBrakeActive())
            parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_on)
        else
            parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_off)

        if(isHandbrakeActive())
            handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
        else
            handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_off)

        /*if (cruiseControlActive)
            cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_on)
        else
            cruiseControl_imageView.setImageResourceWithTag(R.drawable.cruise_control_off)*/
    }

    /* Rear differential slippery limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateRearDifferentialSlipperyLimiterUIItem(){
        when (getRearDifferentialSlipperyLimiter()) {
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
            DIFFERENTIAL_SLIPPERY_LIMITER_ERROR -> differential_slippery_limiter_rear_imageView.
                setImageResourceWithTag(R.drawable.differential_rear_error)
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
        when (getFrontDifferentialSlipperyLimiter()) {
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
            DIFFERENTIAL_SLIPPERY_LIMITER_ERROR -> differential_slippery_limiter_front_imageView.
                setImageResourceWithTag(R.drawable.differential_front_error)
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
        when (getMotorSpeedLimiter()) {
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
            MOTOR_SPEED_LIMITER_ERROR_SPEED -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_error)
            else -> motor_speed_limiter_imageView.
                setImageResourceWithTag(R.drawable.speed_limiter_off)
        }
    }

    /* Handling assistance interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.

        Also, here update the ImageViews of the items, which are connected
        to handling assistance (ex. differential, suspension) and some of their
        functionality values.
     */
    private fun updateHandlingAssistanceUIItem(){
        when (getHandlingAssistanceState()) {
            ASSISTANCE_FULL -> {
                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO)
                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_full)

                /*updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                abmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                esmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                udmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                odmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                cdmState = SensorFeedbackServer.MODULE_IDLE_STATE)*/
            }
            ASSISTANCE_WARNING -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_warning)

                /*updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        abmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        esmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        udmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        odmState = SensorFeedbackServer.MODULE_IDLE_STATE,
                        cdmState = SensorFeedbackServer.MODULE_IDLE_STATE)*/
            }
            ASSISTANCE_NONE -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_manual)

                /*updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        abmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        esmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        udmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        odmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        cdmState = SensorFeedbackServer.MODULE_OFF_STATE)*/
            }
            else -> {
                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_off)

                /*updateAdvancedSensorUIItems(tcmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        abmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        esmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        udmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        odmState = SensorFeedbackServer.MODULE_OFF_STATE,
                        cdmState = SensorFeedbackServer.MODULE_OFF_STATE)*/
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
    /*fun updateAdvancedSensorUIItems(
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
    }*/

    /* Main lights interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMainLightsUIItems(){
        when (getMainLightsState()) {
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
        when (getDirectionLightsState()) {
            DIRECTION_LIGHTS_STRAIGHT -> {
                leftDirectionLightsAnimation.stop()
                leftDirectionLightsAnimation.selectDrawable(0)
                rightDirectionLightsAnimation.stop()
                rightDirectionLightsAnimation.selectDrawable(0)
            }
            DIRECTION_LIGHTS_LEFT -> {
                rightDirectionLightsAnimation.stop()
                rightDirectionLightsAnimation.selectDrawable(0)
                leftDirectionLightsAnimation.start()
            }
            DIRECTION_LIGHTS_RIGHT -> {
                leftDirectionLightsAnimation.stop()
                leftDirectionLightsAnimation.selectDrawable(0)
                rightDirectionLightsAnimation.start()
            }
            else -> {
                leftDirectionLightsAnimation.stop()
                leftDirectionLightsAnimation.selectDrawable(1)
                rightDirectionLightsAnimation.stop()
                rightDirectionLightsAnimation.selectDrawable(1)
            }
        }
    }

    /* This function is for setting and resetting purposes.
     */
    /*fun updateTempUIItems(
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
    }*/

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
