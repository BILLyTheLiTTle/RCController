package car.rccontroller.network

/*
This file is designed to store and manage network-related data,
somehow like the ViewModel class is designed to store and manage UI-related data
 */

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import retrofit2.Call


const val OK_STRING = "OK"
const val EMPTY_STRING = "NULL"

/////////
// General use
/////////
fun runBlockingRequest(url:String) = runBlocking(Dispatchers.IO) { doRequest(url) }

fun <T> launchRequest(block:() -> Call<T>): Job =
    CoroutineScope(Dispatchers.IO).launch { block().execute().body() }

fun <T> runBlockingRequest(block:() -> Call<T>): T? =
    runBlocking(Dispatchers.IO) { block().execute().body() }

private fun doRequest(url: String): String {
    val con: HttpURLConnection?
    val urlGet: URL
    val requestInputStream: InputStream?
    val sb =  StringBuilder()
    try {
        urlGet = URL(url)
        con = urlGet.openConnection() as HttpURLConnection
        requestInputStream = con.run {
            readTimeout = 500 /* milliseconds */
            connectTimeout = 500 /* milliseconds */
            requestMethod = "GET"
            doInput = true
            // Start the query
            connect()
            inputStream
        }

        val bufferReader = BufferedReader(InputStreamReader(requestInputStream), 4096)
        var line: String?

        line = bufferReader.readLine()
        while (line != null) {
            sb.append(line)
            line = bufferReader.readLine()
        }
        bufferReader.close()
    } catch (e: IOException) {
        //handle the exception !
        sb.append(e.message ?: EMPTY_STRING)
    }
    return sb.toString()
}