package car.rccontroller.network.cockpit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ThrottleBrake {
    @GET("/set_throttle_brake_system")
    fun setThrottleBrakeAction(@Query("id") id: Long,
                               @Query("action") action: String,
                               @Query("value") value: Int): Call<String>

    @GET("/get_parking_brake_state")
    fun getParkingBrakeState(): Call<Boolean>

    @GET("/get_handbrake_state")
    fun getHandbrakeState(): Call<Boolean>

    @GET("/get_motion_state")
    fun getMotionState(): Call<String>
}