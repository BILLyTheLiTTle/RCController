package car.rccontroller.network

import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


const val OK_DATA = "OK"
const val NO_DATA = "NULL"

var serverIp: String? = null
var serverPort: Int? = null

/////////
// Engine
/////////
val isEngineStarted: Boolean
get() = doBlockingRequest("http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/get_engine_state").toBoolean()

fun startEngine(serverIp: String?, serverPort: Int?): String{
    // reset this id to the client
    throttleBrakeActionId = 0

    car.rccontroller.network.serverIp = serverIp
    car.rccontroller.network.serverPort = serverPort

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    val url = "http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/start_engine"

    return doBlockingRequest(url)
}

fun stopEngine(): String {
    val msg = doBlockingRequest("http://$serverIp:" +
            "$serverPort/stop_engine")

    if(msg == OK_DATA) {
        serverIp = null
        serverPort = null
    }

    return msg
}


/////////
// Throttle -n- Brakes
/////////
const val ACTION_MOVE_FORWARD = "forward"
const val ACTION_MOVE_BACKWARD = "backwards"
const val ACTION_STILL = "still"
const val ACTION_PARKING_BRAKE = "parking_brake"
const val ACTION_HANDBRAKE = "handbrake"
// Initial value should be 0 cuz in server is -1
var throttleBrakeActionId = 0

//---- Parking Brake ----
val isParkingBrakeActive: Boolean
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_parking_brake_state").toBoolean()

fun activateParkingBrake(state: Boolean) = if (state)
        doBlockingRequest("http://$serverIp:$serverPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=100")
    else
        doBlockingRequest("http://$serverIp:$serverPort/" +
                "set_throttle_brake_system?" +
                "id=${throttleBrakeActionId++}" +
                "&action=$ACTION_PARKING_BRAKE" +
                "&value=0")

//---- Handbrake ----
val isHandbrakeActive: Boolean
    get() = doBlockingRequest("http://$serverIp:$serverPort/" +
            "get_handbrake_state").toBoolean()

fun activateHandbrake(state: Boolean) = if (state)
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=100")
else
    doNonBlockingRequest("http://$serverIp:$serverPort/" +
            "set_throttle_brake_system?" +
            "id=${throttleBrakeActionId++}" +
            "&action=$ACTION_HANDBRAKE" +
            "&value=0")


/////////
// General use
/////////
private fun doNonBlockingRequest(url:String) = launch { doRequest(url) }

private fun doBlockingRequest(url:String) = runBlocking(CommonPool) { doRequest(url) }

private fun doRequest(url: String): String {
    var con: HttpURLConnection?
    val urlGet: URL
    var inputStream: InputStream? = null
    val sb =  StringBuilder()
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        inputStream = con.run {
            readTimeout = 10000 /* milliseconds */
            connectTimeout = 2000 /* milliseconds */
            requestMethod = "GET"
            doInput = true
            // Start the query
            connect()
            inputStream
        }

        val bufferReader = BufferedReader(InputStreamReader(inputStream), 4096)
        var line: String?

        line = bufferReader.readLine()
        while (line != null) {
            sb.append(line)
            line = bufferReader.readLine()
        }
        bufferReader.close()
    } catch (e: IOException) {
        //handle the exception !
        sb.append(e.message ?: NO_DATA)
    }
    return sb.toString()

}
