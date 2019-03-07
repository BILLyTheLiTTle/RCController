package car.rccontroller.network.cockpit

import car.rccontroller.network.*
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
}

private val api: Steering by lazy { retrofit.create<Steering>(Steering::class.java) }

const val ACTION_TURN_RIGHT = "right"
const val ACTION_TURN_LEFT = "left"
const val ACTION_STRAIGHT = "straight"
// Initial value should be 0 cuz in server is -1
var steeringDirectionId = 0L
fun getSteeringDirection(retrofitApi: Steering = api): String {
    return runBlockingRequest { retrofitApi.getSteeringDirection() } ?: EMPTY_STRING
}

fun setSteering(direction: String, value: Int = 0, retrofitApi: Steering = api): Job? {
    return launchRequest { retrofitApi.setSteeringAction(steeringDirectionId++, direction, value) }
}