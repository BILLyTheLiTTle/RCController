package car.rccontroller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RCControllerViewModel: ViewModel() {

    val engineStatusLiveData = MutableLiveData<Boolean>()
    val steeringStatusLiveData = MutableLiveData<Boolean>()
    val throttleStatusLiveData = MutableLiveData<Boolean>()
    val reverseStatusLiveData = MutableLiveData<Boolean>()
    val emergencyLightsStatusLiveData = MutableLiveData<Boolean>()
    val cruiseControlStatusLiveData = MutableLiveData<Boolean>()
    val speedLiveData = MutableLiveData<String>()

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
    }

}