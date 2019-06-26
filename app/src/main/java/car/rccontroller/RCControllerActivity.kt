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
import car.feedback.server.TemperatureWarning
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
            val direction = if (getReverseIntention()) Motion.BACKWARD else Motion.FORWARD
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
        viewModel.rearLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, rearLeftMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.rearRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, rearRightMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.frontLeftMotorTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, frontLeftMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.frontRightMotorTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, frontRightMotorTemps_imageView,
                resources.obtainTypedArray(R.array.motor_temperature_states))
        })
        viewModel.rearHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, rearHbridgeTemps_imageView,
                resources.obtainTypedArray(R.array.h_bridge_temperature_states))
        })
        viewModel.frontHBridgeTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, frontHbridgeTemps_imageView,
                resources.obtainTypedArray(R.array.h_bridge_temperature_states))
        })
        viewModel.raspberryPiTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, raspiTemp_imageView,
                resources.obtainTypedArray(R.array.raspberry_pi_temperature_states))
        })
        viewModel.batteriesTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
            updateUITempItems(it, batteryTemp_imageView,
                resources.obtainTypedArray(R.array.batteries_temperature_states))
        })
        viewModel.shiftRegistersTemperatureLiveData.observe(this, Observer<TemperatureWarning>{
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
        // Setup UI items
        viewModel.handlingAssistanceLiveData.observe(this, Observer<HandlingAssistance>{
            updateHandlingAssistanceUIItem(it, handling_assistance_imageView,
                resources.obtainTypedArray(R.array.handling_assistance_states))
        })
        viewModel.frontDifferentialSlipperyLimiterLiveData.observe(this, Observer<DifferentialSlipperyLimiter>{
            updateDifferentialSlipperyUIItems(it, differential_slippery_limiter_front_imageView,
                resources.obtainTypedArray(R.array.front_differential_slippery_limiter_states))
        })
        viewModel.rearDifferentialSlipperyLimiterLiveData.observe(this, Observer<DifferentialSlipperyLimiter>{
            updateDifferentialSlipperyUIItems(it, differential_slippery_limiter_rear_imageView,
                resources.obtainTypedArray(R.array.rear_differential_slippery_limiter_states))
        })
        viewModel.motorSpeedLimiterLiveData.observe(this, Observer<MotorSpeedLimiter>{
            updateMotorSpeedLimiterUIItem(it, motor_speed_limiter_imageView,
                resources.obtainTypedArray(R.array.motor_speed_limiter_states))
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

                updateMotionUIItems(true)

                viewModel.visionLightsLiveData.value = getMainLightsState()
                viewModel.directionLightsLiveData.value = getDirectionLightsState()

                viewModel.handlingAssistanceLiveData.value = getHandlingAssistanceState()
                viewModel.motorSpeedLimiterLiveData.value = getMotorSpeedLimiter()
            }
            else {
                engineStartStop_imageView.setImageResourceWithTag(R.drawable.engine_stopped_start_action)
                carTemps_imageView.setImageResourceWithTag(R.drawable.car_temps_off)


                viewModel.reverseStatusLiveData.value = false

                //reset the cruise control flag
                viewModel.cruiseControlStatusLiveData.value = false

                viewModel.emergencyLightsStatusLiveData.value = false

                // Update those values with post in order to be executed after postValue of the server!
                viewModel.rearLeftMotorTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.rearRightMotorTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.frontLeftMotorTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.frontRightMotorTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.frontHBridgeTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.rearHBridgeTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.raspberryPiTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.batteriesTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)
                viewModel.shiftRegistersTemperatureLiveData.postValue(TemperatureWarning.NOTHING_TEMPERATURE)

                viewModel.speedLiveData.postValue(getString(R.string.tachometer_null_value))

                updateMotionUIItems(false)

                viewModel.visionLightsLiveData.value = MainLight.LIGHTS_OFF
                viewModel.directionLightsLiveData.value = CorneringLight.STRAIGHT_LIGHTS

                viewModel.handlingAssistanceLiveData.value = HandlingAssistance.NULL

                viewModel.motorSpeedLimiterLiveData.value = MotorSpeedLimiter.NULL

                viewModel.tractionControlModuleLiveData.postValue(ModuleState.OFF)
                viewModel.antilockBrakingModuleLiveData.postValue(ModuleState.OFF)
                viewModel.electronicStabilityModuleLiveData.postValue(ModuleState.OFF)
                viewModel.understeerDetectionModuleLiveData.postValue(ModuleState.OFF)
                viewModel.oversteerDetectionModuleLiveData.postValue(ModuleState.OFF)
                viewModel.collisionDetectionModuleLiveData.postValue(ModuleState.OFF)
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
                        updateMotionUIItems(true)
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
                if (event.action == MotionEvent.ACTION_DOWN) {
                    /* Use the serverIp variable to check if the engine is running.
                       I use the serverIp because I did not want to use a blocking feedback request. */
                    if(viewModel.engineStatusLiveData.value == true) {
                        handbrake_imageView.setImageResourceWithTag(R.drawable.handbrake_on)
                        activateHandbrake( true)
                    }
                } else if (event.action == MotionEvent.ACTION_UP) {
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
                updateMotionUIItems(true)
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
                    if (getMotionState() == Motion.NEUTRAL) {
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
                    setDirectionLightsState(CorneringLight.LEFT_LIGHTS)
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
                    setDirectionLightsState(CorneringLight.RIGHT_LIGHTS)
                }
                viewModel.directionLightsLiveData.value = getDirectionLightsState()
                true
            }
            setOnClickListener {
                Toast.makeText(context, getString(R.string.long_click_info), Toast.LENGTH_SHORT).show()
                //true
            }
        }
        viewModel.directionLightsLiveData.observe(this, Observer<CorneringLight> {
            when (it) {
                CorneringLight.STRAIGHT_LIGHTS -> {
                    leftDirectionLightsAnimation.stop()
                    leftDirectionLightsAnimation.selectDrawable(0)
                    rightDirectionLightsAnimation.stop()
                    rightDirectionLightsAnimation.selectDrawable(0)
                }
                CorneringLight.LEFT_LIGHTS -> {
                    rightDirectionLightsAnimation.stop()
                    rightDirectionLightsAnimation.selectDrawable(0)
                    leftDirectionLightsAnimation.start()
                }
                CorneringLight.RIGHT_LIGHTS -> {
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
                        HandlingAssistance.MANUAL -> setHandlingAssistanceState(HandlingAssistance.WARNING)
                        HandlingAssistance.WARNING -> setHandlingAssistanceState(HandlingAssistance.FULL)
                        HandlingAssistance.FULL ->
                            Toast.makeText(context,
                                    getString(R.string.handling_assistance_full_warning),
                                    Toast.LENGTH_SHORT).show()
                    }
                    viewModel.handlingAssistanceLiveData.value = getHandlingAssistanceState()
                }
                true
            }
            setOnClickListener {
                when (getHandlingAssistanceState()) {
                    HandlingAssistance.FULL -> setHandlingAssistanceState(HandlingAssistance.WARNING)
                    HandlingAssistance.WARNING -> setHandlingAssistanceState(HandlingAssistance.MANUAL)
                    HandlingAssistance.MANUAL ->
                        Toast.makeText(context,
                                getString(R.string.handling_assistance_none_warning),
                                Toast.LENGTH_SHORT).show()
                }
                viewModel.handlingAssistanceLiveData.value = getHandlingAssistanceState()
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
                        MotorSpeedLimiter.NO_SPEED ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_1)
                        MotorSpeedLimiter.SLOW_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_2)
                        MotorSpeedLimiter.SLOW_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_1)
                        MotorSpeedLimiter.MEDIUM_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_2)
                        MotorSpeedLimiter.MEDIUM_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_1)
                        MotorSpeedLimiter.FAST_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_2)
                        MotorSpeedLimiter.FAST_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.FULL_SPEED)
                        MotorSpeedLimiter.FULL_SPEED -> Toast.
                            makeText(context,
                            getString(R.string.motor_speed_limiter_full_warning),
                            Toast.LENGTH_SHORT
                            ).show()
                    }
                    viewModel.motorSpeedLimiterLiveData.value = getMotorSpeedLimiter()
                }
                true
            }
            setOnClickListener {
                if(::retrofit.isInitialized && isEngineStarted()) {
                    when (getMotorSpeedLimiter()) {
                        MotorSpeedLimiter.FULL_SPEED ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_2)
                        MotorSpeedLimiter.FAST_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.FAST_SPEED_1)
                        MotorSpeedLimiter.FAST_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_2)
                        MotorSpeedLimiter.MEDIUM_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.MEDIUM_SPEED_1)
                        MotorSpeedLimiter.MEDIUM_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_2)
                        MotorSpeedLimiter.SLOW_SPEED_2 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.SLOW_SPEED_1)
                        MotorSpeedLimiter.SLOW_SPEED_1 ->
                            setMotorSpeedLimiter(MotorSpeedLimiter.NO_SPEED)
                        MotorSpeedLimiter.NO_SPEED -> Toast.
                            makeText(context,
                                getString(R.string.motor_speed_limiter_none_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                    viewModel.motorSpeedLimiterLiveData.value = getMotorSpeedLimiter()
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
                    if (getHandlingAssistanceState() != HandlingAssistance.FULL) {
                        when (getFrontDifferentialSlipperyLimiter()) {
                            DifferentialSlipperyLimiter.LOCKED ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
                            DifferentialSlipperyLimiter.MEDI_2 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
                            DifferentialSlipperyLimiter.MEDI_1 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
                            DifferentialSlipperyLimiter.MEDI_0 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.OPEN)
                            DifferentialSlipperyLimiter.OPEN -> Toast.makeText(
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
                    viewModel.frontDifferentialSlipperyLimiterLiveData.value =
                        previousFrontDifferentialSlipperyLimiter
                }
                true
            }
            setOnClickListener {
                if(::retrofit.isInitialized && isEngineStarted()) {
                    if (getHandlingAssistanceState() != HandlingAssistance.FULL) {
                        when (getFrontDifferentialSlipperyLimiter()) {
                            DifferentialSlipperyLimiter.OPEN ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
                            DifferentialSlipperyLimiter.MEDI_0 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
                            DifferentialSlipperyLimiter.MEDI_1 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
                            DifferentialSlipperyLimiter.MEDI_2 ->
                                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.LOCKED)
                            DifferentialSlipperyLimiter.LOCKED -> Toast.makeText(
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
                    viewModel.frontDifferentialSlipperyLimiterLiveData.value =
                        previousFrontDifferentialSlipperyLimiter
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
                    if (getHandlingAssistanceState() != HandlingAssistance.FULL) {
                        when (getRearDifferentialSlipperyLimiter()) {
                            DifferentialSlipperyLimiter.LOCKED ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
                            DifferentialSlipperyLimiter.MEDI_2 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
                            DifferentialSlipperyLimiter.MEDI_1 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
                            DifferentialSlipperyLimiter.MEDI_0 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.OPEN)
                            DifferentialSlipperyLimiter.OPEN -> Toast.makeText(
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
                    viewModel.rearDifferentialSlipperyLimiterLiveData.value =
                        previousRearDifferentialSlipperyLimiter
                }
                true
            }
            setOnClickListener {
                if(::retrofit.isInitialized && isEngineStarted()) {
                    if (getHandlingAssistanceState() != HandlingAssistance.FULL) {
                        when (getRearDifferentialSlipperyLimiter()) {
                            DifferentialSlipperyLimiter.OPEN ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_0)
                            DifferentialSlipperyLimiter.MEDI_0 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_1)
                            DifferentialSlipperyLimiter.MEDI_1 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.MEDI_2)
                            DifferentialSlipperyLimiter.MEDI_2 ->
                                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.LOCKED)
                            DifferentialSlipperyLimiter.LOCKED -> Toast.makeText(
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
                    viewModel.rearDifferentialSlipperyLimiterLiveData.value =
                        previousRearDifferentialSlipperyLimiter
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
                    0 -> setSteering(Turn.LEFT, SteeringValue.VALUE_100)
                    10 -> setSteering(Turn.LEFT, SteeringValue.VALUE_80)
                    20 -> setSteering(Turn.LEFT, SteeringValue.VALUE_60)
                    30 -> setSteering(Turn.LEFT, SteeringValue.VALUE_40)
                    40 -> setSteering(Turn.LEFT, SteeringValue.VALUE_20)
                    50 -> {
                        setSteering(Turn.STRAIGHT)
                        viewModel.directionLightsLiveData.value = getDirectionLightsState()
                    }
                    60 -> setSteering(Turn.RIGHT, SteeringValue.VALUE_20)
                    70 -> setSteering(Turn.RIGHT, SteeringValue.VALUE_40)
                    80 -> setSteering(Turn.RIGHT, SteeringValue.VALUE_60)
                    90 -> setSteering(Turn.RIGHT, SteeringValue.VALUE_80)
                    100 -> setSteering(Turn.RIGHT, SteeringValue.VALUE_100)
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

                updateMotionUIItems(true)
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

    private fun updateUITempItems(warningType: TemperatureWarning, item: ImageView, states: TypedArray) {
        val i = when (warningType) {
            TemperatureWarning.NOTHING_TEMPERATURE -> 0
            TemperatureWarning.NORMAL_TEMPERATURE -> 1
            TemperatureWarning.MEDIUM_TEMPERATURE -> 2
            TemperatureWarning.HIGH_TEMPERATURE -> 3
            else -> 0
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Advanced sensor non-interactive images are updated by server actions
        which are sent to the client when the Raspi has to.
     */
    private fun updateUIAdvancedSensorItems(state: ModuleState, item: ImageView, states: TypedArray) {
        val i = when (state) {
            ModuleState.NOTHING, ModuleState.OFF -> 0
            ModuleState.ON -> 1
            ModuleState.IDLE -> 2
            else -> 0
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Motion interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotionUIItems(fromServer: Boolean){
        viewModel.parkingBrakeLiveData.value = if (fromServer) isParkingBrakeActive() else false
        viewModel.handbrakeLiveData.value = if (fromServer) isHandbrakeActive() else false
    }

    /* Differential slippery limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateDifferentialSlipperyUIItems(state: DifferentialSlipperyLimiter, item: ImageView, states: TypedArray) {
        val i = when (state) {
            DifferentialSlipperyLimiter.OPEN -> 1
            DifferentialSlipperyLimiter.MEDI_0 -> 2
            DifferentialSlipperyLimiter.MEDI_1 -> 3
            DifferentialSlipperyLimiter.MEDI_2 -> 4
            DifferentialSlipperyLimiter.LOCKED -> 5
            DifferentialSlipperyLimiter.AUTO -> 6
            DifferentialSlipperyLimiter.ERROR -> 7
            else -> 0 // and OFF state
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Motor speed limiter interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.
     */
    private fun updateMotorSpeedLimiterUIItem(state: MotorSpeedLimiter, item: ImageView, states: TypedArray) {
        val i = when (state) {
            MotorSpeedLimiter.NO_SPEED -> 1
            MotorSpeedLimiter.SLOW_SPEED_1 -> 2
            MotorSpeedLimiter.SLOW_SPEED_2 -> 3
            MotorSpeedLimiter.MEDIUM_SPEED_1 -> 4
            MotorSpeedLimiter.MEDIUM_SPEED_2 -> 5
            MotorSpeedLimiter.FAST_SPEED_1 -> 6
            MotorSpeedLimiter.FAST_SPEED_2 -> 7
            MotorSpeedLimiter.FULL_SPEED -> 8
            //MotorSpeedLimiter.AUTO.value -> 9 // Not implemented yet
            MotorSpeedLimiter.ERROR_SPEED -> 10
            else -> 0 // and OFF state
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()
    }

    /* Handling assistance interactive actions must be depending on each other.
        Their states on the server should be changed by set methods.
        This function here should get these states which must be as I want,
        and if they don't check the set functions between client-server.

        Also, here update the ImageViews of the items, which are connected
        to handling assistance (ex. differential, suspension) and some of their
        functionality values.
     */
    private fun updateHandlingAssistanceUIItem(state: HandlingAssistance, item: ImageView, states: TypedArray){
        val i = when (state) {
            HandlingAssistance.MANUAL -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)
                1
            }
            HandlingAssistance.WARNING -> {
                setFrontDifferentialSlipperyLimiter(previousFrontDifferentialSlipperyLimiter)
                setRearDifferentialSlipperyLimiter(previousRearDifferentialSlipperyLimiter)
                2
            }
            HandlingAssistance.FULL -> {
                setFrontDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.AUTO)
                setRearDifferentialSlipperyLimiter(DifferentialSlipperyLimiter.AUTO)
                3
            }
            HandlingAssistance.NULL ->0
            else -> 0
        }
        item.setImageResourceWithTag(states.getResourceId(i, 0))
        states.recycle()

        if((state == HandlingAssistance.FULL) || (state == HandlingAssistance.WARNING)){
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

        viewModel.frontDifferentialSlipperyLimiterLiveData.value =
            if (state == HandlingAssistance.NULL) DifferentialSlipperyLimiter.NULL
            else getFrontDifferentialSlipperyLimiter()
        viewModel.rearDifferentialSlipperyLimiterLiveData.value =
            if (state == HandlingAssistance.NULL) DifferentialSlipperyLimiter.NULL
            else getRearDifferentialSlipperyLimiter()
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
