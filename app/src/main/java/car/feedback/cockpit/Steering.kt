package car.feedback.cockpit

import car.feedback.*
import car.rccontroller.retrofit
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

    companion object {
        val steeringAPI: Steering by lazy { retrofit.create<Steering>(Steering::class.java) }
    }
}



const val ACTION_TURN_RIGHT = "right"
const val ACTION_TURN_LEFT = "left"
const val ACTION_STRAIGHT = "straight"
// Initial value should be 0 cuz in server is -1
var steeringDirectionId = 0L
fun getSteeringDirection(): String {
    return runBlockingRequest { Steering.steeringAPI.getSteeringDirection() } ?: EMPTY_STRING
}

fun setSteering(direction: String, value: Int = 0): Job? {
    return launchRequest { Steering.steeringAPI.setSteeringAction(steeringDirectionId++, direction, value) }
}