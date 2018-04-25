package car.rccontroller.network

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


const val OK_STATUS = "OK"

var serverIp: String? = null
var serverPort: Int? = null

fun handshake(serverIp: String, serverPort: Int): String{
    var returnMsg: String = "empty"
    runBlocking {
        //TODO add the nanohttp ip and port when needed
        val msg = async { doRequest("http://$serverIp:$serverPort/handshake") }
        returnMsg = msg.await()
    }

    return returnMsg
}

private fun doRequest(url: String): String {
    var con: HttpURLConnection? = null
    val urlGet: URL
    var inputStream: InputStream? = null
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        con.readTimeout = 10000 /* milliseconds */
        con.connectTimeout = 15000 /* milliseconds */
        con.requestMethod = "GET"
        con.doInput = true
        // Start the query
        con.connect()
        inputStream = con.inputStream
    } catch (e: IOException) {
        //handle the exception !
        return e.message ?: "empty"
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