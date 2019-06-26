package car.rccontroller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import car.feedback.cockpit.*
import car.feedback.server.ModuleState
import car.feedback.server.TemperatureWarning

class RCControllerViewModel: ViewModel() {

    val engineStatusLiveData = MutableLiveData<Boolean>()
    val steeringStatusLiveData = MutableLiveData<Boolean>()
    val throttleStatusLiveData = MutableLiveData<Boolean>()
    val reverseStatusLiveData = MutableLiveData<Boolean>()
    val emergencyLightsStatusLiveData = MutableLiveData<Boolean>()
    val cruiseControlStatusLiveData = MutableLiveData<Boolean>()
    val speedLiveData = MutableLiveData<String>()
    val rearLeftMotorTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val rearRightMotorTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val frontLeftMotorTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val frontRightMotorTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val rearHBridgeTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val frontHBridgeTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val raspberryPiTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val batteriesTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val shiftRegistersTemperatureLiveData = MutableLiveData<TemperatureWarning>()
    val parkingBrakeLiveData = MutableLiveData<Boolean>()
    val handbrakeLiveData = MutableLiveData<Boolean>()
    val visionLightsLiveData = MutableLiveData<MainLight>()
    val directionLightsLiveData = MutableLiveData<CorneringLight>()
    val handlingAssistanceLiveData = MutableLiveData<HandlingAssistance>()
    val tractionControlModuleLiveData = MutableLiveData<ModuleState>()
    val antilockBrakingModuleLiveData = MutableLiveData<ModuleState>()
    val electronicStabilityModuleLiveData = MutableLiveData<ModuleState>()
    val understeerDetectionModuleLiveData = MutableLiveData<ModuleState>()
    val oversteerDetectionModuleLiveData = MutableLiveData<ModuleState>()
    val collisionDetectionModuleLiveData = MutableLiveData<ModuleState>()
    val rearDifferentialSlipperyLimiterLiveData = MutableLiveData<DifferentialSlipperyLimiter>()
    val frontDifferentialSlipperyLimiterLiveData = MutableLiveData<DifferentialSlipperyLimiter>()
    val motorSpeedLimiterLiveData = MutableLiveData<MotorSpeedLimiter>()

    /* The values are initialized at false or empty state because
    I want the grey icons at start because
     the controller is not connected to server automatically after the launch*/
    init {
        engineStatusLiveData.value = false

        // disable seek bars from here cuz it did not work from xml
        steeringStatusLiveData.value = false
        throttleStatusLiveData.value = false

        reverseStatusLiveData.value = false
        emergencyLightsStatusLiveData.value = false
        cruiseControlStatusLiveData.value = false
        //speedLiveData.value = "-/-"
        rearLeftMotorTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        rearRightMotorTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        frontLeftMotorTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        frontRightMotorTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        rearHBridgeTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        frontHBridgeTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        raspberryPiTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        batteriesTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE
        shiftRegistersTemperatureLiveData.value = TemperatureWarning.UNCHANGED_TEMPERATURE

        visionLightsLiveData.value = MainLight.LIGHTS_OFF
        directionLightsLiveData.value = CorneringLight.STRAIGHT_LIGHTS

        parkingBrakeLiveData.value = false
        handbrakeLiveData.value = false

        tractionControlModuleLiveData.value = ModuleState.UNCHANGED
        antilockBrakingModuleLiveData.value = ModuleState.UNCHANGED
        electronicStabilityModuleLiveData.value = ModuleState.UNCHANGED
        understeerDetectionModuleLiveData.value = ModuleState.UNCHANGED
        oversteerDetectionModuleLiveData.value = ModuleState.UNCHANGED
        collisionDetectionModuleLiveData.value = ModuleState.UNCHANGED
    }
}