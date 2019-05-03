package car.rccontroller

import android.content.res.TypedArray
import android.graphics.drawable.AnimationDrawable
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
import car.R
import car.RCControllerApplication
import kotlinx.android.synthetic.main.activity_rccontroller.*
import car.feedback.*
import car.feedback.cockpit.*
import car.feedback.server.ModuleState
import car.feedback.server.NanoHTTPDLifecycleAware
import car.feedback.server.TemperatureWarningType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

lateinit var retrofit: Retrofit
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
        // temperature UI items
        viewModel.rearLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearLeftMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.rearRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearRightMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.frontLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontLeftMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.frontRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontRightMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.rearHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, rearHbridgeTemps_imageView,
                resources.obtainTypedArray(R.array.h_bridge_temperature_states))
        })
        viewModel.frontHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, frontHbridgeTemps_imageView,
                resources.obtainTypedArray(R.array.h_bridge_temperature_states))
        })
        viewModel.raspberryPiTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, raspiTemp_imageView,
                resources.obtainTypedArray(R.array.raspberry_pi_temperature_states))
        })
        viewModel.batteriesTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, batteryTemp_imageView,
                resources.obtainTypedArray(R.array.batteries_temperature_states))
        })
        viewModel.shiftRegistersTemperatureLiveData.observe(this, Observer<TemperatureWarningType>{
            updateUITempItems(it, shiftRegisterTemp_imageView,
                resources.obtainTypedArray(R.array.shift_registers_temperature_states))
        })
        // Advanced sensor UI items
        viewModel.tractionControlModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, tcm_imageView,
                resources.obtainTypedArray(R.array.traction_control_module_states))
        })
        viewModel.antilockBrakingModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, abm_imageView,
                resources.obtainTypedArray(R.array.antilock_braking_module_states))
        })
        viewModel.electronicStabilityModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, esm_imageView,
                resources.obtainTypedArray(R.array.electronic_stability_module_states))
        })
        viewModel.understeerDetectionModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, udm_imageView,
                resources.obtainTypedArray(R.array.understeer_detection_module_states))
        })
        viewModel.oversteerDetectionModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, odm_imageView,
                resources.obtainTypedArray(R.array.oversteer_detection_module_states))
        })
        viewModel.collisionDetectionModuleLiveData.observe(this, Observer<ModuleState>{
            updateUIAdvancedSensorItems(it, cdm_imageView,
                resources.obtainTypedArray(R.array.collision_detection_module_states))
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

            nano.changeServerState(it)

            if (it) {
                engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_started_stop_action)
                carTemps_imageView.setImageResourceWithTag(R.drawable.car_temps_on)

                // The values change at the same state as they are currently at the server.
                viewModel.reverseStatusLiveData.value = getReverseIntention()
                viewModel.emergencyLightsStatusLiveData.value = getEmergencyLightsState()
                viewModel.speedLiveData.value = resources.getString(R.string.tachometer_value)
            }
            else {
                engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_stopped_start_action)
                carTemps_imageView.setImageResourceWithTag(R.drawable.car_temps_off)


                viewModel.reverseStatusLiveData.value = false

                //reset the cruise control flag
                viewModel.cruiseControlStatusLiveData.value = false

                viewModel.emergencyLightsStatusLiveData.value = false

                // Update those values with post in order to be executed after postValue of the server!
                viewModel.rearLeftMotorTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.rearRightMotorTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.frontLeftMotorTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.frontRightMotorTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.frontHBridgeTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.rearHBridgeTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.raspberryPiTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.batteriesTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)
                viewModel.shiftRegistersTemperatureLiveData.postValue(TemperatureWarningType.NOTHING)

                viewModel.speedLiveData.postValue(getString(R.string.tachometer_null_value))

                viewModel.tractionControlModuleLiveData.postValue(ModuleState.OFF)
                viewModel.antilockBrakingModuleLiveData.postValue(ModuleState.OFF)
                viewModel.electronicStabilityModuleLiveData.postValue(ModuleState.OFF)
                viewModel.understeerDetectionModuleLiveData.postValue(ModuleState.OFF)
                viewModel.oversteerDetectionModuleLiveData.postValue(ModuleState.OFF)
                viewModel.collisionDetectionModuleLiveData.postValue(ModuleState.OFF)
            }
            /* At start of the application all UI items would be deactivated
            so there is no reason to handle them.
            You want to control them when:
                1. you stop/re-stop the engine
                2. you restart the engine.
            So, '::retrofit.isInitialized' is a guarantee that these updates to UI items
            happen only at any of the above scenarios.
             */
            if(::retrofit.isInitialized) {
                updateMotionUIItems()
                viewModel.visionLightsLiveData.value = getMainLightsState()
                viewModel.directionLightsLiveData.value = getDirectionLightsState()
                // The following function is updating some other ImageViews and more
                updateHandlingAssistanceUIItem()
                /*updateMotorSpeedLimiterUIItem()*/
            }
        })

        //////
        // setup parking brake
        //////
        parkingBrake_imageView.apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(::retrofit.isInitialized && isEngineStarted()) {
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
        viewModel.parkingBrakeLiveData.observe(this, Observer<Boolean>{
            if (it)
                parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_on)
            else
                parkingBrake_imageView.setImageResourceWithTag(R.drawable.parking_brake_off)
        })

        //////
        // setup handbrake
        //////
        handbrake_imageView.apply {
            setOnTouchListener {_, event: MotionEvent ->
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                    /* Use the serverIp variable to check if the engine is running.
                       I use the serverIp because I did not want to use a blocking feedback request. */
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
        viewModel.handbrakeLiveData.observe(this, Observer<Boolean>{
            if (it)
                handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
            else
                handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_off)
        })

        //////
        // setup reverse
        //////
        reverse_imageView. apply {
            setOnLongClickListener {
                // If, for any reason, engine is stopped I should not do anything
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                    if(::retrofit.isInitialized && isEngineStarted()) {
                        setMainLightsState(MainLight.LONG_RANGE_SIGNAL_LIGHTS)
                    }
                    viewModel.visionLightsLiveData.value = getMainLightsState()
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    if (::retrofit.isInitialized && isEngineStarted()){
                        when (getMainLightsState()) {
                            MainLight.LONG_RANGE_LIGHTS -> setMainLightsState(MainLight.DRIVING_LIGHTS)
                            MainLight.DRIVING_LIGHTS -> setMainLightsState(MainLight.POSITION_LIGHTS)
                            MainLight.POSITION_LIGHTS -> setMainLightsState(MainLight.LIGHTS_OFF)
                            MainLight.LIGHTS_OFF ->
                                Toast.makeText(this@RCControllerActivity,
                                    getString(R.string.lights_off_warning),
                                    Toast.LENGTH_SHORT).show()
                        }
                        // update the icon using server info for verification
                        viewModel.visionLightsLiveData.value = getMainLightsState()
                    }
                    return true
                }
        })
        lights_imageView. apply {
            setOnTouchListener{_, event -> gestureDetector.onTouchEvent(event);}
            setOnLongClickListener {
                if (::retrofit.isInitialized && isEngineStarted()){
                    when (getMainLightsState()) {
                        MainLight.LIGHTS_OFF -> setMainLightsState(MainLight.POSITION_LIGHTS)
                        MainLight.POSITION_LIGHTS -> setMainLightsState(MainLight.DRIVING_LIGHTS)
                        MainLight.DRIVING_LIGHTS -> setMainLightsState(MainLight.LONG_RANGE_LIGHTS)
                        MainLight.LONG_RANGE_LIGHTS -> Toast.makeText(this@RCControllerActivity,
                                getString(R.string.long_range_lights_warning),
                                Toast.LENGTH_SHORT).show()
                    }
                    // update the icon using server info for verification
                    viewModel.visionLightsLiveData.value = getMainLightsState()
                }
                true
            }
        }
        viewModel.visionLightsLiveData.observe(this, Observer<MainLight> {
            when (it) {
                MainLight.LONG_RANGE_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_long_range)
                MainLight.DRIVING_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_driving)
                MainLight.POSITION_LIGHTS -> lights_imageView.setImageResourceWithTag(R.drawable.lights_position)
                MainLight.LIGHTS_OFF -> lights_imageView.setImageResourceWithTag(R.drawable.lights_off)
            }
        })

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
                if(::retrofit.isInitialized && isEngineStarted()) {
                    setDirectionLightsState(DirectionLight.DIRECTION_LIGHTS_LEFT)
                }
                viewModel.directionLightsLiveData.value = getDirectionLightsState()
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
                if(::retrofit.isInitialized && isEngineStarted()) {
                    setDirectionLightsState(DirectionLight.DIRECTION_LIGHTS_RIGHT)
                }
                viewModel.directionLightsLiveData.value = getDirectionLightsState()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }
        viewModel.directionLightsLiveData.observe(this, Observer<DirectionLight> {
            when (it) {
                DirectionLight.DIRECTION_LIGHTS_STRAIGHT -> {
                    leftDirectionLightsAnimation.stop()
                    leftDirectionLightsAnimation.selectDrawable(0)
                    rightDirectionLightsAnimation.stop()
                    rightDirectionLightsAnimation.selectDrawable(0)
                }
                DirectionLight.DIRECTION_LIGHTS_LEFT -> {
                    rightDirectionLightsAnimation.stop()
                    rightDirectionLightsAnimation.selectDrawable(0)
                    leftDirectionLightsAnimation.start()
                }
                DirectionLight.DIRECTION_LIGHTS_RIGHT -> {
                    leftDirectionLightsAnimation.stop()
                    leftDirectionLightsAnimation.selectDrawable(0)
                    rightDirectionLightsAnimation.start()
                }
            }
        })

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
                if (::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                if(::retrofit.isInitialized && isEngineStarted()) {
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
                        viewModel.directionLightsLiveData.value = getDirectionLightsState()
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
                        if (NanoHTTPDLifecycleAware.RUN_ON_EMULATOR)
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

                val status = startEngine(applicationContext as RCControllerApplication)
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

    private fun updateUITempItems(warningType: TemperatureWarningType, item: ImageView, states: TypedArray) {
        var i = 0
        when (warningType) {
            //TemperatureWarningType.NOTHING -> i = 0
            TemperatureWarningType.NORMAL -> i = 1
            TemperatureWarningType.MEDIUM -> i = 2
            TemperatureWarningType.HIGH -> i = 3
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Advanced sensor non-interactive images are updated by server actions
        which are sent to the client when the Raspi has to.
     */
    private fun updateUIAdvancedSensorItems(state: ModuleState, item: ImageView, states: TypedArray) {
        var i = 0
        when (state) {
            //ModuleState.NOTHING, ModuleState.OFF -> i = 0
            ModuleState.ON -> i = 1
            ModuleState.IDLE -> i = 2
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Motion interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotionUIItems(){
        viewModel.parkingBrakeLiveData.value = isParkingBrakeActive()
        viewModel.handbrakeLiveData.value = isHandbrakeActive()
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
        val handlingAssistance = getHandlingAssistanceState()
        when (handlingAssistance) {
            ASSISTANCE_FULL -> {
                setFrontDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO)
                setRearDifferentialSlipperyLimiter(DIFFERENTIAL_SLIPPERY_LIMITER_AUTO)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_full)
            }
            ASSISTANCE_WARNING -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_warning)
            }
            ASSISTANCE_NONE -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)

                handling_assistance_imageView.
                    setImageResourceWithTag(R.drawable.handling_assistance_manual)
            }
            else -> handling_assistance_imageView.setImageResourceWithTag(R.drawable.handling_assistance_off)
        }

        if((handlingAssistance == ASSISTANCE_FULL) || (handlingAssistance == ASSISTANCE_WARNING)){
            viewModel.tractionControlModuleLiveData.postValue(ModuleState.IDLE)
            viewModel.antilockBrakingModuleLiveData.postValue(ModuleState.IDLE)
            viewModel.electronicStabilityModuleLiveData.postValue(ModuleState.IDLE)
            viewModel.understeerDetectionModuleLiveData.postValue(ModuleState.IDLE)
            viewModel.oversteerDetectionModuleLiveData.postValue(ModuleState.IDLE)
            viewModel.collisionDetectionModuleLiveData.postValue(ModuleState.IDLE)
        }
        else {
            viewModel.tractionControlModuleLiveData.postValue(ModuleState.OFF)
            viewModel.antilockBrakingModuleLiveData.postValue(ModuleState.OFF)
            viewModel.electronicStabilityModuleLiveData.postValue(ModuleState.OFF)
            viewModel.understeerDetectionModuleLiveData.postValue(ModuleState.OFF)
            viewModel.oversteerDetectionModuleLiveData.postValue(ModuleState.OFF)
            viewModel.collisionDetectionModuleLiveData.postValue(ModuleState.OFF)
        }

        updateFrontDifferentialSlipperyLimiterUIItem()
        updateRearDifferentialSlipperyLimiterUIItem()
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
