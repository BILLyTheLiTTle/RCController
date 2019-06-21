package car.feedback.cockpit

import car.enumContains
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
                          @Query("value") value: String): Call<String>

    @GET("/get_steering_direction")
    fun getSteeringDirection(): Call<String>

    companion object {
        val steeringAPI: Steering by lazy { retrofit.create<Steering>(Steering::class.java) }
    }
}

// Initial value should be 0 cuz in server is -1
var steeringDirectionId = 0L
fun getSteeringDirection(): Turn {

    val steeringDirection = runBlockingRequest {
        Steering.steeringAPI.getSteeringDirection()
    } ?: EMPTY_STRING

    return if (enumContains<Turn>(steeringDirection)) Turn.valueOf(steeringDirection) else Turn.NOTHING
}

fun setSteering(direction: Turn, value: SteeringValue = SteeringValue.VALUE_00): Job? {
    return launchRequest { Steering.steeringAPI.setSteeringAction(steeringDirectionId++, direction.name, value.name) }
}

enum class Turn {
    NOTHING, RIGHT, LEFT, STRAIGHT
}

enum class SteeringValue{
    VALUE_00, VALUE_20, VALUE_40, VALUE_60, VALUE_80, VALUE_100, NOTHING
}