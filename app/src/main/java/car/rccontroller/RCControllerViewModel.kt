package car.rccontroller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import car.feedback.cockpit.DirectionLight
import car.feedback.cockpit.MainLight
import car.feedback.server.TemperatureWarningType

class RCControllerViewModel: ViewModel() {

    val engineStatusLiveData = MutableLiveData<Boolean>()
    val steeringStatusLiveData = MutableLiveData<Boolean>()
    val throttleStatusLiveData = MutableLiveData<Boolean>()
    val reverseStatusLiveData = MutableLiveData<Boolean>()
    val emergencyLightsStatusLiveData = MutableLiveData<Boolean>()
    val cruiseControlStatusLiveData = MutableLiveData<Boolean>()
    val speedLiveData = MutableLiveData<String>()
    val rearLeftMotorTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val rearRightMotorTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val frontLeftMotorTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val frontRightMotorTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val rearHBridgeTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val frontHBridgeTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val raspberryPiTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val batteriesTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val shiftRegistersTemperatureLiveData = MutableLiveData<TemperatureWarningType>()
    val parkingBrakeLiveData = MutableLiveData<Boolean>()
    val handbrakeLiveData = MutableLiveData<Boolean>()
    val visionLightsLiveData = MutableLiveData<MainLight>()
    val directionLightsLiveData = MutableLiveData<DirectionLight>()

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
        rearLeftMotorTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        rearRightMotorTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        frontLeftMotorTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        frontRightMotorTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        rearHBridgeTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        frontHBridgeTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        raspberryPiTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        batteriesTemperatureLiveData.value = TemperatureWarningType.UNCHANGED
        shiftRegistersTemperatureLiveData.value = TemperatureWarningType.UNCHANGED

        visionLightsLiveData.value = MainLight.LIGHTS_OFF
        directionLightsLiveData.value = DirectionLight.DIRECTION_LIGHTS_STRAIGHT

        parkingBrakeLiveData.value = false
        handbrakeLiveData.value = false
    }
}