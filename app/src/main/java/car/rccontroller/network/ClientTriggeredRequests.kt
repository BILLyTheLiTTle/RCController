package car.rccontroller.network

import kotlinx.coroutines.experimental.async
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

val isEngineStarted: Boolean
get() = doBlockingRequest("http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/get_engine_state").toBoolean()

fun startEngine(serverIp: String?, serverPort: Int?): String{
    car.rccontroller.network.serverIp = serverIp
    car.rccontroller.network.serverPort = serverPort

    //TODO add the nanohttp ip and port when needed as argument to the handshake
    val url = "http://${car.rccontroller.network.serverIp}:" +
            "${car.rccontroller.network.serverPort}/start_engine"

    return doBlockingRequest(url)
}

fun stopEngine() = doBlockingRequest("http://${car.rccontroller.network.serverIp}:" +
        "${car.rccontroller.network.serverPort}/stop_engine")


private fun doBlockingRequest(url:String): String {
    var returnMsg: String = NO_DATA
    runBlocking {

        val msg = async { doRequest(url) }
        returnMsg = msg.await()
    }

    return returnMsg
}

private fun doRequest(url: String): String {
    var con: HttpURLConnection?
    val urlGet: URL
    var inputStream: InputStream?
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        con.readTimeout = 10000 /* milliseconds */
        con.connectTimeout = 2000 /* milliseconds */
        con.requestMethod = "GET"
        con.doInput = true
        // Start the query
        con.connect()
        inputStream = con.inputStream
    } catch (e: IOException) {
        //handle the exception !
        return e.message ?: NO_DATA
    }

    val bufferReader = BufferedReader(InputStreamReader(inputStream), 4096)
    var line: String?
    val sb =  StringBuilder()
    line = bufferReader.readLine()
    while (line != null) {
        sb.append(line)
        line = bufferReader.readLine()
    }
    bufferReader.close()
    return sb.toString()

}
