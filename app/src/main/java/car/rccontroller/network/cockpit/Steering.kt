package car.rccontroller.network.cockpit

import car.rccontroller.network.*
import car.rccontroller.retrofit
import car.rccontroller.steeringAPI
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Steering {
    @GET("/set_steering_system")
    fun setSteeringAction(@Query("id") id: Long,
                          @Query("direction") direction: String,
                          @Query("value") value: Int): Call<String>

    @GET("/get_steering_direction")
    fun getSteeringDirection(): Call<String>
}



const val ACTION_TURN_RIGHT = "right"
const val ACTION_TURN_LEFT = "left"
const val ACTION_STRAIGHT = "straight"
// Initial value should be 0 cuz in server is -1
var steeringDirectionId = 0L
fun getSteeringDirection(retrofitAPI: Steering = steeringAPI): String {
    return runBlockingRequest { retrofitAPI.getSteeringDirection() } ?: EMPTY_STRING
}

fun setSteering(direction: String, value: Int = 0, retrofitAPI: Steering = steeringAPI): Job? {
    return launchRequest { retrofitAPI.setSteeringAction(steeringDirectionId++, direction, value) }
}