package car.rccontroller.network

import android.util.Log
import fi.iki.elonen.NanoHTTPD

// constructor default parameters are for emulator
class Server(val ip: String = "localhost", val port: Int = 8081) : NanoHTTPD(ip, port) {

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri

        if (uri == "/hello") {
            val response = "HelloWorld"
            return newFixedLengthResponse(response);
        }
        else
            return newFixedLengthResponse("null");
    }
}